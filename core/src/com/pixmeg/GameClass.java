package com.pixmeg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.pixmeg.Demo.DemoScreen;
import com.pixmeg.screens.MainScreen;

public class GameClass extends Game {
	public SpriteBatch batch;
	public AssetManager manager;

	public MainScreen mainScreen;
	public DemoScreen demoScreen;
	public BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();

		manager = new AssetManager();
		manager.load("img/avengers.atlas", TextureAtlas.class);
		manager.load("skin/uiskin.atlas", TextureAtlas.class);
		manager.finishLoading();

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Pacifico.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 28;
		font = generator.generateFont(parameter);

		/*MainScreen contains the crude version, of what we are doing here or you can say the essence of the game.
		DemoScreen contains a demo, of what we can achieve with this crude version. Basically it contains a demo level */

		setMainScreen(new MainScreen(this));
		//setDemoScreen(new DemoScreen(this));
	}

	public void setMainScreen(MainScreen mainScreen){
		this.mainScreen = mainScreen;
		setScreen(mainScreen);
	}

	public void setDemoScreen(DemoScreen demoScreen){
		this.demoScreen = demoScreen;
		setScreen(demoScreen);
	}

	
	@Override
	public void dispose () {
		mainScreen.dispose();
		//demoScreen.dispose();
		batch.dispose();
		font.dispose();
		manager.dispose();
	}
}
