package com.pixmeg.Demo;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixmeg.Constants;
import com.pixmeg.GameClass;

public class HUD {

    DemoScreen demoScreen;
    public Stage stage;
    Viewport viewport;
    Skin skin;

    private int time = 100;

    private Label timer;
    public HUD(DemoScreen demoScreen){
        this.demoScreen = demoScreen;
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT);
        stage = new Stage(viewport,demoScreen.batch);
        skin = demoScreen.skin;

        Table table = new Table();
        table.setFillParent(true);
      //  table.debug();
        table.top();


        Label label = new Label("Level  ",skin);
        Label level = new Label("2",skin);

        timer = new Label("",skin);

        table.add(label).left().padLeft(30);
        table.add(level).left();
        table.add(timer).expandX().padRight(100);

        stage.addActor(table);
    }

    public void updateTimer(){
        time -= 1;
        timer.setText(String.valueOf(time));
    }

    public void createDialog(final GameClass gameClass){
        Dialog gameOverDialog = new Dialog("",skin,"gameOver"){

            @Override
            public float getPrefWidth() {
                return 400*0.8f;
            }

            @Override
            public float getPrefHeight() {
                return 240*0.8f;
            }

            @Override
            protected void result(Object object) {
                boolean value = (Boolean)object;
                if(value) {
                    Constants.GAME_OVER = false;
                    Constants.BEGIN = false;
                    gameClass.setDemoScreen(new DemoScreen(gameClass));
                }
            }

        };

        TextButton home = new TextButton("",skin,"home");
        TextButton replay = new TextButton("",skin,"replay");
        TextButton next = new TextButton("",skin,"next");

        gameOverDialog.getButtonTable().defaults().width(52).height(52).padBottom(20).spaceRight(40);
        gameOverDialog.button(home,false);
        gameOverDialog.button(replay,true);
        gameOverDialog.button(next,false);

        gameOverDialog.show(stage);

    }

    public float getTime() {
        return time;
    }

    public void setTime(int t){
        time = t;
        timer.setText(String.valueOf(time));
    }
}
