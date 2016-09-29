package controller;
import processing.core.PApplet;

/**
 * @author tgmeow
 * The button class used to detect if mouse is contained.
 */
public class PolygonButton {
	private PApplet parent;
	private int x = 0, y = 0, buttonWidth = 0, buttonHeight = 0;
	private java.awt.Polygon button;

	public PolygonButton(PApplet p) {
		parent = p;
		button = new java.awt.Polygon();
	}
	public PolygonButton(PApplet p, int x, int y, int buttonWidth, int buttonHeight) {
		parent = p;
		this.x = x;
		this.y = y;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		button = new java.awt.Polygon();
	}
	public void drawButton() {
		button = new java.awt.Polygon();
		button.addPoint(x, y);
		button.addPoint(x + buttonWidth, y);
		button.addPoint(x + buttonWidth, y + buttonHeight);
		button.addPoint(x, y + buttonHeight);
		button.addPoint(x, y);
		parent.beginShape();
		for (int i = 0; i < button.npoints; i++) {
			parent.vertex(button.xpoints[i], button.ypoints[i]);
		} 
		parent.endShape();
	}

	public void setX(int newX) {
		this.x = newX;
	}
	public void setY(int newY) {
		this.y = newY;
	}
	public void setWidth(int newWidth) {
		this.buttonWidth = newWidth;
	}
	public void setHeight(int newHeight) {
		this.buttonHeight = newHeight;
	}
	public boolean contains(int clickX, int clickY) {
		button = new java.awt.Polygon();
		button.addPoint(x, y);
		button.addPoint(x + buttonWidth, y);
		button.addPoint(x + buttonWidth, y + buttonHeight);
		button.addPoint(x, y + buttonHeight);
		button.addPoint(x, y);
		//    PApplet.println(x + " " + y + " " + buttonWidth + " " + buttonHeight);
		return button.contains(clickX, clickY);
	}
}
