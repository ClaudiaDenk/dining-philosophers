package pac.dining_philosophers.controller;

import pac.dining_philosophers.view.MainForm;

import javax.swing.*;

/**
 * Create a new main window and run the application.
 *
 * @author Claudia Panoch
 */
public final class Application
{

    /**
     * Used for infinite loops. for (;EVER;).
     */
    public static final boolean EVER = true;

    /**
     * Private constructor for static utility classes.
     */
    private Application() { }

    /**
     * Start the thread that runs the main form and set the look & fell of the application.
     *
     * @param arguments Not used.
     */
    public static void main(final String... arguments)
    {

        /* Native OS look & feel. */
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }

        /* Invoke main form in new thread. */
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new MainForm().start();
            }
        });
    }

}
