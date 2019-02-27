package sim.app.flockbotsim;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.continuous.*;
import javax.swing.*;
import java.awt.*;
import sim.portrayal.simple.*;
import sim.portrayal.*;
import java.awt.geom.*;
import sim.field.*;

import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class MyFlockbotSimWithUI extends GUIState
    {
    public Display2D display;
    public JFrame displayFrame;

    public static void main(String[] args)
        {
        new MyFlockbotSimWithUI().createController();  // randomizes by currentTimeMillis
        }

    public Object getSimulationInspectedObject() { return state; }  // non-volatile

    ContinuousPortrayal2D boxesPortrayal = new ContinuousPortrayal2D();

        public MyFlockbotSimWithUI()
        {
        super(new MyFlockbotSim(System.currentTimeMillis()));
        }

    public MyFlockbotSimWithUI(SimState state)
        {
        super(state);
        }

    public static String getName() { return "Flockbots Example"; }

    public void start()
        {
        super.start();
        setupPortrayals();
        }

    public void load(SimState state)
        {
        super.load(state);
        setupPortrayals();
        }

    public void setupPortrayals()
        {
        MyFlockbotSim jbox2d = (MyFlockbotSim)state;

        boxesPortrayal.setField(jbox2d.boxes);

       boxesPortrayal.setPortrayalForClass(MasonFlockbot.class, new AdjustablePortrayal2D(
             new sim.portrayal.simple.MovablePortrayal2D(
                  new JBox2DPortrayal(new Rectangle2D.Double(0, 0, jbox2d.width, jbox2d.height), Color.pink))));

       boxesPortrayal.setPortrayalForClass(BoxObject.class, new AdjustablePortrayal2D(
                                                new sim.portrayal.simple.MovablePortrayal2D(
                                                     new JBox2DPortrayal(new Rectangle2D.Double(0, 0, jbox2d.width, jbox2d.height), Color.orange))));

       boxesPortrayal.setPortrayalForClass(CanObject.class, new AdjustablePortrayal2D(
                                                new sim.portrayal.simple.MovablePortrayal2D(
                                                     new JBox2DPortrayal(new Rectangle2D.Double(0, 0, jbox2d.width, jbox2d.height), Color.red))));

       boxesPortrayal.setPortrayalForClass(LineObject.class, new AdjustablePortrayal2D(
                                                new sim.portrayal.simple.MovablePortrayal2D(
                                                     new JBox2DPortrayal(new Rectangle2D.Double(0, 0, jbox2d.width, jbox2d.height), Color.green))));


       // boxesPortrayal.setPortrayalForAll(new AdjustablePortrayal2D(
       //                                        new sim.portrayal.simple.MovablePortrayal2D(
       //                                             new JBox2DPortrayal(new Rectangle2D.Double(0, 0, jbox2d.width, jbox2d.height), Color.white))));


        // reschedule the displayer
        display.reset();

        // redraw the display
        display.repaint();
        }

    public void init(Controller c)
        {
        super.init(c);

        // make the displayer
        display = new Display2D(800, 700, this);
        display.setBackdrop(Color.black);
//new Color(113, 101, 155)

        displayFrame = display.createFrame();
        displayFrame.setTitle("Flockbots");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        display.attach( boxesPortrayal, "Flockbots" );
        }

    public void quit()
        {
        super.quit();

        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
        }
    }
