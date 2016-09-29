package nodes;
import processing.core.PApplet;
import java.util.ArrayList;
//import java.util.Random;

import main.Point;

public class PopAIController {
	
	private final boolean useDebugPrints = false;
	
	private boolean paused = false;
	private float pauseTime = -1;

	//private Random rand = new Random();
	private PApplet parent;
	private PopNodeManager superManager;
	private String teamName = "";
	private final int MOVE_DELAY; //Time in ms per move
	private float lastMove = -1;
	private AIState currState = AIState.HOLD;
	private ArrayList<Integer> moveFrom;
	private ArrayList<Integer> moveTo;

	public PopAIController(PApplet p, PopNodeManager superManager, String teamName, int moveDelay) {
		parent = p;
		this.superManager = superManager;
		this.teamName = teamName;
		this.MOVE_DELAY = moveDelay;
		lastMove = parent.millis();
		moveFrom = new ArrayList<Integer>();
		moveTo = new ArrayList<Integer>();
	}

	public void update() {
		if(!paused){
			moveFrom.clear();
			moveTo.clear();
			//Resample the board state
			currState = AIState.HOLD;
			if (parent.millis() - lastMove >= MOVE_DELAY) {
				lastMove = parent.millis();
				//CURRENT STATE
				//how many nodes
				ArrayList<Integer> friendlyNodes = new ArrayList<Integer>();
				ArrayList<Integer> enemyNodes = new ArrayList<Integer>();
				ArrayList<Integer> neutralNodes = new ArrayList<Integer>();
				//how many minions
				ArrayList<Integer> friendlyCount = new ArrayList<Integer>();
				ArrayList<Integer> enemyCount = new ArrayList<Integer>();
				ArrayList<Integer> neutralCount = new ArrayList<Integer>();
				ArrayList<Float> friendlyProduction = new ArrayList<Float>();
				ArrayList<Float> enemyProduction = new ArrayList<Float>();

				ArrayList<Integer> friendlyTransitIndex = new ArrayList<Integer>();
				ArrayList<Integer> friendlyTransitCount = new ArrayList<Integer>();

				ArrayList<Integer> attackingIndex = new ArrayList<Integer>(); //num attacking friendly
				ArrayList<Integer> attackingCount = new ArrayList<Integer>(); //num attacking friendly

				//if there are still friendly nodes

				//ITERATE AND TO SAMPLING OF ALL NODES
				for (int index = 0; index < superManager.getNodes().size(); index++) {
					PopNodeSVG aNode = superManager.getNode(index);
					String name = aNode.teamName();
					//if node belongs to friendly
					if (name.matches(this.teamName)) {
						friendlyNodes.add(index);
						friendlyCount.add(aNode.getPop());
						friendlyProduction.add(1000.0f/aNode.getPopGrowth());
						for (AnimSVGMinion aMinion : aNode.activeMinions()) {
							//if attacking a friendly Node
							if (superManager.getNode(aMinion.getTargetID()).teamName().matches(this.teamName))
							{
								if (!friendlyTransitIndex.contains(aMinion.getTargetID())) {
									friendlyTransitIndex.add(aMinion.getTargetID());
									friendlyTransitCount.add(1);
								} else {
									int number = friendlyTransitIndex.indexOf(aMinion.getTargetID());
									friendlyTransitCount.set(number, friendlyTransitCount.get(number) + 1);
								}
							}
						}
					}
					//if is neutral
					else if (name.matches("Neutral")) {
						neutralNodes.add(index);
						neutralCount.add(aNode.getPop());
					}
					//if not friendly or neutral, must be enemy
					else {
						enemyNodes.add(index);
						enemyCount.add(aNode.getPop());
						enemyProduction.add(1000.0f/aNode.getPopGrowth());
						for (AnimSVGMinion aMinion : aNode.activeMinions()) {
							//if attacking a friendly Node
							if (superManager.getNode(aMinion.getTargetID()).teamName().matches(this.teamName))
							{
								if (!attackingIndex.contains(aMinion.getTargetID())) {
									attackingIndex.add(aMinion.getTargetID());
									attackingCount.add(1);
								} else {
									int attacking = attackingIndex.indexOf(aMinion.getTargetID());
									attackingCount.set(attacking, attackingCount.get(attacking) + 1);
								}
							}
						}
					}
				}
				//END SAMPLING

				if(friendlyNodes.size() > 0){
					//DETERMINE best next move
					//IF NEUTRAL NODE EXISTS
					if (neutralNodes.size() > 0) { 

						// Find closest neutral node
						float minDist = 1000000000;
						//int indexFriendly = -1;
						int indexNeutral = -1;
						int indexNeutralC = -1;
						ArrayList<Integer> targetedNeutrals = new ArrayList<Integer>();
						while (targetedNeutrals.size() < 2 || targetedNeutrals.size() == neutralNodes.size()) {
							//get minimum distance between a friendly and a neutral
							for (int indexF = 0; indexF < friendlyNodes.size(); indexF++) {
								for (int indexN = 0; indexN < neutralNodes.size(); indexN++) {
									float tempDist = Point.distance(superManager.getNode(friendlyNodes.get(indexF)).getNodeLoc(), superManager.getNode(neutralNodes.get(indexN)).getNodeLoc());
									if (tempDist < minDist && !targetedNeutrals.contains(neutralNodes.get(indexN))) {
										minDist = tempDist;
										//indexFriendly = friendlyNodes.get(indexF);
										indexNeutral = neutralNodes.get(indexN);
										indexNeutralC = superManager.getNode(indexNeutral).getPop();
									}
								}
							}
							if(indexNeutral != -1){
								targetedNeutrals.add(indexNeutral);
								//get min dist from enemy node to the same neutral node
								float minDistEE = 1000000000;
								for (int indexE = 0; indexE < enemyNodes.size(); indexE++) {
									float tempDist = Point.distance(superManager.getNode(enemyNodes.get(indexE)).getNodeLoc(), superManager.getNode(indexNeutral).getNodeLoc());
									if (tempDist < minDistEE) minDistEE = tempDist;
								}

								//compare distance of node
								//if enemy node is closer
								if (minDist - minDistEE > 0) {
									currState = AIState.HOLD;
								}

								//if friendly node is closer     //if < 30% the fight for it else HOLD
								if (minDist - minDistEE <= 0 || (minDist - minDistEE > 0 && Math.abs(minDist-minDistEE)/minDistEE < 0.3f)) {
									////
									ArrayList<Float> friendlyDistances = new ArrayList<Float>();
									ArrayList<Integer> indexOfUnchecked = new ArrayList<Integer>(); //holds local indexes of which nodes have been unchecked  IN TERMS OF FRIENDLYNODES
									ArrayList<Integer> indexOfChecked = new ArrayList<Integer>(); //holds local indexes of which nodes have been checked
									for (int index = 0; index < friendlyNodes.size(); index++) {
										friendlyDistances.add(Point.distance(superManager.getNode(friendlyNodes.get(index)).getNodeLoc(), superManager.getNode(indexNeutral).getNodeLoc()));
										indexOfUnchecked.add(index);
									}

									//initialize friendly sum to be equal to the total minions in transit to that one targeted friendly node
									int friendlySum = 0;
									if (friendlyTransitIndex.contains(indexNeutral)) friendlySum += friendlyTransitCount.get(friendlyTransitIndex.indexOf(indexNeutral));

									minDist = 10000000;
									int minIndex = -1;
									//int indexOverallClosest = 0;
									//try to allocate enough minions
									//if the node will be lost if it sends minions then do not send. (remove from unchecked)
									for (int index = 0; index < attackingIndex.size(); index++) {
										//if not this node is not the main one being attacked and if there will NOT be enough if moved
										if (attackingIndex.get(index) != indexNeutral && attackingCount.get(index) > friendlyCount.get(friendlyNodes.indexOf(attackingIndex.get(index)))/2) {
											indexOfUnchecked.remove(indexOfUnchecked.indexOf(friendlyNodes.indexOf(attackingIndex.get(index))));
										}
									}
									while (indexOfUnchecked.size() > 0 && friendlySum <= indexNeutralC) {
										
										minDist = 10000000;
										//find next closest node by iterating through remaining nodes
										for (int indexDist = 0; indexDist < indexOfUnchecked.size(); indexDist++) {
											//determine closest node
											if (friendlyDistances.get(indexOfUnchecked.get(indexDist)) < minDist) {
												minDist = friendlyDistances.get(indexOfUnchecked.get(indexDist));
												//if (minIndex == -1) indexOverallClosest = indexOfUnchecked.get(indexDist);
												minIndex = indexOfUnchecked.get(indexDist);
											}
										}
										indexOfUnchecked.remove(indexOfUnchecked.indexOf(minIndex));
										indexOfChecked.add(minIndex);
										friendlySum += Math.ceil(friendlyCount.get(minIndex)/2.0f);
									}
									////
									currState = AIState.ATTACKING;
									for (int i = 0; i < indexOfChecked.size(); i++) {
										moveFrom.add(friendlyNodes.get(indexOfChecked.get(i)));

										if (friendlyTransitIndex.contains(friendlyNodes.get(indexOfChecked.get(i)))) {
											int index = friendlyTransitIndex.indexOf(friendlyNodes.get(indexOfChecked.get(i)));
											friendlyTransitCount.set(index, (int)(friendlyTransitCount.get(index) + Math.ceil(friendlyCount.get(indexOfChecked.get(i))/2.0f)));
										} else {
											friendlyTransitIndex.add(friendlyNodes.get(indexOfChecked.get(i)));
											friendlyTransitCount.add((int)Math.ceil(friendlyCount.get(indexOfChecked.get(i))/2.0f));
										}
										friendlyCount.set(indexOfChecked.get(i), (int)Math.floor(friendlyCount.get(indexOfChecked.get(i))/2.0f));
									}
									moveTo.add(indexNeutral);
								}
							}
						}
					}//END NEUTRAL NODE EXISTS
					//DEFEND FRIENDLY NODE
					//if enemy attacking a friendly and we are not attacking a neutral 
					if (currState == AIState.HOLD && attackingIndex.size() > 0) {
						currState = AIState.DEFENDING;
						if(useDebugPrints) PApplet.println("***** DEFEND! *****");
						ArrayList<Integer> defended = new ArrayList<Integer>(); //global index
						//sort friendly nodes by distance
						while (defended.size() < 5 && defended.size() < attackingCount.size()) {
							int mostC = 0;
							int mostI = -1;
							for (int index = 0; index < attackingCount.size(); index++) {
								//if attacking a friendly node
								if (superManager.getNode(attackingIndex.get(index)).teamName().matches(this.teamName)) {
									if (!defended.contains(attackingIndex.get(index))) {
										if (attackingCount.get(index) > mostC) {
											mostC = attackingCount.get(index);
											mostI = attackingIndex.get(index);
											//mostI = attackingIndex.get((rand.nextInt(attackingCount.size())));
										}
									}
									if(useDebugPrints) PApplet.println(friendlyNodes.get(index) + " " + attackingCount.get(index));
								}
							}
							if(mostI != -1){
								defended.add(mostI);
								if(useDebugPrints) PApplet.println("YAAAAAAAA" + defended.size());
								ArrayList<Float> friendlyDistances = new ArrayList<Float>();
								ArrayList<Integer> indexOfUnchecked = new ArrayList<Integer>(); //holds local indexes of which nodes have been unchecked  IN TERMS OF FRIENDLYNODES
								ArrayList<Integer> indexOfChecked = new ArrayList<Integer>(); //holds local indexes of which nodes have been checked
								for (int index = 0; index < friendlyNodes.size(); index++) {
									friendlyDistances.add(Point.distance(superManager.getNode(friendlyNodes.get(index)).getNodeLoc(), superManager.getNode(mostI).getNodeLoc()));
									indexOfUnchecked.add(index);
								}

								//initialize friendly sum to be equal to the total minions in transit to that one targeted friendly node
								int friendlySum = 0;
								//for (int index = 0; index < friendlyTransitIndex.size(); index++) {
								//  if (superManager.getNode(friendlyTransitIndex.get(index)).teamName.matches(this.teamName)) {
								//    friendlySum += friendlyTransitCount.get(index);
								//  }
								//}
								if (friendlyTransitIndex.contains(mostI)) friendlySum += friendlyTransitCount.get(friendlyTransitIndex.indexOf(mostI));
								if(useDebugPrints) PApplet.println("NUMFRIENDLIES" + friendlySum);

								float minDist = 10000000;
								int minIndex = -1;
								int indexOverallClosest = 0;
								//try to allocate enough minions
								if(useDebugPrints) PApplet.println("TRYING TO ALLOCATE");
								//if the node will be lost if it sends minions then do not send. (remove from unchecked)
								for (int index = 0; index < attackingIndex.size(); index++) {
									//if not this node is not the main one being attacked and if there will NOT be enough if moved
									if(useDebugPrints) PApplet.println("THERE IS ENOUGH TO SHARE? " + attackingCount.get(index) + " ATTACKING IF MOVE " + friendlyCount.get(friendlyNodes.indexOf(attackingIndex.get(index)))/2 );
									if (attackingIndex.get(index) != mostI && attackingCount.get(index) > friendlyCount.get(friendlyNodes.indexOf(attackingIndex.get(index)))/2) {
										if(useDebugPrints) PApplet.println("ATTACKING" + attackingIndex.get(index));
										indexOfUnchecked.remove(indexOfUnchecked.indexOf(friendlyNodes.indexOf(attackingIndex.get(index))));
										if(useDebugPrints) PApplet.println("TARGETING" + attackingCount.get(index));
									}
								}

								while (indexOfUnchecked.size() > 0 && friendlySum < mostC) {
									minDist = 10000000;
									//find next closest node by iterating through remaining nodes
									for (int indexDist = 0; indexDist < indexOfUnchecked.size(); indexDist++) {
										//determine closest node
										if (friendlyDistances.get(indexOfUnchecked.get(indexDist)) < minDist) {
											minDist = friendlyDistances.get(indexOfUnchecked.get(indexDist));
											if (minIndex == -1) indexOverallClosest = indexOfUnchecked.get(indexDist);
											minIndex = indexOfUnchecked.get(indexDist);
										}
									}
									indexOfUnchecked.remove(indexOfUnchecked.indexOf(minIndex));
									indexOfChecked.add(minIndex);
									if(useDebugPrints) PApplet.println("INDEX " + friendlyNodes.get(minIndex) + " HAS " + superManager.getNode(friendlyNodes.get(minIndex)).getPop());
									friendlySum += Math.ceil(friendlyCount.get(minIndex)/2.0f);
								}
								if(useDebugPrints) PApplet.println("TOTAL ALLOCATABLE FRIENDLIES" + friendlySum + " vs " + mostC);
								//if not enough
								if (friendlySum+2 < mostC) {
									if(useDebugPrints) PApplet.println("NOT ENOUGH");
									//if desparately not enough move all of them to the closest node

									if(useDebugPrints) PApplet.println("ESCAPE!");
									moveTo.add(friendlyNodes.get(indexOverallClosest));
									int timesToMove =  1;//ceil(log(friendlyCount.get(friendlyNodes.indexOf(mostI))/log(2.0)));
									for (int count = 0; count < timesToMove; count++) {
										moveFrom.add(mostI);

										if (friendlyTransitIndex.contains(indexOverallClosest)) {
											int index = friendlyTransitIndex.indexOf(indexOverallClosest);
											friendlyTransitCount.set(index, (int)(friendlyTransitIndex.get(index) + Math.ceil(friendlyCount.get(friendlyNodes.indexOf(mostI))/2.0f)));
										} else {
											friendlyTransitIndex.add(indexOverallClosest);
											friendlyTransitCount.add((int)Math.ceil(friendlyCount.get(friendlyNodes.indexOf(mostI))/2.0f));
										}
										friendlyCount.set(friendlyNodes.indexOf(mostI), (int)Math.floor(friendlyCount.get(friendlyNodes.indexOf(mostI))/2.0f));
									}
								}
								//if enough then move from checked nodes to attacked node
								else {
									if(useDebugPrints) PApplet.println("ENOUGH!");
									//whereever the majority are moving
									moveTo.add(mostI);
									if(useDebugPrints) PApplet.println(superManager.getNode(mostI).getNodeLoc().toString());
									int timesToMove = 1;//ceil(log(friendlyCount.get(friendlyNodes.indexOf(mostI))/log(2.0)));
									for (int i : indexOfChecked) {
										for (int count = 0; count < timesToMove; count++) {
											moveFrom.add(friendlyNodes.get(i));

											if (friendlyTransitIndex.contains(friendlyNodes.get(i))) {
												int index = friendlyTransitIndex.indexOf(friendlyNodes.get(i));
												friendlyTransitCount.set(index, (int)(friendlyTransitIndex.get(index) + Math.ceil(friendlyCount.get(i)/2.0f)));
											} else {
												friendlyTransitIndex.add(friendlyNodes.get(i));
												friendlyTransitCount.add((int)Math.ceil(friendlyCount.get(i)/2.0f));
											}
											friendlyCount.set(i, (int)Math.floor(friendlyCount.get(i)/2.0f));
										}
									}
									

								}
							}
						}
					} //END DEFEND!
					if(moveFrom.size() < 2) currState = AIState.HOLD;

					//Attack enemy node!
					if (currState == AIState.HOLD) {
						currState = AIState.ATTACKING;
						if(useDebugPrints) PApplet.println("***** ATTACK! *****");
						ArrayList<Integer> attacked = new ArrayList<Integer>(); //global index
						//find weakest enemy node
						while (attacked.size() < 2 && attacked.size() < enemyCount.size()) {
							int leastC = Integer.MAX_VALUE;
							int leastI = -1;
							for (int index = enemyNodes.size()-1; index >= 0; index--) {
								if (!attacked.contains(enemyNodes.get(index))) {
									if (enemyCount.get(index) < leastC) {
										leastC = enemyCount.get(index);
										leastI = enemyNodes.get(index);
									} else if(enemyCount.get(index) == leastC){
										//if equal and closer
										float minDistOld = Integer.MAX_VALUE;
										float minDistNew = Integer.MAX_VALUE;
										for(int fIndexD = 0; fIndexD < friendlyNodes.size(); fIndexD++){
											float tempDistOld = Point.distance(superManager.getNode(friendlyNodes.get(fIndexD)).getNodeLoc(), superManager.getNode(leastI).getNodeLoc());
											if(tempDistOld < minDistOld) minDistOld = tempDistOld;
											float tempDistNew = Point.distance(superManager.getNode(friendlyNodes.get(fIndexD)).getNodeLoc(), superManager.getNode(enemyNodes.get(index)).getNodeLoc());
											if(tempDistNew < minDistNew) minDistNew = tempDistNew;
										}
										if(minDistNew < minDistOld){
											leastI = enemyNodes.get(index);
										}
									}
								}

							}
							if(leastI != -1){
								attacked.add(leastI);
								if(useDebugPrints) PApplet.println("YAAAAAAAA" + attacked.size());

								ArrayList<Float> friendlyDistances = new ArrayList<Float>();
								ArrayList<Integer> indexOfUnchecked = new ArrayList<Integer>(); //holds local indexes of which nodes have been unchecked  IN TERMS OF FRIENDLYNODES
								ArrayList<Integer> indexOfChecked = new ArrayList<Integer>(); //holds local indexes of which nodes have been checked
								for (int index = 0; index < friendlyNodes.size(); index++) {
									friendlyDistances.add(Point.distance(superManager.getNode(friendlyNodes.get(index)).getNodeLoc(), superManager.getNode(leastI).getNodeLoc()));
									indexOfUnchecked.add(index);
								}

								int friendlySum = 0;
								float minDist = 10000000;
								int minIndex = -1;
								//int indexOverallClosest = 0;
								//try to allocate enough minions
								if(useDebugPrints) PApplet.println("TRYING TO ALLOCATE");
								//if the node will be lost if it sends minions then do not send. (remove from unchecked)
								for (int index = 0; index < attackingIndex.size(); index++) {
									//if not this node is not the main one being attacked and if there will NOT be enough if moved
									if(useDebugPrints) PApplet.println("THERE IS ENOUGH TO SHARE? " + attackingCount.get(index) + " ATTACKING IF MOVE " + friendlyCount.get(friendlyNodes.indexOf(attackingIndex.get(index)))/2 );
									if (attackingIndex.get(index) != leastI && attackingCount.get(index) > friendlyCount.get(friendlyNodes.indexOf(attackingIndex.get(index)))/2) {
										if(useDebugPrints) PApplet.println("ATTACKING" + attackingIndex.get(index));
										indexOfUnchecked.remove(indexOfUnchecked.indexOf(friendlyNodes.indexOf(attackingIndex.get(index))));
										if(useDebugPrints) PApplet.println("TARGETING" + attackingCount.get(index));
									}
								}

								//try to sum up enough
								while (indexOfUnchecked.size() > 0 && friendlySum-2 < leastC) {
									minDist = 10000000;
									//find next closest node by iterating through remaining nodes
									for (int indexDist = 0; indexDist < indexOfUnchecked.size(); indexDist++) {
										//determine closest node
										if (friendlyDistances.get(indexOfUnchecked.get(indexDist)) < minDist) {
											minDist = friendlyDistances.get(indexOfUnchecked.get(indexDist));
											//if (minIndex == -1) indexOverallClosest = indexOfUnchecked.get(indexDist);
											minIndex = indexOfUnchecked.get(indexDist);
										}
									}
									indexOfUnchecked.remove(indexOfUnchecked.indexOf(minIndex));
									indexOfChecked.add(minIndex);
									if(useDebugPrints) PApplet.println("INDEX " + friendlyNodes.get(minIndex) + " HAS " + superManager.getNode(friendlyNodes.get(minIndex)).getPop());
									friendlySum += Math.ceil(friendlyCount.get(minIndex)/2.0f);
								}
								if(useDebugPrints) PApplet.println("TOTAL ALLOCATABLE FRIENDLIES" + friendlySum + " vs " + leastC);
								//if not enough
								if (friendlySum < leastC) {
									if(useDebugPrints) PApplet.println("NOT ENOUGH");
									if(useDebugPrints) PApplet.println("DON'T ATTACK!");
									//currState = AIState.HOLD;
								}
								//if enough then move from checked nodes to attacked node
								else {
									if(useDebugPrints) PApplet.println("ENOUGH!");
									currState = AIState.ATTACKING;
									//Wherever the majority are moving
									moveTo.add(leastI);
									if(useDebugPrints) PApplet.println(superManager.getNode(leastI).getNodeLoc().toString());
									int timesToMove = 1;//ceil(log(friendlyCount.get(friendlyNodes.indexOf(mostI))/log(2.0)));
									for (int i : indexOfChecked) {
										for (int count = 0; count < timesToMove; count++) {
											moveFrom.add(friendlyNodes.get(i));
											friendlyCount.set(i, (int)Math.floor(friendlyCount.get(i)/2.0f));
										}
									}
								}
							}

						}
					} ///END ATTACK MODE


					//MAKE the move
					switch(currState) {
					case ATTACKING : 
						//Move minions from target friendly node(s) to target ENEMY node 
						for (int indexF = 0; indexF < moveFrom.size(); indexF++) {
							for (int indexT = 0; indexT < moveTo.size(); indexT++) {
								if (moveFrom.get(indexF) != moveTo.get(indexT))
									superManager.getNode(moveFrom.get(indexF)).goTo(moveTo.get(indexT));
							}
						}
						break; 
					case DEFENDING : 
						//Move minions from target friendly node(s) to target FRIENDLY node
						//ALSO do this during a "Hold" to decrease distance between bulk and enemy
						for (int indexF = 0; indexF < moveFrom.size(); indexF++) {
							for (int indexT = 0; indexT < moveTo.size(); indexT++) {
								if (moveFrom.get(indexF) != moveTo.get(indexT))
									superManager.getNode(moveFrom.get(indexF)).goTo(moveTo.get(indexT));
							}
						}
						break; 
					case HOLD : 
						//do nothing. literally.
						break; 
					default : 
						break;
					}
				}
			}
		}
	}

	public void pause(){
		paused = true;
		pauseTime = parent.millis();
	}
	public void resume(){
		paused = false;
		lastMove += (parent.millis() - pauseTime);
	}
}