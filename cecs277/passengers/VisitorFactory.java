package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.bording.CapacityBoarding;
import cecs277.passengers.debarking.AttentiveDebarking;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.embarking.ResponsibleEmbarking;
import cecs277.passengers.travel.SingleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.Random;

public class VisitorFactory implements PassengerFactory {
    private int mWeight;


    public VisitorFactory() { mWeight = 10; }

    public VisitorFactory(int weight) {
        mWeight = weight;
    }

    @Override
    public String factoryName() {
        return "Visitor";
    }

    @Override
    public String shortName() {
        return "V";
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
        int randomFloor = 2 + r.nextInt(buildingFloors - 1);
        long randomDuration = (long)(r.nextGaussian() * 1200 + 3600);

        return new SingleDestinationTravel(randomFloor, randomDuration);
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
