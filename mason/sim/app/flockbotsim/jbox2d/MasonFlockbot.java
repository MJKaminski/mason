package sim.app.flockbotsim.jbox2d;

import sim.app.flockbotsim.api.*;

import sim.field.continuous.*;
import java.util.*;
import org.jbox2d.dynamics.*;

public class MasonFlockbot extends FlockbotObject implements FlockBot {
     private FlockbotProgram program;
     private long msUntilWake = 0;

     public MasonFlockbot(Body body, Continuous2D field, ArrayList<LineObject> walls, HashMap map, FlockbotProgram program) {
          super(body, field, walls, map);
          this.program = program;
          program.setup(this);
     }

     @Override
     public void go() {
          super.go();
          if (msUntilWake <= 0) {
               msUntilWake = 0;
               program.loop(this);
          } else {
               msUntilWake -= 17;
          }
     }

     public short getIRFrontLeft() { return 0; }
     public short getIRLeft() {return 0;}
     public short getIRCenter() {
          return (short)(raycast().mmfromBodyHit);
     }
     public short getIRRight() {return 0;}
     public short getIRFrontRight() {return 0;}
     public short getBattery() {return 0;}
     public boolean getPushbarTrigger() {return false;}
     public boolean getGripperTrigger() {return false;}
     public int getLeftWheelWatcher() {return 0;}
     public int getRightWheelWatcher() {return 0;}
     public int getJoystick() {return 0;}
     public float getXPosition() {return 0.0f;}
     public float getYPosition() {return 0.0f;}
     public float getHeading() {return 0.0f;}
     public float getXVelocity() {return 0.0f;}
     public float getYVelocity() {return 0.0f;}
     public float getForwardVelocity() {return 0.0f;}
     public float getRotationalVelocity() {return 0.0f;}
     public float getLeftVelocity() {return 0.0f;}
     public float getRightVelocity() {return 0.0f;}
     public float getSmoothedForwardVelocity() {return 0.0f;}
     public float getSmoothedRotationalVelocity() {return 0.0f;}
     public float getSmoothedLeftVelocity() {return 0.0f;}
     public float getSmoothedRightVelocity() {return 0.0f;}
     public float getDistance() {return 0.0f;}
     public float getLocalDistance() {return 0.0f;}
     public int getState() {return 0;}
     public boolean isDone() {return false;}
     public int getLeftZeroPoint() {return 0;}
     public int getRightZeroPoint() {return 0;}
     public boolean setGripperPercentage(int val) {
          if (val > 0) {
               return setConstraint();
          } else {
               releaseConstraint();
          }
          return false;
     }
     public void relaxGripper() {}
     public boolean setCameraAngle(int val) {return false;}
     public void relaxCamera() {}
     public boolean setLeftRawVelocity(int val) {return false;}
     public void relaxLeft() {}
     public boolean setRightRawVelocity(int val) {return false;}
     public void relaxRight() {}
     public boolean setRawVelocity(int left, int right) {return false;}
     public void relaxBoth() {}
     public boolean setLeftZeroPoint(int val) {return false;}
     public boolean setRightZeroPoint(int val) {return false;}
     public void saveZeroPoints() {}
     public void setWheelVelocity(double left, double right) {
          setWheelVelocities(right, left);
     }
     public void setRampLength(int milliseconds) {}
     public void doHardStop() {}
     public void doStop() {
          setWheelVelocity(0, 0);
     }
     public void doIdle() {
          doStop();
     }
     public void doForward(double velocity) {
          setWheelVelocity(velocity, velocity);
     }
     public void doForwardToDistance(double velocity, double distance) {}
     public void doRotate(double velocity) {}
     public void doRotateToHeading(double velocity, double heading) {}
     public void doMinimumRotateToHeading(double radiansPerSecond, double headingInRadians) {}
     public void doRotateByAngle(double radiansPerSecond, double relativeAngleInRadians) {}
     public void doCurve(double forwardVelocity, double rotationalVelocity) {}
     public void doCurveToHeading(double forwardVelocity, double rotationalVelocity, double heading) {}
     public void doCurveByAngle(double radiansPerSecond, double millimetersPerSecond, double relativeAngleInRadians) {}
     public void doCurveAboutPointToHeading(double radius, double millimetersPerSecond, double headingInRadians) {}
     public void doCurveAboutPointByAngle(double radius, double millimetersPerSecond, double relativeAngleInRadians) {}
     public void doFollowPath(double[] x, double[] y, double rotationalVelocity, double forwardVelocity) {}
     public void doNothing() {}
     public void resetIntegrator() {}
     public void resetLocalDistance() {}
     public void setPose(double x, double y, double orientation) {}
     public void writeLCDData(byte[] data, int len, int row, int column) {}
     public void writeLCDBitmap(byte[] data, int len, int width, int row, int column) {}
     public void printLCD(String data, int row, int column) {}
     public void printLCDInverted(String data, int row, int column) {}
     public void clearLCD() {}
     public boolean setLCDBacklight(int state) { return false; }

     public void sleep(long milliseconds) {
          msUntilWake = milliseconds;
     }
}
