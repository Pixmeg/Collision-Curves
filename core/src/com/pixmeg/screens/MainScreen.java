package com.pixmeg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixmeg.Constants;
import com.pixmeg.Curves;
import com.pixmeg.GameClass;

public class MainScreen extends ScreenAdapter implements InputProcessor {
    public GameClass gameClass;
    public SpriteBatch batch;
    public Viewport viewport;

    // currentArray----->contains all the points which is currently being touched on the screen.It is used to live render on screen.
    // savedArray----->contains the same points as in currentArray, but it is passed to Line class to render those points later on screen.
    // curvesArray------>Array of Curves class's objects
    public Array<Vector2> currentArray,savedArray;
    public Array<Curves> curvesArray;

    public ShapeRenderer renderer;

    public World world;
    public Body surfaceBody;         //surfaceBody------> ground
    public Box2DDebugRenderer b2dr;

    private boolean firstDraw = false;   // when firstDraw == true ,you can live render on screen using the points from currentArray

    // timer is used to avoid saving the touch points in currentArray and savedArray that are too close to each other.
    float timer = 0;

    public Vector2 start,end;
    public Vector2 tmp;  // tmp is used to save worldpoints for AOP(Area of polygon) calculation


    public MainScreen(GameClass gameClass){
        this.gameClass = gameClass;
        batch = gameClass.batch;
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT);

        savedArray = new Array<Vector2>();
        currentArray = new Array<Vector2>();
        curvesArray = new Array<Curves>();

        renderer = new ShapeRenderer();

        world = new World(new Vector2(0,-9.8f),true);
        b2dr = new Box2DDebugRenderer();

        start = new Vector2();
        end = new Vector2();
        tmp = new Vector2();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        createSurface();
    }



    public void createSurface(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(Constants.V_WIDTH/2/Constants.PPM,20/Constants.PPM);

        surfaceBody = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.V_WIDTH/2/Constants.PPM,10/Constants.PPM);


        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;

        surfaceBody.createFixture(fdef).setUserData(this);

        shape.dispose();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;

        //combined matrix is scaled by Constants.PPM, because worldPoints added in the currentArray are scaled by 1/Constants.PPM
        renderer.setProjectionMatrix(viewport.getCamera().combined.scl(Constants.PPM));
        viewport.getCamera().update();

        if(firstDraw){
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            if(currentArray.size>2){
                for(int i = 0;i<currentArray.size-1;i++){
                    Vector2 start = currentArray.get(i);
                    Vector2 end = currentArray.get(i+1);
                    renderer.rectLine(start,end,Constants.LINE_WIDTH/Constants.PPM);
                }
            }
            renderer.end();
        }

         renderer.begin(ShapeRenderer.ShapeType.Filled);
            if (curvesArray != null) {
                for (Curves curve : curvesArray) {
                    Transform transform = curve.body.getTransform();
                    Vector2[] vrt = curve.vertices;
                    for (int i = 0; i < vrt.length - 1; i++) {
                        start.x = vrt[i].x;
                        start.y = vrt[i].y;

                        end.x = vrt[i + 1].x;
                        end.y = vrt[i + 1].y;

                        transform.mul(start);
                        transform.mul(end);

                        renderer.rectLine(start, end, Constants.LINE_WIDTH / Constants.PPM);
                    }

                }
            }

            renderer.end();


            world.step(1/60f,6,2);
            b2dr.render(world,viewport.getCamera().combined.scl(Constants.PPM));
            viewport.getCamera().update();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }


    @Override
    public void dispose() {
        b2dr.dispose();
        renderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //currentArray should be cleared every touchDown to save points for new line
        currentArray.clear();
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        firstDraw = true; //live rendering begins now

        if(timer > 0.04f) {
            Vector2 worldPoints = viewport.unproject(new Vector2(screenX, screenY));
            if(AOP(tmp,worldPoints.scl(1/Constants.PPM))>0.02f){
                currentArray.add(worldPoints);
                savedArray.add(worldPoints);
                tmp.x = worldPoints.x;
                tmp.y = worldPoints.y;
            }
            else {
                System.out.println("AOP LESS THAN 0.02f");
            }
            timer = 0;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        firstDraw = false; // live rendering ends now
        x4 = 0;
        y4 = 0;

        Vector2[] vertices = new Vector2[savedArray.size];
        for(int i = 0;i< savedArray.size;i++){
            vertices[i] = savedArray.get(i);
            System.out.println("VERTICES ADDED    "+i+"    "+vertices[i]);
        }

        if(savedArray.size > 1){
            curvesArray.add(new Curves(this,vertices,0,0)); // Curves are added to the array
        }
        savedArray.clear();

        return true;
    }

    float x4 = 0;
    float y4 = 0;

    public float AOP(Vector2 v1,Vector2 v2){
        float h = Constants.LINE_WIDTH/Constants.PPM;

        float x1 = v1.x;
        float y1 = v1.y;

        float x2 =v2.x;
        float y2 = v2.y;

        float l= (h*h)/(1+((x2-x1)*(x2-x1))/((y2-y1)*(y2-y1)));
        float m= (h*h)-l;

        float x3 = l>=0 ? (float) Math.sqrt(l)+x2: (float) Math.sqrt(-l)+x2;
        float y3 = m>=0 ? (float) Math.sqrt(m)+y2: (float) Math.sqrt(-m)+y2;

        //for the first fixture, (x4,y4) needs to be calculated explicitly
        if(x4 == 0 && y4 == 0){
            x4 = l>=0 ? (float) Math.sqrt(l)+x1: (float) Math.sqrt(-l)+x1;
            y4 = m>=0 ? (float) Math.sqrt(m)+y1: (float) Math.sqrt(-m)+y1;
        }

        float area = ((x1*y2-y1*x2)+(x2*y3-y2*x3)+(x3*y4-y3*x4)+(x4*y1-y4*x1))/2;  //Area of Polygon

        x4 = x3;
        y4 = y3;

        return area>0?area:-area;

    }



    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
