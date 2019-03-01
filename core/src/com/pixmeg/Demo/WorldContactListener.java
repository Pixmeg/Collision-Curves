package com.pixmeg.Demo;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pixmeg.Constants;

public class WorldContactListener implements ContactListener {
    public DemoScreen demoScreen;
    public WorldContactListener(DemoScreen demoScreen){
        this.demoScreen = demoScreen;
    }
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(cDef){
            case Constants.TONY_BIT | Constants.CAP_BIT:
                Constants.BALLS_TOUCH = true;
                demoScreen.particleEffect.reset();
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
