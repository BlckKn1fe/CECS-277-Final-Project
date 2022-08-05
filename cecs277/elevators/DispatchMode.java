package cecs277.elevators;

import cecs277.buildings.Floor;

/**
 * A DispatchMode elevator is in the midst of a dispatch to a target floor in order to handle a request in a target
 * direction. The elevator will not stop on any floor that is not its destination, and will not respond to any other
 * request until it arrives at the destination.
 */
public class DispatchMode implements OperationMode {
	// The destination floor of the dispatch.
	private Floor mDestination;
	// The direction requested by the destination floor; NOT the direction the elevator must move to get to that floor.
	private Elevator.Direction mDesiredDirection;
	private int mPriority = 2;
	
	public DispatchMode(Floor destination, Elevator.Direction desiredDirection) {
		mDestination = destination;
		mDesiredDirection = desiredDirection;
	}
	
	// TODO: implement the other methods of the OperationMode interface.
	// Only Idle elevators can be dispatched.
	// ==>  the canBeDispatchedToFloor() method in any other mode will return false

	// A dispatching elevator ignores all other requests.

	// It does not check to see if it should stop of floors that are not the destination.

	// Its flow of ticks should go: IDLE_STATE -> ACCELERATING -> MOVING -> ... -> MOVING -> DECELERATING.
	//    When decelerating to the destination floor, change the elevator's direction to the desired direction,
	//    announce that it is decelerating, and then schedule an operation change in 3 seconds to
	//    ActiveOperation in the DOORS_OPENING state.
	// A DispatchOperation elevator should never be in the DOORS_OPENING, DOORS_OPEN, or DOORS_CLOSING states.
	
	
	@Override
	public String toString() {
		return "Dispatching to " + mDestination.getNumber() + " " + mDesiredDirection;
	}

	@Override
	public boolean canBeDispatchedToFloor(Elevator elevator, Floor floor) {
		return false;
	}

	@Override
	public void dispatchToFloor(Elevator elevator, Floor targetFloor, Elevator.Direction targetDirection) {
		// Do nothing ...
	}

	@Override
	public void directionRequested(Elevator elevator, Floor floor, Elevator.Direction direction) {
		// Do nothing ...
	}

	@Override
	public void tick(Elevator elevator) {
		switch(elevator.getState()) {
			case IDLE_STATE:
				if (mDestination.getNumber() > elevator.getCurrentFloor().getNumber()) {
					elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
				}
				else {
					elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
				}
				elevator.scheduleStateChange(Elevator.ElevatorState.ACCELERATING, 0);
				break;

			case ACCELERATING:
				elevator.scheduleStateChange(Elevator.ElevatorState.MOVING, 3);
				break;

			case MOVING:
				//Get current floor number
				int currentFloorNum = elevator.getCurrentFloor().getNumber();
				// Move the elevator. If it goes up, ser current floor to the upper one
				if (elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_UP)) {
					elevator.setCurrentFloor(elevator.getBuilding().getFloor(currentFloorNum + 1));
				}
				// Move the elevator. If it goes down, ser current floor to the bottom on
				else if (elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_DOWN)) {
					elevator.setCurrentFloor(elevator.getBuilding().getFloor(currentFloorNum - 1));
				}
				// Get the next floor number
				int nextFloorNum = elevator.getCurrentFloor().getNumber();

				if (nextFloorNum == mDestination.getNumber()) {
					// Change to DECELERATING state when arrive at the destination
					elevator.scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2);
				}
				else {
					elevator.scheduleStateChange(Elevator.ElevatorState.MOVING, 2);
				}
				break;

			case DECELERATING:

				elevator.setCurrentDirection(mDesiredDirection);
				elevator.announceElevatorDecelerating();
				elevator.scheduleModeChange(new ActiveMode(), Elevator.ElevatorState.DOORS_OPENING, 3);
		}


	}

}
