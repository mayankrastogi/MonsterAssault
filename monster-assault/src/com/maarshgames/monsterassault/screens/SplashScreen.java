package com.maarshgames.monsterassault.screens;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maarshgames.monsterassault.MonsterAssault;

public class SplashScreen implements Screen {

	private static final float SPLASH_SCREEN_DURATION = 3f;

	private MonsterAssault game;

	private int width;
	private int height;
	private float displayTime;

	private Texture splash;
	private SpriteBatch spriteBatch;

	public SplashScreen(MonsterAssault game) {
		this.game = game;
		splash = assets.get("images/splash.jpg", Texture.class);
		spriteBatch = new SpriteBatch();
		displayTime = 0;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(splash, 0, 0, width, height);
		spriteBatch.end();

		displayTime += delta;

		// display main-menu screen
		if (assets.update() && displayTime >= SPLASH_SCREEN_DURATION) {
			game.setScreen(game.mainMenuScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void show() {
		// Load assets for main-menu screen
		assets.load("images/main-menu.jpg", Texture.class);
		// assets.load("bg-music.ogg", Music.class);
		assets.load("images/loading.jpg", Texture.class);
		assets.load("fonts/villa.fnt", BitmapFont.class);
	}

	@Override
	public void hide() {
		// Dispose unused resources
		splash = null;
		assets.unload("images/splash.jpg");
		spriteBatch.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
