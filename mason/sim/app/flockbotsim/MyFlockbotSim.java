package sim.app.flockbotsim;

import sim.util.*;
import sim.field.*;
import java.util.HashMap;

import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class MyFlockbotSim extends FlockbotSim {
     public MyFlockbotSim(long seed) {
          // set world size
          super(seed, 100, 100);
     }

     public void start() {
          super.start();
          HashMap map = new HashMap();
          for (int i = 0; i < 10; i++) {
               createFlockbot(
                    new Double2D(random.nextDouble() * (width - 10) + 5,
                                 random.nextDouble() * (height - 10) + 5),
                    random.nextDouble() * (Math.PI * 2),
                    map,
                    new FlockbotProgram() {
                         private boolean backingUp = false;

                         public void setup(FlockBot bot) {
                              bot.setWheelVelocity(2.0,
                                                   2.0);
                         }
                         public void loop(FlockBot bot) {
                              if (backingUp) {
                                   bot.setGripperPercentage(0);
                                   bot.setWheelVelocity(-2.0, -1.987);
                                   backingUp = false;
                                   bot.sleep(5000);
                              } else {
                                   bot.setWheelVelocity(2.0, 2.0);
                                   short dist = bot.getIRCenter();
                                   if (dist != -1 && dist < 40) {
                                        bot.setGripperPercentage(100);
                                        bot.setWheelVelocity(1.987,2.0);
                                        backingUp = true;
                                        bot.sleep(5000);
                                   }
                              }
                         }
                    }
                    );
          }

          for (int i = 0; i < 10; i++) {
               createBox(new Double2D(
                              random.nextDouble() * (width - 10) + 5,
                              random.nextDouble() * (height - 10) + 5), 0, map);
               createCan(new Double2D(
                              random.nextDouble() * (width - 10) + 5,
                              random.nextDouble() * (height - 10) + 5), map);
          }

          // createWall(new Double2D(50, 0.0), new Double2D(50, 100));

          createWall(new Double2D(2.0, 2.0), new Double2D(2.0, height), map);
          createWall(new Double2D(2.0, 2.0), new Double2D(width, 2.0), map);
          createWall(new Double2D(width-2, 2.0), new Double2D(width-2, height-2), map);
          createWall(new Double2D(0, height-2), new Double2D(width, height-2), map);
          super.configureSim();
     }

     public static void main(String[] args) {
          doLoop(MyFlockbotSim.class, args);
          System.exit(0);
     }
}
