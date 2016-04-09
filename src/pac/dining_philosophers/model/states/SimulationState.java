package pac.dining_philosophers.model.states;

import pac.dining_philosophers.model.utils.PhilosopherNameProvider;
import pac.dining_philosophers.model.entities.Fork;
import pac.dining_philosophers.model.entities.Philosopher;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

/**
 * The state model of the whole simulation.
 */
public class SimulationState extends Observable implements Observer
{

    /**
     * The base speed of the simulation.
     */
    private static final double BASE_SPEED = 100;

    /**
     * The simulation speed.
     */
    private int speed = 50;

    /**
     * Flag if the simulation is active.
     */
    private boolean frozen = false;

    /**
     * The threads for the philosopher objects.
     */
    private Thread[] philosopherThreads = null;

    /**
     * The philosopher objects itself.
     */
    private Philosopher[] philosophers = null;

    /**
     * The forks between the philosophers.
     */
    private Fork[] forks = null;

    /**
     * The current count of philosophers.
     */
    private int philosophersCount = 5;

    /**
     * The logging queue.
     */
    private final Queue<String> log = new LinkedList<>();

    /**
     * Stats the simulation.
     */
    public void start()
    {
        final int count = this.philosophersCount;
        final PhilosopherNameProvider nameProvider = new PhilosopherNameProvider();
        this.philosopherThreads = new Thread[count];
        this.philosophers = new Philosopher[count];
        this.forks = new Fork[count];

        /* Initialize all forks. */
        for (int currentFork = 0; currentFork < count; ++currentFork)
        {
            this.forks[currentFork] = new Fork();
            this.forks[currentFork].addObserver(this);
        }

        /* Now initialize all philosophers and link the folks with the philosophers. Start every philosopher thread. */
        for (int currentPhilosopher = 0; currentPhilosopher < count; ++currentPhilosopher)
        {
            final Fork leftFork = this.forks[(currentPhilosopher - 1 + count) % count];
            final Fork rightFork = this.forks[currentPhilosopher];
            final Philosopher philosopher = new Philosopher(this, nameProvider.nextName(), leftFork, rightFork);
            philosopher.addObserver(this);
            this.philosophers[currentPhilosopher] = philosopher;
            this.philosopherThreads[currentPhilosopher] = new Thread(philosopher);
            this.philosopherThreads[currentPhilosopher].start();
        }
    }

    /**
     * Interrupt all running philosopher threads.
     */
    public void stop()
    {
        for (final Thread philosopherThread : this.philosopherThreads)
        {
            philosopherThread.interrupt();
        }
    }

    /**
     * Get the random sleep time based on the current simulation speed.
     *
     * @return The time to sleep in milliseconds.
     */
    public int sleepTime()
    {
        return (this.speed * ((int) (SimulationState.BASE_SPEED * Math.random())));
    }

    /**
     * Get the random eat time based on the current simulation speed.
     *
     * @return The time to eat in milliseconds.
     */
    public int eatTime()
    {
        return (this.speed * ((int) ((SimulationState.BASE_SPEED / 2) * Math.random())));
    }

    /**
     * The gap between
     *
     * @return
     */
    public long folkGapTime()
    {
        return 500;
    }

    /**
     * Sets the current simulation speed.
     *
     * @param speed The new speed.
     */
    public void setSpeed(final int speed)
    {
        this.speed = speed;
    }

    /**
     * Pauses the simulation.
     */
    public synchronized void freeze()
    {
        this.frozen = true;
    }

    /**
     * Resumes the simulation.
     */
    public synchronized void thaw()
    {
        this.frozen = false;
        this.notifyAll();
    }

    /**
     * Delegates model changes.
     *
     * @param observable The cause of the change (unused).
     * @param argument Additional arguments (unused).
     */
    @Override
    public synchronized void update(final Observable observable, final Object argument)
    {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Get a single philosopher by it's index.
     *
     * @param index The index of the philosopher.
     * @return The philosopher.
     */
    public Philosopher getPhilosopher(final int index)
    {
        return this.philosophers[index];
    }

    /**
     * Gets an array with all philosophers.
     *
     * @return All philosophers.
     */
    public final Philosopher[] getPhilosophers()
    {
        return this.philosophers;
    }

    /**
     * Gets an array with all forks.
     *
     * @return All forks.
     */
    public final Fork[] getForks()
    {
        return this.forks;
    }

    /**
     * Detect deadlocks. A deadlock is given if every philosopher as exactly one right fork and no left fork.
     *
     * @return True if a deadlock was produced.
     */
    public boolean isDeadlocked()
    {
        boolean deadlock = true;
        for (final Philosopher philosopher : this.getPhilosophers())
        {
            deadlock &= (philosopher.getPhilosopherState() == PhilosopherState.GOT_RIGHT_FORK);
        }
        return deadlock;
    }

    /**
     * Add a log entry.
     *
     * @param entry The text of the entry.
     */
    public synchronized void log(final String entry)
    {
        this.log.offer(entry);
    }

    /**
     * Gets the log as string
     *
     * @return The log as string.
     */
    public String getLog()
    {
        final StringBuilder log = new StringBuilder();
        String logEntry = null;
        while ((logEntry = this.log.poll()) != null)
        {
            log.append(logEntry);
        }
        return log.toString();
    }

    /**
     * Change the number of philosophers in the simulation. This will cause a restart of the simulation.
     *
     * @param newNumberOfPhilosophers The new number of philosophers.
     */
    public void changeNumberOfPhilosphers(final Integer newNumberOfPhilosophers)
    {
        this.stop();
        this.philosophersCount = newNumberOfPhilosophers;
        this.start();
    }

}
