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

	private State state = State.MainHall;
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
		MapNode tmpNode = new MapNode(new Coord(811.59,139.32));

		for (MapNode destination : allNodes) {
			if(destination.getLocation().toString().equals(tmpNode.getLocation().toString())){
				lastMapNode = destination;
				return destination.getLocation().clone();
			}
		}

		return new Coord(811.59,139.32);
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
	public MapNode selectNextStateDestination(StatefulShortestPathMapBasedMovement.State state) {
		List<MapNode> allNodes = getMap().getNodes();
		MapNode tmpNode;

		if(state == StatefulShortestPathMapBasedMovement.State.LectureHall) {
			tmpNode = new MapNode(new Coord(1033.57,124.38));
		} else if(state == StatefulShortestPathMapBasedMovement.State.MainHall) {
			//return new MapNode(new Coord(841.74,41.77)); --> Enterance
			tmpNode = new MapNode(new Coord(811.59,139.32));
		} else if(state == StatefulShortestPathMapBasedMovement.State.Mesa) {
			tmpNode = new MapNode(new Coord(581.09,171.69));
		} else {
			tmpNode = new MapNode(new Coord(841.74,41.77));
		}
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
		switch ( state ) {
			case LectureHall: {
				List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
				if(SimClock.getTime() < 400) {
					loc1Probs.add(new Tuple<State, Double>(State.LectureHall, 10.0));
					loc1Probs.add(new Tuple<State, Double>(State.MainHall, 50.0));
					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 40.0));
				} else {
					loc1Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
				}


				Random rand = new Random();
				int index = rand.nextInt(100);
				int sum = 0;

				for (Tuple<State, Double> t : loc1Probs) {
					sum += t.getValue();

					if(sum >= index) {
						return t.getKey();
					}
				}

				// We shouldnt reach this point
				return State.MainHall;
			}
			case MainHall: {
				List<Tuple<State, Double>> loc2Probs = new ArrayList<>();
				if(SimClock.getTime() < 400) {
					loc2Probs.add(new Tuple<State, Double>(State.LectureHall, 40.0));
					loc2Probs.add(new Tuple<State, Double>(State.MainHall, 10.0));
					loc2Probs.add(new Tuple<State, Double>(State.Mesa, 50.0));
				} else {
					loc2Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
				}


				Random rand = new Random();
				int index = rand.nextInt(100);
				int sum = 0;

				for (Tuple<State, Double> t : loc2Probs) {
					sum += t.getValue();

					if(sum >= index) {
						return t.getKey();
					}
				}

				// We shouldnt reach this point
				return State.MainHall;
			}
			case Mesa: {
				List<Tuple<State, Double>> loc3Probs = new ArrayList<>();
				if(SimClock.getTime() < 400) {
					loc3Probs.add(new Tuple<State, Double>(State.LectureHall, 50.0));
					loc3Probs.add(new Tuple<State, Double>(State.MainHall, 20.0));
					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 30.0));
				} else {
					loc3Probs.add(new Tuple<State, Double>(State.Mesa, 100.0));
				}

				Random rand = new Random();
				int index = rand.nextInt(100);
				int sum = 0;

				for (Tuple<State, Double> t : loc3Probs) {
					sum += t.getValue();

					if(sum >= index) {
						return t.getKey();
					}
				}

				// We shouldnt reach this point
				return State.MainHall;
			}
			default: {
				throw new RuntimeException( "Invalid state." );
			}
		}
	}

	private void setWaitTime(State state) {
		maxWaitTime = 0;
		minWaitTime = 0;

		if(state == State.Mesa) {
			maxWaitTime = 200;
			minWaitTime = 50;
		} else if(state == State.LectureHall) {
			maxWaitTime = 500;
			minWaitTime = 300;
		}
	}

	public static enum State {
		LectureHall, MainHall, Mesa
	}
	//==========================================================================//

}
