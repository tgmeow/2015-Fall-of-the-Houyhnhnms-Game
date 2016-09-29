package nodes;
import main.Point;
import processing.core.PApplet;
import processing.core.PShape;

public class AnimSVGMinion extends AnimatedSVG {
	private boolean pause = false;
	private float pauseTime = -1; 
	
	private PApplet parent;
	private float moveSpeed = 3;
	private float health = 1;
	private float attack = 1;
	private String teamName;  
	private int minionColor;
	private int targetID = -1;

	public AnimSVGMinion(PApplet p) {
		super(p);
	}
	public AnimSVGMinion(PApplet p, String fileLoc, float scale, Point startPoint, Point endPoint, int targetID, float moveSpeed, float health, float attack, String teamName, int minionColor) {
		super(p);
		parent = p;
		this.fileLoc = fileLoc;
		this.scale = scale;
		this.startPoint = new Point(startPoint);
		this.endPoint = new Point(endPoint);
		this.targetID = targetID;
		this.moveSpeed = (moveSpeed > 0) ? moveSpeed : 3;
		this.health = (health > 0) ? health : 1;
		this.attack = (attack > 0) ? attack : 1;
		this.teamName = teamName;
		this.minionColor = minionColor;

		PApplet.println("Loading Image: " + fileLoc);
		try {
			SVGImage = parent.loadShape(fileLoc);
			SVGImage.scale(scale);
			imageLoaded = true;
			PApplet.println("Image loaded sucessfully!");
			SVGImage.setFill(minionColor);
		}
		catch(Exception e) {
			PApplet.println(e);
			PApplet.println("ERROR: IMAGE \"" + fileLoc + "\" NOT LOADED CORRECTLY!");
		}
	}
	public AnimSVGMinion(PApplet p, PShape image, float scale, Point startPoint, Point endPoint, int targetID, float moveSpeed, float health, float attack, String teamName, int minionColor) {
		super(p);
		parent = p;
		this.SVGImage = image;
		this.scale = scale;
		this.startPoint = new Point(startPoint);
		this.endPoint = new Point(endPoint);
		this.targetID = targetID;
		this.moveSpeed = (moveSpeed > 0) ? moveSpeed : 300;
		this.health = (health > 0) ? health : 1;
		this.attack = (attack > 0) ? attack : 1;
		this.teamName = teamName;
		this.minionColor = minionColor;
		SVGImage.setFill(minionColor);
		this.imageLoaded = true;
	}
	public float getAttack(){
		return attack;
	}
	public float getHealth(){
		return health;
	}
	public float getSpeed(){
		return moveSpeed;
	}
	public AnimSVGMinion clone(){
		return new AnimSVGMinion(parent, SVGImage, scale, new Point(startPoint), new Point(endPoint), targetID, moveSpeed, health, attack, teamName, minionColor);
	}
	public void setTargetID(int anID){
		this.targetID = anID; 
	}
	public int getTargetID(){
		return this.targetID;
	}

	public void draw() {
		if (isRunning) {
			float timeSince;
			if(pause) timeSince = pauseTime - startTime;
			else timeSince = parent.millis() - startTime;
			float distanceTot = Point.distance(startPoint, endPoint);
			if (timeSince <= distanceTot/moveSpeed) {
				Point currPoint = new Point(animatePointLerp(startPoint, endPoint, (timeSince*moveSpeed)/distanceTot));
				if (imageLoaded) { 
					SVGImage.setFill(parent.color(255,0,0));
					parent.fill(255,0,0);
					parent.shape(SVGImage, currPoint.x - (SVGImage.width*scale*0.5f), currPoint.y - (SVGImage.height*scale*0.5f));
				} else {
					parent.stroke(0);
					parent.strokeWeight(30);
					parent.point(currPoint.x, currPoint.y);
				}
			} else isRunning = false;
		}
	}
	public String teamName(){
		return this.teamName;
	}

	public void pause(){
		pause = true;
		pauseTime = parent.millis();
	}
	public void resume(){
		pause = false;
		startTime += parent.millis() - pauseTime;
	}
}
