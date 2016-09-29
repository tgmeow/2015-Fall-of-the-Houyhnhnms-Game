package controller;
import java.util.ArrayList;

import main.Point;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

/**
 * @author tgmeow
 * The menu that holds all the controllers.
 */
public class ControllersMenu extends ControllerVars {
	private PApplet parent;
	public static PFont textFont;

	private final int TEXT_PADDING_LEFT = 5;
	private Point menuCoordinates = new Point(); 
	@SuppressWarnings("rawtypes")
	private java.util.TreeMap<String, Controller> controllers = new java.util.TreeMap<String, Controller>();
	private PolygonButton closeButton;
	private boolean menuClosed = false;
	private boolean menuWasToggled = false;
	private boolean clickedOnLeftEdge = false;
	private final int EDGE_CLICK_MARGINS = 5;
	private ControllerPosition position = ControllerPosition.TOP_RIGHT;
	private int menuWidth = 100;
	private int controllersHeight = 30;
	private final boolean NO_STROKE = true;
	private final int FRAME_BORDER = 0;
	private int MENU_BACKGROUND_COLOR;
	private int MENU_HOVER_COLOR;
	private int MENU_FRAME_COLOR;
	private final int CONTROLLERS_PADDING = 0;
	private final int HIDE_SIZE = 24;
	private final int CLOSE_BUTTON_PADDING = 1;
	private boolean menuReleased = true;

	public ControllersMenu(PApplet p) {
		parent = p;
		closeButton = new PolygonButton(parent, 0, 0, 0, 0);
		this.initVars();
	}
	public ControllersMenu(PApplet p, ControllerPosition menuPosition, int menuWidth) {
		parent = p;
		closeButton = new PolygonButton(parent, 0, 0, 0, 0);
		this.initVars();
		this.position = menuPosition;
		this.menuWidth = menuWidth;
	}
	private void initVars(){
		MENU_BACKGROUND_COLOR = parent.color(0);
		MENU_HOVER_COLOR = parent.color(17);
		MENU_FRAME_COLOR = parent.color(180);
		textFont = parent.createFont("/resources/CALIBRI.TTF", TEXT_SIZE);
		//parent.textFont(textFont);
		//ellipseWidth = parent.textWidth("...");
	}

	public void setMenuWidth(int newWidth) {
		this.menuWidth = newWidth;
	}
	public int getMenuWidth() {
		return this.menuWidth;
	}
	public int getMenuHeight() {
		return this.numberOfControllers() * controllersHeight + HIDE_SIZE + CONTROLLERS_PADDING*2*this.numberOfControllers();
	}
	public ControllerPosition getMenuPosition() {
		return this.position;
	}
	public void setMenuPosition(ControllerPosition newPosition) {
		this.position = newPosition;
		this.updateMenuPos(this.getMenuPosition());
	}
	@SuppressWarnings("rawtypes")
	public java.util.TreeMap<String, Controller> getControllers() {
		return new java.util.TreeMap<String, Controller>(controllers);
	}
	public int numberOfControllers() {
		return controllers.size();
	}
	public void addIntSlider(String name, int min, int max, int value) {
		controllers.put(name, new IntSlider(parent, name, min, max, value, new Point(this.menuCoordinates)));
	}
	public void addFloatSlider(String name, float min, float max, float value, int accuracy) {
		controllers.put(name, new FloatSlider(parent, name, min, max, value, new Point(this.menuCoordinates), accuracy));
	}
	public void addButtonController(String name, String label) {
		controllers.put(name, new ButtonController(parent, name, label, new Point(this.menuCoordinates)));
	}
	public void addSwitchController(String name, String trueLabel, String falseLabel) {
		controllers.put(name, new SwitchController(parent, name, trueLabel, falseLabel, new Point(this.menuCoordinates)));
	}
	public void setControllersHeight(int newHeight) {
		controllersHeight = newHeight;
	}
	public int getControllersHeight() {
		return controllersHeight;
	}
	public void removeController(String controllerName){
		controllers.remove(controllerName);
	}
	public void closeMenu() {
		menuClosed = false;
		menuWasToggled = true;
	}
	public void openMenu() {
		menuClosed = false;
		menuWasToggled = true;
	}
	public void toggleMenu() {
		menuClosed = !menuClosed;
		menuWasToggled = true;
	}
	public void resetMenu() {
		for (Controller<?> tempController : controllers.values()) {
			tempController.resetControls();
		}
	}

