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
   * While mostly written soundly, there's one hack in here. In the constructor,
   * you define the BodyDef first and then create the body. After that you create the fixture.
   * You can override setupObject to to any extra setup that needs to be done inbetween. It currently takes a float,
   * because I needed it for Wall.java specifically. Please make a better hack. - hatfolk
   */
  private Body body;
  private BodyDef bdef;

    public AbstractObject(Vec2 pos, float angle, World world, BodyType bodyType) throws IllegalArgumentException{
        this(pos, angle, world, bodyType, 0); // eh
    }
    public AbstractObject(Vec2 pos, float angle, World world, BodyType bodyType, float s) throws IllegalArgumentException{
        bdef = new BodyDef();
        if(EnumSet.allOf(BodyType.class).contains(bodyType)){
            bdef.type = bodyType;
        } else {
            throw new IllegalArgumentException("BodyType error, value " + bodyType);
        }
        bdef.angle = angle;
        bdef.linearDamping = 1.0f;
        bdef.angularDamping = 1.5f;
        bdef.position.set(pos);
        body = world.createBody(bdef);

        setupObject(s);
        makeFixture();
    }
    
    public abstract void makeFixture(); // Force each object to create their own fixture.

    public void setupObject(float s){
        // hax
    }


    //get methods
    public BodyDef getBodyDef(){
      return bdef;
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
