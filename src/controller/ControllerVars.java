package controller;

import processing.core.PApplet;
import processing.core.PFont;

public abstract class ControllerVars {
	  protected final PApplet temp = new PApplet();
	  //protected float ellipseWidth;

	  protected final int TEXT_SIZE = 14;
	  protected final int TEXT_COLOR = temp.color(214);
	  protected final int TEXT_PADDING_LEFT = 5;
	  protected final int BUTTON_BACKGROUND_COLOR = temp.color(48);
	  protected final int BACKGROUND_COLOR = temp.color(26);
	  protected final int HOVER_COLOR = temp.color(60);

	  protected final int HIGHLIGHTS_WIDTH = 3;
	  protected PFont textFont;

}
