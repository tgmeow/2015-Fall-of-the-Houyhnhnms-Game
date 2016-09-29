package main;

import controller.PolygonButton;
import processing.core.PApplet;
import processing.core.PConstants;

public class GameplayMenu {
	private PApplet parent;
	private int menuWidth = 0;
	private int buttonWidth = 0;
	private int numSections = 3;
	private PolygonButton play;
	private boolean playValue = false;
	private boolean pressedP = false;
	private PolygonButton restartLevel;
	private boolean restartValue = false;
	private boolean pressedR = false;
	private PolygonButton quitLevel;
	private boolean quitValue = false;
	private boolean pressedQ = false;
	
	public GameplayMenu(PApplet p){
		parent = p;
	}
	public GameplayMenu(PApplet p, int menuWidth, int menuHeight){
		parent = p;
		this.menuWidth = menuWidth;
		this.buttonWidth = menuWidth/numSections;
		play = new PolygonButton(parent, -menuWidth/2 , -menuHeight/2, buttonWidth, menuHeight);
		restartLevel= new PolygonButton(parent, -menuWidth/2 + buttonWidth , -menuHeight/2, buttonWidth, menuHeight);
		quitLevel= new PolygonButton(parent, -menuWidth/2 + 2*buttonWidth , -menuHeight/2, buttonWidth, menuHeight);
	}
	public void draw(){
		//parent.noStroke();
		parent.strokeWeight(1);
		parent.stroke(200,70);
		if(play.contains(parent.mouseX - parent.width/2, parent.mouseY - parent.height/2)) parent.fill(60, 220); 
		else parent.fill(20, 190);
		play.drawButton();
		//parent.rect(-menuWidth/2, -menuHeight/2, menuWidth/2, menuHeight);
		
		if(restartLevel.contains(parent.mouseX - parent.width/2, parent.mouseY - parent.height/2)) parent.fill(60, 220);
		else parent.fill(20, 190);
		restartLevel.drawButton();
		//parent.rect(0, -menuHeight/2, menuWidth/2, menuHeight);
		
		if(quitLevel.contains(parent.mouseX - parent.width/2, parent.mouseY - parent.height/2)) parent.fill(60, 220);
		else parent.fill(20, 190);
		quitLevel.drawButton();
		//parent.rect(0, -menuHeight/2, menuWidth/2, menuHeight);
		
		parent.fill(200);
		parent.textAlign(PConstants.CENTER, PConstants.CENTER);
		parent.textSize(22);
		parent.text("RESUME", -menuWidth/2 + buttonWidth/2, 0);
		parent.text("RESTART", 0, 0);
		parent.text("QUIT", menuWidth/2 - buttonWidth/2, 0);
		parent.textSize(48);
		parent.text("GAME PAUSED", 0, -100);
	}
	public void pressUpdate(int x, int y){
		if(play.contains(x, y)) pressedP = true;
		else pressedP = false;
		if(restartLevel.contains(x,y)) pressedR = true;
		else pressedR = false;
		if(quitLevel.contains(x,y)) pressedQ = true;
		else pressedQ = false;
	}
	public void releaseUpdate(int x, int y){
		if(play.contains(x, y)){
			if(pressedP) playValue = true;
		}
		if(restartLevel.contains(x,y)){
			if(pressedR) restartValue = true;
		}
		if(quitLevel.contains(x,y)){
			if(pressedQ) quitValue = true;
		}
	}
	public boolean playValue(){
		boolean temp = playValue;
		playValue = false;
		return temp;
	}
	public boolean restartValue(){
		boolean temp = restartValue;
		restartValue = false;
		return temp;
	}
	public boolean quitValue(){
		boolean temp = quitValue;
		quitValue = false;
		return temp;
	}


}
