package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.bording.CapacityBoarding;
import cecs277.passengers.debarking.ConfusedDebarking;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.embarking.ClumsyEmbarking;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.travel.SingleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.Random;

public class StonerFactory implements PassengerFactory {
    private int mWeight;

    public StonerFactory() {
        mWeight = 1;
    }

    public StonerFactory(int wight) {
        mWeight = wight;
    }

    @Override
    public String factoryName() {
        return "Stoner";
    }

    @Override
    public String shortName() {
        return "S";
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
        return new ClumsyEmbarking();
    }

    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new ConfusedDebarking();
    }
}
