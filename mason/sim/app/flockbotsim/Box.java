package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Box extends AbstractObject {
    /*
     * Made a Box using AbstractObject template.
     * You can run into it and it'll move with you.
     */

  
    private Box(World world, BodyDef bdef){
        super(world, bdef);
    }
    public Box(Vec2 pos, float angle, World world){
      this(world, BodyDefinitionBuilder.newBodyDefBuilder().setPosition(pos).setAngle(angle).setBodyType(BodyType.DYNAMIC).build());
    }

    @Override
    public void makeFixture(){
      PolygonShape boxShape = new PolygonShape();
      boxShape.setAsBox(2f, 2f);
      FixtureDef fixtureDef = new FixtureDef();
      fixtureDef.density = 2.0f;
      fixtureDef.shape = boxShape;
      Fixture fixture = getBody().createFixture(fixtureDef);
    }

}
