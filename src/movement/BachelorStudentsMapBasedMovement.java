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
import movement.model.BachelorStudentState;
import movement.model.MINodeState;
import movement.model.MasterStudentState;

import java.util.ArrayList;
import java.util.List;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class BachelorStudentsMapBasedMovement extends MapBasedMovement implements
		SwitchableMovement {
	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;

	/** Points Of Interest handler */
	private PointsOfInterest pois;

	private MINodeState state;

	private List<MINodeState.State> hostHistory = new ArrayList<>();
	//==========================================================================//

	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public BachelorStudentsMapBasedMovement(Settings settings) {
		super(settings);
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.pois = new PointsOfInterest(getMap(), getOkMapNodeTypes(),
				settings, rng);

		// Set all nodes
		MasterStudentState.allNodes = getMap().getNodes();
	}

	/**
	 * Copyconstructor.
	 * @param mbm The ShortestPathMapBasedMovement prototype to base
	 * the new object to
	 */
	protected BachelorStudentsMapBasedMovement(BachelorStudentsMapBasedMovement mbm) {
		super(mbm);
		this.pathFinder = mbm.pathFinder;
		this.pois = mbm.pois;
		this.state = new BachelorStudentState(MINodeState.State.Entrance);
	}

	@Override
	public Coord getInitialLocation() {
		lastMapNode = state.getMapNode();
		return lastMapNode.getLocation().clone();
	}

	@Override
	public Path getPath() {
		// Get new state
		MINodeState tmpState = this.state.getNextState((SimClock.getTime()>700 && SimClock.getTime()<900), hostHistory);
		this.state = tmpState != null ? tmpState : this.state;

		setWaitTime(this.state);

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
		hostHistory.add(this.state.currentState);

		return p;
	}

	@Override
	public BachelorStudentsMapBasedMovement replicate() {
		return new BachelorStudentsMapBasedMovement(this);
	}

	private void setWaitTime(MINodeState state) {
		if(state != null) {
			maxWaitTime = state.maxWaitTime;
			minWaitTime = state.minWaitTime;
		}
	}
	//==========================================================================//

}
