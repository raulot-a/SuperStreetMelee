import java.util.ArrayList;

import javax.swing.JFrame;

public class Game {

	static ArrayList<Joueur> tabJoueurs;
	static Joueur j;
	static Menu frame;
	static Collision c;
	ArrayList<Attaque> tabAttaques;
	static int tailleDecor;
	static ArrayList<Decor> tabDecor;
	public int collisionLeft, collisionRight, collisionTop, collisionBottom;
	final static int GRAVITY_MAX = 2;
	final static int INERTIE = 2;
	final static int GRAVITY_SPEED_CAP = 50;
	public static int gameDuration;
	public static STATE CURRENT_STATE = STATE.IN_MENU ;

	//Creation de la fenetre de jeu
	public Game() {
		
		Levels a = new Levels();
		tabDecor = a.levels.get(1);
		
		// Creation des attaques
		tabAttaques = new ArrayList<Attaque>();
		tabAttaques.clear();
		tabAttaques.add(new Attaque("Base", 5, 5, 5, 0, 10, 10,20,20));
		tabAttaques.add(new Attaque("Grosse", 20, 10, 15, 60, 80, 150,100,100));
		//Mettre en focntion de atk et coolldown
		
		// Creation des joueurs
		tabJoueurs = new ArrayList<Joueur>();
		tabJoueurs.clear();
		tabJoueurs.add(new Joueur("Joueur 1", 250, 10, 60, 100, tabAttaques));
		tabJoueurs.add(new Joueur("Joueur 2", 500, 10, 60, 100, tabAttaques));
		
		// Initilisation de la duree de la partie en sec
		gameDuration = 120;
		
		//Appel et ajout du pattern d'affichage	
		VueGraphique vg = new VueGraphique(tabDecor, tabAttaques, tabJoueurs);
		frame = new Menu("SuperStreetMelee", vg) ;
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setFocusable(true) ;
		
		//Ajout des controles
		new ControleurGraphique(frame, tabJoueurs);
		
		c = new Collision();
		
		collisionLeft = -1;
		collisionRight = -1; 
		collisionTop = -1; 
		collisionBottom = -1;
	}
				
	public static void resetGame() {
		for (int i = 0; i < tabJoueurs.size(); i++) {
    		tabJoueurs.get(i).setX(i*250+250);
			tabJoueurs.get(i).setY(110);
			tabJoueurs.get(i).resetLife();
			tabJoueurs.get(i).vitesseX = 0;
			tabJoueurs.get(i).vitesseY = 0;
			tabJoueurs.get(i).left = false;
			tabJoueurs.get(i).right = false;
			tabJoueurs.get(i).jump = false;
			tabJoueurs.get(i).isJumping = false;
			gameDuration = 120;
    	}
	}
	
	public void update() {
		
		// Lois du monde
		
		if (CURRENT_STATE == STATE.IN_GAME) {
			for (Joueur j : tabJoueurs) {
				gravity(j);
				//on verifie si les joueurs ont lance des attaques
				j.verificationAttack();
				// on met a jour les temps lies aux attaques des joueurs (temps de recharge, temps d'affichage, etc)
				j.updateTimeAttack();
			}
			// Intelligence artificielle
		}
	}
	
	public void updateTimer() {
		if (CURRENT_STATE == STATE.IN_GAME) {
			// Timer
			if (gameDuration > 0)
				gameDuration--;
			else {
				// Temps ecoule, partie terminee
				gameDuration = 0;
				main.g = new Game();
			}
		}
	}

