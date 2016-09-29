package main;
import java.util.ArrayList;
import controller.Controller;
import controller.ControllerPosition;
import controller.ControllersMenu;
import controller.IntSlider;
import nodes.AnimSVGMinion;
import nodes.PopAIController;
import nodes.PopNodeManager;
import nodes.PopNodeSVG;
import processing.core.*;

public class FallOfTheHouyhnhnms extends PApplet {
	private final String DATE_TEXT = "Last Updated: September 07, 2016";
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "main.FallOfTheHouyhnhnms" });
	}

	//private PGraphics overlay;
	private ControllersMenu settingsMenu;
	private PopNodeManager nodeManager;
	private PopAIController aiController;
	private PopAIController aiController0;
	private PopAIController aiController1;
	private AnimSVGMinion tempMinion;
	private AnimSVGMinion tempMinion2;
	private AnimSVGMinion tempMinion3;
	private float scale;
	
	private boolean displayCredits = false;
	private boolean displayInstructions = false;
	private boolean gamePaused = false;
	private boolean escapePaused = false;
	private boolean playLevel = false;
	private boolean initializedLevel = false; 
	private boolean win = false;
	private boolean displayWin = false;
	private boolean lose = false;
	private boolean displayLose = false;

	public static final float ACCY = 1E-9f;

	private int mouseColor = color(100, 100, 100);
	private boolean usePointMouse = true;
	public static Point panXY = new Point();

	private PFont cornerFont;    //global font variable for the fps and other counters
	private final boolean enableMatrixMovement = true;
	private int adjMouseX;
	private int adjMouseY;
	private ScrollZoom scrollZoom;
	private PShape menuHorseGrey20;
	private float menuHorseScale = 1.0f;
	private ArrayList<Point> menuHorseXY;
	private ArrayList<Integer> menuHorseXdir;
	private ArrayList<Integer> menuHorseYdir;
	private float menuHvXY;

	public static PShape horseGrey20;
	public static PShape horseGrey60;
	public static PShape horseBlack;
	public static PShape horseMaroon;
	public static PShape horseWhite;

	public static PShape houseGrey20;
	public static PShape houseGrey60;
	public static PShape houseBlack;
	public static PShape houseMaroon;
	public static PShape houseWhite;

	private SVGButton pauseButton;
	private GameplayMenu pauseMenu;
	private final String cScale = "Scale";
	private final String cMoveSE = "Move Speed Enemy";
	private final String cMoveSF = "Move Speed Friendly";
	private final String cInitP = "Initial Population";
	private final String cInitPN = "Initial Population (Neutrals)";
	private final String cNumN = "Number of Neutrals";
	private final String cNumNMAX = "Number of Neutrals MAX";
	private final String cPopGD = "Pop Growth Delay";
	private final String cAIMD = "AI Move Delay";
	private final String cReset = "Reset to Default";
	private final String cFAIvsAI = "Toggle Friendly AI";
	private final String cFAIDelay = "AI Move Delay (Friendly)";
	private final float moveSpeedMax = 2.0f;
	private boolean useFriendlyAI = false;

	public void settings(){
		size(1280,720);
	}
	public void setup(){
		if (usePointMouse) noCursor();    //hides mouse cursor
		smooth();      //ensure antialiasing
		frameRate(60);  //sets framerate to 60
		cornerFont = createFont("Arial", 36);  //make a new font for the fps and spawn count
		background(255);      //set inital background color to black for that fade-in effect

		//load horses and things
		horseGrey20 = tryLoadPShape(this, "/resources/horse_100x100_GRAY20.svg");
		horseGrey60 = tryLoadPShape(this, "/resources/horse_100x100_GRAY60.svg");
		horseBlack = tryLoadPShape(this, "/resources/horse_100x100_BLACK.svg");
		horseMaroon = tryLoadPShape(this, "/resources/horse_100x100_MAROON.svg");
		horseWhite = tryLoadPShape(this, "/resources/horse_100x100_WHITE.svg");
		houseGrey20 = tryLoadPShape(this, "/resources/house_100x100_GRAY20.svg");
		houseGrey60 = tryLoadPShape(this, "/resources/house_100x100_GRAY60.svg");
		houseBlack = tryLoadPShape(this, "/resources/house_100x100_BLACK.svg");
		houseMaroon = tryLoadPShape(this, "/resources/house_100x100_MAROON.svg");
		houseWhite = tryLoadPShape(this, "/resources/house_100x100_WHITE.svg");

		scrollZoom = new ScrollZoom(this);
		settingsMenu = new ControllersMenu(this, ControllerPosition.TOP_RIGHT, width/3+50);
		settingsMenu.addFloatSlider(cScale, 0.1f, 2.0f, 0.7f, 2);
		settingsMenu.addFloatSlider(cMoveSE, 0.01f, moveSpeedMax, 0.11f, 3);
		settingsMenu.addFloatSlider(cMoveSF, 0.01f, moveSpeedMax, 0.11f, 3);
		settingsMenu.addIntSlider(cInitP, 0, 200, 10);
		settingsMenu.addIntSlider(cInitPN, 0, 200, 5);
		settingsMenu.addIntSlider(cNumN, 0, 1000, 13);
		settingsMenu.addIntSlider(cNumNMAX, 15, 1000, 20);
		settingsMenu.addIntSlider(cPopGD, 50, 5000, 2000);
		settingsMenu.addIntSlider(cAIMD, 150, 10000, 6500);
		settingsMenu.addButtonController(cReset, "RESET");
		settingsMenu.addSwitchController(cFAIvsAI, "Friendly AI Enabled", "Friendly AI Disabled");

		nodeManager = new PopNodeManager(this);
		pauseMenu = new GameplayMenu(this, 400, 60);
		pauseButton = new SVGButton(this, "/resources/pauseGREY10.svg", 0.3f, "Pause", new Point(5, 5));
		menuHorseGrey20 = tryLoadPShape(this, "/resources/horse_100x100_GRAY20.svg");
		//menuHorseGrey20.scale(menuHorseScale);
		menuHorseXY = new ArrayList<Point>();
		menuHorseXdir = new ArrayList<Integer>();
		menuHorseYdir = new ArrayList<Integer>();
	}
	public void draw(){
		@SuppressWarnings("rawtypes")
		java.util.TreeMap <String, Controller> tempControllers = new java.util.TreeMap <String, Controller> (settingsMenu.getControllers());
		if((boolean)tempControllers.get(cFAIvsAI).getValue()){
			if(!tempControllers.containsKey(cFAIDelay)){
				settingsMenu.addIntSlider(cFAIDelay, 150, 10000, 6500);
			}
		} else{
			settingsMenu.removeController(cFAIDelay);
		}
		if ((boolean)tempControllers.get(cReset).getValue()) { 
			settingsMenu.resetMenu();
		}
		menuHorseGrey20.resetMatrix();
		menuHorseScale = (float)tempControllers.get(cScale).getValue();
		menuHorseGrey20.scale(menuHorseScale);
		menuHvXY = (float)tempControllers.get(cMoveSE).getValue() * 50.0f/3.0f;
		int numNeutMax = (int)tempControllers.get(cNumNMAX).getValue();
		((IntSlider) tempControllers.get(cNumN)).setMax(numNeutMax);

		int menuNum = (int)tempControllers.get(cInitP).getValue();
		if(menuHorseXY.size() < menuNum){
			for(int i = menuNum - menuHorseXY.size(); i>0; i--){
				menuHorseXY.add(new Point(random(width), random(height)));
				menuHorseXdir.add(floor(random(2))*2-1);
				menuHorseYdir.add(floor(random(2))*2-1);
			}
		} else if(menuHorseXY.size() > menuNum){
			for(int i = menuHorseXY.size()-menuNum; i>0; i--){
				int last = menuHorseXY.size()-1;
				menuHorseXY.remove(last);
				menuHorseXdir.remove(last);
				menuHorseYdir.remove(last);
			}
		}


		if(playLevel){
			background(0);
			if(!initializedLevel){
				//overlay.background(0);
				nodeManager = new PopNodeManager(this);
				win = false;
				lose = false;
				displayWin = false;
				displayLose = false;
				float moveSpeedEnemy = (float)tempControllers.get(cMoveSE).getValue();
				float moveSpeedFriendly = (float)tempControllers.get(cMoveSF).getValue();
				int numNeutrals = (int)tempControllers.get(cNumN).getValue();
				int neutralsPop = (int)tempControllers.get(cInitPN).getValue();
				int aiDelay = (int)tempControllers.get(cAIMD).getValue();

				int popGrowthSpeed = (int)tempControllers.get(cPopGD).getValue();
				int initPop = (int)tempControllers.get(cInitP).getValue();
				useFriendlyAI = (boolean) tempControllers.get(cFAIvsAI).getValue();
				aiController = new PopAIController(this, nodeManager, "Team2", aiDelay);
				if(useFriendlyAI){
					int fAIDelay = (int)tempControllers.get(cFAIDelay).getValue();
					aiController1 = new PopAIController(this, nodeManager, "Team1", fAIDelay);

				}
				//reset svg sizes
				horseGrey20.resetMatrix();
				horseGrey60.resetMatrix();
				horseBlack.resetMatrix();
				horseMaroon.resetMatrix();
				horseWhite.resetMatrix();
				houseGrey20.resetMatrix();
				houseGrey60.resetMatrix();
				houseBlack.resetMatrix();
				houseMaroon.resetMatrix();
				houseWhite.resetMatrix();

				scale = (float)tempControllers.get(cScale).getValue();
				horseGrey20.scale(scale);
				horseGrey60.scale(scale);
				horseBlack.scale(scale);
				horseMaroon.scale(scale);
				horseWhite.scale(scale);
				houseGrey20.scale(scale);
				houseGrey60.scale(scale);
				houseBlack.scale(scale);
				houseMaroon.scale(scale);
				houseWhite.scale(scale);

				tempMinion = new AnimSVGMinion(this, horseGrey60, scale, new Point(-200, -200), new Point(200, 200), -1, moveSpeedFriendly, 1, 1, "Team1", color(255, 0, 0));
				nodeManager.addNode(new PopNodeSVG(this, nodeManager, scale, new Point(-600, -300), initPop, popGrowthSpeed, "Team1", color(255), tempMinion));

				tempMinion2 = new AnimSVGMinion(this, horseMaroon, scale, new Point(-200, -200), new Point(200, 200), -1, moveSpeedEnemy, 1, 1, "Team2", color(0, 0, 255));
				nodeManager.addNode(new PopNodeSVG(this, nodeManager, scale, new Point(600, 300), initPop, popGrowthSpeed, "Team2", color(255), tempMinion2));

				tempMinion3 = new AnimSVGMinion(this, horseMaroon, scale, new Point(-200, -200), new Point(200, 200), -1, moveSpeedEnemy, 1, 1, "Neutral", color(0, 0, 255));
				float tries = 0;
				float genMargins = 2.0f + 0.4f*scale;
				for (int count = 0; count < numNeutrals; count++) {
					tries = 0;
					boolean regenPos = false;
					do {
						if(tries >= 50){
							numNeutrals--;
							break;
						}
						tries++;
						regenPos = false;
						float x = random(-width/genMargins, width/genMargins);
						float y = random(-height/genMargins, height/genMargins);
						for (PopNodeSVG aNode : nodeManager.getNodes()) {
							float dist = Point.distance(aNode.nodeLoc(), new Point(x, y));
							if (dist < (houseWhite.width+5)*scale) regenPos = true;
						}
						if(!regenPos) nodeManager.addNode(new PopNodeSVG(this, nodeManager, scale, new Point(x, y), neutralsPop, popGrowthSpeed, "Neutral", color(255), tempMinion3));
					} while (regenPos);
				}
				initializedLevel = true;
			}
			if(initializedLevel){
				/*******************************************
				 *           BEGIN DRAW CONTENTS           *
				 *******************************************/
				pushMatrix();
				if (enableMatrixMovement) {
					translate(width/2, height/2);  //INITIALLY CENTER SCREEN AT "0,0"
					//translateZoomScroll();
					rotate(radians(0));  //ROTATE ALL CONTENT
					adjMouseX = mouseX - width/2;
					adjMouseY = mouseY - height/2;
				} else {
					adjMouseX = mouseX;
					adjMouseY = mouseY;
				}
				/*******************************************
				 *              BEGIN CONTENT              *
				 *******************************************/
				playLevel = !pauseMenu.quitValue();
				//if not quit
				if(playLevel){
					drawPlayGame();
				} //end if not quit
				else{
					initializedLevel = false;
					gamePaused = false;
				}
			}//end if initialized level
		} 
		//draw the menu
		else if(!displayInstructions && !displayCredits){
			//float drawBegin = millis();
			drawMainMenu();
			//println(millis()-drawBegin);
		} 
		//display instructions 
		else if(displayInstructions) {
			drawInstructions();
		} else if(displayCredits){
			drawCredits();
		}
		/*******************************************
		 *            END DRAW CONTENTS            *
		 *******************************************/

		//drawFPSCounter(); //CORNER TEXTS
		//drawZoomCounter();

		/*******************************************
		 *          MOUSE/WINDOW MANAGEMENT        *
		 *******************************************/
		noStroke();
		if (usePointMouse) {
			strokeCap(ROUND);
			if (frameCount % 2 == 0) {              //lower mouse color sampling rate
				mouseColor = get(mouseX, mouseY);    //get the color under the mouse cursor
			}
			stroke(255-red(mouseColor), 255-green(mouseColor), 255-blue(mouseColor));  //"reverse" the color
			if (mousePressed) {
				strokeWeight(14); //BIG Strokes!
			} else strokeWeight(8);    //small strokes!
			line(mouseX, mouseY, pmouseX, pmouseY);
		}


	}
