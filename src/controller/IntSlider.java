package controller;
import main.Point;
import processing.core.PApplet;

/**
 * @author tgmeow
 * A Integer Slider type controller. Slides and returns integer values.
 */
@SuppressWarnings("rawtypes")
public class IntSlider extends Controller {
	private PApplet parent;

	private int min = 0;
	private int max = 1;
	private int currentValue = 0;

	private final int SLIDER_PADDING = 4;
	private int HIGHLIGHTS_COLOR;

	private int initialValue = 0;
	private int initialMin = 0;
	private int initialMax = 0;

	public IntSlider(PApplet p) {
		parent = p;
		HIGHLIGHTS_COLOR = parent.color(47, 161, 214);

	}
	@SuppressWarnings("unchecked")
	public IntSlider(PApplet p, String name, int min, int max, int value, Point menuXY) {
		parent = p;
		HIGHLIGHTS_COLOR = parent.color(47, 161, 214);

		this.name = name;
		this.min = min;
		this.max = max;
		this.setValue(value);
		this.superMenuCoordinates = new Point(menuXY);
		this.initialValue = value;
		this.initialMax = max;
		this.initialMin = min;
	}

	public int getMin() {
		return this.min;
	}
	public int getMax() {
		return this.max;
	}
	public int getRange() {
		return this.max - this.min;
	}
	public Integer getValue() {
		return this.currentValue;
	}
	@SuppressWarnings("unchecked")
	public void setSliderName(String newName) {
		this.name = newName;
	}
	public void setMin(int newMin) {
		this.min = newMin;
		this.setValue(this.getValue());
	}
	public void setMax(int newMax) {
		this.max = newMax;
		this.setValue(this.getValue());
	}
	public void setValue(int newValue) {
		if (newValue > this.getMax()) newValue = this.getMax();
		if (newValue < this.getMin()) newValue = this.getMin();
		this.currentValue = newValue;
	}
	public Object getReturnType() {
		return Integer.class;
	}
	public void clickUpdate(int clickX, int clickY) {
	}
	@SuppressWarnings("unchecked")
	public void pressUpdate(int clickX, int clickY) {
		int clickedChangeY = 0;
		int clickedChangeX = 0;
		if (isPressed) {
			clickedChangeY = parent.height*2;
			clickedChangeX = parent.width*2;
		}
		float slidingWidth = controllerWidth*2.0f/5.0f;
		isPressed = (clickX >= (slidingWidth - clickedChangeX) && clickX <= (slidingWidth*2 + clickedChangeX) && clickY <= (controllerY + controllerHeight - SLIDER_PADDING + clickedChangeY) && clickY >= (controllerY + SLIDER_PADDING - clickedChangeY));
	}
	@SuppressWarnings("unchecked")
	public void releaseUpdate(int clickX, int clickY) {
		isPressed = false;
	}
	private boolean slidingWidthContains(int clickX, int clickY) {
		float slidingWidth = controllerWidth*2.0f/5.0f;
		return clickX >= slidingWidth && clickX <= slidingWidth*2 && clickY <= controllerY + controllerHeight - SLIDER_PADDING && clickY >= controllerY + SLIDER_PADDING;
	}
	public void resetControls() {
		this.min = initialMin;
		this.max = initialMax;
		this.currentValue = initialValue;
	}
	@SuppressWarnings("unchecked")
	public void drawController(int controllerX, int controllerY, int controllerWidth, int controllerHeight) {

		this.controllerX = controllerX;
		this.controllerY = controllerY;
		this.controllerWidth = controllerWidth;
		this.controllerHeight = controllerHeight;
		float slidingWidth = controllerWidth*0.4f;
		int actualX = (int)(parent.mouseX - superMenuCoordinates.x);
		//int actualY = (int)(parent.mouseY - superMenuCoordinates.y);
		if (isPressed && parent.mousePressed) {
			float newValue = (this.getMax()-this.getMin()) * (actualX - slidingWidth)/(slidingWidth) + this.getMin();
			this.setValue(Math.round(newValue));
		} 
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
		parent.rect(slidingWidth, controllerY, controllerWidth*0.6f, controllerHeight);

		parent.fill(BUTTON_BACKGROUND_COLOR);
		if (this.slidingWidthContains(parent.mouseX - (int)(superMenuCoordinates.x), parent.mouseY - (int)(superMenuCoordinates.y))) parent.fill(HOVER_COLOR);
		parent.rect(slidingWidth, controllerY + SLIDER_PADDING, slidingWidth, controllerHeight - SLIDER_PADDING*2);

		parent.fill(HIGHLIGHTS_COLOR);
		parent.rect(slidingWidth, controllerY + SLIDER_PADDING, Math.round(slidingWidth * (this.getValue()-this.getMin())/(this.getMax()-this.getMin())), controllerHeight - SLIDER_PADDING*2);
		//CurrentValue
		parent.text(this.getValue(), controllerWidth*0.84f, controllerY + controllerHeight*0.5f + TEXT_SIZE*0.25f);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void drawUpdateControllerValue(int controllerX, int controllerY, int controllerWidth, int controllerHeight) {
		this.controllerX = controllerX;
		this.controllerY = controllerY;
		this.controllerWidth = controllerWidth;
		this.controllerHeight = controllerHeight;
		float slidingWidth = controllerWidth*0.4f;
		int actualX = (int)(parent.mouseX - superMenuCoordinates.x);
		//int actualY = (int)(parent.mouseY - superMenuCoordinates.y);
		if (isPressed && parent.mousePressed) {
			float newValue = (this.getMax()-this.getMin()) * (actualX - slidingWidth)/(slidingWidth) + this.getMin();
			this.setValue(Math.round(newValue));
		} 
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
		parent.rect(slidingWidth, controllerY, controllerWidth*0.6f, controllerHeight);
	}
	@Override
	public void drawButtonBackgroundColor() {
		float slidingWidth = controllerWidth*0.4f;
		if (this.slidingWidthContains(parent.mouseX - (int)(superMenuCoordinates.x), parent.mouseY - (int)(superMenuCoordinates.y))) parent.fill(HOVER_COLOR);
		parent.rect(slidingWidth, controllerY + SLIDER_PADDING, slidingWidth, controllerHeight - SLIDER_PADDING*2);
	}
	@Override
	public void drawButtonFillTextColor() {
		parent.fill(HIGHLIGHTS_COLOR);
		float slidingWidth = controllerWidth*0.4f;
		parent.rect(slidingWidth, controllerY + SLIDER_PADDING, Math.round(slidingWidth * (this.getValue()-this.getMin())/(this.getMax()-this.getMin())), controllerHeight - SLIDER_PADDING*2);
		//CurrentValue
		parent.text(this.getValue(), controllerWidth*0.84f, controllerY + controllerHeight*0.5f + TEXT_SIZE*0.25f);
	}
	@Override
	public void drawFinalTextColor() {
		// TODO Auto-generated method stub
		
	}
}
