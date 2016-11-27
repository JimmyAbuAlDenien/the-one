package movement;

import core.Coord;
import core.Settings;
import core.SettingsError;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.MapRoute;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by jimmy on 11/27/16.
 */
public class LocationBiasMapRouteMovement extends MapBasedMovement implements
        SwitchableMovement {

    /**
     * Per node group setting used for selecting a route file ({@value})
     */
    public static final String ROUTE_FILE_S = "routeFile";
    /**
     * Per node group setting used for selecting a route's type ({@value}).
     * Integer value from {@link MapRoute} class.
     */
    public static final String ROUTE_TYPE_S = "routeType";

    /**
     * Per node group setting for selecting which stop (counting from 0 from
     * the start of the route) should be the first one. By default, or if a
     * negative value is given, a random stop is selected.
     */
    public static final String ROUTE_FIRST_STOP_S = "routeFirstStop";

    /**
     * the Dijkstra shortest path finder
     */
    private DijkstraPathFinder pathFinder;

    /**
     * Prototype's reference to all routes read for the group
     */
    private List<MapRoute> allRoutes = null;
    /**
     * next route's index to give by prototype
     */
    private Integer nextRouteIndex = null;
    /**
     * Index of the first stop for a group of nodes (or -1 for random)
     */
    private int firstStopIndex = -1;

    /**
     * Route of the movement model's instance
     */
    private MapRoute route;

    /**
     * Prototype's reference to all routes read for the group
     */
    private LinkedList<MapNode> allLocationBias = new LinkedList<>();

    /**
     * Creates a new movement model based on a Settings object's settings.
     *
     * @param settings The Settings object where the settings are read from
     */
    public LocationBiasMapRouteMovement(Settings settings) {
        super(settings);
        String fileName = settings.getSetting(ROUTE_FILE_S);
        int type = settings.getInt(ROUTE_TYPE_S);
        allRoutes = MapRoute.readRoutes(fileName, type, getMap());
        nextRouteIndex = 0;
        pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
        this.route = this.allRoutes.get(this.nextRouteIndex).replicate();
        if (this.nextRouteIndex >= this.allRoutes.size()) {
            this.nextRouteIndex = 0;
        }

        if (settings.contains(ROUTE_FIRST_STOP_S)) {
            this.firstStopIndex = settings.getInt(ROUTE_FIRST_STOP_S);
            if (this.firstStopIndex >= this.route.getNrofStops()) {
                throw new SettingsError("Too high first stop's index (" +
                        this.firstStopIndex + ") for route with only " +
                        this.route.getNrofStops() + " stops");
            }
        }
        this.allLocationBias = initiatePOIs();
        System.out.println("Methode Call: " + initiatePOIs().size() + " allLocationBias: " + allLocationBias.size());
    }

    /**
     * Copyconstructor. Gives a route to the new movement model from the
     * list of routes and randomizes the starting position.
     *
     * @param proto The LocationBiasMapRouteMovement prototype
     */
    protected LocationBiasMapRouteMovement(LocationBiasMapRouteMovement proto) {
        super(proto);
        this.route = proto.allRoutes.get(proto.nextRouteIndex).replicate();
        this.firstStopIndex = proto.firstStopIndex;

        if (firstStopIndex < 0) {
            /* set a random starting position on the route */
            this.route.setNextIndex(rng.nextInt(route.getNrofStops() - 1));
        } else {
			/* use the one defined in the config file */
            this.route.setNextIndex(this.firstStopIndex);
        }

        this.pathFinder = proto.pathFinder;

        proto.nextRouteIndex++; // give routes in order
        if (proto.nextRouteIndex >= proto.allRoutes.size()) {
            proto.nextRouteIndex = 0;
        }
    }

    @Override
    public Path getPath() {
        Path p = new Path(generateSpeed());
        //MapNode to = route.nextStop();

        MapNode to = new MapNode(new Coord(1, 1));
        Random rn = new Random();
        int ranDom = rn.nextInt(100);

        System.out.println("Coordinate: " + allLocationBias.size());
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

    /**
     * Returns the first stop on the route
     */
    @Override
    public Coord getInitialLocation() {
        if (lastMapNode == null) {
            lastMapNode = route.nextStop();
        }

        return lastMapNode.getLocation().clone();
    }

    @Override
    public Coord getLastLocation() {
        if (lastMapNode != null) {
            return lastMapNode.getLocation().clone();
        } else {
            return null;
        }
    }


    @Override
    public LocationBiasMapRouteMovement replicate() {
        return new LocationBiasMapRouteMovement(this);
    }

    /**
     * Returns the list of stops on the route
     *
     * @return The list of stops
     */
    public List<MapNode> getStops() {
        return route.getStops();
    }


    public LinkedList<MapNode> initiatePOIs() {

        LinkedList<MapNode> listPoi = new LinkedList<>();

        listPoi.add(new MapNode(new Coord(100, -100)));
        listPoi.add(new MapNode(new Coord(600, 100)));
        listPoi.add(new MapNode(new Coord(350, -533)));
        //listPoi.add(new MapNode(new Coord(4, 4)));
        //listPoi.add(new MapNode(new Coord(5, 5)));
        //int[] probabilities = {25, 15, 35, 5, 20};
        int[] probabilities = {10, 70, 20};
        LinkedList<MapNode> finalPoiList = new LinkedList<MapNode>();

        int poi = 0;
        int probIndex = 0;


        int start = 0;
        int end = probabilities[probIndex];


        while (start <= end && start < 100 && poi < listPoi.size()) {
            if (start == end) {
                poi++;
                probIndex++;
                start = 0;
                for (int x = 0; x < probIndex; x++) {
                    start += probabilities[x];
                }
                end = start + probabilities[probIndex];
            }

            finalPoiList.add(new MapNode(new Coord(350, -533)));
            start++;


        }


        return finalPoiList;
    }
}
