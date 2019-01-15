package sim.app.flockbotsim.jbox2d;

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

import java.util.*;

public class LineObject extends JBox2DObject
{
    private double size;

    public LineObject(Body body, Continuous2D field, HashMap map, double size) {
        super(body, field, map);
        this.size = size;
    }

    public Line getLine() {
        return new Line(getField().getObjectLocation(this), size, (double)getBody().getAngle());
    }

    public boolean collidingWithPoint(Double2D center, double radius) {
        Line line = getLine();

        if (line.endpt1.distance(center) < radius/2 || line.endpt2.distance(center) < radius/2) return true;

        double slope = line.slope;
        Double2D q;
        // System.out.println(slope);
        if (Double.isNaN(slope)) {
            // System.out.println("hi");
            q = new Double2D(line.endpt1.x, center.y);
        } else if (slope == 0) {
            q = new Double2D(center.x, line.endpt1.y);
        } else {
            // double perpSlope = -1/slope;
            // double qx = ((2/3.0f)*slope)*((center.x/slope)+center.y+(slope*line.endpt1.x)-line.endpt1.y);
            // double qy = slope*(qx-line.endpt1.x)+line.endpt1.y;
            q = line.closestPointOnLineFromPoint(center);
        }

          //System.out.println("n: " + line.endpt1 + "\tm: " + line.endpt2 + "\tq: " + q);
          double xdist = (Math.abs(line.endpt1.x - q.x) + Math.abs(line.endpt2.x - q.x));
          double ydist = Math.abs(line.endpt1.y - q.y) + Math.abs(line.endpt2.y - q.y);
          //System.out.println("xdist: " + xdist);
          //System.out.println("ydist: " + ydist);
        if( (xdist <= Math.abs(line.endpt1.x-line.endpt2.x))
        && (ydist <= Math.abs(line.endpt1.y-line.endpt2.y))) {
            double dist = center.distance(q);
            //System.out.println("distance: " + dist);
            return dist < radius/2;
        }

        return false;
    }

    public class Line
    {
        public Double2D endpt1;
        public Double2D endpt2;
        double slope;

        public Line(Double2D center, double size, double angle) {
            //System.out.println(angle);
            endpt1 = new Double2D(
                center.x - size*Math.cos(angle),
                center.y + size*Math.sin(angle)
            );
            endpt2 = new Double2D(
                center.x + size*Math.cos(angle),
                center.y - size*Math.sin(angle)
            );
            slope = (endpt1.y-endpt2.y)/(endpt1.x-endpt2.x);
        }

        public Double2D closestPointOnLineFromPoint(Double2D point) {
            Double2D p0 = endpt1;

            if (endpt1.x==endpt2.x && endpt1.y == endpt2.y)  // zero length -- they're the same point anyway, so just return that point.  We do that now to avoid dividing by zero later.
                return p0;

            Double2D p1 = endpt2;
            Double2D q = point;

            // http://www.exaflop.org/docs/cgafaq/cga1.html#Subject%201.02:%20How%20do%20I%20find%20the%20distance%20from%20a%20point%20to%20a%20line?
            //double L = Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));
            //double r = (p0.y - q.y) * (p0.y - p1.y) - (p0.x - q.x) * (p1.x - p0.x);
            //r /= L * L;

            // Fixed by Sean to avoid unnecessary square root
            double LL = (p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y);
            double r = (p0.y - q.y) * (p0.y - p1.y) - (p0.x - q.x) * (p1.x - p0.x);
            r /= LL;


            //
            //               (Ay-Cy)(Ay-By)-(Ax-Cx)(Bx-Ax)
            //        r = -----------------------------
            //                        L^2
            Double2D qPrime = new Double2D(p0.x + r * (p1.x - p0.x), p0.y + r * (p1.y - p0.y));

            //      Px = Ax + r(Bx-Ax)
            //        Py = Ay + r(By-Ay)

            double distQP0 = qPrime.distanceSq(p0);  // distanceSq is monotonic with distance, so this should be fine.
            double distQP1 = qPrime.distanceSq(p1);
            double distP0P1 = p0.distanceSq(p1);
            if (distQP0 <= distP0P1 && distQP1 <= distP0P1) //qPrime is in the segment
                return qPrime;
            else if (distQP0 > distP0P1)
                return p1;
            else
                return p0;
        }
    }
}
