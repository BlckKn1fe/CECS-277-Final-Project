package cecs277.passengers.debarking;

import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class ConfusedDebarking implements DebarkingStrategy {
    @Override
    public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
        return elevator.getCurrentFloor().getNumber() == 1;
    }

    @Override
    public void departedElevator(Passenger passenger, Elevator elevator) {
        elevator.removePassenger(passenger);
        elevator.removeObserver(passenger);
        passenger.setState(Passenger.PassengerState.BUSY);

        String message = passenger.getName() + " " + passenger.getId()
                + " is confused and left the building after debarking elevator "
                + elevator.getNumber() + ".";

        Logger.getInstance().logString(message);

    }
}
