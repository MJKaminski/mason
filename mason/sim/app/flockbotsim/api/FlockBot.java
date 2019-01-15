package sim.app.flockbotsim.api;

import java.io.*;
import java.net.*;

public interface FlockBot
{
  ///// Print statements for debugging
  public static final boolean _DEBUG = false;

  ///// Common constants from FlockBotBasics.h and FlockBotMotion.h
  public static final int DEFAULT_RAMP_TIME   = 250;
  public static final double GOOD_FORWARD     = 175;
  public static final double GOOD_ROTATE      = 2.0;
  public static final int GRIPPER_CAN         = 50;
  public static final int GRIPPER_OPEN        = 100;
  public static final int GRIPPER_CLOSED      = 0;
  public static final int CAMERA_UP           = 90;
  public static final int CAMERA_UP_45        = 45;
  public static final int CAMERA_FORWARD      = 0;
  public static final int CAMERA_DOWN_45      = -45;
  public static final int CAMERA_DOWN         = -90;
  public static final int MAX_SERVO_SPEED     = 2000;
  public static final int MIN_SERVO_SPEED     = 1000;
  public static final int MAX_ZERO_POINT      = 1700;
  public static final int MIN_ZERO_POINT      = 1300;
  public static final int BACKLIGHT_OFF       = 0;
  public static final int BACKLIGHT_ON        = 255;
  public static final byte JOYSTICK_OFF       = 0;
  public static final byte JOYSTICK_CENTER    = 1;
  public static final byte JOYSTICK_UP        = 2;
  public static final byte JOYSTICK_DOWN      = 3;
  public static final byte JOYSTICK_LEFT      = 4;
  public static final byte JOYSTICK_RIGHT     = 5;
  public static final int ERROR_BAD_IR_RESULT = -4;   // can be returned by the getIR...() functions

  ///// Default socket port for serial daemon
  public static final int DEFAULT_PORT        = 5000;

  ///// Packets sent and received over serial take the form
  ///// SYNC_BYTE_1 SYNC_BYTE_2 ID LENGTH[2] PAYLOAD[....] CHECKSUM
  ///// Where SYNC_BYTE_1 and SYNC_BYTE_2 are bytes, then
  ///// ID is a byte indicating a unique number for the
  ///// packet (until it rolls over of course).  LENGTH is two
  ///// bytes, little endian -- least significant first -- of the
  ///// PAYLOAD.  Finally, the CHECKSUM is just an XOR of everything
  ///// but the SYNC_BYTE_1 and SYNC_BYTE_2.
  static final byte SYNC_BYTE_1			 = 0x7E;
  static final byte SYNC_BYTE_2			 = 0x7E;

  ///// A Payload starts with a COMMAND (or "TLV") byte, followed by
  ///// arguments as data.  The following are commands we receive from
  ///// the arduino.
  static final int ACK                 = 0;
  static final int NAK                 = 1;
  static final int BOOT_UP_MESSAGE     = 2;
  static final int FLOCKBOT_INIT_ERROR = 3;
  static final int SENSOR_PACKET       = 5;

  ///// A NAK can contain a further subcommand indicating why the NAK
  ///// occurred.
  static final int BAD_PACKET          = 1;
  static final int BAD_PAYLOAD         = 2;

  ///// Here are the commands we can send to the arduino
  static final byte SET_GRIPPER         = 10;
  static final byte RELAX_GRIPPER       = 11;
  static final byte SET_CAMERA          = 12;
  static final byte RELAX_CAMERA        = 13;
  static final byte SET_LEFT_RAW        = 14;
  static final byte RELAX_LEFT          = 15;
  static final byte SET_RIGHT_RAW       = 16;
  static final byte RELAX_RIGHT         = 17;
  static final byte SET_BOTH_RAW        = 18;
  static final byte RELAX_BOTH          = 19;
  static final byte SET_LEFT_ZERO       = 20;
  static final byte SET_RIGHT_ZERO      = 21;
  static final byte SAVE_ZERO           = 22;
  static final byte SET_WHEEL_VEL       = 23;
  static final byte SET_RAMP            = 24;
  static final byte DO_HARD_STOP        = 25;
  static final byte DO_STOP             = 26;
  static final byte DO_IDLE             = 27;
  static final byte DO_FORWARD          = 28;
  static final byte DO_FORWARD_TO       = 29;
  static final byte DO_ROTATE           = 30;
  static final byte DO_ROTATE_TO        = 31;
  static final byte DO_CURVE            = 32;
  static final byte DO_CURVE_TO         = 33;
  static final byte DO_PATH             = 34;
  static final byte DO_NOTHING          = 35;
  static final byte RESET_INTEGRATOR    = 36;
  static final byte RESET_LOCAL         = 37;
  static final byte SET_POSE            = 38;
  static final byte LCD_WRITE_DATA      = 39;
  static final byte LCD_WRITE_PIC       = 40;
  static final byte LCD_PRINT           = 41;
  static final byte LCD_PRINT_INVERTED  = 42;
  static final byte LCD_CLEAR           = 43;
  static final byte LCD_PATTERN         = 44;
  static final byte LCD_SET_LIGHT       = 45;

static final int SENSOR_PAYLOAD_LENGTH = 89;
static final int BOOTUP_MESSAGE_PAYLOAD_LENGTH = 0;
static final int INIT_ERROR_PAYLOAD_LENGTH = 0;
static final int ACK_PAYLOAD_LENGTH = 1;
static final int NAK_PAYLOAD_LENGTH = 2;


