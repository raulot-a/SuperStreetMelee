import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;


public class Game {

	static ArrayList<Joueur> tabJoueurs;
	static Joueur j;
	static JFrame frame;
	static Collision c;
	static ArrayList<Decor> tabDecor;
	static ArrayList<Attaque> tabAttaques;
	static int tailleDecor;
	public int collisionLeft, collisionRight, collisionTop, collisionBottom;
	final static int GRAVITY_MAX = 2;
	
	//Creation de la fenetre de jeu
	public Game() {
		frame = new JFrame();
		
		// Création des attaques
		tabAttaques = new ArrayList<Attaque>();
		tabAttaques.clear();
		tabAttaques.add(new Attaque("Base", 5, 5, 2, 100, 500));
		tabAttaques.add(new Attaque("Grosse", 20, 10, 5, 300, 2000));
		
		// Creation des joueurs
		tabJoueurs = new ArrayList<Joueur>();
		tabJoueurs.clear();
		tabJoueurs.add(new Joueur("Joueur 1", 250, 10, 60, 100, tabAttaques));
		tabJoueurs.add(new Joueur("Joueur 2", 500, 10, 60, 100, tabAttaques));
		
		//Creation du terrain
		tabDecor = new ArrayList<Decor>();
		tabDecor.clear();
		tabDecor.add(new Decor(30,500,500,10));
		tabDecor.add(new Decor(600,500,200,10));
		tabDecor.add(new Decor(200,300,400,10));
		tabDecor.add(new Decor(520,440,10,60));
		tabDecor.add(new Decor(820,380,60,40));
		tabDecor.add(new Decor(800,420,60,20));
		tabDecor.add(new Decor(939,380,60,40));
		tabDecor.add(new Decor(660,340,80,20));
		
		//Appel et ajout du pattern d'affichage	
		VueGraphique vg = new VueGraphique(j, tabDecor, tabAttaques, tabJoueurs);
		frame.add(vg);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		Point p = frame.getLocation();
		
		//Ajout des controles
		ControleurGraphique cg = new ControleurGraphique(frame, tabJoueurs);
		
		c = new Collision();
		
		collisionLeft = -1;
		collisionRight = -1; 
		collisionTop = -1; 
		collisionBottom = -1;
	}
				
	
	public void update() {
		
		// Lois du monde
		// Collisions
		/*if (c.Collision(j.x, j.y, j.w, j.h, sol.x, sol.y, sol.w, sol.h)) {
			System.out.println("Collision");
		}*/
		for (Joueur j : tabJoueurs) {
			//gravity(j);
			// on met à jour les temps liés aux attaques des joueurs (temps de recharge, temps d'affichage, etc)
			j.updateTimeAttack();
		}
		checkKeys();
		// Intelligence artificielle
		
	}
	
	private void gravity(Joueur j) {
		int y = j.getY() + GRAVITY_MAX;
		collisionBottom = c.collisionCalculation(j.getX(), y, j.getW(), j.getH(), tabDecor);
		if (collisionBottom > -1) {
			j.setY(j.getY() + tabDecor.get(collisionBottom).y - j.getY()
					- j.getH());
			j.canJump = true;
		} else {
			j.setY(y);
			j.canJump = false;
		}

	}

	public void render() {
		frame.repaint();
	}
	
	
	public void checkKeys() {
		/////////
		//TODO Compter les secondes avant utilisation du jump !!!
		//////
		for (Joueur j : tabJoueurs) {
			if (j.isAlive) {
				if (j.jump) {
					jump(j);
					/*if (j.canJump) {
						jump(j);
						j.canJump = false;
					}*/
				}
				if (j.left) {
					left(j);
				}
				if (j.right) {
					right(j);
				}
				if (j.crouch) {
					crouch(j);
				}
			}
		}
	}

	public void left(Joueur j) {
	
		int x = j.getX() - j.vitesseH;
		collisionLeft = c.collisionCalculation(x, j.getY(), j.getW(), j.getH(), tabDecor);
		if (j.left) {
			if (collisionLeft > -1) {
				j.setX(j.getX() + tabDecor.get(collisionLeft).x
						+ tabDecor.get(collisionLeft).w - j.getX());
			} else {
				j.setX(x);
			}
		}
	}
	
	public void right(Joueur j) {
	
		int x = j.getX() + j.vitesseH;
		collisionRight = c.collisionCalculation(x, j.getY(), j.getW(), j.getH(), tabDecor);
		if (j.right) {
			if (collisionRight > -1) {
				j.setX(j.getX() + tabDecor.get(collisionRight).x - j.getX()
						- j.getW());
			} else {
				j.setX(x);
			}
		}
	}
	
	public void jump(Joueur j) {
	
		int y = j.getY() - 2/*130*/;
		// TODO Faire verification si decor au dessus du joueur
	
		//
		collisionTop = c.collisionCalculation(j.getX(), y, j.getW(), j.getH(), tabDecor);
		if (j.jump) {
			if (collisionTop > -1) {
				j.setY(j.getY() + tabDecor.get(collisionTop).y
						+ tabDecor.get(collisionTop).h - j.getY());
			} else {
				j.setY(y);
			}
		}
	}
	
	public void crouch(Joueur j) {
	
		int y = j.getY() + 2;
		collisionBottom = c.collisionCalculation(j.getX(), y, j.getW(), j.getH(), tabDecor);
		if (j.crouch) {
			if (collisionBottom > -1) {
				j.setY(j.getY() + tabDecor.get(collisionBottom).y - j.getY()
						- j.getH());
			} else {
				j.setY(y);
			}
		}
	}
}
