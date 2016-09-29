package controller;
import main.Point;
import processing.core.PApplet;

/**
 * @author tgmeow
 * A button controller that is meant to trigger an event when pressed. The value resets after it is checked through getValue().
 */
@SuppressWarnings("rawtypes")
public class ButtonController extends Controller {
	private PApplet parent;

	private PolygonButton button;
	private boolean currentValue = false;
	private String buttonLabel = "Null";

	private final int BUTTON_PADDING = 4;
	private final int HIGHLIGHTS_COLOR;

	private boolean initialValue = false;

	public ButtonController(PApplet p) {
		parent = p;
		button = new PolygonButton(parent);
		HIGHLIGHTS_COLOR = parent.color(211, 111, 30);
	}
	@SuppressWarnings("unchecked")
	public ButtonController(PApplet p, String name, String buttonLabel, Point menuXY) {
		parent = p;
		HIGHLIGHTS_COLOR = parent.color(211, 111, 30);
		this.name = name;
		this.buttonLabel = buttonLabel;
		this.superMenuCoordinates = new Point(menuXY);
		int slidingWidth = (int)(controllerWidth*0.4f);
		button = new PolygonButton(parent, controllerX + slidingWidth, controllerY + BUTTON_PADDING, slidingWidth, controllerHeight - BUTTON_PADDING*2);
	}
	public Boolean getValue() {
		boolean temp = currentValue;
		currentValue = false;
		return temp;
	}
	private String getLabel(){
		return this.buttonLabel;
	}
	@SuppressWarnings("unchecked")
	public void setSliderName(String newName) {
		this.name = newName;
	}
	public void setValue(boolean newValue) {
		this.currentValue = newValue;
	}
	public Object getReturnType() {
		return Float.class;
	}
	public void clickUpdate(int clickX, int clickY) {
		//currentValue = true;
	}
	@SuppressWarnings("unchecked")
	public void pressUpdate(int clickX, int clickY) {
		isPressed = this.slidingWidthContains(clickX, clickY);
	}
	@SuppressWarnings("unchecked")
	public void releaseUpdate(int clickX, int clickY) {
		if (isPressed) {
			currentValue = this.slidingWidthContains(clickX, clickY);
		}
		isPressed = false;
	}
	private boolean slidingWidthContains(int clickX, int clickY) {
		float slidingWidth = controllerX + controllerWidth*0.4f;
		return clickX >= slidingWidth && clickX <= slidingWidth*2 && clickY <= controllerY + controllerHeight - BUTTON_PADDING && clickY >= controllerY + BUTTON_PADDING;
	}
	public void resetControls() {
		currentValue = initialValue;
	}
	@SuppressWarnings("unchecked")
	public void drawController(int controllerX, int controllerY, int controllerWidth, int controllerHeight) {
		this.controllerX = controllerX;
		this.controllerY = controllerY;
		this.controllerWidth = controllerWidth;
		this.controllerHeight = controllerHeight;
		float slidingWidth = controllerWidth*0.4f;
		parent.fill(BACKGROUND_COLOR);
		parent.rect(controllerX, controllerY, controllerWidth, controllerHeight);

		parent.fill(HIGHLIGHTS_COLOR);
		parent.rect(controllerX, controllerY, HIGHLIGHTS_WIDTH, controllerHeight);
		//REMOVED TEXT SHORTENER FEATURE -- SHAVES approx 5 ms off per draw on my laptop
		parent.fill(TEXT_COLOR);
		String tempText = this.getControllerName();
//		float nameArea = slidingWidth - HIGHLIGHTS_WIDTH - TEXT_PADDING_LEFT;
//		if (parent.textWidth("...") > nameArea) {
//		} else if (parent.textWidth(tempText) > nameArea) {
//			while (parent.textWidth(tempText + "...") > nameArea && tempText.length()>0) {
//				tempText = tempText.substring(0, tempText.length()-1);
//			}
//			tempText += "...";
//		}
		parent.text(tempText, controllerX + HIGHLIGHTS_WIDTH + TEXT_PADDING_LEFT, controllerY + controllerHeight*0.5f + TEXT_SIZE*.25f);

		//Hides Text Overflow
		parent.fill(BACKGROUND_COLOR);
		parent.rect(controllerX + slidingWidth, controllerY, controllerWidth*0.6f, controllerHeight);

		//BUTTON
		if (this.slidingWidthContains(parent.mouseX - (int)(superMenuCoordinates.x), parent.mouseY - (int)(superMenuCoordinates.y))) {
			parent.fill(HOVER_COLOR);
			if(isPressed) parent.fill(HIGHLIGHTS_COLOR);
		} else parent.fill(BUTTON_BACKGROUND_COLOR);

		button = new PolygonButton(parent, controllerX + (int)(slidingWidth), controllerY + BUTTON_PADDING, (int)(slidingWidth), controllerHeight - BUTTON_PADDING*2);
		button.drawButton();

		parent.fill(TEXT_COLOR);
		parent.text(this.getLabel(), controllerX + 1.5f*(int)(slidingWidth) - 0.5f*parent.textWidth(this.getLabel()), controllerY + controllerHeight/2.0f + TEXT_SIZE/4.0f);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void drawUpdateControllerValue(int controllerX, int controllerY, int controllerWidth, int controllerHeight) {
		this.controllerX = controllerX;
		this.controllerY = controllerY;
		this.controllerWidth = controllerWidth;
		this.controllerHeight = controllerHeight;
	}
	@Override
	public void drawControllerBackgroundColor() {
		parent.rect(controllerX, controllerY, controllerWidth, controllerHeight);
	}
	@Override
	public void drawControllerHighlightsColor() {
		parent.fill(HIGHLIGHTS_COLOR);
		parent.rect(controllerX, controllerY, HIGHLIGHTS_WIDTH, controllerHeight);
	}
	@Override
	public void drawControllerTextColor() {
		//REMOVED TEXT SHORTENER FEATURE -- SHAVES approx 5 ms off per draw on my laptop
//		float slidingWidth = controllerWidth*0.4f;
		String tempText = this.getControllerName();
//		float nameArea = slidingWidth - HIGHLIGHTS_WIDTH - TEXT_PADDING_LEFT;
//		if (ellipseWidth > nameArea) {
//			PApplet.println("DONOTHING");
//		} else if (parent.textWidth(tempText) > nameArea) {
//			while (parent.textWidth(tempText + "...") > nameArea && tempText.length()>0) {
//				tempText = tempText.substring(0, tempText.length()-1);
//			}
//			tempText += "...";
//		}
		parent.text(tempText, controllerX + HIGHLIGHTS_WIDTH + TEXT_PADDING_LEFT, controllerY + controllerHeight*0.5f + TEXT_SIZE*.25f);
	}
	@Override
	public void drawTextOverflowBackgroundColor() {
		float slidingWidth = controllerWidth*0.4f;
		parent.rect(controllerX + slidingWidth, controllerY, controllerWidth*0.6f, controllerHeight);
	}
	@Override
	public void drawButtonBackgroundColor() {
		float slidingWidth = controllerWidth*0.4f;
		if (this.slidingWidthContains(parent.mouseX - (int)(superMenuCoordinates.x), parent.mouseY - (int)(superMenuCoordinates.y))) {
			parent.fill(HOVER_COLOR);
			if(isPressed) parent.fill(HIGHLIGHTS_COLOR);
		} else parent.fill(BUTTON_BACKGROUND_COLOR);
		button = new PolygonButton(parent, controllerX + (int)(slidingWidth), controllerY + BUTTON_PADDING, (int)(slidingWidth), controllerHeight - BUTTON_PADDING*2);
		button.drawButton();
	}
	@Override
	public void drawButtonFillTextColor() {

	}
	@Override
	public void drawFinalTextColor() {
		float slidingWidth = controllerWidth*0.4f;
		parent.text(this.getLabel(), controllerX + 1.5f*(int)(slidingWidth) - 0.5f*parent.textWidth(this.getLabel()), controllerY + controllerHeight/2.0f + TEXT_SIZE/4.0f);
	}
}