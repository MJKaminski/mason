package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.app.flockbotsim.jbox2d.*;

public class Wall extends AbstractObject{
    /*
     * Made a Wall using AbstractObject
     * Had to make a hack here because higher order functions are wonky in Java. See AbstractObject.java
     *
     */
    private float size;

    public Wall(Vec2 pos, float size, float angle, World world) { 
        super(pos, angle, world, BodyType.STATIC, size);
    }

    @Override
    public void setupObject(float s){
        setSize(size);
    }

    public void setSize(float size){
        this.size = size;
    }

    public float getSize(){
        return size;
    }

    @Override
    public void makeFixture(){
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size, 0.1f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        Fixture fixture = getBody().createFixture(fixtureDef);
    }
}
