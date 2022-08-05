package cecs277.elevators;


import cecs277.buildings.Floor;

/**
 * An IdleMode elevator is not servicing any requests, and is available for dispatch.
 */
public class IdleMode implements OperationMode {
	/**
	 * An idle elevator can be dispatched to any floor that it is not on.
	 */
	@Override
	public boolean canBeDispatchedToFloor(Elevator elevator, Floor floor) {
		return elevator.getCurrentFloor().getNumber() != floor.getNumber();
	}
	
	/**
	 * Schedules an operation change to DispatchMode.
	 */
	@Override
	public void dispatchToFloor(Elevator elevator, Floor targetFloor, Elevator.Direction targetDirection) {
		// Must remove ourselves as an observer of our floor, since we are moving on.
		elevator.getCurrentFloor().removeObserver(elevator);
		
		// TODO: you will need to create this method in the Elevator class, along with ElevatorModeEvent to support it.
		elevator.scheduleModeChange(new DispatchMode(targetFloor, targetDirection),
		 Elevator.ElevatorState.IDLE_STATE, 0);
	}
	
	/**
	 * Called when an elevator is set to IdleMode. There are no physical state changes when idle, so this tick()
	 * will only be called once when the elevator first goes idle.
	 */
	@Override
	public void tick(Elevator elevator) {
		Floor currentFloor = elevator.getCurrentFloor();
		
		// Paranoia: I found a bug where an elevator observed its floor twice. This prevents it.
		currentFloor.removeObserver(elevator);
		currentFloor.addObserver(elevator);
		
		// TODO: cause the elevator to announce elevatorWentIdle() to its observers.
		elevator.announceElevatorIdle();
	}

	@Override
	public void directionRequested(Elevator elevator, Floor floor, Elevator.Direction direction) {
		floor.removeObserver(elevator);
		elevator.setCurrentDirection(direction);
		elevator.announceElevatorDecelerating();
		
		elevator.scheduleModeChange(new ActiveMode(), Elevator.ElevatorState.DOORS_OPENING, 0);
	}
	
	// I like to print elevator operation modes when debugging.
	@Override
	public String toString() {
		return "Idle";
	}
}
