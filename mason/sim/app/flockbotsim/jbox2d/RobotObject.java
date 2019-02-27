package sim.app.flockbotsim.jbox2d;

import sim.field.*;

import java.util.HashMap;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.portrayal.*;
import sim.field.continuous.*;
import sim.util.*;

import org.jbox2d.callbacks.*;
import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.serialization.*;
import org.jbox2d.collision.shapes.*;
import sim.engine.*;

public abstract class RobotObject extends JBox2DObject
{
    /*
     * A generalized class for implementing dynamic agents.
     */
    public RobotObject(Body body, Continuous2D field) {
    	this(body, field, false);
    }

    public RobotObject(Body body, Continuous2D field, HashMap map) {
      super(body, field, map);
    }

    public RobotObject(Body body, Continuous2D field, boolean invertY) {
        super(body, field, invertY);
    }

    public RobotObject(Body body, Continuous2D field, Vec2 translation) {
    	this(body, field, false, translation);
    }

    public RobotObject(Body body, Continuous2D field, boolean invertY, Vec2 translation) {
			this(body, field, invertY);
			body.setTransform(body.getPosition().add(translation), body.getAngle());
	}

	public void step(SimState state) {
		super.step(state);
		go();
	}

	/**
	 * Method containing custom robot behavior.
	 * Called every step.
	 */
	public abstract void go();
}
