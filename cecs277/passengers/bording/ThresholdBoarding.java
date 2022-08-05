package cecs277.passengers.bording;

import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class ThresholdBoarding implements BoardingStrategy {
    private int mThreshold;

    public ThresholdBoarding(int threshold) { mThreshold = threshold; }

    @Override
    public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
        if (elevator.getPassengerCount() > mThreshold) {
            Elevator.Direction passengerDirection;
            int passengerDestination = passenger.getDestination();

            if (passengerDestination > elevator.getCurrentFloor().getNumber()) {
                passengerDirection = Elevator.Direction.MOVING_UP;
            }
            else {
                passengerDirection = Elevator.Direction.MOVING_DOWN;
            }
            // When a passenger won't get on a elevation going the same direction as the passenger expect,
            // this passenger will request direction one more time. If this passenger does not do this,
            // The up button or down button will not be pressed.
            elevator.getCurrentFloor().requestDirection(passengerDirection);

            String message = passenger.getName() + " " + passenger.getId()
                    + " won't board elevator " + elevator.getNumber() + " on floor "
                    + elevator.getCurrentFloor().getNumber() + " because it is above their threshold of "
                    + mThreshold + ".";
            Logger.getInstance().logString(message);

        }

        return elevator.getPassengerCount() <= mThreshold;
    }
}


