package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class Box {
  private Body body;
  private BodyDef bdef;

  // BodyDef bd = new BodyDef();
	// 			bd.type = BodyType.DYNAMIC;
	// 			bd.position.set(y.add(new Vec2(40.0f, 40.0f)));
	// 			Body body = world.createBody(bd);
	// 			Fixture fixture = body.createFixture(shape, 5.0f);
	// 			y.addLocal(deltaY);

	// 			// Add to to MASON
  // 			JBox2DObject object = new JBox2DObject(body, boxes, true);
  
    public Box(Vec2 pos, float angle, World world){
      bdef = new BodyDef();
      bdef.type = BodyType.DYNAMIC;
      bdef.angle = angle;
      bdef.linearDamping = 1.0f;
      bdef.angularDamping = 1.5f;
      bdef.position.set(pos);

      body = world.createBody(bdef);

      PolygonShape boxShape = new PolygonShape();
      boxShape.setAsBox(2f, 2f);
      FixtureDef fixtureDef = new FixtureDef();
      fixtureDef.density = 2.0f;
      fixtureDef.shape = boxShape;

      Fixture fixture = body.createFixture(fixtureDef);
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
      PolygonShape boxShape = new PolygonShape();
      boxShape.setAsBox(2f, 2f);
      FixtureDef fixtureDef = new FixtureDef();
      fixtureDef.density = 2.0f;
      fixtureDef.shape = boxShape;

      Fixture fixture = body.createFixture(fixtureDef);
    }
    public void setVelocity(Vec2 vec){
      body.setLinearVelocity(vec);
    }

}
