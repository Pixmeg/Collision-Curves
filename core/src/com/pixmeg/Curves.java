package com.pixmeg;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixmeg.screens.MainScreen;

public class Curves {
public MainScreen mainScreen;
public World world;
public Body body;

public Vector2[] vertices;



public Curves(MainScreen mainScreen, Vector2[] vertices, float x, float y){
    this.mainScreen = mainScreen;
    world = mainScreen.world;

    this.vertices = vertices;

    createCurve(vertices,x,y);
}

    private void createCurve(Vector2[] vertices, float x, float y){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x,y);

        body = world.createBody(bdef);
        body.setUserData(this);

        FixtureDef fdef = new FixtureDef();
        fdef.density = 1;

        float x4 = 0;
        float y4 = 0;

        float h = Constants.LINE_WIDTH/Constants.PPM;

        for(int i =0;i<vertices.length-1;i++){

            float x1 = vertices[i].x;
            float y1 = vertices[i].y;

            float x2 = vertices[i+1].x;
            float y2 = vertices[i+1].y;

            float l= (h*h)/(1+((x2-x1)*(x2-x1))/((y2-y1)*(y2-y1)));  // l = a*a, m = b*b
            float m= (h*h)-l;

            float x3 = l>=0 ? (float) Math.sqrt(l)+x2: (float) Math.sqrt(-l)+x2;
            float y3 = m>=0 ? (float) Math.sqrt(m)+y2: (float) Math.sqrt(-m)+y2;

            //for the first fixture, (x4,y4) needs to be calculated explicitly
            if(x4 == 0 && y4 == 0){
                x4 = l>=0 ? (float) Math.sqrt(l)+x1: (float) Math.sqrt(-l)+x1;
                y4 = m>=0 ? (float) Math.sqrt(m)+y1: (float) Math.sqrt(-m)+y1;
            }


            float[] vert = {x1,y1,x2,y2,x3,y3,x4,y4};

            PolygonShape shape = new PolygonShape();
            shape.set(vert);

            fdef.shape = shape;

            body.createFixture(fdef).setUserData(this);

            shape.dispose();

            x4 = x3;
            y4 = y3;

        }

    }


    public void destroyBody(){
    world.destroyBody(body);
    }


}
