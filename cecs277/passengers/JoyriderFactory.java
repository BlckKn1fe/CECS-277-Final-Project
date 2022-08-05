package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.buildings.Floor;
import cecs277.passengers.bording.BoardingStrategy;
import cecs277.passengers.bording.CapacityBoarding;
import cecs277.passengers.debarking.AttentiveDebarking;
import cecs277.passengers.debarking.DebarkingStrategy;
import cecs277.passengers.embarking.EmbarkingStrategy;
import cecs277.passengers.embarking.ResponsibleEmbarking;
import cecs277.passengers.travel.SingleDestinationTravel;
import cecs277.passengers.travel.TravelStrategy;

import java.util.List;
import java.util.Random;

public class JoyriderFactory implements PassengerFactory {

    private int mWeight;
    public JoyriderFactory(){ mWeight = 2; }
    public JoyriderFactory(int weight) {
        mWeight = weight;
    }

    @Override
    public String factoryName() {
        return "Joyrider";
    }

    @Override
    public String shortName() {
        return "JR";
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

        int total = 0;
        for (int i = 2; i <= buildingFloors; i++) {
            total += i;
        }
        int s = r.nextInt(total);

        int destinationFloor = 2;
        int weight = 2;
        while (s >= weight) {
            destinationFloor++;
            weight += destinationFloor;
        }

        long randomDuration = (long)(r.nextGaussian() * 1200 + 3600);

        return new SingleDestinationTravel(destinationFloor, randomDuration);
    }

    @Override
    public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
        // I add a if statement in elevator. When adding a passenger onto the elevator,
        // it will check the name of the passenger. If the passenger is a joyrider, then
        // it will not set a floor request.
        return new ResponsibleEmbarking();
    }

    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new AttentiveDebarking();
    }
}
