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
import movement.model.MIState;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;

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

	private MIState state = MIState.getStateByName(MIState.State.Enterance);

	private List<MIState.State> hostHistory = new ArrayList<>();
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

		// Set all nodes
		MIState.allNodes = getMap().getNodes();
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

		for (MapNode destination : allNodes) {
			if(destination.getLocation().toString().equals(state.coord.toString())){
				lastMapNode = destination;
				return destination.getLocation().clone();
			}
		}

		return null;
	}

	@Override
	public Path getPath() {
		// Get new state
		this.state = this.state.getNextState((SimClock.getTime()>700 && SimClock.getTime()<900), hostHistory.size() >3);

		setWaitTime(this.state.currentState);

		Path p = new Path(generateSpeed());
		MapNode to = this.state.getMapNode();

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



	private void setWaitTime(MIState.State state) {
		maxWaitTime = 150;
		minWaitTime = 50;

		// TODO: add wait time for all states
		if(state == MIState.State.Mensa)
		{
			maxWaitTime = 200;
			minWaitTime = 50;
		} else if(state == MIState.State.LectureHall1)
		{
			maxWaitTime = 500;
			minWaitTime = 300;
		}
	}
	//==========================================================================//

}
