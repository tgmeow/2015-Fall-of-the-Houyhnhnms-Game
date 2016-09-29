package nodes;
import main.Point;
import processing.core.PApplet;
import processing.core.PShape;

public class AnimatedSVG {
	private PApplet parent;

	protected boolean imageLoaded = false;
	protected String fileLoc;
	protected PShape SVGImage;
	protected float scale = 1;
	protected boolean isRunning = false;
	protected Point startPoint;
	protected Point endPoint;

	private float animationTime = 0;
	protected int startTime = 0;

	public AnimatedSVG(PApplet p) {
		parent = p;
	}
	public AnimatedSVG(PApplet p, String fileLoc, float scale, Point startPoint, Point endPoint, float animationTime) {
		parent = p;
		this.fileLoc = fileLoc;
		this.scale = scale;
		this.startPoint = new Point(startPoint);
		this.endPoint = new Point(endPoint);
		this.animationTime = (animationTime > 0) ? animationTime : 1;
		PApplet.println("Loading Image: " + fileLoc);
		try {
			SVGImage = parent.loadShape(fileLoc);
			SVGImage.scale(scale);
			imageLoaded = true;
			PApplet.println("Image loaded sucessfully!");
		}
		catch(Exception e) {
			PApplet.println(e);
			PApplet.println("ERROR: IMAGE \"" + fileLoc + "\" NOT LOADED CORRECTLY!");
		}
	}
	public Point getStartPoint() {
		return new Point(startPoint);
	}
	public Point getEndPoint() {
		return new Point(endPoint);
	}
	public void setStartPoint(Point aPoint) {
		startPoint = new Point(aPoint);
	}
	public void setEndPoint(Point aPoint) {
		endPoint = new Point(aPoint);
	}
	public boolean isRunning() {
		return isRunning;
	}

	public void start() {
		isRunning = true;
		startTime = parent.millis();
		//TODO
	}

	public void stop() {
		isRunning = false;
		//TODO
	}
	public void draw() {
		if (isRunning) {
			int timeSince = parent.millis() - startTime;
			if (timeSince <= animationTime) {
				Point currPoint = new Point(animatePointLerp(startPoint, endPoint, ((float)timeSince)/animationTime));
				if (imageLoaded) { 
					parent.shape(SVGImage, currPoint.x - (SVGImage.width*scale*0.5f), currPoint.y - (SVGImage.height*scale*0.5f));
				} else {
					parent.stroke(0);
					parent.strokeWeight(30);
					parent.point(currPoint.x, currPoint.y);
				}
			} else isRunning = false;
		}
	}

	public AnimatedSVG clone() {
		return new AnimatedSVG(parent, fileLoc, scale, new Point(startPoint), new Point(endPoint), animationTime);
	}

	//OBJECTS PASS BY REFERENCE!
	//Move from here to here with this many frames remaining
	public static Point animatePointLerp(Point startPoint, Point targetPoint, float lerpRatio) {
		float currPointX = PApplet.lerp(startPoint.x, targetPoint.x, lerpRatio);
		float currPointY = PApplet.lerp(startPoint.y, targetPoint.y, lerpRatio);
		return new Point(currPointX, currPointY);
	}

}


