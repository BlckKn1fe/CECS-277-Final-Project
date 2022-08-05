package cecs277.elevators;

import cecs277.buildings.Building;
import cecs277.buildings.Floor;
import cecs277.passengers.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An ActiveMode elevator is handling at least one floor request.
 */
public class ActiveMode implements OperationMode {
	
	// TODO: implement this class.
	// An active elevator cannot be dispatched, and will ignore direction requests from its current floor. (Only idle
	//    mode elevators observe floors, so an ActiveMode elevator will never observe directionRequested.)

	// The bulk of your Project 2 tick() logic goes here, except that you will never be in IDLE_STATE when active.

	// If you used to schedule a transition to IDLE_STATE, you should instead schedule an operation change to
	//    IdleMode in IDLE_STATE.

	// Otherwise your code should be almost identical, except you are no longer in the Elevator class, so you need
	//    to use accessors and mutators instead of directly addressing the fields of Elevator.
	
	
	
	@Override
	public String toString() {
		return "Active";
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
		switch (elevator.getState()) {

			case DOORS_OPENING:
				elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_OPEN, 2);
				break;


 			case DOORS_OPEN:
				int outNum = 0;
				int beforeOutInNum = 0;
				int afterOutRemainNumber, realInNumber;

				// Get how may passengers want to go outside elevator

				List<Integer> beforeOutPassenger = elevator.getPassengers()
						.stream()
						.map(Passenger::getId)
						.collect(Collectors.toList());

//				for (Passenger p : elevator.getPassengers()) {
//					if (p.getDestination() == elevator.getCurrentFloor().getNumber()) {
//						outNum++;
//					}
//				}

				// Get the number of passengers waiting on floor
				beforeOutInNum = elevator.getCurrentFloor().getWaitingPassengers().size();

				// Notify all observers the door has opened
				for (int i = 0; i < elevator.getObServers().size(); i++) {
					int startSize = elevator.getObServers().size();
					elevator.getObServers().get(i).elevatorDoorsOpened(elevator);
					int endSize = elevator.getObServers().size();
					if (endSize < startSize) { i--; }
				}

				List<Integer> afterOutPassenger = elevator.getPassengers()
						.stream()
						.map(Passenger::getId)
						.collect(Collectors.toList());

				int counter = 0;
				for (Integer i : beforeOutPassenger) {
					if (afterOutPassenger.contains(i)) {
						counter++;
					}
				}

				outNum = beforeOutPassenger.size() - counter;
				afterOutRemainNumber = elevator.getCurrentFloor().getWaitingPassengers().size();
				realInNumber = beforeOutInNum - afterOutRemainNumber;
				double time = Math.floor((outNum + realInNumber) / 2);
				// Schedule next elevator state event
				elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_CLOSING, 1 + (long)time);
				break;


			case DOORS_CLOSING:
				// This is to handle s situation where two elevators arrive at the same floors at the same, they
				// call elevatorDecelerating twice, so it will remove the extra observers from the observer list.
				if (elevator.getPassengers().isEmpty()) {
					while (elevator.getObServers().get(elevator.getObServers().size() - 1) instanceof Passenger) {
						elevator.getObServers().remove(elevator.getObServers().size() - 1);
					}
				}
				else {
					while (!elevator.getPassengers().get(elevator.getPassengers().size() -1 ).equals
							(elevator.getObServers().get(elevator.getObServers().size() - 1))) {
						elevator.getObServers().remove(elevator.getObServers().size() - 1);
					}
				}
				// We confirm the direction in DECELERATING already, so we only need to check
				// if the elevator is empty or not. If it is empty, then change to IDLE mode.
				// If not, then ACCELERATING
				if (!elevator.getPassengers().isEmpty()) {
					if (elevator.getRequestedFloor().toString().equals("{}")) {
						elevator.scheduleModeChange(new IdleMode(), Elevator.ElevatorState.IDLE_STATE, 2);
					}

					else {
						elevator.scheduleStateChange(Elevator.ElevatorState.ACCELERATING, 2);
					}
				}
				else {
					//mBuilding.elevatorWentIdle(this);
					if (!elevator.getRequestedFloor().isEmpty()) {
						elevator.scheduleStateChange(Elevator.ElevatorState.ACCELERATING, 2);
					}
					else {
						elevator.scheduleModeChange(new IdleMode(), Elevator.ElevatorState.IDLE_STATE, 2);
					}
//					elevator.scheduleModeChange(new IdleMode(), Elevator.ElevatorState.IDLE_STATE, 2);
				}
				break;


