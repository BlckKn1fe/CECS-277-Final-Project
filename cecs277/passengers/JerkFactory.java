package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.bording.CapacityBoarding;
import cecs277.passengers.debarking.AttentiveDebarking;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.embarking.DisruptiveEmbarking;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.travel.SingleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.Random;

public class JerkFactory implements PassengerFactory {
    private int mWeight;

    public JerkFactory() {
        mWeight = 2;
    }

    public JerkFactory(int weight) {
        mWeight = weight;
    }

    @Override
    public String factoryName() {
        return "Jerk";
    }

    @Override
    public String shortName() {
        return "J";
    }

    @Override
    public int factoryWeight() {
        return mWeight;
    }

    @Override
    public BoardingStrategy createBoardingStrategy(Simulation simulation) {
        return new CapacityBoarding();
    }

    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        Random r = simulation.getRandom();
        Building b = simulation.getBuilding();
        int buildingFloors = b.getFloorCount();
        int randomFloor = 2 + r.nextInt(buildingFloors - 2 + 1);
        int randomDuration = (int)(r.nextGaussian() * 1200 + 3600);

        return new SingleDestinationTravel(randomFloor, randomDuration);
    }

    @Override
    public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
        return new DisruptiveEmbarking();
    }

    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new AttentiveDebarking();
    }
}
