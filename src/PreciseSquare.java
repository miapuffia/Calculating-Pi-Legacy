import javafx.scene.shape.Rectangle;

public class PreciseSquare extends Rectangle {
	private double precisePos = 0.0;
	private double velocity = 0.0;
	private double mass = 0.0;
	
	PreciseSquare(int width, int height, double precisePos, double velocity, double mass) {
		super(width, height);
		this.precisePos = precisePos;
		this.velocity = velocity;
		this.mass = mass;
	}
	
	public double getPrecisePos() {
		return this.precisePos;
	}
	
	public void setPrecisePos(double precisePos) {
		this.precisePos = precisePos;
	}
	
	public double getVelocity() {
		return this.velocity;
	}
	
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}
}