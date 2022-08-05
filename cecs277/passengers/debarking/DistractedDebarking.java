package cecs277.passengers.debarking;

import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.events.PassengerNextDestinationEvent;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class DistractedDebarking implements DebarkingStrategy {
    private boolean willDebark = false;
    private int x = 0;

    // When x = 0: passenger will not leave when arrive at the destination first time.
    // When x = 1: passenger will leave the building when elevator open the door,
    // no matter which floor the elevator arrive.
    // When x = 2: passenger will be on the right track.
    @Override
    public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
        if (x == 1) {
            // When X = 1, the distracted passenger will leave the elevator asap
            return true;
        }
        else if (passenger.getDestination() != elevator.getCurrentFloor().getNumber()) {
            return false;
        }
        else if (passenger.getDestination() == elevator.getCurrentFloor().getNumber() && willDebark) {
            return true;
        }
        else {
            String message = passenger.getName() + " " + passenger.getId() +
                    " is distracted and missed their stop on floor " + elevator.getCurrentFloor().getNumber() +
                    "!";
            Logger.getInstance().logString(message);

            willDebark = true;
            x++;
            return false;
        }
    }

    @Override
    public void departedElevator(Passenger passenger, Elevator elevator) {

        // When willDebark is true, it means this passenger has already miss the destination.
        // When x == 1, it means this passenger will get off after the door reopen
        Simulation sim = elevator.getBuilding().getSimulation();
        if (willDebark && x == 1) {
            // Only removes this distracted passenger from elevator
            elevator.removeObserver(passenger);
            elevator.removePassenger(passenger);
            passenger.setState(Passenger.PassengerState.BUSY);

            // Schedule a reappearance event
            sim = elevator.getBuilding().getSimulation();
            PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(sim.currentTime() + 5,
                    passenger, elevator.getCurrentFloor());
            sim.scheduleEvent(ev);
            // Increment one so that the next time this method is called,
            // this passenger will debark the elevator correctly
            x++;

            String message = passenger.getName() + " " + passenger.getId()
                    + " got off elevator " + elevator.getNumber() + " on the wrong floor!";
            Logger.getInstance().logString(message);

        }
        // When x == 2, the passenger has already gotten on the elevator after he / she gets off the elevator wrongly.
        // So, this time, this passenger will be on the right track.
        else if (willDebark && x == 2) {
            elevator.removePassenger(passenger);
            elevator.removeObserver(passenger);
            passenger.scheduleNextTrip(elevator.getCurrentFloor());
            passenger.setState(Passenger.PassengerState.BUSY);

            String message = passenger.getName() + " " + passenger.getId()
                            + " finally debarked at their destination floor "
                            + elevator.getCurrentFloor().getNumber() + ".";
            Logger.getInstance().logString(message);
        }
    }
}
