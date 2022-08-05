package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.bording.ThresholdBoarding;
import cecs277.passengers.debarking.AttentiveDebarking;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.embarking.ResponsibleEmbarking;
import cecs277.passengers.travel.MultipleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.ArrayList;
import java.util.Random;

public class WorkerFactory implements PassengerFactory {
    private int mWeight;

    public WorkerFactory() { mWeight = 2; }

    public WorkerFactory(int weight) { mWeight = weight; }

    @Override
    public String factoryName() {
        return "Worker";
    }

    @Override
    public String shortName() {
        return "W";
    }

    @Override
    public int factoryWeight() {
        return mWeight;
    }

    @Override
    public BoardingStrategy createBoardingStrategy(Simulation simulation) {
        return new ThresholdBoarding(3);
    }

    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        Random r = simulation.getRandom();
        Building b = simulation.getBuilding();

        ArrayList<Integer> destination = new ArrayList<>();
        ArrayList<Long> duration = new ArrayList<>();

        int buildingFloors = b.getFloorCount();
        int X = 2 + r.nextInt(5 - 2 + 1);

        // Create random multiple destination
        for (int i = 0; i < X; i++) {
			// Get a random floor each time and add into the ArrayList
			int randomFloor = 2 + r.nextInt(buildingFloors - 2 + 1);
			// Avoid add same destination continuously
			if (!destination.isEmpty() && destination.get(destination.size() - 1) == randomFloor) {
				i--;
				continue;
			}
			destination.add(randomFloor);
		}
        // Create random duration time
        for (int i = 0; i < X; i++) {
			long randomDuration = (int)(r.nextGaussian() * 180 + 600);
			duration.add(randomDuration);
		}

        return new MultipleDestinationTravel(destination, duration);
    }

    @Override
    public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
        return new ResponsibleEmbarking();
    }

    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new AttentiveDebarking();
    }
}
