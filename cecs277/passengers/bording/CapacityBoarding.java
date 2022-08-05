package cecs277.passengers.bording;

import cecs277.elevators.Elevator;
import cecs277.passengers.Passenger;

/**
 * A CapacityBoarding is a boarding strategy for a Passenger that will get on any elevator that has not reached its
 * capacity.
 */
public class CapacityBoarding implements BoardingStrategy {
	@Override
	public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
		return elevator.getPassengerCount() < elevator.getCapacity();
	}
}
