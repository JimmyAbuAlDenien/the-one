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
import movement.model.MINodeState;
import movement.model.MasterStudentState;
import movement.model.StaffState;

import java.util.ArrayList;
import java.util.List;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class StaffMapBasedMovement extends MIGroupMapBasedMovement {

	public StaffMapBasedMovement(Settings settings) {
		super(settings);
	}

	protected StaffMapBasedMovement(StaffMapBasedMovement mbm) {
		super(mbm);
		state = new StaffState(MINodeState.State.Entrance);
	}

	@Override
	public StaffMapBasedMovement replicate() {
		return new StaffMapBasedMovement(this);
	}

	//==========================================================================//

}
