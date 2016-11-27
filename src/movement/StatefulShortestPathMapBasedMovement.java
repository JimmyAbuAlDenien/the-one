/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import core.Coord;
import core.Settings;
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

	private State state = State.LOC1;
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
	public Path getPath() {
		setWaitTime(this.state);
		// Get new state
		this.state = updateState(this.state);

		Path p = new Path(generateSpeed());
		MapNode to = selectNextStateDestination(this.state);

		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";

		for (MapNode node : nodePath) { // create a Path from the shortest path
			p.addWaypoint(node.getLocation());
		}

		lastMapNode = to;

		return p;
	}

	@Override
	public StatefulShortestPathMapBasedMovement replicate() {
		return new StatefulShortestPathMapBasedMovement(this);
	}

	///////////////////////////////////////////////
	public MapNode selectNextStateDestination(StatefulShortestPathMapBasedMovement.State state) {
		if(state == StatefulShortestPathMapBasedMovement.State.LOC1) {
			return new MapNode(new Coord(100,100));
		} else if(state == StatefulShortestPathMapBasedMovement.State.LOC2) {
			return new MapNode(new Coord(350,533));
		} else if(state == StatefulShortestPathMapBasedMovement.State.LOC3) {
			return new MapNode(new Coord(600,100));
		} else {
			return new MapNode(new Coord(100,100));
		}
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
			case LOC1: {
				List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
				loc1Probs.add(new Tuple<State, Double>(State.LOC1, 10.0));
				loc1Probs.add(new Tuple<State, Double>(State.LOC2, 50.0));
				loc1Probs.add(new Tuple<State, Double>(State.LOC3, 40.0));

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
				return State.LOC2;
			}
			case LOC2: {
				List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
				loc1Probs.add(new Tuple<State, Double>(State.LOC1, 40.0));
				loc1Probs.add(new Tuple<State, Double>(State.LOC2, 10.0));
				loc1Probs.add(new Tuple<State, Double>(State.LOC3, 50.0));

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
				return State.LOC2;
			}
			case LOC3: {
				List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
				loc1Probs.add(new Tuple<State, Double>(State.LOC1, 50.0));
				loc1Probs.add(new Tuple<State, Double>(State.LOC2, 20.0));
				loc1Probs.add(new Tuple<State, Double>(State.LOC3, 30.0));

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
				return State.LOC2;
			}
			default: {
				throw new RuntimeException( "Invalid state." );
			}
		}
	}

	private void setWaitTime(State state) {
		maxWaitTime = 0;
		minWaitTime = 0;

		if(state == State.LOC3) {
			maxWaitTime = 500;
			minWaitTime = 0;
		}
	}

	public static enum State {
		LOC1, LOC2, LOC3
	}
	//==========================================================================//

}
