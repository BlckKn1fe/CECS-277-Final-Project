package cecs277.events;

import cecs277.buildings.Building;
import cecs277.passengers.Passenger;
import cecs277.Simulation;
import cecs277.passengers.PassengerFactory;

import java.util.List;
import java.util.Random;

/**
 * A simulation event that adds a new random passenger on floor 1, and then schedules the next spawn event.
 */
public class SpawnPassengerEvent extends SimulationEvent {
	// After executing, will reference the Passenger object that was spawned.
	private Passenger mPassenger;
	private Building mBuilding;
	
	public SpawnPassengerEvent(long scheduledTime, Building building) {
		super(4, scheduledTime);
		mBuilding = building;
	}
	
	@Override
	public String toString() {
		return super.toString() + "Adding " + mPassenger + " to floor 1.";
	}
	
	@Override
	public void execute(Simulation sim) {
		Random r = mBuilding.getSimulation().getRandom();
		List<PassengerFactory> factory = sim.getFactory();
		int total = 0;
		for (PassengerFactory pf : factory) {
			total += pf.factoryWeight();
		}
		int s = r.nextInt(total);

		int index = 0;
		int weight = factory.get(0).factoryWeight();
		while (s >= weight) {
			index++;
			weight += factory.get(index).factoryWeight();
		}

		PassengerFactory pf = factory.get(index);

		mPassenger = new Passenger(pf.factoryName(),
				pf.shortName(),
				pf.createTravelStrategy(sim),
				pf.createBoardingStrategy(sim),
				pf.createEmbarkingStrategy(sim),
				pf.createDebarkingStrategy(sim));

		mBuilding.getFloor(1).addWaitingPassenger(mPassenger);

		long randTime = 1 + sim.getRandom().nextInt(30) + sim.currentTime();
		SpawnPassengerEvent sp = new SpawnPassengerEvent(randTime, mBuilding);
		sim.scheduleEvent(sp);
	}
}
