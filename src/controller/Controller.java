package controller;
import main.Point;
import processing.core.PFont;

/**
 * @author tgmeow
 * Abstract class for each controller type on the ControllersMenu.java
 * @param <T>
 */
public abstract class Controller<T> extends ControllerVars{

	  protected final PFont textFont = ControllersMenu.textFont;
	  protected int controllerX = 0;
	  protected int controllerY = 0;
	  protected int controllerWidth = 0;
	  protected int controllerHeight = 0;
	  protected String name = "Null";
	  protected Point superMenuCoordinates = new Point();
	  protected boolean isPressed = false;


	  public void updateMenuXY(Point newXY) {
	    this.superMenuCoordinates = new Point(newXY);
	  }
	  public String getControllerName() {
	    return this.name;
	  }
	  public abstract void resetControls();
	  public abstract void pressUpdate(int clickX, int clickY);
	  public abstract void clickUpdate(int clickX, int clickY);
	  public abstract void releaseUpdate(int clickX, int clickY);
	  public abstract void drawController(int controllerX, int controllerY, int controllerWidth, int controllerHeight);

	  public abstract T getValue();
	  public abstract Object getReturnType();
	  
	  //TESTING
		public abstract void drawUpdateControllerValue(int controllerX, int controllerY, int controllerWidth, int controllerHeight);
		public abstract void drawControllerBackgroundColor();
		public abstract void drawControllerHighlightsColor();
		public abstract void drawControllerTextColor();
		public abstract void drawTextOverflowBackgroundColor();
		public abstract void drawButtonBackgroundColor();
		public abstract void drawButtonFillTextColor();
		public abstract void drawFinalTextColor();
	}