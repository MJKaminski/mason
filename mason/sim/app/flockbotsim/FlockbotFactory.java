package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.engine.*;

import sim.field.*;
import sim.util.*;
import sim.field.continuous.*;
import java.util.*;

import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class FlockbotFactory {
    private World world;
    private Continuous2D con2D;
    private ArrayList<LineObject> walls;

    public FlockbotFactory(World world, Continuous2D con2D) {
        this.world = world;
        this.con2D = con2D;
        walls = new ArrayList<LineObject>();
    }

    // public FlockbotObject makeFlockbotObject(SimState state) {
    //     return makeFlockbotObject(
    //         new Vec2(state.random.nextFloat() * 50 + 5,
    //                  state.random.nextFloat() * 50 + 5),
    //         state.random.nextFloat() * (float)(Math.PI * 2));
    // }

    public MasonFlockbot makeFlockbot(Vec2 pos, float rotation, HashMap map, FlockbotProgram program) {
        pos = new Vec2(pos.x, (float)con2D.getHeight() - pos.y);
        Flockbot bot = new Flockbot(pos, rotation, world);
        Body bod = bot.getBody();
        bod.setTransform(bod.getPosition(), rotation);
        MasonFlockbot botObj = new MasonFlockbot(bod, con2D, walls, map, program);
        // FlockbotObject botObj = new FlockbotObject(bot.getBody(), con2D, walls);
        // botObj.getBody().setTransform(bot.getBody().getPosition(), rotation);
        return botObj;
    }

    public LineObject makeWall(Double2D endpt1, Double2D endpt2, HashMap map) {
        double midX = (endpt1.x + endpt2.x) / 2.0;
        double midY = (endpt1.y + endpt2.y) / 2.0;
        Double2D midpt = new Double2D(midX, midY);

        double dist = endpt1.distance(endpt2);

        double deltaY = endpt1.y - endpt2.y;
        double deltaX = endpt1.x - endpt2.x;

        double radians = Math.atan2(deltaY, deltaX);

        Vec2 pos = new Vec2((float)midpt.x, (float)midpt.y);
        float size = (float)dist;
        float rotation = (float)radians;
        return makeWall(pos, size, rotation, map);
    }

    public LineObject makeWall(Vec2 pos, float size, float rotation, HashMap map) {
        pos = new Vec2(pos.x, (float)con2D.getHeight() - pos.y); // invert y
        Wall wall = new Wall(pos, size, rotation, world);
        LineObject wallObj = new LineObject(wall.getBody(), con2D, map, size);
        // wallObj.getBody().setTransform(wall.getBody().getPosition(), rotation);
        walls.add(wallObj);
        return wallObj;
    }

    public BoxObject makeBox(Vec2 pos, float rotation, HashMap map) {
        pos = new Vec2(pos.x, (float)con2D.getHeight() - pos.y);
        Box box = new Box(pos, rotation, world);
        // box.setBody(world.createBody(box.getBodyDef()));
        BoxObject boxObj = new BoxObject(box.getBody(), con2D, map);
        boxObj.getBody().setTransform(box.getBody().getPosition(), rotation);
        return boxObj;
    }

    public CanObject makeCan(Vec2 pos, float rotation, HashMap map) {
        pos = new Vec2(pos.x, (float)con2D.getHeight() - pos.y);
        Can can = new Can(pos, rotation, world);
        // box.setBody(world.createBody(box.getBodyDef()));
        CanObject canObj = new CanObject(can.getBody(), con2D, map);
        canObj.getBody().setTransform(can.getBody().getPosition(), rotation);
        return canObj;
    }
}
