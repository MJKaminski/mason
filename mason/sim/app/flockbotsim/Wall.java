package sim.app.flockbotsim;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import sim.app.flockbotsim.jbox2d.*;

public class Wall extends AbstractObject{
    /*
     * Made a Wall using AbstractObject
     *
     */
    private float size;
    
    private Wall(World world, BodyDef bdef){
        super(world, bdef).build();
    }

    public Wall(Vec2 pos, float size, float angle, World world) { 
        this(world, BodyDefintionBuilder.setPosition(pos).setAngle(angle).setBodyType(BodyType.STATIC).build());
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
