package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.app.flockbotsim.jbox2d.*;

public class Wall {
    private Body body;
    private BodyDef bdef;  
    private float size;

    /*
     BodyDef bd = new BodyDef();
    bd.type = BodyType.DYNAMIC;
    bd.position.set(y.add(new Vec2(40.0f, 40.0f)));
    Body body = world.createBody(bd);
    Fixture fixture = body.createFixture(shape, 5.0f);
    y.addLocal(deltaY);

    // Add to to MASON
    JBox2DObject object = new JBox2DObject(body, boxes, true);
     */

    public Wall(Vec2 pos, float size, float angle, World world) { 
        bdef = new BodyDef();
        bdef.type = BodyType.STATIC;
        bdef.position.set(pos);
        bdef.angle = angle;
        this.size = size;
        
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size, 0.1f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Fixture fixture = body.createFixture(fixtureDef);
    }

    public void setBody(Body b){
        // bod = b;
        // PolygonShape shape = new PolygonShape();
        // shape.setAsBox(size, 0.1f);
        // FixtureDef fixtureDef = new FixtureDef();
        // fixtureDef.shape = shape;
        // // fixtureDef.density = 0.5f;
        // // fixtureDef.friction = 0.4f;
        // // fixtureDef.restitution = 0.6f; // Make it bounce a little bit
        // Fixture fixture = bod.createFixture(fixtureDef);
      }
      public Body getBody() {
          return body;
      }
      public BodyDef getBodyDef() {
          return bdef;
      }
}