private void drawPlayGame(){
	boolean winLosePause = false;
	if(!gamePaused && !(win || lose)){
		if(!win && nodeManager.noEnemies()){
			win = true;
			//gamePaused = true;
			displayWin = true;
			winLosePause = true;
		}
		if(!lose && nodeManager.noFriendly()){
			lose = true;
			//gamePaused = true;
			displayLose = true;
			winLosePause = true;
		}
	}

	if(gamePaused && pauseMenu.restartValue()){
		initializedLevel = false;
		gamePaused = false;
	}
	boolean pause = pauseButton.getValue() || winLosePause || escapePaused;
	if(escapePaused) escapePaused = false;
	boolean paused = false;
	boolean resume = false;
	if(gamePaused){
		pause = pause || pauseMenu.playValue();
	}
	//if init pause
	if (pause) {
		//if game was not paused, then it is now paused
		if (!gamePaused){
			gamePaused = true;
			paused = true;
		}
		else {
			//game is unpaused. resume.
			gamePaused = false;
			resume = true;
		}
	}
	textFont(cornerFont, scale*32);
	nodeManager.updateMousePos(adjMouseX, adjMouseY);
	nodeManager.drawNodes();
	//if game is not paused
	if (!gamePaused) {
		//if game is not resumed
		if (!resume) {
			aiController.update();
			if(useFriendlyAI) aiController1.update();
			displayWin = false;
		} 
		//IF GAME IS RESUMED
		else {
			//do resumeing methods
			aiController.resume();
			if(useFriendlyAI) aiController1.resume();
			nodeManager.resume();
		}
	}
	//IF GAME IS PAUSED
	else {
		noStroke();
		fill(0, 135);
		rect(-width/2, -height/2, width, height);
		textFont(cornerFont, 25);
		pauseMenu.draw();
		//IF GAME WAS JUST PAUSED
		if(paused){
			aiController.pause();
			if(useFriendlyAI) aiController1.pause();
			nodeManager.pause();
		}
		//draw pause overlay menu
		if(displayWin){
			textFont(cornerFont, 60);
			text("YOU WIN!", 0, -180);
		}
		if(displayLose){
			textFont(cornerFont, 60);
			text("YOU LOSE!", 0, -180);
		}
	}
	/*******************************************
	 *               END CONTENT               *
	 *******************************************/
	popMatrix();
	textAlign(BASELINE);
	noStroke();
	pauseButton.draw();
	//textAlign(LEFT, TOP);
}
	private void drawMainMenu(){
		//background trails
		fill(0, 80);
		noStroke();
		rect(0, 0, width, height);
		//update horse movement
		//float updateHorsesTime = millis();
		for(int index = 0; index < menuHorseXY.size(); index++){
			Point tempHorseP = menuHorseXY.get(index);
			if(tempHorseP.x + menuHorseGrey20.width*menuHorseScale > width) menuHorseXdir.set(index, -1);
			else if(tempHorseP.x < 0) menuHorseXdir.set(index, 1);
			if(tempHorseP.y + menuHorseGrey20.height*menuHorseScale > height) menuHorseYdir.set(index, -1);
			else if(tempHorseP.y < 0) menuHorseYdir.set(index, 1);
			menuHorseXY.set(index, new Point(tempHorseP.x + menuHvXY * menuHorseXdir.get(index), tempHorseP.y + menuHvXY *  menuHorseYdir.get(index)));
			shape(menuHorseGrey20, tempHorseP.x, tempHorseP.y);
		}
		//println("Horses " + (millis()-updateHorsesTime));
		//updateHorsesTime = millis();
		textAlign(CENTER, CENTER);
		textFont(cornerFont, 60);
		fill(30);
		text("Fall of the Houyhnhnms", width/3+2, height/4+2);
		fill(214);
		text("Fall of the Houyhnhnms", width/3, height/4);

		fill(200, abs((frameCount*1.4f)%512-255));
		textFont(cornerFont, 26);
		text("press space to begin", width/2, 480-20);
		fill(200, abs(((frameCount+120)*1.2f)%512-255));
		text("press h for help", width/2, 480 + 50);
		fill(200, abs(((frameCount+210)*1.2f)%512-255));
		text("press c for credits", width/2, 480 + 120);
		//println("DrawTextTime " + (millis() - updateHorsesTime));
		//updateHorsesTime = millis();
		settingsMenu.drawMenu();
		//println("MenuTime " + (millis()-updateHorsesTime));
	}
