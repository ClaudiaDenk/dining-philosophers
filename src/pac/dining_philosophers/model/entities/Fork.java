package pac.dining_philosophers.model.entities;

import java.util.Observable;

/**
 * Model of a single fork.
 */
public class Fork extends Observable
{

    /**
     * True if the fork is taken, otherwise false.
     */
    private boolean taken = false;

    /**
     * Puts back the fork on the table.
     */
    public final synchronized void put()
    {
        this.taken = false;
        this.notify();
    }

    /**
     * Gets the fork form the table. The operation will wait until the fork is available.
     * This step may cause dead-locks.
     *
     * @throws InterruptedException Thrown if the operation was interrupted.
     */
    public final synchronized void get() throws InterruptedException
    {
        while (this.taken)
        {
            this.wait();
        }
        this.taken = true;
    }

    /**
     * Checks if the fork is currently in use.
     *
     * @return True if the fork is taken, false if the fork is available.
     */
    public final synchronized boolean isTaken()
    {
        return this.taken;
    }

}
