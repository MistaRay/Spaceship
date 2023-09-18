package spaceship;

import java.awt.image.BufferedImage;

public class PhysicsBody {

	Vector2D position;
	double radius; 
	Vector2D direction;
	public BufferedImage img;
	public PhysicsBody(double radius) {
		position = new Vector2D(0,0);
		direction = new Vector2D(0,0);
		this.radius = radius;
	}
	
	public void update(double delta) {
		
	}
	
	public boolean isColliding(PhysicsBody body) {
		return body.position.subtract(position).length() < body.radius + radius;
	}
}
