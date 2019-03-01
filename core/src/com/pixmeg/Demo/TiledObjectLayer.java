package com.pixmeg.Demo;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixmeg.Constants;

public class TiledObjectLayer {
    public static void objectLayerParser(World world, MapObjects objects){
        for(MapObject object:objects){
            Shape shape;
            if(object instanceof PolylineMapObject){
                shape = createPolyline((PolylineMapObject)object);
            }

            else{
                continue;}

            Body body;
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(bdef);

            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;
            fdef.density = 1;
            fdef.filter.categoryBits = Constants.STAGE_BIT;
            fdef.filter.maskBits = Constants.LINE_BIT|Constants.TONY_BIT|Constants.CAP_BIT;

            body.createFixture(fdef);
            shape.dispose();

        }

    }

    private static ChainShape createPolyline(PolylineMapObject object){
        float[] vertices = object.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length/2];
        for(int i = 0;i<worldVertices.length;i++){
            worldVertices[i] = new Vector2(vertices[i*2]/ Constants.PPM,vertices[i*2+1]/Constants.PPM);
        }

        ChainShape shape = new ChainShape();
        shape.createChain(worldVertices);
        return shape;
    }
}
