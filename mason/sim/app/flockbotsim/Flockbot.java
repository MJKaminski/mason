package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.field.*;

import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class Flockbot extends AbstractObject{
  private final float radius = 2f;
  private float xPos;
  private float yPos;
  private int theta;
  private int vF;
  private int omega;
  private int length;
  private int vR;
  private int vL;
  private boolean gripperState;

  /*
   * Made a Flockbot using AbstractObject template.
   * Based on FlockbotMotion.h and other Flockbot stuff.
   * Turns out Flockbots are reaaally complicated.
   */
    
    public Flockbot(World world, BodyDef bdef){
        super(world, bdef);
    }

    public Flockbot(Vec2 pos, float angle, World world){
        this(world, BodyDefinitionBuilder.newBodyDefBuilder().setBodyType(BodyType.KINEMATIC).setAngle(angle).setPosition(pos));
    }

    @Override
    public void makeFixture(){
        // Making a Flockbot shaped object is hard.

      PolygonShape shape1 = new PolygonShape();
      Vec2[] vertices = new Vec2[6];
      vertices[0] = new Vec2(0, 0);

      float startAngle = 30.0f;
      float endAngle = 90.0f;
      float diff = endAngle - startAngle;
      for (int i = 0; i < 4; i++) {
          float offset = (diff/4) * i;
          double target = Math.toRadians(startAngle + offset);
          vertices[i+1] = new Vec2((float)Math.cos(target)*radius, (float)Math.sin(target)*radius);
      }
       vertices[5] = new Vec2(0, radius);


       PolygonShape shape2 = new PolygonShape();
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
            vertices3[i] = new Vec2((float)Math.cos(target)*radius, (float)Math.sin(target)*radius);
        }
        vertices3[9] = new Vec2(0, 0);

        Vec2[] barVertices = new Vec2[4];
        double rad = Math.toRadians(30);
        Vec2 topPt = new Vec2((float)Math.cos(rad)*radius, (float)Math.sin(rad)*radius);
        rad = 300;
        Vec2 btmPt = new Vec2((float)Math.cos(rad)*radius, (float)Math.sin(rad)*radius);
        Vec2 midPt = new Vec2(topPt.x, (topPt.y + btmPt.y)/2f);
        PolygonShape bar = new PolygonShape();
        bar.setAsBox(0.1f, 1f, new Vec2(radius-0.35f, 0), 0f);

      shape1.set(vertices, vertices.length);
      shape2.set(vertices2, vertices2.length);
      shape3.set(vertices3, vertices3.length);
      FixtureDef fixtureDefPol1 = new FixtureDef();
      fixtureDefPol1.shape = shape1;
      FixtureDef fixtureDefPol2 = new FixtureDef();
      fixtureDefPol2.shape = shape2;
      FixtureDef fixtureDefPol3 = new FixtureDef();
      fixtureDefPol3.shape = shape3;

      FixtureDef barDef = new FixtureDef();
      barDef.shape = bar;
      barDef.filter.groupIndex = -1;
      Fixture fixturePol1 = getBody().createFixture(fixtureDefPol1);
      Fixture fixturePol2 = getBody().createFixture(fixtureDefPol2);
      Fixture fixturePol3 = getBody().createFixture(fixtureDefPol3);
      Fixture barFixture = getBody().createFixture(barDef);
    }

    // Getters and Setters!
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
    public void setxPos(int x){
      xPos = x;
    }
    public void setyPos(int y){
      yPos = y;
    }
    public void setTheta(int angle){
      theta = angle;
    }
    public void setGripperState(boolean b){
      gripperState = b;
    }

}
