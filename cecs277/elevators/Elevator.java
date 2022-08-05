package cecs277.elevators;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.events.ElevatorModeEvent;
import cecs277.events.ElevatorStateEvent;
import cecs277.passengers.Passenger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Elevator implements FloorObserver {
	
	public enum ElevatorState {
		IDLE_STATE,
		DOORS_OPENING,
		DOORS_CLOSING,
		DOORS_OPEN,
		ACCELERATING,
		DECELERATING,
		MOVING
	}
	
	public enum Direction {
		NOT_MOVING,
		MOVING_UP,
		MOVING_DOWN
	}
	
	private OperationMode mOperationMode;
	private int mNumber;
	private Building mBuilding;

	private ElevatorState mCurrentState = ElevatorState.IDLE_STATE;
	private Direction mCurrentDirection = Direction.NOT_MOVING;
	private Floor mCurrentFloor;
	private List<Passenger> mPassengers = new ArrayList<>();
	
	private List<ElevatorObserver> mObservers = new ArrayList<>();
	
	// TODO: declare a field to keep track of which floors have been requested by passengers.
	private BitSet mRequestedFloor = new BitSet();

	public Elevator(int number, Building bld) {
		mNumber = number;
		mBuilding = bld;
		mCurrentFloor = bld.getFloor(1);
		mOperationMode = new IdleMode();
		
		scheduleStateChange(ElevatorState.IDLE_STATE, 0);
	}
	
	/**
	 * Helper method to schedule a state change in a given number of seconds from now.
	 */
	public void scheduleStateChange(ElevatorState state, long timeFromNow) {
		Simulation sim = mBuilding.getSimulation();
		sim.scheduleEvent(new ElevatorStateEvent(sim.currentTime() + timeFromNow, state, this));
	}
	
	/**
	 * Adds the given passenger to the elevator's list of passengers, and requests the passenger's destination floor.
	 */
	public void addPassenger(Passenger passenger) {
		// TODO: add the passenger's destination to the set of requested floors.
		mPassengers.add(passenger);
		if (passenger.getShortName().equals("JR")) {
			// Joyrider will not request any floors
		}
		else {
			mRequestedFloor.set(passenger.getDestination() - 1, true);
		}
	}
	
	public void removePassenger(Passenger passenger) {
		mPassengers.remove(passenger);
	}

	/**
	 * determine the first floor larger than fromFloor that has been requested
	 * @param fromFloor is the starting floor
	 * @return the first floor larger than fromFloor that has been requested
	 */
	private int nextRequestUp(int fromFloor) {
		return mRequestedFloor.nextSetBit(fromFloor - 1);
	}

	/**
	 * @param fromFloor is the starting floor
	 * @return the first floor less than fromFloor that has been requested
	 */
	private int nextRequestDown(int fromFloor) {
		return mRequestedFloor.nextSetBit(fromFloor - 1);
	}

	
	/**
	 * Schedules the elevator's next state change based on its current state.
	 */
	public void tick() {
		mOperationMode.tick(this);
	}
	
	
	/**
	 * Sends an idle elevator to the given floor.
	 */
	public void dispatchTo(Floor floor, Direction desiredDirection) {
		if (mOperationMode.canBeDispatchedToFloor(this, floor)) {
			mOperationMode.dispatchToFloor(this, floor, desiredDirection);
		}
	}
	
	// Simple accessors

	public int getNumber() { return mNumber; }
	public ElevatorState getState() { return mCurrentState; }
	public Floor getCurrentFloor() { return mCurrentFloor; }
	public Direction getCurrentDirection() { return mCurrentDirection; }
	public Building getBuilding() {
		return mBuilding;
	}
	public List<ElevatorObserver> getObServers() { return mObservers; }
	public List<Passenger> getPassengers() { return mPassengers; }
	public BitSet getRequestedFloor() { return mRequestedFloor;}
	
	/**
	 * Returns true if this elevator is in the idle state.
	 * @return
	 */
	public boolean canBeDispatched(Floor floor) {
		// TODO: complete this method.
		return mOperationMode.canBeDispatchedToFloor(this, floor);
	}
	
	// All elevators have a capacity of 10, for now.
	public int getCapacity() {
		return 10;
	}
	
	public int getPassengerCount() {
		return mPassengers.size();
	}
	
	// Simple mutators
	public void setState(ElevatorState newState) {
		mCurrentState = newState;
	}

	public void setMode(OperationMode mode) { mOperationMode = mode;}
	
	public void setCurrentDirection(Direction direction) {
		mCurrentDirection = direction;
	}
	
	public void setCurrentFloor(Floor floor) {
		mCurrentFloor = floor;
	}

	public void pressFloorButton(int floorNum) { mRequestedFloor.set(floorNum - 1, true);}

	public void setCurrentOperationMode(OperationMode om) {
		mOperationMode = om;
	}
	
	// Observers
	public void addObserver(ElevatorObserver observer) {
		mObservers.add(observer);
	}
	
	public void removeObserver(ElevatorObserver observer) {
		mObservers.remove(observer);
	}

	public void scheduleModeChange(OperationMode mode, ElevatorState state, int time) {
		Simulation sim = mBuilding.getSimulation();
		sim.scheduleEvent(new ElevatorModeEvent(sim.currentTime() + time, mode, state, this));
		// State and time are used to schedule a stateChange event
		//scheduleStateChange(state, time);
	}

	public void announceElevatorIdle() {
		for (int i = 0; i < mObservers.size(); i++) {
			mObservers.get(i).elevatorWentIdle(this);
		}
	}

	public void announceElevatorDecelerating() {
		for (int i = 0; i < mObservers.size(); i++) {
			mObservers.get(i).elevatorDecelerating(this);
		}
	}

	// FloorObserver methods
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// Not used.
	}
	
	/**
	 * Triggered when our current floor receives a direction request.
	 */
	@Override
	public void directionRequested(Floor sender, Direction direction) {
		mOperationMode.directionRequested(this, sender, direction);
	}
	
	// Voodoo magic.
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String original = mRequestedFloor.toString();
		ArrayList<Integer> arr = new ArrayList<>();

		Pattern pt = Pattern.compile("[0-9]+");
		Matcher mc = pt.matcher(original);

		while (mc.find()) {
			arr.add(Integer.parseInt(mc.group()) + 1);
		}

		sb.append("{");
		for (int i = 0; i < arr.size(); i++) {
			if (i == arr.size() - 1) {
				sb.append(arr.get(i));
				break;
			}
			sb.append(arr.get(i)).append(", ");
		}
		sb.append("}");


		return "Elevator " + mNumber + " [" + mOperationMode + "]"+ " - "
				+ mCurrentFloor + " - " + mCurrentState + " - " + mCurrentDirection + " - "
				+ "["
				+ mPassengers.stream()
							.map(p -> p.getShortName() + Integer.toString(p.getId()))
							.collect(Collectors.joining(", "))
				+ "] "
				+ sb.toString();
	}
	
}
