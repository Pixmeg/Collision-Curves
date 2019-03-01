package com.pixmeg.Demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixmeg.Constants;
import com.pixmeg.Demo.objects.Cap;
import com.pixmeg.Demo.objects.Tony;
import com.pixmeg.GameClass;

public class DemoScreen extends ScreenAdapter implements InputProcessor {
    public GameClass gameClass;
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;

    // currentArray----->contains all the points which is currently being touched on the screen.It is used to live render on screen.
    // savedArray----->contains the same points as in currentArray, but it is passed to Line class to render those points later on screen.
    // curvesArray------>Array of Line class's objects
    public Array<Vector2> currentArray,savedArray;
    public Array<Curves> curvesArray;

    public ShapeRenderer renderer;

    public World world;
    public Box2DDebugRenderer b2dr;

    private boolean firstTimeDraw = false;   // when firstTimeDraw == true ,you can live render on screen using the points from currentArray

    // timer is used to avoid saving the touch points in currentArray and savedArray that are too close to each other.
    float timer = 0;

    public Vector2 start,end;
    public Vector2 tmp;  // tmp is used to save worldpoints for AOP(Area of polygon) calculation

    public OrthogonalTiledMapRenderer tmr;
    public TiledMap map;

    public AssetManager manager;

    public Tony tony;
    public Cap cap;

    public ParticleEffect particleEffect;

    public Skin skin;
    public HUD hud;
    public float levelTimer;

    InputMultiplexer multiplexer;


    public CatmullRomSpline<Vector2> spline;

    public DemoScreen(GameClass gameClass){
        this.gameClass = gameClass;
        batch = gameClass.batch;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT,camera);

        savedArray = new Array<Vector2>();
        currentArray = new Array<Vector2>();
        curvesArray = new Array<Curves>();

        renderer = new ShapeRenderer();

        world = new World(new Vector2(0,-9.8f),true);
        b2dr = new Box2DDebugRenderer();
        world.setContactListener(new WorldContactListener(this));

        start = new Vector2();
        end = new Vector2();
        tmp = new Vector2();

        map = new TmxMapLoader().load("map/level1.tmx");
        tmr = new OrthogonalTiledMapRenderer(map,1);
        manager = gameClass.manager;

        tony = new Tony(this,manager.get("img/avengers.atlas", TextureAtlas.class).findRegion("tony"));
        cap = new Cap(this,manager.get("img/avengers.atlas", TextureAtlas.class).findRegion("cap"));

        skin = new Skin();
        skin.addRegions(manager.get("skin/uiskin.atlas",TextureAtlas.class));
        skin.add("font",gameClass.font);
        skin.load(Gdx.files.internal("skin/uiskin.json"));
        hud = new HUD(this);


