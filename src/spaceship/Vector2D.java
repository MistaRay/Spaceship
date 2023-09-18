package spaceship;

public class Vector2D {
	public double x;
	public double y;
	public Vector2D(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2D subtract(Vector2D v) {
		return new Vector2D(this.x - v.x, this.y - v.y);
	}
	
	public Vector2D normalized() {
		if (x == 0 && y == 0) {
			return this;
		}
		
		double length = length();
		
		if (x == 0) {
			return new Vector2D(0, y / length);
		}
		
		if (y == 0) {
			return new Vector2D(x / length, 0);
		}
		
		return new Vector2D(x / length, y / length);
	}
	public Vector2D add(Vector2D v) {
		return new Vector2D(this.x + v.x, this.y + v.y);
	}
	
	public double length() {
		return Math.sqrt((x*x) + (y*y));
	}
	
	public boolean equals(Vector2D v) {
		return y == v.y && x == v.x;
	}
	
	public Vector2D multiply(double k) {
		return new Vector2D(x*k, y*k);
	}
	
	public double angleTo(Vector2D v) {
		double dx = v.x - x;
		double h = v.subtract(this).length();
		double angle = Math.toDegrees(Math.acos(dx/h));
		
		if (v.y < this.y) {
			angle *= -1;
		}
		return angle;
	}
	
}