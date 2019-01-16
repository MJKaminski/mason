package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Box extends AbstractObject {
    /*
     * Made a Box using AbstractObject template.
     * You can run into it and it'll move with you.
     */
  
    public Box(Vec2 pos, float angle, World world){
      super(pos, angle, world, BodyType.DYNAMIC);
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