        spline = new CatmullRomSpline<Vector2>();
        }

    @Override
    public void show() {
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

        TiledObjectLayer.objectLayerParser(world,map.getLayers().get("object").getObjects());

        TextureAtlas atlas = manager.get("img/avengers.atlas",TextureAtlas.class);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/particle.p"),atlas);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Constants.BALLS_TOUCH){
            if (tony.getBody().getPosition().x > cap.getBody().getPosition().x) {
                particleEffect.getEmitters().first().setPosition(tony.getBody().getPosition().x * Constants.PPM - Constants.BALL_RADIUS * Constants.PPM, tony.getBody().getPosition().y * Constants.PPM);
            } else {
                particleEffect.getEmitters().first().setPosition(cap.getBody().getPosition().x * Constants.PPM - Constants.BALL_RADIUS * Constants.PPM, cap.getBody().getPosition().y * Constants.PPM);
            }
        }

        tmr.setView(camera);
        tmr.render();

        timer += delta;

        //combined matrix is scaled by Constants.PPM, because worldPoints added in the currentArray are scaled by 1/Constants.PPM
        renderer.setProjectionMatrix(viewport.getCamera().combined.scl(Constants.PPM));
        viewport.getCamera().update();

        if(firstTimeDraw){
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            if(currentArray.size>2){
               /* for(int i = 0;i<currentArray.size-1;i++){
                    Vector2 start = currentArray.get(i);
                    Vector2 end = currentArray.get(i+1);
                    renderer.rectLine(start.x,start.y, end.x,end.y, Constants.LINE_WIDTH / Constants.PPM,Constants.LINE_COLOR1,Constants.LINE_COLOR2);
                }*/
                Vector2[] vrt = new Vector2[currentArray.size+2];
                vrt[0] = currentArray.get(0); //adding duplicate, first and last point in vrt array for catmullRomSpline
                vrt[currentArray.size+1] = currentArray.get(currentArray.size-1);
                for(int i = 0;i<currentArray.size;i++){
                    vrt[i+1] = currentArray.get(i);
                }
                spline.set(vrt,false);
                for(int i = 0; i < 100; ++i){
                    float t = i /100f;
                    spline.valueAt(start,t);
                    spline.valueAt(end, t+0.01f);
                    renderer.rectLine(start.x,start.y, end.x,end.y, Constants.LINE_WIDTH / Constants.PPM,Constants.LINE_COLOR1,Constants.LINE_COLOR2);
                }
            }
            renderer.end();
        }



        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if (curvesArray != null)
            for (Curves curve : curvesArray) {
                Transform transform = curve.body.getTransform();
                Vector2[] vrt = curve.rendererVertices;
                spline.set(vrt,false);
                for(int i = 0; i < 100; ++i){
                    float t = i /100f;
                    spline.valueAt(start,t);
                    spline.valueAt(end, t+0.01f);
                    transform.mul(start);
                    transform.mul(end);
                    renderer.rectLine(start.x,start.y, end.x,end.y, Constants.LINE_WIDTH / Constants.PPM,Constants.LINE_COLOR1,Constants.LINE_COLOR2);
                }
            }
        renderer.end();

        tony.update(delta);
        cap.update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        tony.draw(batch);
        cap.draw(batch);
        particleEffect.draw(batch,delta);
        batch.end();

        if(Constants.BALLS_TOUCH) {
            if (particleEffect.isComplete()) {
                Constants.BALLS_TOUCH = false;
                hud.createDialog(gameClass);
            }
        }


        if(Constants.BEGIN) {
            world.step(1 / 60f, 6, 2);
        }
       // b2dr.render(world,viewport.getCamera().combined.scl(Constants.PPM));
        viewport.getCamera().update();

        if(Constants.GAME_OVER == false) {
            levelTimer += delta;
            if (levelTimer > 1) {
                hud.updateTimer();
                levelTimer = 0;
            }
        }

        if(hud.getTime()<0){
            hud.setTime(0);
            Constants.GAME_OVER = true;
            hud.createDialog(gameClass);
        }

        hud.stage.act(delta);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }


    @Override
    public void dispose() {
        b2dr.dispose();
        renderer.dispose();
        tmr.dispose();
        map.dispose();
        particleEffect.dispose();
        hud.stage.dispose();
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
        firstTimeDraw = true; //live rendering begins now

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
        firstTimeDraw = false; // live rendering ends now
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

        Constants.BEGIN = true;

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

        float l= (h*h)/(1+((x2-x1)*(x2-x1))/((y2-y1)*(y2-y1)));  // a*a = l;
        float m= (h*h)-l;                                         // b*b = m;

        float x3 = l>=0 ? (float) Math.sqrt(l)+x2: (float) Math.sqrt(-l)+x2;
        float y3 = m>=0 ? (float) Math.sqrt(m)+y2: (float) Math.sqrt(-m)+y2;

        //for the first fixture, (x4,y4) needs to be calculated explicitly
        if(x4 == 0 && y4 == 0){
            x4 = l>=0 ? (float) Math.sqrt(l)+x1: (float) Math.sqrt(-l)+x1;
            y4 = m>=0 ? (float) Math.sqrt(m)+y1: (float) Math.sqrt(-m)+y1;
        }

        float area = ((x1*y2-y1*x2)+(x2*y3-y2*x3)+(x3*y4-y3*x4)+(x4*y1-y4*x1))/2; //Area of Polygon

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
