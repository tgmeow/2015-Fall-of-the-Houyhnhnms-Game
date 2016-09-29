package main;

import controller.PolygonButton;
import processing.core.PApplet;
import processing.core.PShape;

public class SVGButton {
	private PApplet parent;
	private PShape SVGImage;
	private boolean imageLoaded = false;
	private String name = "Null";
	private boolean currentValue = false;
	private String fileLoc;
	private Point buttonXY;
	private boolean isPressed = false;
	private PolygonButton button;

	private boolean initialValue = false;

	public SVGButton(PApplet p) {
		parent = p;
		 button = new PolygonButton(parent);
	}
	public SVGButton(PApplet p, String fileLoc, float scale, String name, Point buttonXY) {
		parent = p;
		this.fileLoc = fileLoc;
		this.name = name;
		this.buttonXY = new Point(buttonXY);
		SVGImage = parent.createShape();
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
		 button = new PolygonButton(parent, (int)buttonXY.x, (int)buttonXY.y, (int)(SVGImage.width*scale), (int)(SVGImage.height*scale));
	}
	public boolean getValue() {
		boolean temp = currentValue;
		currentValue = false;
		return temp;
	}
	public void setValue(boolean newValue) {
		this.currentValue = newValue;
	}
	public void pressUpdate(int clickX, int clickY) {
		isPressed = this.contains(clickX, clickY);
	}
	public void releaseUpdate(int clickX, int clickY) {
		if (isPressed) {
			currentValue = this.contains(clickX, clickY);
		}
		isPressed = false;
	}
	public boolean contains(int x, int y){
		return button.contains(x, y);
	}
	public void resetControls() {
		currentValue = initialValue;
	}
	public void draw(){
		if(imageLoaded) parent.shape(SVGImage, buttonXY.x, buttonXY.y);
	}
	public String getName() {
		return name;
	}
	public String getFileLoc() {
		return fileLoc;
	}
}
