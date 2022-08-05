package cecs277.passengers.travel;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.events.PassengerNextDestinationEvent;
import cecs277.passengers.Passenger;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MultipleDestinationTravel implements TravelStrategy {

    private ArrayList<Integer> mDestination = new ArrayList<>();
    private ArrayList<Long> mDuration = new ArrayList<>();

    public MultipleDestinationTravel(ArrayList<Integer> destination, ArrayList<Long> duration) {
        mDestination = destination;
        mDuration = duration;
    }

    @Override
    public int getDestination() {
        return mDestination.get(0);
    }

    @Override
    public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {
        if (currentFloor.getNumber() != 1) {
            mDestination.remove(0);
            Simulation sim = currentFloor.getBuilding().getSimulation();

            PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(sim.currentTime()
                    + mDuration.get(0), passenger, currentFloor);
            sim.scheduleEvent(ev);

            mDuration.remove(0);
            passenger.setState(Passenger.PassengerState.BUSY);

            if (mDestination.isEmpty()) {
                mDestination.add(1);
            }
        }
        else {
//            System.out.println(passenger.getShortName() + passenger.getId() + " is leaving now!");
        }
    }
}
