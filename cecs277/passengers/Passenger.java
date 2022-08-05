package cecs277.passengers;

import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.elevators.Elevator;
import cecs277.elevators.ElevatorObserver;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.debarking.ConfusedDebarking;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.debarking.DistractedDebarking;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.travel.TravelStrategy;

/**
 * A passenger that is either waiting on a floor or riding an elevator.
 */
public class Passenger implements FloorObserver, ElevatorObserver {
	// An enum for determining whether a Passenger is on a floor, an elevator, or busy (visiting a room in the building).
	public enum PassengerState {
		WAITING_ON_FLOOR,
		ON_ELEVATOR,
		BUSY
	}
	
	// A cute trick for assigning unique IDs to each object that is created. (See the constructor.)
	private static int mNextId;
	protected static int nextPassengerId() {
		return ++mNextId;
	}

	private TravelStrategy mTravelStrategy;
	private BoardingStrategy mBoardingStrategy;
	private EmbarkingStrategy mEmbarkingStrategy;
	private DebarkingStrategy mDebarkingStrategy;

	private int mIdentifier;
	private String mName;
	private String mShortName;
	private PassengerState mCurrentState;
	
	public Passenger(String name, String sName, TravelStrategy t, BoardingStrategy b, EmbarkingStrategy e, DebarkingStrategy d) {
		mIdentifier = nextPassengerId();
		mCurrentState = PassengerState.WAITING_ON_FLOOR;

		mName = name;
		mShortName = sName;

		mTravelStrategy = t;
		mBoardingStrategy = b;
		mEmbarkingStrategy = e;
		mDebarkingStrategy = d;
	}
	
	public void setState(PassengerState state) {
		mCurrentState = state;
	}
	
	/**
	 * Gets the passenger's unique identifier.
	 */
	public int getId() { return mIdentifier; }

	public String getName() { return mName; }

	public String getShortName() { return mShortName; }

	public void scheduleNextTrip(Floor floor) {
		mTravelStrategy.scheduleNextDestination(this, floor);
	}


	/**
	 * Handles an elevator arriving at the passenger's current floor.
	 */
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// This is a sanity check. A Passenger should never be observing a Floor they are not waiting on.
		if (floor.getWaitingPassengers().contains(this) && mCurrentState == PassengerState.WAITING_ON_FLOOR) {
			Elevator.Direction elevatorDirection = elevator.getCurrentDirection();
			
			// TODO: check if the elevator is either NOT_MOVING, or is going in the direction that this passenger wants.
			// If so, this passenger becomes an observer of the elevator.

			Elevator.Direction passengerDirection;

			// Get passenger's desired direction
			if (this.getDestination() > floor.getNumber()) {
				passengerDirection = Elevator.Direction.MOVING_UP;
			}
			else {
				passengerDirection = Elevator.Direction.MOVING_DOWN;
			}


			// When elevator door open, if the passenger goes the same direction as the elevator,
			// then observe the elevator. For next step, passenger will decide if he/she will
			// get on the elevator, depending on different strategies.
			if (elevatorDirection.equals(Elevator.Direction.NOT_MOVING) ||
				elevatorDirection.equals(passengerDirection)) {
				elevator.addObserver(this);
			}
		}
		// This else should not happen if your code is correct. Do not remove this branch; it reveals errors in your code.
		else {
			throw new RuntimeException("Passenger " + toString() + " is observing Floor " + floor.getNumber() + " but they are " +
			 "not waiting on that floor.");
		}
	}

	/**
	 * Handles an observed elevator opening its doors. Depart the elevator if we are on it; otherwise, enter the elevator.
	 */
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// The elevator is arriving at our destination. Remove ourselves from the elevator, and stop observing it.
		// Does NOT handle any "next" destination...
		if (mCurrentState == PassengerState.ON_ELEVATOR && elevator.getCurrentFloor().getNumber() == getDestination()) {
			// TODO: remove this passenger from the elevator, and as an observer of the elevator. Call the
			// leavingElevator method to allow a derived class to do something when the passenger departs.
			// Set the current state to BUSY.
			if (mDebarkingStrategy.willLeaveElevator(this, elevator)) {
				mDebarkingStrategy.departedElevator(this, elevator);
			}
		}

		// The elevator has arrived on the floor we are waiting on. If the elevator has room for us, remove ourselves
		// from the floor, and enter the elevator.
		else if (mCurrentState == PassengerState.WAITING_ON_FLOOR) {
			// TODO: determine if the passenger will board the elevator using willBoardElevator.
			// If so, remove the passenger from the current floor, and as an observer of the current floor;
			// then add the passenger as an observer of and passenger on the elevator. Then set the mCurrentState
			// to ON_ELEVATOR.

			if (mBoardingStrategy.willBoardElevator(this, elevator)) {
				mEmbarkingStrategy.enteredElevator(this, elevator);
			}
			else {
				elevator.removeObserver(this);
			}
		}

		// This is for distractedDebarking passengers. When this passenger miss the correct
		// destination, he/she will leave the elevator asap. So the next time, the doors open,
		// this passenger will leave even if it's not his/her destination floor.
		else if (mDebarkingStrategy instanceof DistractedDebarking &&
				mDebarkingStrategy.willLeaveElevator(this, elevator)) {
			mDebarkingStrategy.departedElevator(this, elevator);
		}

		else if (mDebarkingStrategy instanceof ConfusedDebarking &&
				mDebarkingStrategy.willLeaveElevator(this, elevator)) {
			mDebarkingStrategy.departedElevator(this, elevator);
		}

	}
	
	/**
	 * Returns the passenger's current destination (what floor they are travelling to).
	 */
	public int getDestination() { return mTravelStrategy.getDestination(); }
	
	/**
	 * Called to determine whether the passenger will board the given elevator that is moving in the direction the
	 * passenger wants to travel.
	 */
	protected boolean willBoardElevator(Elevator elevator) {
		return mBoardingStrategy.willBoardElevator(this, elevator);
	}
	
	/**
	 * Called when the passenger is departing the given elevator.
	 */
	protected void leavingElevator(Elevator elevator) {
		mTravelStrategy.scheduleNextDestination(this, elevator.getCurrentFloor());
	}
	
	// This will be overridden by derived types.
	@Override
	public String toString() {
		return mName + " " + getId() +
				" [-> "+ Integer.toString(getDestination()) + "]";
	}
	
	@Override
	public void directionRequested(Floor sender, Elevator.Direction direction) {
		// Don't care.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// Don't care about this.
	}

	@Override
	public void elevatorDecelerating(Elevator sender) {
		// Don't care.
	}
	
	// The next two methods allow Passengers to be used in data structures, using their id for equality. Don't change 'em.
	@Override
	public int hashCode() {
		return Integer.hashCode(mIdentifier);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Passenger passenger = (Passenger)o;
		return mIdentifier == passenger.mIdentifier;
	}
	
}