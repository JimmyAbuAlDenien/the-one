/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import core.Settings;
import movement.model.BachelorStudentState;
import movement.model.MINodeState;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class BachelorStudentsMapBasedMovement extends MIGroupMapBasedMovement {

	public BachelorStudentsMapBasedMovement(Settings settings) {
		super(settings);
	}

	protected BachelorStudentsMapBasedMovement(BachelorStudentsMapBasedMovement mbm) {
		super(mbm);
		state = new BachelorStudentState(MINodeState.State.Entrance);
	}

	@Override
	public BachelorStudentsMapBasedMovement replicate() {
		return new BachelorStudentsMapBasedMovement(this);
	}

	//==========================================================================//

}
