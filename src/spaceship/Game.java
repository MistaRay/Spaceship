package spaceship;

import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.*;

public class Game extends JFrame implements MouseListener, MouseMotionListener, KeyListener, ActionListener{
	Ship player;
	Vector2D mousePosition;
	HashSet <Asteroid> asteroids = new HashSet<Asteroid>();
	HashSet <Bullet> bullets = new HashSet<Bullet>();
	int asteroidSpawnTimer = 120;
	int bulletFireCooldown = 30;
	boolean firing = false;
	int lives = 3;
	BufferedImage bg;
	int START_SCREEN = 0;
	int GAME_SCREEN = 1;
	int END_SCREEN = 2;
	int screen = START_SCREEN;
	
	JComponent draw = new JComponent(){
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (screen == GAME_SCREEN) {
				g.drawImage(bg,0,0,this);
				g.setColor(new Color(255, 215, 0));
				for (Bullet bullet : bullets) {
					g.fillOval((int)(bullet.position.x - bullet.radius/2), (int)(bullet.position.y - bullet.radius/2), (int)bullet.radius*2, (int)bullet.radius*2);
				}
				g.setColor(new Color(190, 190, 190));
				for (Asteroid asteroid : asteroids) {
					g.fillOval((int)(asteroid.position.x - asteroid.radius/2), (int)(asteroid.position.y - asteroid.radius/2), (int)asteroid.radius*2, (int)asteroid.radius*2);
				}
				g.setColor(new Color(0, 0, 0));
				BufferedImage rotated = player.img;
				if (player.position.x != 0) {
					rotated = rotateImage(player.img, player.position.angleTo(mousePosition));
				}
				g.drawImage(rotated,(int)(player.position.x - player.radius/2), (int)(player.position.y - player.radius/2), this);
				//g.fillOval((int)(player.position.x - player.radius/2), (int)(player.position.y - player.radius/2), (int)player.radius*2, (int)player.radius*2);
			}
		}
	};
	
	static BufferedImage rotateImage(BufferedImage buffImage, double angle) {
	    double radian = Math.toRadians(angle);
	    double sin = Math.abs(Math.sin(radian));
	    double cos = Math.abs(Math.cos(radian));

	    int width = buffImage.getWidth();
	    int height = buffImage.getHeight();

	    int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
	    int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);

	    BufferedImage rotatedImage = new BufferedImage(
	            nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D graphics = rotatedImage.createGraphics();

	    graphics.setRenderingHint(
	            RenderingHints.KEY_INTERPOLATION,
	            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    
	    // rotation around the center point
	    graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
	    graphics.drawImage(buffImage, 0, 0, null);
	    graphics.dispose();

	    return rotatedImage;
	}

	public void start() {
		screen = GAME_SCREEN;
		try {
			gameScreen();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    repaint();
	}
	

	public void actionPerformed(ActionEvent e) {
		 if ("Easy".equals(e.getActionCommand())) {
			 try {
				screen = GAME_SCREEN;
				gameScreen();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		 } 
	}

	
	public Game() {
		player = new Ship();
		setSize(1000, 1000);
		try {
		    bg = ImageIO.read(new File("src/spaceship/space.png")); 
		} catch (IOException e) {
		}
		
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		add(draw);
	}
	
	public void fireBullet() {
		Bullet bullet = new Bullet();
		bullet.position = player.position;
		bullet.direction = mousePosition.subtract(player.position).normalized();
		bullets.add(bullet);
		
	}
	
	public void spawnAsteroid() {
		int num = (int)(Math.random() * 4);
		Asteroid asteroid = new Asteroid((int)(Math.random()*25+25));
		Vector2D destination;
		if (num == 0) {
			asteroid.position = new Vector2D(-100, (int)(Math.random()*1000));
			destination = new Vector2D(1100, (int)(Math.random()*1000));
		}
		else if (num == 1) {
			asteroid.position = new Vector2D(1100, (int)(Math.random()*1000));
			destination = new Vector2D(-100, (int)(Math.random()*1000));
		}
		else if (num == 2) {
			asteroid.position = new Vector2D((int)(Math.random()*1000), -100);
			destination = new Vector2D((int)(Math.random()*1000), 1100);
		}
		else {
			asteroid.position = new Vector2D((int)(Math.random()*1000), 1100);
			destination = new Vector2D((int)(Math.random()*1000), -100);
		}
		asteroid.direction = destination.subtract(asteroid.position).normalized();
		asteroids.add(asteroid);
	}
	
	public boolean isOffScreen(PhysicsBody val) {
		if (val.position.x >= 1100 || val.position.y >= 1100 || val.position.x <= -100 || val.position.y <= -100) {
			return true;
		}
		return false;
	}

	public void gameScreen() throws InterruptedException {
		while (true) {
			player.velocity = player.velocity.add(player.direction.normalized().multiply(0.3));
			if(player.velocity.length() >= 5) {
				player.velocity = player.velocity.normalized().multiply(5);
			}
			player.position = player.position.add(player.velocity);
			
			
			Iterator <Bullet> bullets3 = bullets.iterator();
			while (bullets3.hasNext()) {
				Bullet bullet = bullets3.next();
				bullet.position = bullet.position.add(bullet.direction.normalized().multiply(25));
				if(isOffScreen(bullet)) {
					bullets3.remove();
				}
			}
			

			if (asteroidSpawnTimer == 0) {
				spawnAsteroid();
				asteroidSpawnTimer = 80;
			}

			Iterator <Asteroid> asteroids3 = asteroids.iterator();
			while (asteroids3.hasNext()) {
				Asteroid asteroid = asteroids3.next();
				asteroid.position = asteroid.position.add(asteroid.direction.normalized().multiply(5));
				if(isOffScreen(asteroid)) {
					asteroids3.remove();
				}
			}
			//check bullet to asteroid collision
			Iterator <Bullet> bullets2 = bullets.iterator(); 
			while(bullets2.hasNext()) {
				Iterator <Asteroid> asteroids2 = asteroids.iterator(); 
				Bullet b = bullets2.next();
				while(asteroids2.hasNext()) {
					if(b.isColliding(asteroids2.next())) {
						bullets2.remove();
						asteroids2.remove();
						break;
					}
				}
			}
			
			Iterator <Asteroid> a2 = asteroids.iterator(); 
			while(a2.hasNext()) {
				if(player.isColliding(a2.next())) {
					a2.remove();
					lives--;
				}
			}
			
			if(lives <= 0) {
				break;
			}
			
			asteroidSpawnTimer--;
			if (firing && bulletFireCooldown == 0) {
				fireBullet();
				bulletFireCooldown = 20;
			}
			if (bulletFireCooldown > 0) {
				bulletFireCooldown--;
			}
			repaint();
			Thread.sleep(1000/60);
		}
		System.out.println("GAME OVER");
	}
	
	public static void main(String[] args) throws InterruptedException {
		Game game = new Game();
	    JFrame.setDefaultLookAndFeelDecorated(true);
	    game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.setSize(1000, 900);
	    game.setVisible(true);
		game.start();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = new Vector2D(e.getX(),e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			player.direction.y = -1;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			player.direction.y = 1;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			player.direction.x = -1;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			player.direction.x = 1;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			firing = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			player.direction.y = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			player.direction.y = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			player.direction.x = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			player.direction.x = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			firing = false;
		}
	}

}