	public boolean clickOnLeftEdge(int clickX, int clickY, int plusMinus) {
		int clickedYChange = 0;
		if (clickedOnLeftEdge) clickedYChange = parent.height*2;
		clickedOnLeftEdge = (clickX <= menuCoordinates.x+plusMinus && clickX >= menuCoordinates.x && clickY >= (menuCoordinates.y - clickedYChange) && clickY <= (menuCoordinates.y + this.getMenuHeight() + clickedYChange));
		return clickedOnLeftEdge;
	}
	private void updateMenuPos(ControllerPosition newPos) {
		if (newPos == ControllerPosition.TOP) {
			menuCoordinates = new Point(parent.width/2-this.getMenuWidth()/2.0f, -FRAME_BORDER);
		} else if (newPos == ControllerPosition.TOP_RIGHT) { 
			menuCoordinates = new Point(parent.width-this.getMenuWidth(), -FRAME_BORDER);
		}
		//else if (newPos == Position.TOP_LEFT) { 
		//  menuCoordinates = new Point(-FRAME_BORDER, -FRAME_BORDER);
		//} 
		//else if (newPos == Position.LEFT) { 
		// menuCoordinates = new Point(-FRAME_BORDER, (height/2.0)-(menuHeight/2.0));
		//}
		//else if (newPos == Position.RIGHT) {
		// menuCoordinates = new Point(width-this.getMenuWidth(), (height/2.0)-(menuHeight/2.0));
		//}
		//else if (newPos == Position.BOTTOM) {
		// menuCoordinates = new Point(width/2-(this.getMenuWidth()/2.0), height-menuHeight);
		//}
		//else if (newPos == Position.BOTTOM_LEFT) {
		// menuCoordinates = new Point(-FRAME_BORDER, height-menuHeight);
		//}
		//else if (newPos == Position.BOTTOM_RIGHT) {
		// menuCoordinates = new Point(width-this.getMenuWidth(), height-menuHeight);
		//}
		parent.pushMatrix();
		parent.translate(menuCoordinates.x, menuCoordinates.y);
		if (menuWasToggled) {
			parent.rect(0, 0, this.getMenuWidth(), this.getMenuHeight());
		}
		parent.popMatrix();

		if (menuClosed) menuCoordinates.y -= (this.getMenuHeight() - HIDE_SIZE);
		for (Controller<?> tempController : controllers.values()) {
			tempController.updateMenuXY(menuCoordinates);
		}
	}

	public void clickManager(int clickX, int clickY) {
		menuReleased = true;
		int actualX = clickX - (int)(menuCoordinates.x);
		int actualY = clickY - (int)(menuCoordinates.y);
		if (closeButton.contains(actualX, actualY)) this.toggleMenu();
		for (Controller<?> tempController : controllers.values()) {
			tempController.clickUpdate(actualX, actualY);
		}
	}
	public void pressManager(int clickX, int clickY) {
		if (menuReleased) this.clickOnLeftEdge(clickX, clickY, EDGE_CLICK_MARGINS);
		int actualX = clickX - (int)(menuCoordinates.x);
		int actualY = clickY - (int)(menuCoordinates.y);
		for (Controller<?> tempController : controllers.values()) {
			tempController.pressUpdate(actualX, actualY);
		}
	}
	public void releaseManager(int clickX, int clickY) {
		menuReleased = true;
		int actualX = clickX - (int)(menuCoordinates.x);
		int actualY = clickY - (int)(menuCoordinates.y);
		for (Controller<?> tempController : controllers.values()) {
			tempController.releaseUpdate(actualX, actualY);
		}
	}
	public boolean isMenuReleased() {
		return menuReleased;
	}
	public boolean clickOnMenu(int clickX, int clickY) {
		int actualX = clickX;
		int actualY = clickY;
		boolean state = (actualX >= menuCoordinates.x && actualX <= (menuCoordinates.x + menuWidth) && actualY >= menuCoordinates.y && actualY <= (menuCoordinates.y + this.getMenuHeight()));
		if (state) menuReleased = false;
		return state;
	}

