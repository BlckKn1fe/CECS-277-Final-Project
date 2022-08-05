package cecs277.passengers.debarking;

import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class AttentiveDebarking implements DebarkingStrategy {
    @Override
    public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
        return passenger.getDestination() == elevator.getCurrentFloor().getNumber();
    }

    @Override
    public void departedElevator(Passenger passenger, Elevator elevator) {
        elevator.removePassenger(passenger);
        elevator.removeObserver(passenger);

        String message = passenger.getName() + " " + passenger.getId()
                + " debarked at their destination floor " + passenger.getDestination() + ".";

        Logger.getInstance().logString(message);

        passenger.scheduleNextTrip(elevator.getCurrentFloor());
        passenger.setState(Passenger.PassengerState.BUSY);
    }
}
