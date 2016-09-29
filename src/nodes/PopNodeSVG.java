package nodes;
import java.util.ArrayList;

import main.FallOfTheHouyhnhnms;
import main.Point;
import processing.core.PApplet;
import processing.core.PShape;

//class to create nodes on the screen using svg shapes. these shapes serve as the "houses" for the horses

public class PopNodeSVG {
	private PApplet parent;
	private PopNodeManager superManager;

	private boolean pause = false;
	private float pauseTime = -1;

	private ArrayList<AnimSVGMinion> activeMinions = new ArrayList<AnimSVGMinion>();
	private String fileLoc;
	private PShape SVGImage;
	private float scale;
	private boolean imageLoaded = false;
	private int popSize = 0;
	private int popGrowthSpeed; //(How many ms = +1)
	private float lastGrow = -1;
	private Point nodeLoc;
	private String teamName;  
	private int nodeColor;
	private boolean isPressed = false;
	private boolean isReleased = false;
	private float mousePMult = 1.2f;

	private ArrayList<Integer> sendRemaining = new ArrayList<Integer>();
	private float lastSent = -1;
	private final int SEND_DELAY = 20; //ms between new minion


	//Pop Info (NEED TO ALSO PUT THIS IN THE ANIMATEDSVG CLASS)!!!!
	private AnimSVGMinion nodePopModel;

	//load svg shape, XY, curr population, add population, subtract population, set color, set team String, growth speed, update grow method
	//horse speed px/ms, health, attack

	public PopNodeSVG(PApplet p, PopNodeManager superManager, String fileLoc, float scale, Point nodeLoc, int initPopSize, int popGrowthSpeed, String teamName, int nodeColor, AnimSVGMinion nodePopModel) {
		parent = p;
		this.superManager = superManager;
		this.fileLoc = fileLoc;
		this.scale = scale;
		this.nodeLoc = new Point(nodeLoc);
		this.popSize = initPopSize;
		this.popGrowthSpeed = popGrowthSpeed; 
		this.teamName = teamName;
		this.nodeColor = nodeColor;
		this.nodePopModel = nodePopModel.clone();
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
		this.lastGrow = parent.millis();
	}
	public PopNodeSVG(PApplet p, PopNodeManager superManager, float scale, Point nodeLoc, int initPopSize, int popGrowthSpeed, String teamName, int nodeColor, AnimSVGMinion nodePopModel) {
		parent = p;
		this.superManager = superManager;
		this.SVGImage = FallOfTheHouyhnhnms.teamSVG("house", teamName);
		this.scale = scale;
		this.nodeLoc = new Point(nodeLoc);
		this.popSize = initPopSize;
		this.popGrowthSpeed = popGrowthSpeed;
		this.teamName = teamName;
		this.nodeColor = nodeColor;
		this.nodePopModel = nodePopModel.clone();

		imageLoaded = true;
		this.lastGrow = parent.millis();
	}

