package cecs277.passengers.embarking;

import cecs277.buildings.Building;
import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class DisruptiveEmbarking implements EmbarkingStrategy {
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {
        Elevator.Direction passengerDirection;
        Building b = elevator.getBuilding();
        int floorNum = b.getFloorCount();
        int passengerDestination = passenger.getDestination();

        if (passengerDestination > elevator.getCurrentFloor().getNumber()) {
            passengerDirection = Elevator.Direction.MOVING_UP;
        }
        else {
            passengerDirection = Elevator.Direction.MOVING_DOWN;
        }

        elevator.getCurrentFloor().removeWaitingPassenger(passenger);
        elevator.getCurrentFloor().removeObserver(passenger);
        elevator.addPassenger(passenger);

        if (passengerDirection.equals(Elevator.Direction.MOVING_UP)) {
            for (int i = passengerDestination; i < floorNum; i++) {
                elevator.pressFloorButton(i + 1);
            }
        }
        else {
            for (int i = passengerDestination; i > 1; i--) {
                elevator.pressFloorButton(i - 1);
            }
        }
        passenger.setState(Passenger.PassengerState.ON_ELEVATOR);

        String message = passenger.getName() + " " + passenger.getId() +
                " is being disruptive and requested floor " + passenger.getDestination() +
                " and everything above it on elevator " + elevator.getNumber() + ".";
        Logger.getInstance().logString(message);

    }
}
