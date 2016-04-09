package pac.dining_philosophers.view;

import pac.dining_philosophers.model.states.SimulationState;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

/**
 * The main frame of the application.
 */
public class MainForm extends JFrame implements Observer
{

    /**
     * The drawing surface for the diner.
     */
    private final DinersDrawingSurface dinersDrawingSurface = new DinersDrawingSurface();

    /**
     * Text-field for log output.
     */
    private final JTextArea log = new JTextArea("Log:\n");

    /**
     * The current state model of the simulation (speed, running and so on...).
     */
    private final SimulationState simulationState = new SimulationState();

    /**
     * The application title.
     */
    private static final String TITLE = "CLAUDIA PANOCH: Dining Philosophers Problem Simulation";

    /**
     * Initialize the components and setup the model.
     */
    public MainForm()
    {

        /* Register observers of the main model. */
        this.simulationState.addObserver(this);
        this.simulationState.addObserver(this.dinersDrawingSurface);

        /* Setup the main-frame. */
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setLayout(new BorderLayout());

        /* Place the drawing surface. */
        this.dinersDrawingSurface.setSize(600, 640);
        this.add(BorderLayout.CENTER, this.dinersDrawingSurface);

        /* Add a slider for speed changes. */
        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider.setValue((slider.getMaximum() - slider.getMinimum()) / 2);
        slider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent event)
            {
                simulationState.setSpeed(slider.getMaximum() - slider.getValue());
            }
        });

        /* Add a drop down for changing the number of participants. */
        final JComboBox<Integer> scale = new JComboBox<>();
        for (final int i : new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 50, 75, 100, 125, 150, 200, 250, 333, 500, 1000, 2000, 5000, 10000 })
        {
            scale.addItem(i);
        }
        scale.setSelectedIndex(4);
        scale.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                simulationState.changeNumberOfPhilosphers((Integer) scale.getSelectedItem());
            }
        });

        /* Add the restart button. */
        final JButton restart = new JButton("Restart");
        restart.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                if (simulationState.isDeadlocked())
                {
                    simulationState.stop();
                    simulationState.start();
                }
                simulationState.thaw();
            }
        });

        /* Add the freez button. */
        final JButton freeze = new JButton("Freeze");
        freeze.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                simulationState.freeze();
            }
        });

        /* Place the controls on the main form. */
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, slider);
        mainPanel.add(BorderLayout.EAST, restart);
        mainPanel.add(BorderLayout.LINE_END, scale);
        mainPanel.add(BorderLayout.WEST, freeze);

        /* Enable the logging component. */
        this.log.setPreferredSize(new Dimension(300, 0));
        this.log.setEnabled(false);
        this.add(BorderLayout.EAST, this.log);

        this.add(BorderLayout.SOUTH, mainPanel);

        /* Let's start. */
        this.pack();
        this.setVisible(true);
    }

    /**
     * Updates the user interface on model changes. Currently only the log is updated.
     *
     * @param observable The changed model.
     * @param argument Additional arguments (currently not used).
     */
    @Override
    public void update(final Observable observable, final Object argument)
    {
        this.log.insert(((SimulationState) observable).getLog(), 0);
    }

    /**
     * Start the simulation.
     */
    public void start()
    {
        this.simulationState.start();
    }

}
