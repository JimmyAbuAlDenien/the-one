package movement.model;

import core.Coord;
import movement.map.MapNode;
import util.Tuple;

import java.util.*;

/**
 * Created by jimmy on 12/8/16.
 */
///////////////////////////////////////////////
// Library: 147.56,291.82
// Entrance: 881.02,216.67
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
public class MINodeState {
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

    public MINodeState(State state) {
        MINodeState tmpState = getStateByName(state);

        this.currentState = tmpState.currentState;
        this.notDoneVisitingProbability = tmpState.notDoneVisitingProbability;
        this.lunchProbability = tmpState.lunchProbability;
        this.doneVisitingProbability = tmpState.doneVisitingProbability;
        this.startProbability = tmpState.startProbability;
        this.doneVisitingLunchProbability = tmpState.doneVisitingProbability;

        this.coord = tmpState.coord;
        this.maxCapacity = tmpState.maxCapacity;
        this.maxWaitTime = tmpState.maxWaitTime;
        this.minWaitTime = tmpState.minWaitTime;
    }

    public MINodeState(State state, int[] startProbability, int[] lunchProbability,
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

    public MINodeState getNextState(boolean isLunch, List<MINodeState.State> history) {
        return null;
    }

    protected void increaseStateCapacity(MINodeState.State state) {
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

    protected void decreaseStateCapacity(MINodeState.State state) {
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

    protected int[] removeFullStates(int[] probs) {

        if(probs == null) {
            return new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        }

        int[] updatedProbs = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        LinkedList<MINodeState> allStates = getSystemStates();

        int emptyStatesCount = 0;
        int totalToDistribute = 0;
        int amountToDistribute = 0;

        for (int i=0; i < allStates.size(); i++) {
            MINodeState stateToCheck = allStates.get(i);

            if(getStateCapacity(stateToCheck.currentState) < stateToCheck.maxCapacity) {
//                System.out.println(Arrays.toString(probs));
//                System.out.println(Arrays.toString(updatedProbs));
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

    private Integer getStateCapacity(MINodeState.State state) {
        for (Tuple<State, Integer> updatedState : stateCapacity) {
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

    public MINodeState getStateByName(State state) {
        for (MINodeState systemState : getSystemStates()) {
            if (systemState.currentState == state) {
                return systemState;
            }
        }

        return null;
    }

    public LinkedList<MINodeState> getSystemStates() {
        return null;
    }

    protected MINodeState getRandomState(int[] probs, LinkedList<MINodeState> states) {
        Random rand = new Random();
        int index = rand.nextInt(99) + 1;
        int sum = 0;

        for (int i=0; i<probs.length; i++) {
            sum += probs[i];

            if (sum >= index) {
                System.out.println(this.currentState);
                System.out.println(states.get(i).currentState + " : " + probs[i]);

                decreaseStateCapacity(this.currentState);
                increaseStateCapacity(states.get(i).currentState);

                return states.get(i);
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
        Entrance,
        ComputerHall,
        Library
    }
}
