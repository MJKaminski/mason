package sim.app.flockbotsim.jbox2d;

import sim.field.*;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.portrayal.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.engine.*;

import org.jbox2d.callbacks.*;
import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.serialization.*;
import org.jbox2d.collision.shapes.*;

import java.util.*;

public class BoxObject extends JBox2DObject
{
    public BoxObject(Body body, Continuous2D field, HashMap map) 
    	{
        super(body, field, map);
    }

}
