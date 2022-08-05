package cecs277.passengers.embarking;

import cecs277.elevators.Elevator;
import cecs277.logging.Logger;
import cecs277.passengers.Passenger;

public class ResponsibleEmbarking implements EmbarkingStrategy {
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {
        elevator.getCurrentFloor().removeObserver(passenger);
        elevator.getCurrentFloor().removeWaitingPassenger(passenger);
        elevator.addPassenger(passenger);

        passenger.setState(Passenger.PassengerState.ON_ELEVATOR);

        if (!passenger.getShortName().equals("JR")) {
            String message = passenger.getName() + " " + passenger.getId()
                    + " requested floor " + passenger.getDestination()
                    + " on elevator " + elevator.getNumber() + ".";
            Logger.getInstance().logString(message);
        }
        else {
            String message = passenger.getName() + " " + passenger.getId()
                    + " get on the elevator but does not requested any floors on elevator "
                    + elevator.getNumber() + ".";
            Logger.getInstance().logString(message);
        }

    }
}