private void drawInstructions(){
	background(0);
	textAlign(CENTER, TOP);
	int margin = 40;
	menuHorseGrey20.resetMatrix();
	float instrHorseScale = 1.1f;
	menuHorseGrey20.scale(instrHorseScale);
	shape(menuHorseGrey20, margin, margin);
	shape(menuHorseGrey20, width - menuHorseGrey20.width*instrHorseScale - margin, margin);
	shape(menuHorseGrey20, width - menuHorseGrey20.width*instrHorseScale - margin, height - menuHorseGrey20.height*instrHorseScale - margin);
	shape(menuHorseGrey20, margin, height - menuHorseGrey20.height*instrHorseScale - margin);
	
	textFont(cornerFont, 60);
	fill(30);
	text("Fall of the Houyhnhnms", width/2+2, 40+2);
	fill(214);
	text("Fall of the Houyhnhnms", width/2, 40);
	textFont(cornerFont, 26);
	text("After Lemuel Gulliver's visit to the Houyhnhnm Society in Houyhnhnm Land, Gulliver"
			+ "\nhad unknowingly corrupted the Houyhnhnms and split them into two factions"
			+ "\n- the Old Society and the New Society."
			+ "\nCommand the New Society and unite the Houyhnhnms by using war"
			+ "\ntactics to bring down the Old Society!"
			, width/2, 120);
	stroke(214);
	strokeWeight(2);
	line(0, 340, width, 340);
	text("Click and drag over the grey houses to select them."
			+ "\nRelease over any other house to send your troops there."
			+ "\nDefeat the Old Society (Maroon) by taking over all their houses!"
			, width/2, 360);
	text("Press space to select all your friendly houses."
			+ "\nPress escape at any time to pause, restart, or quit."
			, width/2, 500);
	fill(200, abs(((frameCount)*1.2f)%512-255));
	text("press h to return", width/2, 600);
}
private void drawCredits(){
	background(0);
	menuHorseGrey20.resetMatrix();
	int margin = 40;
	float instrHorseScale = 1.1f;
	menuHorseGrey20.scale(instrHorseScale);
	shape(menuHorseGrey20, margin, margin);
	shape(menuHorseGrey20, width - menuHorseGrey20.width*instrHorseScale - margin, margin);
	shape(menuHorseGrey20, width - menuHorseGrey20.width*instrHorseScale - margin, height - menuHorseGrey20.height*instrHorseScale - margin);
	shape(menuHorseGrey20, margin, height - menuHorseGrey20.height*instrHorseScale - margin);
	textAlign(CENTER, CENTER);
	textFont(cornerFont, 60);
	fill(214);
	text("Tiger Mou", width/2, height/2 - 160);
	textSize(40);
	text("tigermou16@gmail.com", width/2, height/2);
	textSize(20);
	text("This game uses the Processing libraries"
			+ "\n processing.org"
			, width/2, height/2 + 160);
	

	int dateSize = 14;
	textSize(dateSize);
	text(DATE_TEXT, width - (textWidth(DATE_TEXT)/2), height - dateSize + 5);
	
	textSize(26);
	fill(200, abs(((frameCount)*1.2f)%512-255));
	text("press c to return", width/2, 600);

}
	//Draws a gradiant line
	void line(int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd, int steps) {
		float[] xs = new float[steps+1];
		float[] ys = new float[steps+1];
		int[] cs = new int[steps+1];
		for (int i=0; i<=steps; i++) {
			float amount = (float) i / steps;
			xs[i] = lerp(xStart, xEnd, amount);
			ys[i] = lerp(yStart, yEnd, amount);
			cs[i] = lerpColor(colorStart, colorEnd, amount);
		}
		for (int i=0; i<steps; i++) {
			stroke(cs[i]);
			line(xs[i], ys[i], xs[i+1], ys[i+1]);
		}
	}

	//OBJECTS PASS BY REFERENCE!
	//Move from here to here with this many frames remaining
	void animatePointFrames(Point currPoint, Point targetPoint, int framesLeft) {
		float deltaXLeftPerFrame = (targetPoint.x - currPoint.x)/framesLeft;
		float deltaYLeftPerFrame = (targetPoint.y - currPoint.y)/framesLeft;
		currPoint.x += deltaXLeftPerFrame;
		currPoint.y += deltaYLeftPerFrame;
	}

	void drawFPSCounter() { 
		noStroke();
		fill(0);            //black rectangle in corner
		rect(0, 0, 5, 5);  //rectangle location
		fill(255);        //white text
		textFont(cornerFont, 16);  //font size 16
		text((int)(frameRate), 5, 5);  //display framerate in the top
	}
	void drawZoomCounter() { 
		noStroke();
		fill(0);            //black rectangle in corner
		int fpsWidth = 45;
		int fpsHeight = 25;
		rect(width-fpsWidth, height-fpsHeight, fpsWidth, fpsHeight);  //rectangle location
		fill(255);        //white text
		textFont(cornerFont, 18);  //font size 18
		text((int)(scrollZoom.zoom()), width-fpsWidth+3, height - fpsHeight + 5);  //display zoom in the bottomRight
	}
	public void mouseClicked() {
		if(!playLevel) settingsMenu.clickManager(mouseX, mouseY);
		//print("\nClicked: " + mouseX + ", " + mouseY);
	}
	public void mousePressed() {
		//print("\nPressed: " + mouseX + ", " + mouseY);
		if(playLevel) pauseButton.pressUpdate(mouseX, mouseY);
		if(playLevel) nodeManager.updateMousePress(mouseX - width/2, mouseY - height/2);
		if(gamePaused) pauseMenu.pressUpdate(mouseX - width/2, mouseY - height/2);
		if(!playLevel) settingsMenu.pressManager(mouseX, mouseY);
	}
	public void mouseReleased() {
		//print("\nReleased: " + mouseX + ", " + mouseY);
		if(playLevel) pauseButton.releaseUpdate(mouseX, mouseY);
		if(playLevel) nodeManager.updateMouseRelease(mouseX - width/2, mouseY - height/2);
		if(gamePaused) pauseMenu.releaseUpdate(mouseX - width/2, mouseY - height/2);
		if(!playLevel) settingsMenu.releaseManager(mouseX, mouseY);
	}
	public void keyPressed(){
		if(key==27){
			key=0;
			if(playLevel) escapePaused = true;
		}
	}
	public void keyReleased(){
		if(key == ' '){
			if(playLevel) nodeManager.selectAllPossible();
			if(!playLevel && !displayInstructions) playLevel = true;
		}
		if(key == 'h' || key == 'H'){
			displayInstructions = !displayInstructions;
		}
		if(key == 'c' || key == 'C'){
			if(!displayInstructions) displayCredits = !displayCredits;
		}
	}

	public static PShape teamSVG(String type, String teamName) {
		//team 1 = grey60
		//team 2 = maroon
		//neutral = white

		if (type.matches("horse")) {
			if (teamName.matches("Team1")) {
				return horseGrey60;
			} else if (teamName.matches("Team2")) {
				return horseMaroon;
			} else if (teamName.matches("Neutral")) {
				return horseGrey20;
			}
		} else if (type.matches("house")) {
			if (teamName.matches("Team1")) {
				return houseGrey60;
			} else if (teamName.matches("Team2")) {
				return houseMaroon;
			} else if (teamName.matches("Neutral")) {
				return houseGrey20;
			}
		}
		return horseWhite;
	}
	public static PShape tryLoadPShape(PApplet parent, String fileLoc){
		PApplet.println("Loading Image: " + fileLoc);
		PShape SVGImage = parent.createShape();
		try {
			SVGImage = parent.loadShape(fileLoc);
			PApplet.println("Image loaded sucessfully!");
		}
		catch(Exception e) {
			PApplet.println(e);
			PApplet.println("ERROR: IMAGE \"" + fileLoc + "\" NOT LOADED CORRECTLY!");
		}
		return SVGImage;
	}

}