	@SuppressWarnings("rawtypes")
	public void drawMenu() {
		parent.textAlign(PConstants.LEFT, PConstants.BASELINE);
		if (clickedOnLeftEdge && parent.mousePressed) {
			if (menuWidth < EDGE_CLICK_MARGINS) {
				menuWidth = EDGE_CLICK_MARGINS;
			}
			menuWidth -= (parent.mouseX - parent.pmouseX);
			if (position == ControllerPosition.TOP) menuWidth -= (parent.mouseX - parent.pmouseX);
		} 
		int controllersWidth = this.getMenuWidth() - CONTROLLERS_PADDING*2;
		int menuHeight = this.getMenuHeight();
		parent.pushMatrix();
		this.updateMenuPos(this.getMenuPosition());
		parent.translate(menuCoordinates.x, menuCoordinates.y);
		//SET FONT FOR ALL CONTROLLERS
		parent.textFont(textFont);
		boolean useAlternativeDraw = false;
		//alternative draw still has (small) color issues with hover
		if(!menuClosed){
			ArrayList<Controller> temp = new ArrayList<Controller>(this.getControllers().values());

			//drawBackground
			parent.fill(MENU_BACKGROUND_COLOR);
			parent.strokeWeight(FRAME_BORDER);
			parent.stroke(MENU_FRAME_COLOR);
			if (NO_STROKE) parent.noStroke();

			parent.rect(0, 0, this.getMenuWidth(), menuHeight);

			if (controllersWidth < 0) controllersWidth = 0;
			if(useAlternativeDraw){

				for (int index = 0; index < this.numberOfControllers(); index++) {
					temp.get(index).drawUpdateControllerValue(CONTROLLERS_PADDING, CONTROLLERS_PADDING*2*index + CONTROLLERS_PADDING + this.getControllersHeight()*index, controllersWidth, this.getControllersHeight());
				}

				parent.fill(BACKGROUND_COLOR);
				for(Controller tempC : temp){
					tempC.drawControllerBackgroundColor();
				}
				
				for(Controller tempC : temp){
					tempC.drawControllerHighlightsColor();
				}
				float time = parent.millis();
				parent.fill(TEXT_COLOR);
				for(Controller tempC : temp){
					tempC.drawControllerTextColor();
				}
				PApplet.println("DRAWMENUTIME: " + (parent.millis()-time));
				parent.fill(BACKGROUND_COLOR);
				for(Controller tempC : temp){
					tempC.drawTextOverflowBackgroundColor();
				}
				parent.fill(BUTTON_BACKGROUND_COLOR);
				for(Controller tempC : temp){
					tempC.drawButtonBackgroundColor();
				}
				for(Controller tempC : temp){
					tempC.drawButtonFillTextColor();
				}
				parent.fill(TEXT_COLOR);
				for(Controller tempC : temp){
					tempC.drawFinalTextColor();
				}


			}else{
				for (int index = 0; index < this.numberOfControllers(); index++) {
					temp.get(index).drawController(CONTROLLERS_PADDING, CONTROLLERS_PADDING*2*index + CONTROLLERS_PADDING + this.getControllersHeight()*index, controllersWidth, this.getControllersHeight());
				}
			}
		}
		//DRAW CLOSE BUTTON MENU IS VERY LAGGY SAD ME
		int closeButtonWidth = controllersWidth - CLOSE_BUTTON_PADDING*2;
		if (closeButtonWidth < 0) closeButtonWidth = 0;
		closeButton = new PolygonButton(parent, CONTROLLERS_PADDING + CLOSE_BUTTON_PADDING, menuHeight - HIDE_SIZE + CLOSE_BUTTON_PADDING, closeButtonWidth, HIDE_SIZE - CLOSE_BUTTON_PADDING*2 - CONTROLLERS_PADDING);

		parent.fill(MENU_BACKGROUND_COLOR);
		if (closeButton.contains(parent.mouseX - (int)(menuCoordinates.x), parent.mouseY - (int)(menuCoordinates.y))) parent.fill(MENU_HOVER_COLOR);
		closeButton.drawButton();

		parent.fill(TEXT_COLOR);
		String openClose = (menuClosed)? "Open Controls" : "Close Controls";
		float textX = (parent.textWidth(openClose) <= controllersWidth - CLOSE_BUTTON_PADDING*2 + TEXT_PADDING_LEFT) ? menuWidth*0.5f - parent.textWidth(openClose)*0.5f : (CONTROLLERS_PADDING + CLOSE_BUTTON_PADDING + TEXT_PADDING_LEFT); 
		parent.text(openClose, textX, menuHeight - HIDE_SIZE*0.5f - CLOSE_BUTTON_PADDING + TEXT_SIZE*.333f);

		parent.popMatrix();
		menuWasToggled = false;
	}
}
