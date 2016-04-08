package pac.dining_philosophers.model.states;

/**
 * Represents all possible states of a philosopher.
 *
 * @author Claudia Panoch
 */
public enum PhilosopherState
{

    THINKING, HUNGRY, GOT_RIGHT_FORK, GOT_LEFT_FORK, EATING;

    /**
     * Gets the readable name of the state.
     *
     * @return The readable name of the state.
     */
    public String getName()
    {
        return this.name().replace('_', ' ').toLowerCase();
    }

}
