package com.pixmeg.Demo.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.pixmeg.Constants;
import com.pixmeg.Demo.DemoScreen;

public class Tony extends Balls {
    public Tony(DemoScreen demoScreen, TextureRegion textureRegion) {
        super(demoScreen, textureRegion);
        createBody();
    }

    @Override
    public void createBody() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(Constants.TONY_INITIAL_POSITION);

        body = world.createBody(bdef);

        CircleShape circle = new CircleShape();
        circle.setRadius(Constants.BALL_RADIUS);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;
        fdef.density =1;
        fdef.filter.categoryBits = Constants.TONY_BIT;
        fdef.filter.maskBits = Constants.STAGE_BIT | Constants.LINE_BIT |Constants.CAP_BIT;

        body.createFixture(fdef).setUserData(this);

        circle.dispose();
    }
}