			case ACCELERATING:
				elevator.getCurrentFloor().removeObserver(elevator);
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

				if (elevator.getRequestedFloor().get(nextFloorNum - 1) ||
						elevator.getCurrentFloor().directionIsPressed(elevator.getCurrentDirection())) {
					elevator.scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2);
				}
				else {
					if (elevator.getCurrentFloor().getNumber() == 1 && elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_DOWN)) {
						elevator.scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2);

					}
					else if (elevator.getCurrentFloor().getNumber() == elevator.getBuilding().getFloorCount() &&
							elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_UP)) {
						elevator.scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2);

					}
					else {
						elevator.scheduleStateChange(Elevator.ElevatorState.MOVING, 2);
					}
				}
				break;


			case DECELERATING:
				if (elevator.getRequestedFloor().get(elevator.getCurrentFloor().getNumber() - 1)) {
					elevator.getRequestedFloor().flip(elevator.getCurrentFloor().getNumber() - 1);
				}

				int floorNum = elevator.getCurrentFloor().getNumber();
				boolean up = false;
				boolean down = false;

				if (elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_UP)) { up = true; }
				if (elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_DOWN)) {down = true; }

				// If there is a request from the current floor
				// having the same direction as the elevator, then do nothing
				if (elevator.getCurrentFloor().directionIsPressed(elevator.getCurrentDirection())) {}
				// If no same direction request from the current floor and elevator is MOVING UP,
				// and there are still some passengers in the elevator want to MOVING UP,
				// then do nothing
				else if (up && elevator.getRequestedFloor().nextSetBit(floorNum) != -1) {}
				// If no same direction request from the current floor and elevator is MOVING DOWN,
				// and there are still some passengers in the elevator want to MOVING DOWN,
				// then do nothing
				else if (down && elevator.getRequestedFloor().previousSetBit(floorNum) != -1) {}
				// When no same direction request from the current floor
				// and no passengers in the elevator want to go to the
				// same direction as the elevator
				else {
					// Limit the elevator is going up and
					// there no more MOVING UP request from the passengers in elevator
					if (up && elevator.getRequestedFloor().nextSetBit(floorNum) == -1) {
						// If there is a passenger on the current floor want to MOVING DOWN,
						// then change the direction
						if (elevator.getCurrentFloor().directionIsPressed(Elevator.Direction.MOVING_DOWN)) {
							elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
						}
						// If no passengers on the current floor want to MOVING DOWN but
						// there are passengers in the elevator want to MOVING DOWN,
						// then change the direction
						else if (elevator.getRequestedFloor().previousSetBit(floorNum) != -1) {
							elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
						}
						// When no passenger on current floor and
						// all passengers in the elevator are going to go out
						// The mRequestedFloor are all false
						else {
							elevator.setCurrentDirection(Elevator.Direction.NOT_MOVING);
						}
					}
					// Limit the elevator is going dow and
					// there no more MOVING DOWN request from the passengers in elevator
					else if (down && elevator.getRequestedFloor().previousSetBit(floorNum) == -1) {
						// If there is a passenger on the current floor want to MOVING UP,
						// then change the direction
						if (elevator.getCurrentFloor().directionIsPressed(Elevator.Direction.MOVING_UP)) {
							elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
						}
						// If no passengers on the current floor want to MOVING UP but
						// ther are passengers in the elevator want to MOVING UP,
						// then change the direction
						else if (elevator.getRequestedFloor().nextSetBit(floorNum) != -1) {
							elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
						}
						// When no passenger on current floor and
						// all passengers in the elevator are going to go out
						// The mRequestedFloor are all false
						else {
							elevator.setCurrentDirection(Elevator.Direction.NOT_MOVING);
						}
					}

				}

				for (int i = 0; i < elevator.getObServers().size(); i++) {
					elevator.getObServers().get(i).elevatorDecelerating(elevator);

				}

				elevator.scheduleStateChange(Elevator.ElevatorState.DOORS_OPENING, 3);
				break;
		}
	}

}
