package cecs277.events;

import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.elevators.OperationMode;

public class ElevatorModeEvent extends SimulationEvent {
    private OperationMode mMode;
    private Elevator mElevator;
    private Elevator.ElevatorState mState;

    public ElevatorModeEvent(long scheduledTime, OperationMode mode, Elevator.ElevatorState state, Elevator elevator) {
        super(1, scheduledTime);
        mMode = mode;
        mElevator = elevator;
        mState = state;
    }

    @Override
    public void execute(Simulation sim) {
        mElevator.setMode(mMode);
        mElevator.setState(mState);
        mElevator.tick();
    }

    @Override
    public String toString() {
        return super.toString() + mElevator;
    }
}
