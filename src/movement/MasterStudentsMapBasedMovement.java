/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import core.Settings;
import movement.model.MINodeState;
import movement.model.MasterStudentState;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class MasterStudentsMapBasedMovement extends MIGroupMapBasedMovement {

	public MasterStudentsMapBasedMovement(Settings settings) {
		super(settings);
	}

	protected MasterStudentsMapBasedMovement(MasterStudentsMapBasedMovement mbm) {
		super(mbm);
		state = new MasterStudentState(MINodeState.State.Entrance);
	}

	@Override
	public MasterStudentsMapBasedMovement replicate() {
		return new MasterStudentsMapBasedMovement(this);
	}
	//==========================================================================//

}
