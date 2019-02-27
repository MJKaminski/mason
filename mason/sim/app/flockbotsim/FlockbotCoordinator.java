package sim.app.flockbotsim;

import sim.field.*;
import sim.util.*;
import sim.field.continuous.*;
import java.util.*;
import sim.engine.*;
import java.io.*;

import sim.app.flockbotsim.jbox2d.*;
import sim.app.flockbotsim.api.*;

public class FlockbotCoordinator implements Steppable {
    private Continuous2D field;
    private List<FlockbotObject> bots;
    private Map<FlockbotObject, Boolean> bots_bool;

    public FlockbotCoordinator(Continuous2D field, List<FlockbotObject> bots) {
        this.field = field;
        this.bots = bots;
    }

    @Override
    public void step(SimState state) {
          Collections.shuffle(bots);
          bots_bool = new HashMap<FlockbotObject, Boolean>();
          for (int j = 0; j < bots.size(); j++) {
                bots_bool.put(bots.get(j), false);
          }
          for (int i = 0; i < bots.size(); i++){
              boolean move = true;
            FlockbotObject bot = bots.get(i);
            Double2D myNextLocation = bot.getNextLocation();
            for (int j = i - 1; j >= 0; j--){
                if (bots_bool.get(bots.get(j)) == true){
                  //if previous bots have set a next location then compare
                  //my next location with theirs
                  Double2D otherLocation = bots.get(j).getNextLocation();
                  double distance = otherLocation.distance(myNextLocation);
                  if (distance <= 4.0){
                      move = false;
                  }
                  Double2D otherLocation1 = field.getObjectLocation(bots.get(j));
                  double distance1 = field.getObjectLocation(bot).distance(otherLocation1);
                }
                else{
                  //if previous bots did not set a next location then compare my
                  //next location with their current
                  Double2D otherLocation = field.getObjectLocation(bots.get(j));
                  double distance = myNextLocation.distance(otherLocation);
                  if (distance <= 4.0){
                      move = false;
                  }
                }
            }
            for (int j = i + 1; j < bots.size(); j++){
              if (bots_bool.get(bots.get(j)) == false){
                Double2D otherLocation = field.getObjectLocation(bots.get(j));
                double distance = myNextLocation.distance(otherLocation);
                if (distance <= 4.0){
                    move = false;
                }
              }
            }
            bot.setCanMove(move);
            bots_bool.put(bot, true);
          }

    //     Collections.shuffle(bots);
    //
    //     System.out.println(bots);
    //     boolean move;
    //     for (int i = 0; i < bots.size(); i++) {
    //         move = true;
    //         // get bots near bots[i]
    //         FlockbotObject bot = bots.get(i);
    //         Double2D myLocation = field.getObjectLocation(bot);
    //
    //         // check next positions of bots before me
    //         for (int j = i - 1; j >= 0; j--) {
    //           System.out.println("previous bot: " + bots.get(j).getCanMove() + " move");
    //           if (bots.get(j).getCanMove()){
    //             Double2D otherLocation = bots.get(j).getNextLocation();
    //             double distance = otherLocation.distance(myLocation);
    //
    //             Double2D otherLocation1 = field.getObjectLocation(bots.get(j));
    //             double distance1 = myLocation.distance(otherLocation1);
    //
    //             if (distance <= 4.0) {
    //                 move = false;
    //             }
    //
    //             System.out.println("curr before: " + distance1);
    //             System.out.println("proj before: " + distance);
    //           }
    //           else{
    //             Double2D otherLocation = field.getObjectLocation(bots.get(j));
    //             double distance = bot.getNextLocation().distance(otherLocation);
    //             if (distance <= 4.0) {
    //                 move = false;
    //             }
    //             Double2D otherLocation1 = field.getObjectLocation(bots.get(j));
    //             double distance1 = myLocation.distance(otherLocation1);
    //             System.out.println("curr before: " + distance1);
    //             System.out.println("proj before: " + distance);
    //           }
    //         }
    //
    //         // check current positions of bots after me
    //         for (int j = i + 1; j < bots.size(); j++) {
    //             Double2D otherLocation = field.getObjectLocation(bots.get(j));
    //             double distance = bot.getNextLocation().distance(otherLocation);
    //             if (distance <= 4.0) {
    //                 move = false;
    //             }
    //             Double2D otherLocation1 = field.getObjectLocation(bots.get(j));
    //             double distance1 = myLocation.distance(otherLocation1);
    //             System.out.println("curr after: " + distance1);
    //             System.out.println("proj after: " + distance);
    //             System.out.println(bot + "L: " + myLocation);
    //             System.out.println(bots.get(j) + "L: " + otherLocation);
    //         }
    //         bot.setCanMove(move);
    // //        System.out.println(bot + ": " + move);
    //
    //     }
    //
    //     for (int j = 0; j < bots.size(); j++) {
    //       System.out.println(bots.get(j) + ": " + bots.get(j).getCanMove());
    //     }

    }

}
