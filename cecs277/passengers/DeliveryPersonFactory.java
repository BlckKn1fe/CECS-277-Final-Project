package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.bording.ThresholdBoarding;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.debarking.DistractedDebarking;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.embarking.ResponsibleEmbarking;
import cecs277.passengers.travel.MultipleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.ArrayList;
import java.util.Random;

public class DeliveryPersonFactory implements PassengerFactory {
    private int mWeight;

    public DeliveryPersonFactory() {
        mWeight = 2;
    }

    public DeliveryPersonFactory(int weight) {
        mWeight = weight;
    }

    @Override
    public String factoryName() {
        return "Delivery Person";
    }

    @Override
    public String shortName() {
        return "D";
    }

    @Override
    public int factoryWeight() {
        return mWeight;
    }

    @Override
    public BoardingStrategy createBoardingStrategy(Simulation simulation) {
        return new ThresholdBoarding(5);
    }

    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        Building b = simulation.getBuilding();
        Random r = simulation.getRandom();
        ArrayList<Integer> destination = new ArrayList<>();
        ArrayList<Long> duration = new ArrayList<>();

        int buildingFloors = b.getFloorCount();
        int X = 1 + r.nextInt((int)Math.floor(b.getFloorCount() * 2 / 3));

        int randomFloor = 0;
        // Create random multiple destination
        for (int i = 0; i < X; i++) {
            // Get a random floor each time and add into the ArrayList
            randomFloor = 2 + r.nextInt(buildingFloors - 2 + 1);
            // All destinations should be distinct, because a delivery person
            // won't go to the same floors twice
            while (destination.contains(randomFloor)) {
                randomFloor = 2 + simulation.getRandom().nextInt(buildingFloors - 2 + 1);
            }
            destination.add(randomFloor);
        }
        // Create random duration time
        for (int i = 0; i < X; i++) {
            long randomDuration = (int)(r.nextGaussian() * 10 + 60);
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
        return new DistractedDebarking();
    }
}
