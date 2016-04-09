package pac.dining_philosophers.view;

import pac.dining_philosophers.model.entities.Fork;
import pac.dining_philosophers.model.entities.Philosopher;
import pac.dining_philosophers.model.states.SimulationState;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;

/**
 * Draws the current simulation state.
 */
public class DinersDrawingSurface extends JPanel implements Observer
{

    /**
     * The size of one plate.
     */
    public static final int PLATE_SIZE = 50;

    /**
     * The size of one plate.
     */
    public static final int FORK_LENGTH = 90;

    /**
     * The size of one plate.
     */
    public static final int FORK_WIDTH = 9;

    /**
     * The size of the whole table.
     */
    public static final int TABLE_SIZE = 330;

    /**
     * Standard font for big texts.
     */
    public static final Font FONT_BIG = new Font(Font.SANS_SERIF, Font.BOLD, 44);

    /**
     * Standard font for small texts.
     */
    public static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.BOLD, 20);

    /**
     * The reference width of the window.
     */
    public static final double REFERENCE_WIDTH = 1024;

    /**
     * The reference height of the window.
     */
    public static final double REFERENCE_HEIGHT = 1024;

    /**
     * .
     */
    public static final double DOUBLE = 2.0;

    /**
     * .
     */
    public static final double HALF = 0.5;

    /**
     * Deadlock text.
     */
    private static final String MESSAGE_DEADLOCKED = "DEADLOCKED";

    /**
     * Deadlock image name.
     */
    private static final String NAME_DEADLOCKED = "deadlocked";

    /**
     * The current state representation of the simulation.
     */
    private SimulationState simulationState = null;

    /**
     * Setup the surface.
     */
    public DinersDrawingSurface()
    {
        super();
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
    }

    /**
     * Paints a single frame.
     *
     * @param graphics The drawing object.
     */
    public void paint(final Graphics graphics)
    {
        super.paint(graphics);
        if (this.simulationState == null) return;
        final SimulationState simulationState = this.simulationState;
        final Philosopher[] philosophers = simulationState.getPhilosophers();
        final Fork[] forks = simulationState.getForks();
        final double scalingFactor = Math.min((double) this.getWidth() / REFERENCE_WIDTH, (double) this.getHeight() / REFERENCE_HEIGHT);
        final Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.translate(this.getWidth() * HALF, this.getHeight() * HALF);
        graphics2D.scale(scalingFactor, scalingFactor);

        /* Draw the table */
        graphics2D.setColor(Color.DARK_GRAY);
        graphics2D.fillOval(-(int) (TABLE_SIZE * HALF), -(int) (TABLE_SIZE * HALF), TABLE_SIZE, TABLE_SIZE);
        graphics2D.setColor(Color.BLACK);


        /*  */
        final int count = philosophers.length;
        for (int currentPlace = 0; currentPlace < count; ++currentPlace)
        {

            /* Draw philosopher. */
            final String imageName = simulationState.isDeadlocked() ? NAME_DEADLOCKED : simulationState.getPhilosopher(currentPlace).getPhilosopherState().name().toLowerCase();
            final Image philosopherImage = this.getImage(imageName);
            graphics2D.drawImage(this.getImage(imageName), this.getTransformationMatrix(currentPlace, count, philosopherImage.getWidth(this), philosopherImage.getHeight(this), TABLE_SIZE), this);

            /* Draw plate. */
            final Rectangle plateRectangle = this.getPlateRectangle(currentPlace, count);
            graphics2D.setColor(Color.LIGHT_GRAY);
            graphics2D.fillOval((int) plateRectangle.getX(), (int) plateRectangle.getY(), (int) plateRectangle.getWidth(), (int) plateRectangle.getHeight());

            /* Draw fork. */
            if (!forks[currentPlace].isTaken())
            {
                final Shape forkShape = this.getForkShape(currentPlace, count);
                graphics2D.setColor(Color.BLACK);
                graphics2D.fill(forkShape);
            }

            /* Draw label. */
            final String name = philosophers[currentPlace].toString();
            final FontRenderContext fontRenderContext = new FontRenderContext(null, false, false);
            final TextLayout wordLayout = new TextLayout(name, FONT_SMALL, fontRenderContext);
            final int labelWidth = (int) FONT_SMALL.getStringBounds(name, fontRenderContext).getWidth();
            final Shape label = wordLayout.getOutline(this.getTransformationMatrix(currentPlace, count, labelWidth, FONT_SMALL.getSize(), (int) (TABLE_SIZE / 1.8)));
            graphics2D.setColor(Color.LIGHT_GRAY);
            graphics2D.fill(label);
        }
        if (simulationState.isDeadlocked())
        {
            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(FONT_BIG);
            graphics2D.drawString(MESSAGE_DEADLOCKED, -(int)(3.5 * FONT_BIG.getSize()), FONT_BIG.getSize() / 3);
        }
    }

    /**
     *
     * @param number
     * @param count
     * @param width
     * @param height
     * @param diameter
     * @return
     */
    private AffineTransform getTransformationMatrix(final int number, final int count, final int width, final int height, final int diameter)
    {
        final double radians = this.getRadiansForPlace(number, count);
        final AffineTransform transformationMatrix = new AffineTransform();
        transformationMatrix.rotate(radians);
        transformationMatrix.translate(0, -diameter);
        transformationMatrix.translate(-(width * HALF), -(height * HALF));
        return transformationMatrix;
    }

    /**
     *
     * @param plateNumber
     * @param plateCount
     * @return
     */
    private Rectangle getPlateRectangle(final int plateNumber, final int plateCount)
    {
        final double radians = this.getRadiansForPlace(plateNumber, plateCount);
        final int x = (int) ((-Math.sin(radians) * TABLE_SIZE * 0.33) - (PLATE_SIZE * HALF));
        final int y = (int) ((-Math.cos(radians) * TABLE_SIZE * 0.33) - (PLATE_SIZE * HALF));
        return new Rectangle(x, y, PLATE_SIZE, PLATE_SIZE);
    }

    /**
     *
     * @param forkNumber
     * @param forkCount
     * @return
     */
    private Shape getForkShape(final int forkNumber, final int forkCount)
    {
        final double radians = this.getRadiansForPlace(forkNumber, forkCount) + Math.PI / 2;
        final int x = 0;//(int) ((-Math.sin(radians) * TABLE_SIZE * 0.33));
        final int y = 0;//(int) ((-Math.cos(radians) * TABLE_SIZE * 0.33));
        final Rectangle rectangle = new Rectangle(0, 0, FORK_WIDTH, FORK_LENGTH);
        return this.getTransformationMatrix(forkNumber, forkCount, +FORK_WIDTH +2 *PLATE_SIZE, -PLATE_SIZE, TABLE_SIZE / 2).createTransformedShape(rectangle);
    }

    /**
     *
     * @param number
     * @param count
     * @return
     */
    private double getRadiansForPlace(final int number, final int count)
    {
        return (DOUBLE * Math.PI) * (1.0 - (double) number / (double) count);
    }

    /**
     * The model has been updated, so the view must be re-rendered with the new model information.
     *
     * @param observable The model.
     * @param argument An additional argument. Not used here.
     */
    @Override
    public synchronized void update(final Observable observable, final Object argument)
    {
        this.simulationState = (SimulationState) observable;
        this.repaint();
    }

    /**
     * Gets an image resource.
     *
     * @param name The name of the image.
     * @return The image resource.
     */
    private Image getImage(final String name)
    {
        return new ImageIcon("res/" + name + ".png").getImage();
    }

}
