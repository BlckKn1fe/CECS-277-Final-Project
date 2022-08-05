package cecs277;

import cecs277.buildings.Building;
import cecs277.events.SimulationEvent;
import cecs277.events.SpawnPassengerEvent;
import cecs277.logging.Logger;
import cecs277.logging.StandardOutLogger;
import cecs277.passengers.*;
import java.util.*;

public class Simulation {
	private Random mRandom;
	private Building b;
	private PriorityQueue<SimulationEvent> mEvents = new PriorityQueue<>();
	private long mCurrentTime;
	private List<PassengerFactory> mPassengerType = new ArrayList<>();

	/**
	 * Seeds the Simulation with a given random number generator.
	 */
	public Simulation(Random random) {
		mRandom = random;
		/* 1. Can comment the factory that someone does not want
 		 * 2. Can pass in the weight value to factory constructor
 		 * Default weight:
 		 * 		Visitor: 10
 		 * 		Worker: 2
		 * 		Child: 3
		 * 		Delivery: 2
		 * 		Stoner: 1
		 * 		Jerk: 1
		 *      Prankster: 1
		 *      Joyrider: 2
		 */
		mPassengerType.add(new VisitorFactory());
		mPassengerType.add(new WorkerFactory());
		mPassengerType.add(new ChildFactory());
		mPassengerType.add(new DeliveryPersonFactory());
		mPassengerType.add(new StonerFactory());
		mPassengerType.add(new JerkFactory());
		mPassengerType.add(new PranksterFactory());
		mPassengerType.add(new JoyriderFactory());
	}
	
	/**
	 * Gets the current time of the simulation.
	 */
	public long currentTime() {
		return mCurrentTime;
	}
	
	/**
	 * Access the Random object for the simulation.
	 */
	public Random getRandom() {
		return mRandom;
	}

	/**
	 * Gets the Factory List for creating passengers
	 */

	/***
	 * Gets the reference of the building
	 */

	public Building getBuilding() {
		return this.b;
	}

	public List<PassengerFactory> getFactory() { return mPassengerType; }

	
	/**
	 * Adds the given event to a priority queue sorted on the scheduled time of execution.
	 */
	public void scheduleEvent(SimulationEvent ev) {
		mEvents.add(ev);
	}
	
	public void startSimulation(Scanner input) {
		Logger.setInstance(new StandardOutLogger(this));
		/*
		 *	Set the number of floors and the number of elevators
		 */
		int floorsNum = 10;
		int elevatorsNum = 1;

		b = new Building(floorsNum, elevatorsNum, this);
		SpawnPassengerEvent ev = new SpawnPassengerEvent(0, b);
		scheduleEvent(ev);

		System.out.println("Starting simulation with " + floorsNum + " floors, " + elevatorsNum
				+ " elevators, and these factories: ");
		StringBuilder simInfo = new StringBuilder();
		for (PassengerFactory pf : mPassengerType) {
			simInfo.append(pf.factoryName()).append(", weight ").append(pf.factoryWeight()).append("\n");
		}
		System.out.println(simInfo.toString());
		
		long nextSimLength = -1;
		
		// Set this boolean to true to make the simulation run at "real time".
		boolean simulateRealTime = false;
		// Change the scale below to less than 1 to speed up the "real time".
		double realTimeScale = 1.0;
		
		// TODO: the simulation currently stops at 200s. Instead, ask the user how long they want to simulate.
		nextSimLength = 100;
		
		long nextStopTime = mCurrentTime + nextSimLength;
		// If the next event in the queue occurs after the requested sim time, then just fast forward to the requested sim time.
		if (mEvents.peek().getScheduledTime() >= nextStopTime) {
			mCurrentTime = nextStopTime;
		}
		
		// As long as there are events that happen between "now" and the requested sim time, process those events and
		// advance the current time along the way.
		while (!mEvents.isEmpty() && mEvents.peek().getScheduledTime() <= nextStopTime) {
			SimulationEvent nextEvent = mEvents.poll();
			
			long diffTime = nextEvent.getScheduledTime() - mCurrentTime;
			if (simulateRealTime && diffTime > 0) {
				try {
					Thread.sleep((long)(realTimeScale * diffTime * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			mCurrentTime += diffTime;
			nextEvent.execute(this);
			Logger.getInstance().logEvent(nextEvent);
		}
		
		// TODO: print the Building after simulating the requested time.
		System.out.println(b.toString());
		
		/*
		 TODO: the simulation stops after one round of simulation. Write a loop that continues to ask the user
		 how many seconds to simulate, simulates that many seconds, and stops only if they choose -1 seconds.
		*/
		Logger.getInstance().logString("Enter how many second you want to continue simulating (-1 to stop): ");
		int time = input.nextInt();
		nextStopTime = mCurrentTime + time;
		while (time != -1) {
			while (!mEvents.isEmpty() && mEvents.peek().getScheduledTime() <= nextStopTime) {
				SimulationEvent nextEvent = mEvents.poll();

				long diffTime = nextEvent.getScheduledTime() - mCurrentTime;
				if (simulateRealTime && diffTime > 0) {
					try {
						Thread.sleep((long) (realTimeScale * diffTime * 1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				mCurrentTime += diffTime;
				nextEvent.execute(this);
				Logger.getInstance().logEvent(nextEvent);
			}

			System.out.println(b.toString());

			Logger.getInstance().logString("Enter how many second you want to continue simulating (-1 to stop): ");
			time = input.nextInt();
			nextStopTime = mCurrentTime + time;
		}
	}


	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		// TODO: ask the user for a seed value and change the line below.
		System.out.print("Please enter a seed value: ");
		int seedValue = s.nextInt();

		Simulation sim = new Simulation(new Random(seedValue));
		sim.startSimulation(s);

	}
}
