package spaceship;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Ship extends PhysicsBody{
	Vector2D velocity;
	int ammo;
	
	public Ship() {
		super(42);
		ammo = 100;
		velocity = new Vector2D(0,0);
		try {
		    img = ImageIO.read(new File("src/spaceship/ship.png")); 
		    //img = Game.rotateImage(img, 45);
		} catch (IOException e) {
		}
	}
	
	public void update(double delta) {
		
	}

}
