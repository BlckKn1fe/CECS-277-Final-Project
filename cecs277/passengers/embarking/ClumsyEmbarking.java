package cecs277.passengers.embarking;

import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class ClumsyEmbarking implements EmbarkingStrategy {
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {
        Elevator.Direction passengerDirection;
        int passengerDestination = passenger.getDestination();

        if (passengerDestination > elevator.getCurrentFloor().getNumber()) {
            passengerDirection = Elevator.Direction.MOVING_UP;
        }
        else {
            passengerDirection = Elevator.Direction.MOVING_DOWN;
        }

        elevator.addPassenger(passenger);
        elevator.getCurrentFloor().removeWaitingPassenger(passenger);
        elevator.getCurrentFloor().removeObserver(passenger);

        // Press the extra button
        int additionalFloor = 0;
        if (passengerDirection.equals(Elevator.Direction.MOVING_UP)) {
            additionalFloor = passengerDestination - 1;
            if (passengerDestination == elevator.getCurrentFloor().getNumber() + 1) {
                // Do nothing...
            }
            else {
                elevator.pressFloorButton(additionalFloor);
            }
        }
        else {
            additionalFloor = passengerDestination + 1;
            if (passengerDestination == elevator.getCurrentFloor().getNumber() - 1) {
                // Do nothing...
            }
            else {
                elevator.pressFloorButton(additionalFloor);
            }
        }

        passenger.setState(Passenger.PassengerState.ON_ELEVATOR);

        String additionalResult = additionalFloor == 0 ? "" : Integer.toString(additionalFloor);

        String message = passenger.getName() + " " + passenger.getId() + " "
                + "clumsily requested floors " + passengerDestination
                + " and " + additionalResult + " on elevator " + elevator.getNumber() + ".";
        Logger.getInstance().logString(message);

    }
}