	private void gravity(Joueur j) {
		if (CURRENT_STATE == STATE.IN_GAME) {
			j.vitesseY = j.vitesseY + GRAVITY_MAX;
			if (j.vitesseY > GRAVITY_SPEED_CAP) {
				j.vitesseY = GRAVITY_SPEED_CAP;
			}
			int y = j.getY() + j.vitesseY / 10;
			int x = j.getX() + j.vitesseX / 10;
			if (j.vitesseY > 0) {

				//Le personnage est en train d'aller vers le bas
				collisionBottom = c.collisionCalculation(j.getX(), y, j.getW(),
						j.getH(), tabDecor);
				if (collisionBottom > -1) {
					//Contact avec le sol
					j.setY(j.getY() + tabDecor.get(collisionBottom).y
							- j.getY() - j.getH());
					j.vitesseY = 0;
					j.jumps = j.jumpsBase;
				} else {
					//Le personnage tombe
					j.setY(y);
					collisionBottom = -1;
				}
			} else if (j.vitesseY < 0) {
				//Le personnage est en train d'aller vers le haut
				collisionTop = c.collisionCalculation(j.getX(), y, j.getW(),
						j.getH(), tabDecor);
				if (collisionTop > -1) {
					//Contact avec un decor situe au-dessus
					j.setY(j.getY() + tabDecor.get(collisionTop).y
							+ tabDecor.get(collisionTop).h - j.getY());
					j.vitesseY = 0;
				} else {
					//Le personnage monte
					j.setY(y);
				}
			}
			if (j.vitesseX == 0) {
				if (j.right && j.isJumping) {
					j.vitesseX += INERTIE / 2;
				} else if (j.left && j.isJumping) {
					j.vitesseX -= INERTIE / 2;
				} else if (j.right) {
					j.vitesseX += INERTIE;
				} else if (j.left) {
					j.vitesseX -= INERTIE;
				}
			} else if (j.vitesseX > 0) {
				//Le personnage est en train d'aller vers la droite

				//Si touche droite non enfoncee, inertie mise en place pour freiner
				if (!j.right) {
					j.vitesseX -= INERTIE / 2;
				} else {
					if (j.status == j.EJECTED) {
						j.vitesseX -= INERTIE / 2;
						if (j.vitesseX <= j.MAX_RUN_SPEED) {
							j.status = j.NORMAL;
						}
					} else {
						j.vitesseX += INERTIE;
						if (j.vitesseX >= j.MAX_RUN_SPEED) {
							j.vitesseX = j.MAX_RUN_SPEED;
						}
					}
				}

				x = j.getX() + j.vitesseX / 10;
				collisionRight = c.collisionCalculation(x, j.getY(), j.getW(),
						j.getH(), tabDecor);
				if (collisionRight > -1) {
					//Contact avec le decor sur la droite
					j.vitesseX = 0;
					j.setX(j.getX() + tabDecor.get(collisionRight).x - j.getX()
							- j.getW());
				} else {
					//Le personnage se deplace sur la droite
					j.setX(x);
				}

			} else if (j.vitesseX < 0) {
				//Le personnage est en train d'aller vers la gauche

				//Si touche gauche non enfoncee, inertie mise en place pour freiner
				if (!j.left) {
					j.vitesseX += INERTIE / 2;
				} else {
					if (j.status == j.EJECTED) {
						j.vitesseX += INERTIE / 2;
						if (j.vitesseX >= -j.MAX_RUN_SPEED) {
							j.status = j.NORMAL;
						}
					} else {
						j.vitesseX -= INERTIE;
						if (j.vitesseX <= -j.MAX_RUN_SPEED) {
							j.vitesseX = -j.MAX_RUN_SPEED;
						}
					}
				}

				x = j.getX() + j.vitesseX / 10;
				collisionLeft = c.collisionCalculation(x, j.getY(), j.getW(),
						j.getH(), tabDecor);
				if (collisionLeft > -1) {
					//Contact avec le d�cor sur la gauche
					j.vitesseX = 0;
					j.setX(j.getX() + tabDecor.get(collisionLeft).x
							+ tabDecor.get(collisionLeft).w - j.getX());
				} else {
					//Le personnage se d�place sur la gauche
					j.setX(x);
				}
			}
		} 
	}

	public void render() {
		frame.repaint();
	}
	
	
	

	
}
