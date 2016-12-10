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
 * Created by jimmy on 12/10/16.
 */
public class MIGroupMapBasedMovement extends MapBasedMovement implements SwitchableMovement {
    /** the Dijkstra shortest path finder */
    protected DijkstraPathFinder pathFinder;

    /** Points Of Interest handler */
    protected PointsOfInterest pois;

    protected MINodeState state;

    protected List<MINodeState.State> hostHistory = new ArrayList<>();
    //==========================================================================//

    /**
     * Creates a new movement model based on a Settings object's settings.
     * @param settings The Settings object where the settings are read from
     */
    public MIGroupMapBasedMovement(Settings settings) {
        super(settings);
        this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
        this.pois = new PointsOfInterest(getMap(), getOkMapNodeTypes(),
                settings, rng);

        // Set all nodes
        MINodeState.allNodes = getMap().getNodes();
    }

    /**
     * Copyconstructor.
     * @param mbm The ShortestPathMapBasedMovement prototype to base
     * the new object to
     */
    protected MIGroupMapBasedMovement(MIGroupMapBasedMovement mbm) {
        super(mbm);
        this.pathFinder = mbm.pathFinder;
        this.pois = mbm.pois;
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
    public MIGroupMapBasedMovement replicate() {
        return new MIGroupMapBasedMovement(this);
    }

    private void setWaitTime(MINodeState state) {
        if(state != null) {
            maxWaitTime = state.maxWaitTime;
            minWaitTime = state.minWaitTime;
        }
    }
    //==========================================================================//
}
