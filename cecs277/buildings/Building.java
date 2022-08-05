package cecs277.buildings;

import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.elevators.ElevatorObserver;
import cecs277.passengers.Passenger;

import java.util.*;

public class Building implements ElevatorObserver, FloorObserver {
	private List<Elevator> mElevators = new ArrayList<>();
	private List<Floor> mFloors = new ArrayList<>();
	private Simulation mSimulation;
	private Queue<Integer> mWaitingFloors = new ArrayDeque<>();

	public Building(int floors, int elevatorCount, Simulation sim) {
		mSimulation = sim;
		
		// Construct the floors, and observe each one.
		for (int i = 0; i < floors; i++) {
			Floor f = new Floor(i + 1, this);
			f.addObserver(this);
			mFloors.add(f);
		}
		
		// Construct the elevators, and observe each one.
		for (int i = 0; i < elevatorCount; i++) {
			Elevator elevator = new Elevator(i + 1, this);
			elevator.addObserver(this);
			for (Floor f : mFloors) {
				elevator.addObserver(f);
			}
			mElevators.add(elevator);
		}
	}
	

	// TODO: recreate your toString() here.
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String arrow = "";
		for (int i = mFloors.size() - 1; i >= 0; i--) {
			if (i >= 9) {
				sb.append(i + 1).append(": |");
			}
			else {
				sb.append(" ").append(i + 1).append(": |");
			}

			for (Elevator e : mElevators) {
				if (e.getCurrentFloor().getNumber() == i + 1) {
					sb.append(" X |");
				}
				else {
					sb.append("   |");
				}
			}

			Floor fl = mFloors.get(i);
			if (fl.directionIsPressed(Elevator.Direction.MOVING_UP) &&
				fl.directionIsPressed(Elevator.Direction.MOVING_DOWN)) {
				arrow = "↕";
			}
			else if (fl.directionIsPressed(Elevator.Direction.MOVING_UP)) {
				arrow = "↑";
			}
			else if (fl.directionIsPressed(Elevator.Direction.MOVING_DOWN)) {
				arrow = "↓";
			}
			sb.append(" " + arrow);
			arrow = "";

			List<Passenger> pl = mFloors.get(i).getWaitingPassengers();

			for (int k = 0; k < mFloors.get(i).getWaitingPassengers().size(); k++) {
				Passenger p = pl.get(k);
				sb.append(" ").append(p.getShortName()).append(p.getId())
						.append("->").append(p.getDestination());
			}

			sb.append("\n");

		}
		for (Elevator e : mElevators) {
			sb.append(e.toString()).append("\n");
		}

		return sb.toString();
	}
	
	public int getFloorCount() {
		return mFloors.size();
	}
	
	public Floor getFloor(int floor) {
		return mFloors.get(floor - 1);
	}
	
	public Simulation getSimulation() {
		return mSimulation;
	}
	
	
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// Have to implement all interface methods even if we don't use them.
	}
	
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Don't care.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// TODO: if mWaitingFloors is not empty, remove the first entry from the queue and dispatch the elevator to that floor.

		if (!mWaitingFloors.isEmpty()) {
			int floorNum = mWaitingFloors.poll();
			Elevator.Direction desiredDirection;
			if (floorNum > 10) {
				desiredDirection = Elevator.Direction.MOVING_DOWN;
				elevator.dispatchTo(getFloor(floorNum - 10), desiredDirection);
			}
			else {
				desiredDirection = Elevator.Direction.MOVING_UP;
				elevator.dispatchTo(getFloor(floorNum), desiredDirection);
			}

		}
		else {
			elevator.setState(Elevator.ElevatorState.IDLE_STATE);
		}

	}
	
	@Override
	public void elevatorArriving(Floor sender, Elevator elevator) {
		// TODO: add the floor mWaitingFloors if it is not already in the queue.

		if (elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_UP) &&
				mWaitingFloors.contains(sender.getNumber())) {
			mWaitingFloors.remove((Integer)sender.getNumber());
		}
		else if (elevator.getCurrentDirection().equals(Elevator.Direction.MOVING_DOWN) &&
				mWaitingFloors.contains(sender.getNumber() + 10)) {
			mWaitingFloors.remove((Integer)(sender.getNumber() + 10));
		}

	}
	
	@Override
	public void directionRequested(Floor floor, Elevator.Direction direction) {
		// TODO: go through each elevator. If an elevator is idle, dispatch it to the given floor.
		// TODO: if no elevators are idle, then add the floor number to the mWaitingFloors queue.

		boolean canDispatch = false;
		int index = 0;
		for (Elevator e : mElevators) {
			if (e.canBeDispatched(floor)) {
				canDispatch = true;
				break;
			}
			index++;
		}

		int flNum = floor.getNumber();
		if (direction.equals(Elevator.Direction.MOVING_UP) && mWaitingFloors.contains(flNum)) {
			// Do nothing...
		}
		else if (direction.equals(Elevator.Direction.MOVING_DOWN) && mWaitingFloors.contains(flNum + 10)) {
			// Do nothing...
		}
		else {
			if (direction.equals(Elevator.Direction.MOVING_UP)) {
				if (canDispatch) {
					mElevators.get(index).dispatchTo(floor, direction);
				}
				else {
					mWaitingFloors.add(floor.getNumber());
				}
			}
			else if (direction.equals(Elevator.Direction.MOVING_DOWN)) {
				if (canDispatch) {
					mElevators.get(index).dispatchTo(floor, direction);
				}
				else {
					mWaitingFloors.add(floor.getNumber() + 10);
				}
			}
		}

	}
}
