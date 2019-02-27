package sim.app.flockbotsim;
import java.util.EnumSet;
import java.util.function.Function;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public abstract class AbstractObject {
    /*
     * All objects that interact in the simulation need to extend from this, it'll make it easier.
     * At least make something like this to give everything equal gravity etc.
     * Basically you're defining two fields, Body body and BodyDef bdef, to define how your object should interact in space.
     * After that, you create the fixture to actually give it a physical presence in that space.
     *
     * This supports direct extension or a Builder pattern.
     * 
     */
    private Body body;
    private BodyDefinitionBuilder bdef = BodyDefinitionBuilder.newBodyDefBuilder().setLinearDamping(1.0f).setAngularDamping(1.5f);
    private World world;


    public AbstractObject(World world, BodyDef bdef){
        body = world.createBody(bdef);
        makeFixture();
    }

    public abstract void makeFixture(); // Force each object to create their own fixture.

    public AbstractObject setPosition(Vec2 pos){
        bdef = bdef.setPosition(pos);
        return this;
    }

    public AbstractObject setAngle(float angle){
        bdef = bdef.setAngle(angle);
        return this;
    }

    public AbstractObject setWorld(World world){
        this.world = world;
        return this;
    }

    public AbstractObject setBodyType(BodyType bodyType) throws IllegalArgumentException{
        if(EnumSet.allOf(BodyType.class).contains(bodyType)){
            bdef = bdef.setBodyType(bodyType);
        } else {
            throw new IllegalArgumentException("BodyType error, value " + bodyType);
        }
        return this;
    }

    public AbstractObject build() throws NullPointerException{
        body = world.createBody(bdef.build());
        makeFixture();
        return this;
    }

    //get methods
    public BodyDef getBodyDef(){
        return bdef.build();
    }
    public Body getBody(){
        return body;
    }
    //modifier set methods
    public void setBody(Body b){
        body = b;
        makeFixture();
    }
    public void setVelocity(Vec2 vec){
        body.setLinearVelocity(vec);
    }

}