	public void draw() {
		//draw node
		if (imageLoaded) parent.shape(SVGImage, nodeLoc.x - scale*SVGImage.width/2.0f, nodeLoc.y - scale*SVGImage.height/2.0f);
		else {
			parent.stroke(255);
			parent.strokeWeight(30);
			parent.point(nodeLoc.x, nodeLoc.y);
		}

		if(!pause){
			//GrowNode
			if (!teamName.matches("Neutral")) {
				if (parent.millis()-lastGrow >= popGrowthSpeed) {
					lastGrow = parent.millis();
					this.popSize++;
					//if (this.popSize < 0) {
					//  this.popSize = 0;
					//  this.teamName = "Neutral";
					//  this.SVGImage = teamSVG("house", "Neutral");
					//  //this.nodePopModel = aMinion.clone();
					//  sendRemaining.clear();
					//}
				}
			}
			int sendCount = 1;
			while(sendCount > 0){
				sendCount--;
				//SendMinions
				//check if empty or not
				if (sendRemaining.size() > 0) {
					//remove finished pairs
					if (sendRemaining.get(0) <= 0) {
						sendRemaining.remove(0);
						sendRemaining.remove(0);
						lastSent = -1;
					}
				}
				//check if empty after removal
				if (popSize <=0) sendRemaining.clear();
				if (sendRemaining.size() > 0) {
					if (parent.millis() - lastSent >= SEND_DELAY) {
						sendCount += (int) ((parent.millis() - lastSent)/SEND_DELAY);
						sendRemaining.set(0, sendRemaining.get(0) - 1);

						int targetID = sendRemaining.get(1);

						nodePopModel.setStartPoint(this.getNodeLoc());
						nodePopModel.setEndPoint(superManager.getNode(targetID).getNodeLoc());
						nodePopModel.setTargetID(targetID);

						activeMinions.add(nodePopModel.clone());
						activeMinions.get(activeMinions.size()-1).start();
						popSize--;
						if (lastSent == -1) lastSent = parent.millis();
						else lastSent += SEND_DELAY;
					}
				}
			}
		}

		//draw the minions of the node
		for (int index = 0; index < activeMinions.size(); index++) {
			AnimSVGMinion tempMin = activeMinions.get(index);
			tempMin.draw();
			//if finished
			if (!tempMin.isRunning()) {
				superManager.getNode(tempMin.getTargetID()).visit(tempMin);
				activeMinions.remove(index);
			}
		}
		//minions count text
		parent.textAlign(PApplet.CENTER, PApplet.TOP);
		parent.fill(40);
		parent.text(popSize, nodeLoc.x+2, nodeLoc.y+1);
		parent.fill(220);
		parent.text(popSize, nodeLoc.x, nodeLoc.y);
	}
	public Point getNodeLoc() {
		return new Point(nodeLoc);
	}
	public boolean contains(Point aPoint) {
		int circleRadius = (int)(mousePMult*scale*SVGImage.height/2.0f);
		float dist = (float)Math.sqrt((nodeLoc.x - aPoint.x)*(nodeLoc.x - aPoint.x) + (nodeLoc.y - aPoint.y)*(nodeLoc.y - aPoint.y));
		return dist <= circleRadius;
	}
	public void visit(AnimSVGMinion aMinion) {
		if (aMinion.teamName().matches(this.teamName())) {
			popSize++;
		} else {
			popSize -= (aMinion.getAttack() * aMinion.getHealth());
			//CHANGING NODE OWNERSHIP
			if (popSize <= 0) {
				this.popSize +=2;
				this.teamName = aMinion.teamName();
				this.SVGImage = FallOfTheHouyhnhnms.teamSVG("house", aMinion.teamName());
				this.nodePopModel = aMinion.clone();
				sendRemaining.clear();
				lastGrow = parent.millis();
			}
		}
	}
	public void goTo(int aNodeIndex) {
		int inMotion = 0;
		for (int index = 0; index < sendRemaining.size(); index+=2) {
			inMotion += sendRemaining.get(index);
		}
		sendRemaining.add((int)Math.round((popSize-inMotion)/2.0f));
		sendRemaining.add(aNodeIndex);
	}
	public boolean isPressed() {
		if (isPressed) {
			isPressed = false;
			return true;
		} else return false;
	}
	public boolean isReleased() {
		if (isReleased) {
			isReleased = false;
			return true;
		} else return false;
	}
	public boolean selectTeam1() {
		if (teamName.matches("Team1")) {
			isPressed = true;
			return true;
		}
		return false;
	}
	public boolean updateMousePress(int pressX, int pressY) {
		if (teamName.matches("Team1")) {
			if (this.contains(new Point(pressX, pressY))) {
				isPressed = true;
				return true;
			}
		}
		return false;
	}
	public boolean updateMouseRelease(int releaseX, int releaseY) {
		if (this.contains(new Point(releaseX, releaseY))) {
			isReleased = true;
			return true;
		}
		return false;
	}
	public void resetMouse() {
		isPressed = false;
		isReleased = false;
	}
	public PopNodeSVG clone() {
		return new PopNodeSVG(parent, superManager, fileLoc, scale, nodeLoc.clone(), popSize, popGrowthSpeed, teamName, nodeColor, nodePopModel.clone());
	}
	public int getPop() {
		return popSize;
	}
	public int getPopGrowth() {
		return popGrowthSpeed;
	}
	public Point nodeLoc(){
		return this.nodeLoc;
	}
	public String teamName(){
		return this.teamName;
	}
	public ArrayList<AnimSVGMinion> activeMinions(){
		return this.activeMinions;
	}
	public void pause(){
		pause = true;
		pauseTime = parent.millis();
		for(AnimSVGMinion aMinion : activeMinions){
			aMinion.pause();
		}
	}
	public void resume(){
		pause = false;
		lastGrow += parent.millis() - pauseTime;
		for(AnimSVGMinion aMinion : activeMinions){
			aMinion.resume();
		}
	}
}
