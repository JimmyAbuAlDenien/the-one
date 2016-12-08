package movement.model;

import core.Coord;
import movement.map.MapNode;
import util.Tuple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by jimmy on 12/6/16.
 */
public class StaffState {

    public static List<MapNode> allNodes;

    public static List<Tuple<State, Integer>> stateCapacity = new ArrayList<>();

    public State currentState;

    // From entrance
    public int[] startProbability;

    // Lunch and didnt reach our visits threshold
    public int[] lunchProbability;

    // After or Before lunch and DIDNT reached visits threshold
    public int[] notDoneVisitingProbability;

    // After or Before lunch and reached visits threshold
    public int[] doneVisitingProbability;

    // Lunch time and reached visits threshold
    public int[] doneVisitingLunchProbability;

    // Current coordinates
    public Coord coord;

    public double maxWaitTime = 50;
    public double minWaitTime = 0;

    public int maxCapacity = 0;

    public StaffState(State state, int[] startProbability, int[] lunchProbability,
                      int[] doneVisitingProbability, int[] notDoneVisitingProbability, Coord coordinates,
                      int maxCapacity, double minWaitTime, double maxWaitTime ) {

        this.currentState = state;
        this.notDoneVisitingProbability = notDoneVisitingProbability;
        this.lunchProbability = lunchProbability;
        this.doneVisitingProbability = doneVisitingProbability;
        this.startProbability = startProbability;

        this.coord = coordinates;
        this.maxCapacity = maxCapacity;
        this.maxWaitTime = maxWaitTime;
        this.minWaitTime = minWaitTime;
    }

    public StaffState getNextState(boolean isLunch, boolean isVisited) {
        int[] probs = startProbability;

        if(isVisited && isLunch) {
            probs = doneVisitingLunchProbability;
        } else if(isLunch) {
            probs = lunchProbability;
        } else if(isVisited) {
            probs = doneVisitingProbability;
        }

        probs = removeFullStates(probs);

        Random rand = new Random();
        int index = rand.nextInt(100);
        int sum = 0;

        for (int i=0; i<probs.length; i++) {
            sum += probs[i];

            if (sum >= index) {
                decreaseStateCapacity(this.currentState);
                increaseStateCapacity(getSystemStates().get(i).currentState);
                return getSystemStates().get(i);
            }
        }

        return null;
    }

    private void increaseStateCapacity(StaffState.State state) {
        Boolean found = false;

        for (Tuple<StaffState.State, Integer> updatedState : stateCapacity) {
            if(updatedState.getKey() == state) {
                Tuple<StaffState.State, Integer> newState = new Tuple<StaffState.State, Integer>(state, updatedState.getValue() + 1);
                stateCapacity.remove(updatedState);
                stateCapacity.add(newState);
                found = true;
                break;
            }
        }

        if(!found) {
            stateCapacity.add(new Tuple<StaffState.State, Integer>(state, 1));
        }
    }

    private void decreaseStateCapacity(StaffState.State state) {
        for (Tuple<StaffState.State, Integer> updatedState : stateCapacity) {
            if(updatedState.getKey() == state) {
                Integer val = (updatedState.getValue() > 0)? updatedState.getValue()-1 : 0;
                Tuple<StaffState.State, Integer> newState = new Tuple<StaffState.State, Integer>(state, val);
                stateCapacity.remove(updatedState);
                stateCapacity.add(newState);
                break;
            }
        }
    }

    private int[] removeFullStates(int[] probs) {
        int[] updatedProbs = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        LinkedList<StaffState> allStates = getSystemStates();

        int emptyStatesCount = 0;
        int totalToDistribute = 0;
        int amountToDistribute = 0;

        for (int i=0; i < allStates.size(); i++) {
            StaffState stateToCheck = allStates.get(i);

            if(getStateCapacity(stateToCheck.currentState) < stateToCheck.maxCapacity) {
                updatedProbs[i] = probs[i];
                emptyStatesCount ++;
            } else {
                totalToDistribute += probs[i];
            }
        }

        if(totalToDistribute > 0 && emptyStatesCount > 0) {
            amountToDistribute = totalToDistribute / emptyStatesCount;

            for (int i=0; i < updatedProbs.length; i++) {
                if(updatedProbs[i] > 0) {
                    updatedProbs[i] = updatedProbs[i] + amountToDistribute;
                }
            }
        }

        return updatedProbs;
    }

