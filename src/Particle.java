public class Particle {
	public static final double G = 6.67e-11;//m^3 * kg^-1 * s^-2
	public double x;
	public double y;
	public double motionX;
	public double motionY;
	public double mass;
	public double radius;
	public Particle(double x, double y, double motionX, double motionY, double mass, double radius){
		this.x = x; //m
		this.y = y; //m
		this.motionX = motionX; //m/s
		this.motionY = motionY; //m/s
		this.mass = mass; //kg
		this.radius = radius; //m
	}
	public void tick(){
		x += motionX;
		y += motionY;
	}
	public void attract(Particle p){
		double xDist = x - p.x;
		double yDist = y - p.y;
		double distanceSq = xDist * xDist + yDist * yDist;
		double accThis = p.mass / distanceSq * G;
		double accOther = mass / distanceSq * G;
		double distance = Math.sqrt(distanceSq);
		motionX -= accThis / distance * xDist;
		motionY -= accThis / distance * yDist;
		p.motionX += accOther / distance * xDist;
		p.motionY += accOther / distance * yDist;
		if(distanceSq < (radius + p.radius) * (radius + p.radius)){
			Sim.list.remove(p);
			radius = Math.sqrt(radius * radius + p.radius * p.radius);
			motionX = (motionX * mass + p.motionX * p.mass) / (mass + p.mass);
			motionY = (motionY * mass + p.motionY * p.mass) / (mass + p.mass);
			x -= xDist / 2 / (radius / p.radius);
			y -= yDist / 2 / (radius / p.radius);
			mass += p.mass;
		}
	}
}
