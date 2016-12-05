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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static movement.StatefulShortestPathMapBasedMovement.State.*;

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
	public static List<Tuple<State, Integer>> stateCapacity = new ArrayList<>();
	private List<State> hostHistory = new ArrayList<>();
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

			case Mensa: {
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
//		} else if(state == State.Mensa) {
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
	private State updateState( final State state )
	{
		// TODO: Add time as parameter
		// TODO: Set wait time
		// TODO: Record node history

		if(state == State.Enterance)
		{
			//The node has not been in any other place before
			if(hostHistory.size() == 0)
				{
					//It is lunch Time
					if (SimClock.getTime()>700 && SimClock.getTime()<900)
					{
						return getProbabilitiesList("case1");
					}
					//It is not Lunch Time
					else
					{
						return getProbabilitiesList("case2");
					}

				}
			else
				{
					//It is lunch Time
					if (SimClock.getTime()>700 && SimClock.getTime()<900)
					{
						return getProbabilitiesList("case3");
					}
					//It is not Lunch Time
					else
					{
						return getProbabilitiesList("case4");
					}
				}
		} else if(state == LectureHall1)
				{
					if(hostHistory.size() == 0)
					{
						//It is lunch Time
						if (SimClock.getTime()>700 && SimClock.getTime()<900)
						{
							return getProbabilitiesList("case5");
						}
						//It is not Lunch Time
						else
						{
							return getProbabilitiesList("case6");
						}
					}
					else
					{
						//It is lunch Time
						if (SimClock.getTime()>700 && SimClock.getTime()<900)
						{
							return getProbabilitiesList("case7");
						}
						//It is not Lunch Time
						else
						{
							return getProbabilitiesList("case8");
						}

					}
		}

		if(state == State.LectureHall2)
		{
			//The node has not been in any other place before
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case9");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case10");
				}

			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case11");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case12");
				}
			}
		} else if(state == LectureHall3)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case13");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case14");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case15");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case16");
				}

			}
		}

		else if(state == SeminarHall1)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case17");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case18");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case19");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case20");
				}

			}
		}

		else if(state == SeminarHall2)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case21");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case22");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case23");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case24");
				}

			}
		}

		else if(state == SeminarHall3)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case25");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case26");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case27");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case28");
				}

			}
		}

		else if(state == SeminarHall4)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case29");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case30");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case31");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case32");
				}

			}
		}

		else if(state == SeminarHall5)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case33");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case34");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case35");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case36");
				}

			}
		}


		else if(state == MainHall1)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case37");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case38");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case39");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case40");
				}

			}
		}

		else if(state == MainHall2)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case41");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case42");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case43");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case44");
				}

			}
		}

		else if(state == MainHall3)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case45");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case46");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case47");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case48");
				}

			}
		}

		else if(state == Mensa)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case49");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case50");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case51");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case52");
				}

			}
		}


		else if(state == ComputerHall)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case53");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case54");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case55");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case56");
				}

			}
		}

		else if(state == ComputerHall)
		{
			if(hostHistory.size() == 0)
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case57");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case58");
				}
			}
			else
			{
				//It is lunch Time
				if (SimClock.getTime()>700 && SimClock.getTime()<900)
				{
					return getProbabilitiesList("case59");
				}
				//It is not Lunch Time
				else
				{
					return getProbabilitiesList("case60");
				}

			}
		}

		if(hostHistory.size() >= 3 && state != State.Mensa) {
			decreaseStateCapacity(state);
			return State.Enterance;
		} else {
			List<Tuple<State, Double>> loc1Probs = new ArrayList<>();
			loc1Probs.add(new Tuple<State, Double>(LectureHall1, 7.14));
			loc1Probs.add(new Tuple<State, Double>(LectureHall2, 7.14));
			loc1Probs.add(new Tuple<State, Double>(LectureHall3, 7.14));

			loc1Probs.add(new Tuple<State, Double>(State.SeminarHall1, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.SeminarHall2, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.SeminarHall3, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.SeminarHall4, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.SeminarHall5, 7.14));

			loc1Probs.add(new Tuple<State, Double>(State.MainHall1, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.MainHall2, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.MainHall3, 7.14));

			loc1Probs.add(new Tuple<State, Double>(Library, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.ComputerHall, 7.14));
			loc1Probs.add(new Tuple<State, Double>(State.Mensa, 7.14));

			Random rand = new Random();
			int index = rand.nextInt(100);
			int sum = 0;

			for (Tuple<State, Double> t : loc1Probs) {
				sum += t.getValue();

				if(sum >= index) {

					System.out.println(getStateCapacity(t.getKey()) + " " + t.getKey());

					if(getStateCapacity(t.getKey()) > 2) {
						System.out.println("Room IS FULLL!!!!! " + t.getKey() + " Wait in your state! dont move!");
						return state;
					} else {
						decreaseStateCapacity(state);
						increaseStateCapacity(t.getKey());
						System.out.println(getStateCapacity(t.getKey()) + " " + t.getKey());

						// Track its history
						hostHistory.add(t.getKey());
					}

					return t.getKey();
				}
			}
		}

		return getProbabilitiesList("none");
	}

	public State getProbabilitiesList(String escenario)
	{

		//Array that will contain all the possible states for the state machine
		LinkedList<State> states = new LinkedList<>();
		states.add(LectureHall1);
		states.add(LectureHall2);
		states.add(LectureHall3);
		states.add(SeminarHall1);
		states.add(SeminarHall2);
		states.add(SeminarHall3);
		states.add(SeminarHall4);
		states.add(SeminarHall5);
		states.add(MainHall1);
		states.add(MainHall2);
		states.add(MainHall3);
		states.add(Mensa);
		states.add(Enterance);
		states.add(ComputerHall);
		states.add(Library);
		//Array with the probability for each one of the states. It is in the same order
		//than the states list e.g. LectureHall1 = 10.
		int[] case1 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case2 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case3 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case4 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case5 = {0,2,0,0,0,0,8,6,15,10,13,35,0,5,6};
		int[] case6 = {20,5,8,13,8,10,8,6,2,2,1,2,10,1,4};
		int[] case7 = {5,0,0,0,0,0,6,6,10,10,10,29,19,1,4};
		int[] case8 = {9,0,0,5,2,7,4,5,6,5,5,15,20,7,10};
		int[] case9 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case10 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case11 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case12 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case13 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case14 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case15 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case16 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case17 = {0,2,0,0,0,0,8,6,15,10,13,35,0,5,6};
		int[] case18 = {20,5,8,13,8,10,8,6,2,2,1,2,10,1,4};
		int[] case19 = {5,0,0,0,0,0,6,6,10,10,10,29,19,1,4};
		int[] case20 = {9,0,0,5,2,7,4,5,6,5,5,15,20,7,10};
		int[] case21 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case22 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case23 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case24 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case25 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case26 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case27 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case28 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case29 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case30 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case31 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case32 = {0,2,0,0,0,0,8,6,15,10,13,35,0,5,6};
		int[] case33 = {20,5,8,13,8,10,8,6,2,2,1,2,10,1,4};
		int[] case34 = {5,0,0,0,0,0,6,6,10,10,10,29,19,1,4};
		int[] case35 = {9,0,0,5,2,7,4,5,6,5,5,15,20,7,10};
		int[] case36 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case37 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case38 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case39 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case40 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case41 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case42 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case43 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case44 = {0,2,0,0,0,0,8,6,15,10,13,35,0,5,6};
		int[] case45 = {20,5,8,13,8,10,8,6,2,2,1,2,10,1,4};
		int[] case46 = {5,0,0,0,0,0,6,6,10,10,10,29,19,1,4};
		int[] case47 = {9,0,0,5,2,7,4,5,6,5,5,15,20,7,10};
		int[] case48 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case49 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case50 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case51 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case52 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case53 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case54 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case55 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};
		int[] case56 = {0,2,0,0,0,0,8,6,15,10,13,35,0,5,6};
		int[] case57 = {20,5,8,13,8,10,8,6,2,2,1,2,10,1,4};
		int[] case58 = {5,0,0,0,0,0,6,6,10,10,10,29,19,1,4};
		int[] case59 = {9,0,0,5,2,7,4,5,6,5,5,15,20,7,10};
		int[] case60 = {0,7,0,0,0,0,8,6,15,10,8,35,0,5,6};
		int[] case61 = {20,15,8,13,8,10,8,6,2,2,1,2,0,1,4};
		int[] case62 = {5,7,8,6,4,5,8,6,8,5,6,12,15,1,4};
		int[] case63 = {9,8,10,5,2,7,4,5,6,5,5,3,14,7,10};



		//final state list
		LinkedList <State> result = new LinkedList<State>();

		//-----------------------------------------------------------
		//Case1: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
			if (escenario.equals("case1"))
			{
				//System.out.println("Size: "+states.size());
				for (int i=0;i<states.size();i++)
				{
					for (int j=0;j<case1[i];j++)
					{
						result.add(states.get(i));
					}
				}
				Random rn = new Random();
				int ranDom = rn.nextInt(100);
				return result.get(ranDom);
			}

		//-----------------------------------------------------------
		//Case2: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case2"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case2[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case3: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case3"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case3[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case4: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case4"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case4[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case5: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case5"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case5[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case6: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case6"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case6[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case7: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case7"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case7[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case8: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case8"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case8[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case9: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case9"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case9[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case10: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case10"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case10[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case11: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case11"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case11[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case12: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case12"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case12[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case13: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case13"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case13[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case14: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case14"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case14[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case15: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case15"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case15[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case16: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case16"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case16[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case17: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case17"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case17[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case18: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case18"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case18[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case19: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case19"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case19[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case20: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case20"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case20[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case21: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case21"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case21[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case22: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case22"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case22[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case23: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case23"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case23[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case24: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case24"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case24[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case25: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case25"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case25[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case26: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case26"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case26[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case27: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case27"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case27[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case28: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case28"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case28[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case29: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case29"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case29[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case30: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case30"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case30[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case31: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case31"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case31[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case32: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case32"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case32[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case33: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case33"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case33[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case34: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case34"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case34[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case35: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case35"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case35[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case36: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case36"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case36[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case37: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case37"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case37[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case38: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case38"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case38[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case39: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case39"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case39[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case40: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case40"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case40[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case41: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case41"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case41[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case42: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case42"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case42[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case43: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case43"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case43[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case44: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case44"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case44[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case45: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case45"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case45[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case46: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case46"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case46[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case47: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case47"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case47[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case48: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case48"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case48[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case49: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case49"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case49[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case50: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case50"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case50[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case51: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case51"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case51[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case52: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case52"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case52[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case53: Initial State = LectureHall | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case53"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case53[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case54: Initial State = LectureHall | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case54"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case54[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case55: Initial State = LectureHall | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case55"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case55[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case56: Initial State = LectureHall | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case56"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case56[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case57: Initial State = Entrance | Historic: NO | Lunch Time
		//-----------------------------------------------------------
		if (escenario.equals("case57"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case57[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case58: Initial State = Entrance | Historic: NO | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case58"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case58[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}


		//-----------------------------------------------------------
		//Case59: Initial State = Entrance | Historic: YES | Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case59"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case59[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}

		//-----------------------------------------------------------
		//Case60: Initial State = Entrance | Historic: YES | NO Lunch Time
		//-----------------------------------------------------------

		if (escenario.equals("case60"))
		{
			//System.out.println("Size: "+states.size());
			for (int i=0;i<states.size();i++)
			{
				for (int j=0;j<case60[i];j++)
				{
					result.add(states.get(i));
				}
			}
			Random rn = new Random();
			int ranDom = rn.nextInt(100);
			return result.get(ranDom);
		}



		return null;
	}


	private void increaseStateCapacity(State state) {
		Boolean found = false;

		for (Tuple<State, Integer> updatedState : stateCapacity) {
			if(updatedState.getKey() == state) {
				Tuple<State, Integer> newState = new Tuple<State, Integer>(state, updatedState.getValue() + 1);
				stateCapacity.remove(updatedState);
				stateCapacity.add(newState);
				found = true;
				break;
			}
		}

		if(!found) {
			stateCapacity.add(new Tuple<State, Integer>(state, 1));
		}
	}

	private void decreaseStateCapacity(State state) {
		for (Tuple<State, Integer> updatedState : stateCapacity) {
			if(updatedState.getKey() == state) {
				Integer val = (updatedState.getValue() > 0)? updatedState.getValue()-1 : 0;
				Tuple<State, Integer> newState = new Tuple<State, Integer>(state, val);
				stateCapacity.remove(updatedState);
				stateCapacity.add(newState);
				break;
			}
		}
	}

	private Integer getStateCapacity(State state) {
		for (Tuple<State, Integer> updatedState : stateCapacity) {
			if(updatedState.getKey() == state) {
				return updatedState.getValue();
			}
		}
		return 0;
	}

	private void setWaitTime(State state) {
		maxWaitTime = 150;
		minWaitTime = 50;

		// TODO: add wait time for all states
		if(state == State.Mensa)
		{
			maxWaitTime = 200;
			minWaitTime = 50;
		} else if(state == LectureHall1)
		{
			maxWaitTime = 500;
			minWaitTime = 300;
		}
	}

	public static enum State
	{
		LectureHall1, LectureHall2, LectureHall3,
		SeminarHall1, SeminarHall2, SeminarHall3, SeminarHall4, SeminarHall5,
		MainHall1, MainHall2, MainHall3,
		Mensa,
		Enterance,
		ComputerHall,
		Library
	}
	//==========================================================================//

}
