package pac.dining_philosophers.model.entities;

import pac.dining_philosophers.model.states.PhilosopherState;
import pac.dining_philosophers.model.states.SimulationState;

import java.util.Observable;

import static pac.dining_philosophers.controller.Application.EVER;

/**
 * Model of a single philosopher.
 */
public class Philosopher extends Observable implements Runnable
{

    /**
     * The simulation state of the application.
     */
    private SimulationState simulationState;

    /**
     * Reference to the right fork.
     */
    private Fork leftFork;

    /**
     * Reference to the left fork.
     */
    private Fork rightFork;

    /**
     * The state of the philosopher, initially he is thinking.
     */
    private PhilosopherState philosopherState = PhilosopherState.THINKING;

    /**
     * The philosopher's name.
     */
    private String name = null;

    /**
     * Creates a new philosopher.
     *
     * @param leftFork The fork on the left.
     * @param rightFork The fork on the right.
     */
    public Philosopher(final SimulationState simulationState, final String name, final Fork leftFork, final Fork rightFork)
    {
        this.simulationState = simulationState;
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    /**
     * Brings the philosopher to life.
     * This runs the lifecicle (thinking, hungry, got right, eating) while the object is alive.
     * The thinking times are random, the rest happens immediatelly.
     */
    @Override
    public void run()
    {
        try
        {
            for (;EVER;)
            {

                /* thinking */
                this.setPhilosopherState(PhilosopherState.THINKING);
                Thread.sleep(this.simulationState.sleepTime());

                /* hungry */
                this.setPhilosopherState(PhilosopherState.HUNGRY);
                rightFork.get();

                /* gotright chopstick */
                this.setPhilosopherState(PhilosopherState.GOT_RIGHT_FORK);
                Thread.sleep(this.simulationState.folkGapTime());
                leftFork.get();

                /* eating */
                this.setPhilosopherState(PhilosopherState.EATING);
                Thread.sleep(this.simulationState.eatTime());
                this.rightFork.put();
                this.leftFork.put();
            }
        }
        catch (final InterruptedException ignored) { }
    }

    /**
     * Changes the current state.
     *
     * @param philosopherState The new state.
     */
    private final void setPhilosopherState(final PhilosopherState philosopherState)
    {
        this.simulationState.log(this.name + ": " + philosopherState.getName() + "\n");
        this.philosopherState = philosopherState;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Returns the current state.
     *
     * @return The current state of the philosopher.
     */
    public final PhilosopherState getPhilosopherState()
    {
        return this.philosopherState;
    }

    /**
     * Returns the name of the philosopher.
     *
     * @return The name.
     */
    @Override
    public String toString()
    {
        return this.name;
    }

}
