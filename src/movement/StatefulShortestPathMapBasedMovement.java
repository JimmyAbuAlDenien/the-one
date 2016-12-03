/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import core.Coord;
import core.Settings;
import core.SimClock;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.PointsOfInterest;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class StatefulShortestPathMapBasedMovement extends MapBasedMovement implements
	SwitchableMovement {
	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;

	/** Points Of Interest handler */
	private PointsOfInterest pois;

	private State state = State.Enterance;
	private List<Tuple<State, Integer>> stateCapacity = new ArrayList<>();
	//==========================================================================//

	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public StatefulShortestPathMapBasedMovement(Settings settings) {
		super(settings);
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.pois = new PointsOfInterest(getMap(), getOkMapNodeTypes(),
				settings, rng);

		List<MapNode> allNodes = getMap().getNodes();
		for (int i=0; i<allNodes.size(); i++) {
			System.out.println(i + "; " + SimClock.getTime() + "; " + allNodes.get(i).getLocation().toString());
		};
	}

	/**
	 * Copyconstructor.
	 * @param mbm The ShortestPathMapBasedMovement prototype to base
	 * the new object to
	 */
	protected StatefulShortestPathMapBasedMovement(StatefulShortestPathMapBasedMovement mbm) {
		super(mbm);
		this.pathFinder = mbm.pathFinder;
		this.pois = mbm.pois;
	}

	@Override
	public Coord getInitialLocation() {
		List<MapNode> allNodes = getMap().getNodes();
		MapNode tmpNode = new MapNode(new Coord(881.02,216.67));

		for (MapNode destination : allNodes) {
			if(destination.getLocation().toString().equals(tmpNode.getLocation().toString())){
				lastMapNode = destination;
				return destination.getLocation().clone();
			}
		}

		return new Coord(881.02,216.67);
	}


	@Override
	public Path getPath() {
		// Get new state
		this.state = updateState(this.state);
		setWaitTime(this.state);

		Path p = new Path(generateSpeed());
		MapNode to = selectNextStateDestination(this.state);

		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);
		//List<MapNode> nodePath = pathFinder.getPathToDestination(lastMapNode, to, getMap().getNodes());

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";

		for (MapNode node : nodePath) { // create a Path from the shortest path
			p.addWaypoint(node.getLocation());
		}

		lastMapNode = nodePath.get(nodePath.size()-1);

		return p;
	}

	@Override
	public StatefulShortestPathMapBasedMovement replicate() {
		return new StatefulShortestPathMapBasedMovement(this);
	}

	///////////////////////////////////////////////
	// Library: 147.56,291.82
	// Enterance: 881.02,216.67
	// Cateferia: 548.61,393.59

	// Lecture Hall 1: 987.70,343.45
	// Lecture Hall 2: 707.16,447.99
	// Lecture Hall 3: 860.44,495.73

	// Seminar Hall 1: 120.56,0.00
	// Seminar Hall 2: 637.79,511.05
	// Seminar Hall 3: 481.63,193.80 (Beside computer hall)
	// Seminar Hall 4: 278.89,157.64
	// Seminar Hall 5: 149.85,135.00

	// Main Hall 1: 814.56,368.49
	// Main Hall 2: 453.13,278.73
	// Main Hall 3: 257.52,223.94

	// Computer Hall: 667.17,208.08

	public MapNode selectNextStateDestination(State state) {
		List<MapNode> allNodes = getMap().getNodes();
		MapNode tmpNode = null;

		if(state == null) {
			System.out.print("State is null");
			state = State.Enterance;
		}

		switch (state) {
			case Enterance: {
				tmpNode = new MapNode(new Coord(881.02,216.67));
				break;
			}

			case Mesa: {
				tmpNode = new MapNode(new Coord(548.61,393.59));
				break;
			}

			case Library: {
				tmpNode = new MapNode(new Coord(147.56,291.82));
				break;
			}

			case ComputerHall: {
				tmpNode = new MapNode(new Coord(667.17,208.08));
				break;
			}

			case MainHall1: {
				tmpNode = new MapNode(new Coord(814.56,368.49));
				break;
			}
			case MainHall2: {
				tmpNode = new MapNode(new Coord(453.13,278.73));
				break;
			}
			case MainHall3: {
				tmpNode = new MapNode(new Coord(257.52,223.94));
				break;
			}

			case LectureHall1: {
				tmpNode = new MapNode(new Coord(987.70,343.45));
				break;
			}
			case LectureHall2: {
				tmpNode = new MapNode(new Coord(707.16,447.99));
				break;
			}case LectureHall3: {
				tmpNode = new MapNode(new Coord(860.44,495.73));
				break;
			}

			case SeminarHall1: {
				tmpNode = new MapNode(new Coord(120.56,0.00));
				break;
			}

			case SeminarHall2: {
				tmpNode = new MapNode(new Coord(637.79,511.05));
				break;
			}

			case SeminarHall3: {
				tmpNode = new MapNode(new Coord(481.63,193.80));
				break;
			}

			case SeminarHall4: {
				tmpNode = new MapNode(new Coord(278.89,157.64));
				break;
			}

			case SeminarHall5: {
				tmpNode = new MapNode(new Coord(149.85,135.00));
				break;
			}
			default: {
				System.out.print("NO STATE FOUND! " + state);
			}
		}

//		if(state == State.LectureHall) {
//			tmpNode = new MapNode(new Coord(1033.57,124.38));
//		} else if(state == State.MainHall) {
//			tmpNode = new MapNode(new Coord(811.59,139.32));
//		} else if(state == State.Mesa) {
//			tmpNode = new MapNode(new Coord(581.09,171.69));
//		} else if(state == State.Enterance) {
//			tmpNode = new MapNode(new Coord(881.02,216.67));
//		} else if(state == State.ComputerHall) {
//			tmpNode = new MapNode(new Coord(659.81,0.00));
//		} else {
//			tmpNode = new MapNode(new Coord(841.74,41.77));
//		}
		//tmpNode.getNeighbors()
		for (MapNode destination : allNodes) {
			if(destination.getLocation().toString().equals(tmpNode.getLocation().toString())){
				return destination;
			}
		}
		return tmpNode;
	}

	/**
	 * This method defines the transitions in the state machine.
	 *
	 * @param state
	 *  the current state
	 * @return
	 *  the next state
	 */
	private State updateState( final State state ) {
		// TODO: Add time as parameter
		// TODO: Set wait time
		// TODO: Record node history
//		switch ( state ) {
//			case Enterance: {
//				List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
//				loc1Probs.add(new Tuple<State, Double>(State.LectureHall1, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.LectureHall2, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.LectureHall3, 7.14));
//
//				loc1Probs.add(new Tuple<State, Double>(State.SeminarHall1, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.SeminarHall2, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.SeminarHall3, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.SeminarHall4, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.SeminarHall5, 7.14));
//
//				loc1Probs.add(new Tuple<State, Double>(State.MainHall1, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.MainHall2, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.MainHall3, 7.14));
//
//				loc1Probs.add(new Tuple<State, Double>(State.Library, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 7.14));
//				loc1Probs.add(new Tuple<State, Double>(State.Mesa, 7.14));
//
////				if(SimClock.getTime() < 400) {
////					// TODO: Include node history in probabilities
////					loc1Probs.add(new Tuple<State, Double>(State.LectureHall, 10.0));
////					loc1Probs.add(new Tuple<State, Double>(State.MainHall, 20.0));
////					loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 30.0));
////					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 40.0));
////				} else if(SimClock.getTime() > 400 && SimClock.getTime() < 600) {
////					// TODO: Include node history in probabilities
////					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
////				} else {
////					// TODO: Include node history in probabilities
////					loc1Probs.add(new Tuple<State, Double>(State.LectureHall, 10.0));
////					loc1Probs.add(new Tuple<State, Double>(State.MainHall, 40.0));
////					loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 40.0));
////					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 10.0));
////				}
//
//
//				Random rand = new Random();
//				int index = rand.nextInt(100);
//				int sum = 0;
//
//				for (Tuple<State, Double> t : loc1Probs) {
//					sum += t.getValue();
//
//					if(sum >= index) {
//						return t.getKey();
//					}
//				}
//
//				// We shouldnt reach this point
//				return State.MainHall1;
//			}
//			case LectureHall: {
//				List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
//				if(SimClock.getTime() < 400) {
//					// TODO: Include node history in probabilities
//					loc1Probs.add(new Tuple<State, Double>(State.LectureHall, 10.0));
//					loc1Probs.add(new Tuple<State, Double>(State.MainHall, 20.0));
//					loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 30.0));
//					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 40.0));
//				} else if(SimClock.getTime() > 400 && SimClock.getTime() < 600) {
//					// TODO: Include node history in probabilities
//					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
//				} else {
//					// TODO: Include node history in probabilities
//					loc1Probs.add(new Tuple<State, Double>(State.LectureHall, 10.0));
//					loc1Probs.add(new Tuple<State, Double>(State.MainHall, 40.0));
//					loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 40.0));
//					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 10.0));
//				}
//
//
//				Random rand = new Random();
//				int index = rand.nextInt(100);
//				int sum = 0;
//
//				for (Tuple<State, Double> t : loc1Probs) {
//					sum += t.getValue();
//
//					if(sum >= index) {
//						return t.getKey();
//					}
//				}
//
//				// We shouldnt reach this point
//				return State.MainHall;
//			}
//			case MainHall: {
//				List<Tuple<State, Double>> loc2Probs = new ArrayList<>();
//				if(SimClock.getTime() < 400) {
//					// TODO: Include node history in probabilities
//					loc2Probs.add(new Tuple<State, Double>(State.LectureHall, 20.0));
//					loc2Probs.add(new Tuple<State, Double>(State.ComputerHall, 30.0));
//					loc2Probs.add(new Tuple<State, Double>(State.MainHall, 10.0));
//					loc2Probs.add(new Tuple<State, Double>(State.Mesa, 40.0));
//				} else if(SimClock.getTime() > 400 && SimClock.getTime() < 600) {
//					// TODO: Include node history in probabilities
//					loc2Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
//				} else {
//					// TODO: Include node history in probabilities
//					loc2Probs.add(new Tuple<State, Double>(State.LectureHall, 40.0));
//					loc2Probs.add(new Tuple<State, Double>(State.ComputerHall, 40.0));
//					loc2Probs.add(new Tuple<State, Double>(State.MainHall, 10.0));
//					loc2Probs.add(new Tuple<State, Double>(State.Mesa, 10.0));
//				}
//
//
//				Random rand = new Random();
//				int index = rand.nextInt(100);
//				int sum = 0;
//
//				for (Tuple<State, Double> t : loc2Probs) {
//					sum += t.getValue();
//
//					if(sum >= index) {
//						return t.getKey();
//					}
//				}
//
//				// We shouldnt reach this point
//				return State.MainHall;
//			}
//			case Mesa: {
//				List<Tuple<State, Double>> loc3Probs = new ArrayList<>();
//				if(SimClock.getTime() < 400) {
//					// TODO: Include node history in probabilities
//					loc3Probs.add(new Tuple<State, Double>(State.LectureHall, 40.0));
//					loc3Probs.add(new Tuple<State, Double>(State.MainHall, 20.0));
//					loc3Probs.add(new Tuple<State, Double>(State.ComputerHall, 30.0));
//					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 10.0));
//				} else if(SimClock.getTime() > 400 && SimClock.getTime() < 600) {
//					// TODO: Include node history in probabilities
//					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
//				} else {
//					// TODO: Include node history in probabilities
//					loc3Probs.add(new Tuple<State, Double>(State.LectureHall, 30.0));
//					loc3Probs.add(new Tuple<State, Double>(State.ComputerHall, 30.0));
//					loc3Probs.add(new Tuple<State, Double>(State.MainHall, 40.0));
//					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 0.0));
//				}
//
//				Random rand = new Random();
//				int index = rand.nextInt(100);
//				int sum = 0;
//
//				for (Tuple<State, Double> t : loc3Probs) {
//					sum += t.getValue();
//
//					if(sum >= index) {
//						return t.getKey();
//					}
//				}
//
//				// We shouldnt reach this point
//				return State.MainHall;
//			}
//			case ComputerHall: {
//				List<Tuple<State, Double>> loc3Probs = new ArrayList<>();
//				if(SimClock.getTime() < 400) {
//					// TODO: Include node history in probabilities
//					loc3Probs.add(new Tuple<State, Double>(State.LectureHall, 40.0));
//					loc3Probs.add(new Tuple<State, Double>(State.MainHall, 50.0));
//					loc3Probs.add(new Tuple<State, Double>(State.ComputerHall, 0.0));
//					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 10.0));
//				} else if(SimClock.getTime() > 400 && SimClock.getTime() < 600) {
//					// TODO: Include node history in probabilities
//					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
//				} else {
//					// TODO: Include node history in probabilities
//					loc3Probs.add(new Tuple<State, Double>(State.LectureHall, 30.0));
//					loc3Probs.add(new Tuple<State, Double>(State.ComputerHall, 0.0));
//					loc3Probs.add(new Tuple<State, Double>(State.MainHall, 40.0));
//					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 30.0));
//				}
//
//				Random rand = new Random();
//				int index = rand.nextInt(100);
//				int sum = 0;
//
//				for (Tuple<State, Double> t : loc3Probs) {
//					sum += t.getValue();
//
//					if(sum >= index) {
//						return t.getKey();
//					}
//				}
//
//				// We shouldnt reach this point
//				return State.MainHall1;
//			}
//			default: {
//				throw new RuntimeException( "Invalid state." );
//			}
//		}

		List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
		loc1Probs.add(new Tuple<State, Double>(State.LectureHall1, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.LectureHall2, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.LectureHall3, 7.14));

		loc1Probs.add(new Tuple<State, Double>(State.SeminarHall1, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.SeminarHall2, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.SeminarHall3, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.SeminarHall4, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.SeminarHall5, 7.14));

		loc1Probs.add(new Tuple<State, Double>(State.MainHall1, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.MainHall2, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.MainHall3, 7.14));

		loc1Probs.add(new Tuple<State, Double>(State.Library, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 7.14));
		loc1Probs.add(new Tuple<State, Double>(State.Mesa, 7.14));

		Random rand = new Random();
		int index = rand.nextInt(100);
		int sum = 0;

		for (Tuple<State, Double> t : loc1Probs) {
			sum += t.getValue();

			if(sum >= index) {
				return t.getKey();
			}
		}
		return null;
	}

	private void setWaitTime(State state) {
		maxWaitTime = 0;
		minWaitTime = 0;

		if(state == State.Mesa) {
			maxWaitTime = 200;
			minWaitTime = 50;
		} else if(state == State.LectureHall1) {
			maxWaitTime = 500;
			minWaitTime = 300;
		}
	}

	public static enum State {
		LectureHall1, LectureHall2, LectureHall3,
		SeminarHall1, SeminarHall2, SeminarHall3, SeminarHall4, SeminarHall5,
		MainHall1, MainHall2, MainHall3,
		Mesa,
		Enterance,
		ComputerHall,
		Library
	}
	//==========================================================================//

}
