package cecs277.logging;

import cecs277.Simulation;

public class StandardOutLogger extends Logger {

    public StandardOutLogger(Simulation simulation) {
        super(simulation);
    }

    @Override
    public void logString(String message) {
        System.out.println(getSimulation().currentTime() + "s: " + message);
    }
}
