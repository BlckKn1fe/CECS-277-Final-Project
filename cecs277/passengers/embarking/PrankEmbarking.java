package cecs277.passengers.embarking;

import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

import java.util.Random;

public class PrankEmbarking implements EmbarkingStrategy {
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {

        Random r = elevator.getBuilding().getSimulation().getRandom();
        int floorNum = elevator.getBuilding().getFloorCount();

        // Press random floor n/2 times
        for (int i = 0; i < floorNum / 2; i++) {
            int randFloor = 1 + r.nextInt(floorNum);
            // Avoid pressing the current floor
            while(randFloor == elevator.getCurrentFloor().getNumber()) {
                randFloor = 1 + r.nextInt(floorNum);
            }
            elevator.pressFloorButton(randFloor);
        }

        // Exit Building and stop observing the elevator
        elevator.getCurrentFloor().removeObserver(passenger);
        elevator.getCurrentFloor().removeWaitingPassenger(passenger);
        elevator.removeObserver(passenger);

        passenger.setState(Passenger.PassengerState.BUSY);

        String message = passenger.getName() + " " + passenger.getId()
                        + " immediately leave the elevator and exit the building!";
        Logger.getInstance().logString(message);
    }
}
