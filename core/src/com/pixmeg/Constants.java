package com.pixmeg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {

    public static final float V_WIDTH = 800;             //viewport width
    public static final float V_HEIGHT = 480;            //viewport height

    public static final float PPM = 32;                  //pixel per meter
    public static final float LINE_WIDTH = 8;
    public static final Color LINE_COLOR1 = Color.DARK_GRAY;
    public static final Color LINE_COLOR2 = Color.DARK_GRAY;

    public static final Vector2 TONY_INITIAL_POSITION = new Vector2(150/PPM,400/PPM);
    public static final Vector2 CAP_INITIAL_POSITION = new Vector2(500/PPM,350/PPM);

    public static final float BALL_RADIUS = 26/PPM;
    public static boolean BEGIN = false;
    public static boolean BALLS_TOUCH = false;
    public static final short TONY_BIT = 2;
    public static final short CAP_BIT = 4;
    public static final short LINE_BIT = 8;
    public static final short STAGE_BIT = 16;


    public static  boolean GAME_OVER = false;
}
