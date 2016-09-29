package nodes;
import java.util.ArrayList;

import main.Point;
import processing.core.PApplet;

public class PopNodeManager {
	private PApplet parent;
	private ArrayList<Integer> pressedNodeIndex = new ArrayList<Integer>();
	private ArrayList<PopNodeSVG> nodes = new ArrayList<PopNodeSVG>();
	private boolean selectedAll = false;


	public PopNodeManager(PApplet p) {
		parent = p;
		pressedNodeIndex.clear();
	}

	public void addNode(PopNodeSVG aNode) {
		nodes.add(aNode);
	}
	public void drawNodes() {
		for (PopNodeSVG aNode : nodes) {
			aNode.draw();
		}
	}
	public void selectAllPossible() {
		if(!selectedAll){
			for (int index = 0; index < nodes.size(); index++) {
				if(nodes.get(index).selectTeam1()) pressedNodeIndex.add(index);
			}
			selectedAll = true;
		} else{
			pressedNodeIndex.clear();
			selectedAll = false;
		}

	}
	public void updateMousePress(int pressX, int pressY) {
		if(!selectedAll){
			for (int index = 0; index < nodes.size(); index++) {
				if (nodes.get(index).updateMousePress(pressX, pressY)) {
					pressedNodeIndex.clear();
					pressedNodeIndex.add(index);
				}
			}
		}
	}
	public void updateMouseRelease(int releaseX, int releaseY) {
		//if Something was selected
		if (pressedNodeIndex.size() > 0) {
			for (int index = 0; index < nodes.size(); index++) {
				//send release position
				if (nodes.get(index).updateMouseRelease(releaseX, releaseY)) {
					//if this node was released on
					for (int sendIndex : pressedNodeIndex) {
						if (sendIndex!=index) if(nodes.get(sendIndex).teamName().matches("Team1"))nodes.get(sendIndex).goTo(index);
					}
				}
			}
			pressedNodeIndex.clear();
		}
		if(selectedAll) selectedAll = false;
	}
	public void updateMousePos(int mousePosX, int mousePosY) {
		//if something was selected
		if (pressedNodeIndex.size() > 0) {
			for (int index = 0; index < nodes.size(); index++) {
				//if mouse is pressed and hovered over a node
				if (nodes.get(index).updateMousePress(mousePosX, mousePosY)) {
					//only add if does not alreay contain
					if (!pressedNodeIndex.contains(index)) pressedNodeIndex.add(index);
				}
			}
			parent.stroke(230);
			parent.strokeWeight(10);
			for (int sendIndex : pressedNodeIndex) {
				Point.line(parent, mousePosX, mousePosY, nodes.get(sendIndex).getNodeLoc());
			}
		}
	}
	public PopNodeSVG getNode(int index) {
		return nodes.get(index);
	}
	public  ArrayList<PopNodeSVG> getNodes() {
		return nodes;
	}
	public void pause(){
		for(PopNodeSVG aNode: nodes){
			aNode.pause();
		}
	}
	public void resume(){
		for(PopNodeSVG aNode: nodes){
			aNode.resume();
		}
	}
	public boolean noEnemies(){
		for(PopNodeSVG aNode: nodes){
			if(aNode.teamName().matches("Team2")) return false;
			for(AnimSVGMinion aMinion : aNode.activeMinions()){
				if(aMinion.teamName().matches("Team2")) return false;
			}
		}
		return true;
	}
	public boolean noFriendly(){
		for(PopNodeSVG aNode: nodes){
			if(aNode.teamName().matches("Team1")) return false;
			for(AnimSVGMinion aMinion : aNode.activeMinions()){
				if(aMinion.teamName().matches("Team1")) return false;
			}
		}
		return true;
	}
}


