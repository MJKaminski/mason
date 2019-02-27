package sim.app.flockbotsim;
import java.util.EnumSet;
import java.util.function.Function;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class BodyDefinitionBuilder{
    /*
     *  Builder Pattern for BodyDef. There's a lot of stuff that can be set.
     *  See this for specs: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/physics/box2d/BodyDef.html
     */
    private BodyDef bdef = new BodyDef();

    private BodyDefinitionBuilder(){
    }

    public static BodyDefinitionBuilder newBodyDefBuilder(){
        return new BodyDefinitionBuilder();
    }


    public BodyDefinitionBuilder setPosition(Vec2 pos){
        bdef.position.set(pos);
        return this;
    }

    public BodyDefinitionBuilder setAngle(float angle){
        bdef.angle = angle;
        return this;
    }

    public BodyDefinitionBuilder setWorld(World world){
        this.world = world;
        return this;
    }

    public BodyDefinitionBuilder setBodyType(BodyType bodyType) throws IllegalArgumentException{
        if(EnumSet.allOf(BodyType.class).contains(bodyType)){
            bdef.type = bodyType;
        } else {
            throw new IllegalArgumentException("BodyType error, value " + bodyType);
        }
        return this;
    }

    public BodyDefinitionBuilder setLinearDamping(float damping){
        bdef.linearDamping = damping;
        return this;
    }

    public BodyDefinitionBuilder setAngularDamping(float damping){
        bdef.angularDamping = damping;
        return this;
    } 

    public BodyDef build() throws NullPointerException{
        if(bdef == null){
            throw new NullPointerException("Someone unset the BodyDefinition!");
        }
        return bdef;
    }

}
