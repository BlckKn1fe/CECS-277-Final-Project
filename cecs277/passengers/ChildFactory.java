package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.passengers.bording.AwkwardBoarding;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.debarking.DistractedDebarking;
import cecs277.passengers.embarking.ClumsyEmbarking;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.travel.SingleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.Random;

public class ChildFactory implements PassengerFactory {
    private int mWeight;

    public ChildFactory() { mWeight = 3; }

    public ChildFactory(int weight) { mWeight = weight; }

    @Override
    public String factoryName() {
        return "Child";
    }

    @Override
    public String shortName() {
        return "C";
    }

    @Override
    public int factoryWeight() {
        return mWeight;
    }

    @Override
    public BoardingStrategy createBoardingStrategy(Simulation simulation) {
        return new AwkwardBoarding(4);
    }

    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        Random r = simulation.getRandom();
        Building b = simulation.getBuilding();

        int buildingFloors = b.getFloorCount();
        int randomFloor = 2 + r.nextInt(buildingFloors - 1);
        int randomDuration = (int)(r.nextGaussian() * 1800 + 7200);

        return new SingleDestinationTravel(randomFloor, randomDuration);
    }

    @Override
    public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
        return new ClumsyEmbarking();
    }

    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new DistractedDebarking();
    }
}
