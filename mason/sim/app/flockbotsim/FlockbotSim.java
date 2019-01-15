package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.util.*;

import com.sun.javafx.geom.Vec2d;

import sim.engine.JBox2DStep;
import sim.engine.Sequence;
import sim.engine.SimState;
import sim.field.*;
import sim.field.continuous.*;
import java.util.*;
import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class FlockbotSim extends SimState {

    private static final long serialVersionUID = 1;

    public Continuous2D boxes;
    private ArrayList agents = new ArrayList();
    private ArrayList<FlockbotObject> bots = new ArrayList<FlockbotObject>();
    private World world;
    private FlockbotFactory factory;
    private JBox2DStep step;
    protected double width;
    protected double height;

    public FlockbotSim(long seed) {
        super(seed);
    }

    public FlockbotSim(long seed, double width, double height) {
        this(seed);
        this.width = width;
        this.height = height;
    }

    public MasonFlockbot createFlockbot(Double2D position, double rotation, HashMap map, FlockbotProgram program) {
        MasonFlockbot bot = factory.makeFlockbot(new Vec2((float)position.x, (float)position.y), (float)rotation, map, program);
        agents.add(bot);
        bots.add(bot);
        return bot;
    }

    public LineObject createWall(Double2D endpt1, Double2D endpt2, HashMap map) {
        LineObject wall = factory.makeWall(endpt1, endpt2, map);
        agents.add(wall);
        return wall;
    }

    public BoxObject createBox(Double2D position, double rotation, HashMap map) {
        BoxObject box = factory.makeBox(new Vec2((float)position.x, (float)position.y), (float)rotation, map);
        agents.add(box);
        return box;
    }

    public CanObject createCan(Double2D position, HashMap map) {
        CanObject can = factory.makeCan(new Vec2((float)position.x, (float)position.y), 0f, map);
        agents.add(can);
        return can;
    }

    public void configureSim() {
        schedule.scheduleRepeating(step.getTimestep(), 0, step);
        // We schedule the agents to all get updated immediately after this Steppable,
        // so they're in Ordering 1.
        FlockbotCoordinator coordinator = new FlockbotCoordinator(boxes, bots);
        schedule.scheduleRepeating(step.getTimestep(), 1, coordinator);
        schedule.scheduleRepeating(step.getTimestep(), 2, new Sequence(agents));
    }

    public void start() {
        super.start();
        world = new World(new Vec2(0, 0));
        bots = new ArrayList<FlockbotObject>();
        agents = new ArrayList();
        boxes = new Continuous2D(4, width, height);
        factory = new FlockbotFactory(world, boxes);
        step = new JBox2DStep(world);

        //IF ROBOT RUNS INTO WALL HE STOPS, IF HE RUNS INTO A ROBOT HE ALSO STOPS. ROBOTS ARE CIRCLES IN MASON TO CALCULATE IF
        //kinematic uses velocity, user sets wheel velocity and where cans and boxes are
        //robots have an x and a y and rotation.
    }

    public static void main(String[] args) {
        doLoop(FlockbotSim.class, args);
        System.exit(0);
    }
}
