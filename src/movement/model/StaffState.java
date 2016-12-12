package movement.model;

import core.Coord;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by jimmy on 12/6/16.
 */
public class StaffState extends MINodeState {

    public StaffState(State state) {
        super(state);
    }

    public StaffState(State state, int[] startProbability, int[] lunchProbability,
                      int[] doneVisitingProbability, int[] notDoneVisitingProbability, Coord coordinates,
                      int maxCapacity, double minWaitTime, double maxWaitTime) {

        // To achieve location Bais, all states should have same probs
        super(state, new int[]{0,0,0,20,20,0,60,0,0,0,0,0,0,0,0}, new int[]{0,0,0,20,20,0,60,0,0,0,0,0,0,0,0},
                new int[]{0,0,0,20,20,0,60,0,0,0,0,0,0,0,0},new int[]{0,0,0,20,20,0,60,0,0,0,0,0,0,0,0},
                coordinates,maxCapacity, minWaitTime, maxWaitTime);
    }

    @Override
    public MINodeState getNextState(boolean isLunch, List<State> history) {
        int[] probs = startProbability;

        if(history.size() > 3 && isLunch) {
            probs = doneVisitingLunchProbability;
        } else if(isLunch) {
            probs = lunchProbability;
        } else if(history.size() > 3) {
            probs = doneVisitingProbability;
        }

        probs = removeFullStates(probs);

        return getRandomState(probs, getSystemStates());
    }

    @Override
    public LinkedList<MINodeState> getSystemStates() {
        LinkedList<MINodeState> systemStates = new LinkedList<>();

        MINodeState lectureHall1 = new StaffState(State.LectureHall1, null, null,null, null, new Coord(987.70,343.45), 200, 500, 500);
        systemStates.add(lectureHall1);

        MINodeState lectureHall2 = new StaffState(State.LectureHall2, null, null,null, null, new Coord(707.16,447.99), 150, 500, 500);
        systemStates.add(lectureHall2);

        MINodeState lectureHall3 = new StaffState(State.LectureHall3, null, null,null, null, new Coord(860.44,495.73), 100, 500, 500);
        systemStates.add(lectureHall3);

        MINodeState seminarHall1 = new StaffState(State.SeminarHall1, null, null,null, null, new Coord(120.56,0.00), 50, 500, 500);
        systemStates.add(seminarHall1);

        MINodeState seminarHall2 = new StaffState(State.SeminarHall2, null, null,null, null, new Coord(637.79,511.05), 50, 500, 500);
        systemStates.add(seminarHall2);

        MINodeState seminarHall3 = new StaffState(State.SeminarHall3, null, null,null, null, new Coord(481.63,193.80), 50, 500, 500);
        systemStates.add(seminarHall3);

        MINodeState seminarHall4 = new StaffState(State.SeminarHall4, null, null,null, null, new Coord(278.89,157.64), 50, 500, 500);
        systemStates.add(seminarHall4);

        MINodeState seminarHall5 = new StaffState(State.SeminarHall5, null, null,null, null, new Coord(149.85,135.00), 50, 500, 500);
        systemStates.add(seminarHall5);

        MINodeState mainHall1 = new StaffState(State.MainHall1, null, null,null, null, new Coord(814.56,368.49), 100, 0, 500);
        systemStates.add(mainHall1);

        MINodeState mainHall2 = new StaffState(State.MainHall2, null, null,null, null, new Coord(453.13,278.73), 100, 0, 500);
        systemStates.add(mainHall2);

        MINodeState mainHall3 = new StaffState(State.MainHall3, null, null,null, null, new Coord(257.52,223.94), 100, 0, 500);
        systemStates.add(mainHall3);

        MINodeState mensa = new StaffState(State.Mensa, null, null,null, null, new Coord(548.61,393.59), 100, 300, 600);
        systemStates.add(mensa);

        MINodeState entrance = new StaffState(State.Entrance, null, null,null, null, new Coord(881.02,216.67), 10000000, 0, 0);
        systemStates.add(entrance);

        MINodeState computerHall = new StaffState(State.ComputerHall, null, null,null, null, new Coord(667.17,208.08), 200, 100, 600);
        systemStates.add(computerHall);

        MINodeState library = new StaffState(State.Library, null, null,null, null,new Coord(147.56,291.82), 300, 300, 600);
        systemStates.add(library);

        return systemStates;
    }
}
