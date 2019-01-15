package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.field.*;

import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class Flockbot{
  private final float radius;
  private float xPos;
  private float yPos;
  private int theta;
  private int vF;
  private int omega;
  private int length;
  private int vR;
  private int vL;
  private Body body;
  private BodyDef bdef;
  private boolean gripperState;

    public Flockbot(Vec2 pos, float angle, World world){
      bdef = new BodyDef();
      bdef.type = BodyType.KINEMATIC;
      bdef.angle = angle;
      bdef.position.set(pos);


      body = world.createBody(bdef);

      radius = 2f;

      PolygonShape shape1 = new PolygonShape();
      Vec2[] vertices = new Vec2[6];
      vertices[0] = new Vec2(0, 0);

      float startAngle = 30.0f;
      float endAngle = 90.0f;
      float diff = endAngle - startAngle;
      // System.out.println("---- begin bot ----");
      for (int i = 0; i < 4; i++) {
          float offset = (diff/4) * i;
          double target = Math.toRadians(startAngle + offset);
          vertices[i+1] = new Vec2((float)Math.cos(target)*radius, (float)Math.sin(target)*radius);
      }
       vertices[5] = new Vec2(0, radius);


       PolygonShape shape2 = new PolygonShape();
       //Vec2[] vertices = new Vec2[12];
       Vec2[] vertices2 = new Vec2[6];
       vertices2[0] = new Vec2(0, 0);
       for (int i = 0; i < 4; i++) {
           vertices2[i+1] = new Vec2(vertices[i+1].x, (float)((-1)*vertices[i+1].y));
       }
        vertices2[5] = new Vec2(0, (-1)*radius);

        float startAngle2 = 90.0f;
        float endAngle2 = 300.0f;
        float diff2 = endAngle2 - startAngle2;
        PolygonShape shape3 = new PolygonShape();
        Vec2[] vertices3 = new Vec2[10];
        for (int i = 0; i < 9; i++) {
            float offset = (diff2/8) * i;
            double target = Math.toRadians(startAngle2 + offset);
            // System.out.println("degree: " + (startAngle2 + offset));
            // System.out.println("radians: " + target);
            vertices3[i] = new Vec2((float)Math.cos(target)*radius, (float)Math.sin(target)*radius);
            // System.out.println(target + " cos: " + Math.cos(target));
            // System.out.println(target + " sin: " + Math.sin(target));
            // System.out.println("next angle step");
        }
        vertices3[9] = new Vec2(0, 0);

      // // System.out.println("SHAPE 1: PIZZA TOP");
      // for (Vec2 v : vertices) {
      //     // System.out.println(v.x + ", " + v.y);
      // }
      // System.out.println("SHAPE 2: PIZZA BOTTOM");
      // for (Vec2 v : vertices2) {
      //     System.out.println(v.x + ", " + v.y);
      // }
      // System.out.println("SHAPE 3: SEMICIRCLE");
      // for (Vec2 v : vertices3) {
      //     System.out.println(v.x + ", " + v.y);
      // }
        Vec2[] barVertices = new Vec2[4];
        double rad = Math.toRadians(30);
        Vec2 topPt = new Vec2((float)Math.cos(rad)*radius, (float)Math.sin(rad)*radius);
        rad = 300;
        Vec2 btmPt = new Vec2((float)Math.cos(rad)*radius, (float)Math.sin(rad)*radius);
        Vec2 midPt = new Vec2(topPt.x, (topPt.y + btmPt.y)/2f);
        PolygonShape bar = new PolygonShape();
        bar.setAsBox(0.1f, 1f, new Vec2(radius-0.35f, 0), 0f);

      // add vertices to polygon shape
      shape1.set(vertices, vertices.length);
      shape2.set(vertices2, vertices2.length);
      shape3.set(vertices3, vertices3.length);
      // add polygon shape to fixture def
      FixtureDef fixtureDefPol1 = new FixtureDef();
      fixtureDefPol1.shape = shape1;
      FixtureDef fixtureDefPol2 = new FixtureDef();
      fixtureDefPol2.shape = shape2;
      FixtureDef fixtureDefPol3 = new FixtureDef();
      fixtureDefPol3.shape = shape3;

      FixtureDef barDef = new FixtureDef();
      barDef.shape = bar;
      barDef.filter.groupIndex = -1;
      // make fixture with fixture def
      Fixture fixturePol1 = body.createFixture(fixtureDefPol1);
      Fixture fixturePol2 = body.createFixture(fixtureDefPol2);
      Fixture fixturePol3 = body.createFixture(fixtureDefPol3);
      Fixture barFixture = body.createFixture(barDef);
    }

    //get methods
    public BodyDef getBodyDef(){
      return bdef;
    }
    public Body getBody(){
      return body;
    }
    public float getxPos(){
      return xPos;
    }
    public float getyPos(){
      return yPos;
    }
    public int getTheta(){
      return theta;
    }
    public int getvF(){
      return vF;
    }
    public int getOmega(){
      return omega;
    }
    public int getvR(){
      return vR;
    }
    public int getvL(){
      return vL;
    }
    public boolean getGripperState(){
      return gripperState;
    }
    //modifier set methods
    public void setBody(Body b){
      // bod = b;
      // CircleShape circle = new CircleShape();
      // circle.setRadius(radius);
      // FixtureDef fixtureDef = new FixtureDef();
      // fixtureDef.shape = circle;
      // fixtureDef.density = 0.5f;
      // fixtureDef.friction = 0.4f;
      // fixtureDef.restitution = 0.6f; // Make it bounce a little bit
      // Fixture fixture = bod.createFixture(fixtureDef);
    }
    public void setxPos(int x){
      xPos = x;
    }
    public void setyPos(int y){
      yPos = y;
    }
    public void setTheta(int angle){
      theta = angle;
    }
    public void setVelocity(Vec2 vec){
      body.setLinearVelocity(vec);
    }
    public void setGripperState(boolean b){
      gripperState = b;
    }

}