  ///// Indicates that a variable hasn't been set yet
  public static final int UNINITIALIZED		 = -500;

  ///// numResets is set to this to indicate that we have failed to connect
  ///// and can make no further progress.  We're toast.
  public static final int INIT_ERROR		     = -1000;
    public short getIRFrontLeft();
    public short getIRLeft();
    public short getIRCenter();
    public short getIRRight();
    public short getIRFrontRight();
    public short getBattery();
    public boolean getPushbarTrigger();
    public boolean getGripperTrigger();
    public int getLeftWheelWatcher();
    public int getRightWheelWatcher();
    public int getJoystick();
    public float getXPosition();
    public float getYPosition();
    public float getHeading();
    public float getXVelocity();
    public float getYVelocity();
    public float getForwardVelocity();
    public float getRotationalVelocity();
    public float getLeftVelocity();
    public float getRightVelocity();
    public float getSmoothedForwardVelocity();
    public float getSmoothedRotationalVelocity();
    public float getSmoothedLeftVelocity();
    public float getSmoothedRightVelocity();
    public float getDistance();
    public float getLocalDistance();
    public int getState();
    public boolean isDone();
    public int getLeftZeroPoint();
    public int getRightZeroPoint();
    public boolean setGripperPercentage(int val);
    public void relaxGripper();
    public boolean setCameraAngle(int val);
    public void relaxCamera();
    public boolean setLeftRawVelocity(int val);
    public void relaxLeft();
    public boolean setRightRawVelocity(int val);
    public void relaxRight();
    public boolean setRawVelocity(int left, int right);
    public void relaxBoth();
    public boolean setLeftZeroPoint(int val);
    public boolean setRightZeroPoint(int val);
    public void saveZeroPoints();
    public void setWheelVelocity(double left, double right);
    public void setRampLength(int milliseconds);
    public void doHardStop();
    public void doStop();
    public void doIdle();
    public void doForward(double velocity);
    public void doForwardToDistance(double velocity, double distance);
    public void doRotate(double velocity);
    public void doRotateToHeading(double velocity, double heading);
    public void doMinimumRotateToHeading(double radiansPerSecond, double headingInRadians);
    public void doRotateByAngle(double radiansPerSecond, double relativeAngleInRadians);
    public void doCurve(double forwardVelocity, double rotationalVelocity);
    public void doCurveToHeading(double forwardVelocity, double rotationalVelocity, double heading);
    public void doCurveByAngle(double radiansPerSecond, double millimetersPerSecond, double relativeAngleInRadians);
    public void doCurveAboutPointToHeading(double radius, double millimetersPerSecond, double headingInRadians);
    public void doCurveAboutPointByAngle(double radius, double millimetersPerSecond, double relativeAngleInRadians);
    public void doFollowPath(double[] x, double[] y, double rotationalVelocity, double forwardVelocity);
    public void doNothing();
    public void resetIntegrator();
    public void resetLocalDistance();
    public void setPose(double x, double y, double orientation);
    public void writeLCDData(byte[] data, int len, int row, int column);
    public void writeLCDBitmap(byte[] data, int len, int width, int row, int column);
    public void printLCD(String data, int row, int column);
    public void printLCDInverted(String data, int row, int column);
    public void clearLCD();
    public boolean setLCDBacklight(int state);
    public void sleep(long milliseconds);		// remove this?
}