    private Integer getStateCapacity(StaffState.State state) {
        for (Tuple<StaffState.State, Integer> updatedState : stateCapacity) {
            if(updatedState.getKey() == state) {
                return updatedState.getValue();
            }
        }
        return 0;
    }

    public MapNode getMapNode() {
        if(currentState == null) {
            System.out.print("State is null");
            return null;
        }

        for (MapNode destination : allNodes) {
            if(destination.getLocation().toString().equals(this.coord.toString())){
                return destination;
            }
        }
        return null;
    }

    public static StaffState getStateByName(State state) {
        for (StaffState systemState : getSystemStates()) {
            if (systemState.currentState == state) {
                return systemState;
            }
        }

        return null;
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

    // TODO: to accomplish location bais, each state should have the same probs
    public static LinkedList<StaffState> getSystemStates() {
        LinkedList<StaffState> systemStates = new LinkedList<>();

        int[] case1 = {6,6,6,4,4,4,4,4,3,3,3,40,0,5,5};
        int[] case2 = {8,8,8,6,6,6,6,6,5,5,5,10,0,10,10};
        int[] case3 = {5,5,5,3,3,3,3,3,2,2,2,50,10,3,3};
        int[] case4 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState lectureHall1 = new StaffState(StaffState.State.LectureHall1, case1, case2,case3, case4, new Coord(987.70,343.45), 200, 500, 500);
        systemStates.add(lectureHall1);

        int[] case5 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case6 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case7 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case8 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState lectureHall2 = new StaffState(StaffState.State.LectureHall2, case5, case6,case7, case8, new Coord(707.16,447.99), 150, 500, 500);
        systemStates.add(lectureHall2);

        int[] case9 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case10 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case11 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case12 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState lectureHall3 = new StaffState(StaffState.State.LectureHall3, case9, case10,case11, case12, new Coord(860.44,495.73), 100, 500, 500);
        systemStates.add(lectureHall3);

        int[] case13 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case14 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case15 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case16 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState seminarHall1 = new StaffState(StaffState.State.SeminarHall1, case13, case14,case15, case16, new Coord(120.56,0.00), 50, 500, 500);
        systemStates.add(seminarHall1);

        int[] case17 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case18 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case19 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case20 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState seminarHall2 = new StaffState(StaffState.State.SeminarHall2, case17, case18,case19, case20, new Coord(637.79,511.05), 50, 500, 500);
        systemStates.add(seminarHall2);

        int[] case21 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case22 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case23 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case24 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState seminarHall3 = new StaffState(StaffState.State.SeminarHall3, case21, case22,case23, case24, new Coord(481.63,193.80), 50, 500, 500);
        systemStates.add(seminarHall3);

        int[] case25 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case26 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case27 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case28 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState seminarHall4 = new StaffState(StaffState.State.SeminarHall4, case25, case26,case27, case28, new Coord(278.89,157.64), 50, 500, 500);
        systemStates.add(seminarHall4);

        int[] case29 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case30 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case31 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case32 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState seminarHall5 = new StaffState(StaffState.State.SeminarHall5, case29, case30,case31, case32, new Coord(149.85,135.00), 50, 500, 500);
        systemStates.add(seminarHall5);

        int[] case33 = {2,2,2,2,2,2,2,2,5,5,5,40,20,5,5};
        int[] case34 = {6,6,6,4,4,4,4,4,2,2,2,5,20,15,15};
        int[] case35 = {3,3,3,2,2,2,2,2,7,7,7,30,25,3,3};
        int[] case36 = {5,5,5,3,3,3,3,3,5,5,5,10,15,15,15};

        StaffState mainHall1 = new StaffState(StaffState.State.MainHall1, case33, case34,case35, case36, new Coord(814.56,368.49), 100, 0, 500);
        systemStates.add(mainHall1);

        int[] case37 = {3,3,3,2,2,2,2,2,10,10,10,35,5,5,5};
        int[] case38 = {5,5,5,4,4,4,4,4,8,8,8,15,5,10,10};
        int[] case39 = {5,5,5,3,3,3,3,3,0,0,0,30,20,10,10};
        int[] case40 = {5,5,5,3,3,3,3,3,0,0,0,10,30,15,15};

        StaffState mainHall2 = new StaffState(StaffState.State.MainHall2, case37, case38,case39, case40, new Coord(453.13,278.73), 100, 0, 500);
        systemStates.add(mainHall2);

        int[] case41 = {3,3,3,2,2,2,2,2,10,10,10,35,5,5,5};
        int[] case42 = {5,5,5,4,4,4,4,4,8,8,8,15,5,10,10};
        int[] case43 = {5,5,5,3,3,3,3,3,0,0,0,30,20,10,10};
        int[] case44 = {5,5,5,3,3,3,3,3,0,0,0,10,30,15,15};

        StaffState mainHall3 = new StaffState(StaffState.State.MainHall3, case41, case42,case43, case44, new Coord(257.52,223.94), 100, 0, 500);
        systemStates.add(mainHall2);

        int[] case45 = {3,3,3,2,2,2,2,2,10,10,10,35,5,5,5};
        int[] case46 = {5,5,5,4,4,4,4,4,8,8,8,15,5,10,10};
        int[] case47 = {5,5,5,3,3,3,3,3,0,0,0,30,20,10,10};
        int[] case48 = {5,5,5,3,3,3,3,3,0,0,0,10,30,15,15};

        StaffState mensa = new StaffState(StaffState.State.Mensa, case45, case46,case47, case48, new Coord(548.61,393.59), 100, 300, 600);
        systemStates.add(mensa);

        int[] case49 = {5,5,5,4,4,4,4,4,17,17,17,0,15,0,0};
        int[] case50 = {6,6,6,4,4,4,4,4,8,8,8,0,15,10,10};
        int[] case51 = {2,2,2,1,1,1,1,1,7,7,7,0,50,10,10};
        int[] case52 = {1,1,1,1,1,1,1,1,2,2,2,5,70,8,8};

        StaffState entrance = new StaffState(StaffState.State.Enterance, case49, case50,case51, case52, new Coord(881.02,216.67), 10000000, 0, 0);
        systemStates.add(entrance);

        int[] case53 = {5,5,5,3,3,3,3,3,10,10,10,40,0,0,0};
        int[] case54 = {11,11,11,7,7,7,7,7,7,7,7,5,5,0,0};
        int[] case55 = {3,3,3,2,2,2,2,2,5,5,5,40,25,0,0};
        int[] case56 = {1,1,1,1,1,1,1,1,2,2,2,5,70,8,8};

        StaffState computerHall = new StaffState(StaffState.State.ComputerHall, case53, case54,case55, case56, new Coord(667.17,208.08), 200, 100, 600);
        systemStates.add(computerHall);

        int[] case57 = {5,5,5,3,3,3,3,3,10,10,10,40,0,0,0};
        int[] case58 = {11,11,11,7,7,7,7,7,7,7,7,5,5,0,0};
        int[] case59 = {3,3,3,2,2,2,2,2,5,5,5,40,25,0,0};
        int[] case60 = {1,1,1,1,1,1,1,1,2,2,2,5,70,8,8};

        StaffState library = new StaffState(StaffState.State.Library, case57, case58,case59, case60, new Coord(147.56,291.82), 300, 300, 600);
        systemStates.add(library);

        return systemStates;
    }
}
