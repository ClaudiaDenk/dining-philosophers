package pac.dining_philosophers.model.utils;

/**
 * Provides names for philosophers.
 *
 * @author Claudia Panoch
 */
public class PhilosopherNameProvider
{

    /**
     * The current pointer.
     */
    private int currentIndex = -1;

    /**
     * A static list of philosopher names.
     */
    private static final String[] NAMES = new String[]
    {
        "Herr mymuesli", "Herr Dr. Oetker", "Herr Seitenbacher", "Herr Kellogg", "Herr Kölln"
    };

    /**
     * Returns philosopher names as long as they are available. If no more names are available the index is returned.
     *
     * @return The name or index for the philosopher.
     */
    public String nextName()
    {
        return ((this.currentIndex++) < (NAMES.length - 1)) ? NAMES[this.currentIndex] : "#" + Integer.toString(this.currentIndex + 1);
    }

}
