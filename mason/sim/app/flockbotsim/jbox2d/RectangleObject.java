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

public class RectangleObject extends JBox2DObject 
{
    private double width;
    private double height;
    // rotation = getBody().getAngle()
    public RectangleObject(Body body, Continuous2D field, double width, double height) {
        super(body, field, true);
        this.width = width;
        this.height = height;
    }

    public Double2D[] getVertices() {        
        // order = top left, top right, bottom left, bottom right
        Double2D[] vertices = {
            new Double2D(-width, -height),
            new Double2D(width, -height),
            new Double2D(-width, height),
            new Double2D(width, height)
        };
        Double2D center = getField().getObjectLocation(this);
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = transformToRotation(vertices[i], center); 
        }
        return vertices;
    }

    private Double2D transformToRotation(Double2D vertex, Double2D center) {
        float angle = (float)getBody().getAngle();
        // System.out.println(angle);
        return new Double2D (
            vertex.x * Math.cos(angle) - vertex.y * Math.sin(angle) + center.x,
            vertex.x * Math.sin(angle) + vertex.y * Math.cos(angle) + center.y
        );
    }

    public boolean containsPoint(Double2D point) {
        // area 1 = point, v0, v1
        // area 2 = point, v0, v2
        // area 3 = point, v2, v3
        // area 4 = point, v1, v3
        Double2D[] vertices = getVertices();
        double a1 = new Triangle(point, vertices[0], vertices[1]).getArea();
        double a2 = new Triangle(point, vertices[0], vertices[2]).getArea();
        double a3 = new Triangle(point, vertices[2], vertices[3]).getArea();
        double a4 = new Triangle(point, vertices[1], vertices[3]).getArea();
        double rectArea = 2*width * 2*height;
        double areaTriangles = a1 + a2 + a3 + a4;
        System.out.printf("area: %f, area triangles: %f\n", rectArea, areaTriangles);
        return areaTriangles <= rectArea;
    }
}

class Triangle
{
    Double2D point1;
    Double2D point2;
    Double2D point3;
    private double area;

    public Triangle(Double2D point1, Double2D point2, Double2D point3) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;

        area = calculateArea();
    }

    public double getArea() {
        return area;
    }

    private double calculateArea() {
        double side1 = point1.distance(point2);
        double side2 = point1.distance(point3);
        double side3 = point2.distance(point3);
        double s = (side1+side2+side3)/2;
        return Math.sqrt(s*(s-side1)*(s-side2)*(s-side3));
    }
}