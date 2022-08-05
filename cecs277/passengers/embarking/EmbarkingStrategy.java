package cecs277.passengers.embarking;

import cecs277.elevators.Elevator;
import cecs277.passengers.Passenger;

/**
 * An EmbarkingStrategy specifies what to do when a passenger entered an open elevator.
 */
public interface EmbarkingStrategy {
	/**
	 * Called when the passenger entered the given elevator, giving a chance to request floors, etc.
	 */
	void enteredElevator(Passenger passenger, Elevator elevator);
}
