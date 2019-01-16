package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Can extends AbstractObject {
    /*
     * Made a Can using AbstractObject template.
     * It ~bounces~ a little. These are little collectibles for Flockbots to have.
     */

    public Can(Vec2 pos, float angle, World world){
      super(pos, angle, world, BodyType.DYNAMIC);
    }
    
    @Override
    public void makeFixture(){
      CircleShape circle = new CircleShape();
      circle.setRadius(0.75f);
      FixtureDef fixtureDef = new FixtureDef();
      fixtureDef.shape = circle;
      fixtureDef.density = 0.5f;
      fixtureDef.friction = 0.4f;
      fixtureDef.restitution = 0.6f; // Make it bounce a little bit
      fixtureDef.filter.groupIndex = -1;
      Fixture fixture = getBody().createFixture(fixtureDef);
    }
}
