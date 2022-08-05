package cecs277.passengers.travel;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.events.PassengerNextDestinationEvent;
import cecs277.passengers.Passenger;

public class SingleDestinationTravel implements TravelStrategy{
    private int mDestination;
    private long mDuration;

    public SingleDestinationTravel(int destination, long duration) {
        mDestination = destination;
        mDuration = duration;
    }

    @Override
    public int getDestination() {
        return mDestination;
    }

    @Override
    public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {

        if (getDestination() != 1) {
            mDestination = 1;
            Simulation s = currentFloor.getBuilding().getSimulation();
            PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() +
                    mDuration, passenger, currentFloor);
            s.scheduleEvent(ev);
        }
        else {
//            System.out.println(passenger.getShortName() + passenger.getId() + " is leaving!");
//            passenger.setState(Passenger.PassengerState.BUSY);
        }

    }
}
