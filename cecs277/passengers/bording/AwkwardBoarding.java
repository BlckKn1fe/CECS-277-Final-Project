package cecs277.passengers.bording;

import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class AwkwardBoarding implements BoardingStrategy {
    private int mThreshold;

    public AwkwardBoarding(int threshold) {
        mThreshold = threshold;
    }

    @Override
    public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
        if (elevator.getPassengerCount() > mThreshold) {
            mThreshold += 2;

            String message = passenger.getName() + " " + passenger.getId()
                            + " was too awkward to board the elevator on floor " + elevator.getNumber()
                            + ", now has threshold " + mThreshold + ".";
            Logger.getInstance().logString(message);

            int passengerDestination = passenger.getDestination();
            Elevator.Direction passengerDirection;
            if (passengerDestination > elevator.getCurrentFloor().getNumber()) {
                passengerDirection = Elevator.Direction.MOVING_UP;
            }
            else {
                passengerDirection = Elevator.Direction.MOVING_DOWN;
            }
            elevator.getCurrentFloor().requestDirection(passengerDirection);

            return false;
        }
        else {
            return true;
        }
    }
}
