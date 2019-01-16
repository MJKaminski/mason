package sim.app.flockbotsim.jbox2d;

import sim.field.*;

import java.util.HashMap;
import sim.field.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.portrayal.*;
import sim.field.continuous.*;
import sim.util.*;
import sim.engine.*;

import org.jbox2d.callbacks.*;
import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.serialization.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.dynamics.joints.*;
import java.util.*;

public class FlockbotObject extends RobotObject
{
    /*
     * Physics for how Flockbots can interact with other objects in space
     */
     enum State {
          MOVING, BLOCKED
     }

     private static final float FORWARD_SPEED = 2.0f;
     public static final float FLOCKBOT_RADIUS = 4.0f;
     private static final float TIMESTEP = 1/60f;

     private Double2D previousLocation;
     private State state = State.MOVING;
     private ArrayList<LineObject> walls;

     private boolean canMove = true;

     private float rWheel;
     private float lWheel;

     private Joint jointWithCan;
     private HashMap map;

     public FlockbotObject(Body body, Continuous2D field, ArrayList<LineObject> walls, HashMap map){
          super(body, field, map);
          this.walls = walls;
          this.map = map;

          rWheel = 0.0f;
          lWheel = 0.0f;

          previousLocation = getField().getObjectLocation(this);
     }

     public void go() {

          if(canMove && !areWallsAtLocation(getNextLocation())) {
               state = State.MOVING;
          }
          else {
               state = State.BLOCKED;
          }
          switch (state)
          {
          case MOVING:
               getField().setObjectLocation(this, getNextLocation());
               Double2D currentLocation = getField().getObjectLocation(this);
               Double2D forward = getForwardDouble2D();
               getBody().setLinearVelocity(new Vec2((float)forward.x, (float)forward.y));
               previousLocation = currentLocation;
               break;

          case BLOCKED:
               getField().setObjectLocation(this, previousLocation);
               getBody().setLinearVelocity(new Vec2(0, 0));
               break;
          }
     }

     public float getRightWheelVelocity() { return rWheel; }
     public float getLeftWheelVelocity() { return lWheel; }

     public void setRightWheelVelocity(float rWheel) { this.rWheel = rWheel; }
     public void setLeftWheelVelocity(float lWheel) { this.lWheel = lWheel; }
     public void setWheelVelocities(double vR, double vL) {
          rWheel = (float)vR;
          lWheel = (float)vL;
     }
     public void setCanMove(boolean move) { canMove = move; }
     public boolean getCanMove() { return canMove; }

     /**
      * @return the flockbot's forward vector
      */
     protected Double2D getForwardDouble2D() {
          float magnitude = (0.5f)*(rWheel + lWheel);
          float L = FLOCKBOT_RADIUS * 2;
          float deltaAngle = (1/L)*(rWheel - lWheel);

          float angle = getBody().getAngle() + deltaAngle;
          getBody().setTransform(getBody().getPosition(), angle);

          // System.out.println("mag: " + magnitude + "    angle: " + angle);
          float x = (float)(Math.cos((double)angle) * magnitude);
          float y = (float)(Math.sin((double)angle) * magnitude);
          return new Double2D(x, y);
     }

     public Double2D getNextLocation() {
          Double2D currentLocation = getField().getObjectLocation(this);
          Double2D forward = getForwardDouble2D();
          // TODO: remove magic number, the timestep should be able to get seen from somewhere
          Double2D nextStep = new Double2D(forward.x * TIMESTEP, (-1) * forward.y * TIMESTEP);
          Double2D nextLocation = currentLocation.add(nextStep);
          return nextLocation;
     }
     /**
      * Check if the given point is inside any of the wall rectangles
      * Ideas: https://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle
      */
     private boolean areWallsAtLocation(Double2D location) {
          // Double2D center = getField().getObjectLocation(this);
          for (LineObject wall : walls) {
               if (wall.collidingWithPoint(location, FLOCKBOT_RADIUS)) return true;
          }
          return false;
     }

     protected RaycastResult raycast() {
          Double2D loc = getField().getObjectLocation(this);
          final Double2D currentLocation = new Double2D(loc.x, 100 - loc.y);
          final double angle = (double)getBody().getAngle();
          final double raycastDist = 4.5;

          final Double2D endpt = new Double2D(currentLocation.x + Math.cos(angle)*raycastDist,
                                              (currentLocation.y + Math.sin(angle)*raycastDist));

          final RaycastResult[] result = new RaycastResult[1]; // im sorry for this
          result[0] = new RaycastResult();
          getBody().m_world.raycast(new RayCastCallback() {
                    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
                         if (fixture.m_body == getBody()) {
                              return -1;
                         } else {
                              double dist = currentLocation.distance((double)point.x, (double)point.y);
                              result[0].bodyHit = fixture.m_body;

                              if (map.get(fixture.m_body) instanceof LineObject)
                              {
                                System.out.println("Hitting a Wall");
                              }
                              if (map.get(fixture.m_body) instanceof BoxObject)
                              {
                                System.out.println("Hitting a Box");
                              }
                              if (map.get(fixture.m_body) instanceof CanObject)
                              {
                                System.out.println("Hitting a Can");
                              }
                              if (map.get(fixture.m_body) instanceof MasonFlockbot)
                              {
                                System.out.println("Hitting a Flockbot");
                              }


                              result[0].setDistanceFromMasonUnits(dist);
                              System.out.println(dist);
                              return 0;
                         }
                    }
               }, new Vec2((float)currentLocation.x, (float)currentLocation.y), new Vec2((float)endpt.x, (float)endpt.y));

          return result[0];
     }

     protected boolean setConstraint() {
          if (jointWithCan != null) return false;

          World world = getBody().m_world;

          Bag neighbors = getField().getNeighborsExactlyWithinDistance(getField().getObjectLocation(this), 2);
          JBox2DObject closest = (JBox2DObject)neighbors.get(0);

          if(closest instanceof CanObject) {
               PrismaticJointDef jdef = new PrismaticJointDef();
               jdef.bodyA = getBody();
               jdef.bodyB = closest.getBody();
               jdef.enableLimit = true;
               jdef.upperTranslation = 2f;
               jdef.lowerTranslation = 1f;
               // jdef.collideConnected = true;
               // jdef.dampingRatio = 1f;
               // jdef.initialize(body, closest.body, new Vec2(0, 0), new Vec2(0,0));
               jointWithCan = world.createJoint(jdef);
               return true;
          }

          return false;
     }

     protected void releaseConstraint() {
          if (jointWithCan != null) {
               getBody().m_world.destroyJoint(jointWithCan);
               jointWithCan = null;
          }
     }
}

class RaycastResult {
     double mmfromBodyHit = -1.0;
     Body bodyHit;

     void setDistanceFromMasonUnits(double masonDist) {
          double distMeters = masonDist * 0.04445;
          double distMM = distMeters * 1000;
          mmfromBodyHit = (distMM == 0) ? -1 : distMM;
     }
}
