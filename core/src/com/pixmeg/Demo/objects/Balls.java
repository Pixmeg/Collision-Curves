package com.pixmeg.Demo.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.pixmeg.Constants;
import com.pixmeg.Demo.DemoScreen;

public abstract class Balls extends Sprite {
    DemoScreen demoScreen;
    Body body;
    World world;

    TextureRegion textureRegion;

    public Balls(DemoScreen demoScreen, TextureRegion textureRegion){
        this.demoScreen = demoScreen;
        world = demoScreen.world;
        this.textureRegion = textureRegion;
        setRegion(textureRegion);
        setBounds(0,0,Constants.BALL_RADIUS*Constants.PPM*2,Constants.BALL_RADIUS*Constants.PPM*2);
        setOrigin(getWidth()/2,getHeight()/2);
    }

    public abstract void createBody();

    public Body getBody() {
        return body;
    }

    public void update(float delta){
        setRotation(body.getAngle()* MathUtils.radiansToDegrees);
        setPosition(body.getPosition().x* Constants.PPM-getWidth()/2,body.getPosition().y*Constants.PPM-getHeight()/2);
    }
}
