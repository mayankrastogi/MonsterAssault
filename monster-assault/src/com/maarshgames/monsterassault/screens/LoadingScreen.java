package com.maarshgames.monsterassault.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.maarshgames.monsterassault.MonsterAssault;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

public class LoadingScreen implements Screen {
	MonsterAssault game;

	private BitmapFont font;
	private SpriteBatch spriteBatch;
	private int progress;
	private Texture background;
	private int width;
	private int height;

	public LoadingScreen(MonsterAssault game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!assets.update()) {
			progress = (int) (assets.getProgress() * 100);
			spriteBatch.begin();
			spriteBatch.draw(background, 0, 0, width, height);
			font.draw(spriteBatch, progress + "%",
					width / 2f - font.getBounds(progress + "%").width / 2f,
					height / 2f - 0.167f * height);
			spriteBatch.end();
		} else {
			background = null;
			spriteBatch.dispose();
			game.setScreen(game.gameScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void show() {
		spriteBatch = new SpriteBatch();
		font = assets.get("fonts/villa.fnt", BitmapFont.class);
		background = assets.get("images/loading.jpg", Texture.class);
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		// Load backgrounds
		assets.load("images/game-over.jpg", Texture.class);
		assets.load("images/game-paused.png", Texture.class);

		// Load textures
		assets.load("images/textures/BobAndMap.pack", TextureAtlas.class);
		assets.load("images/textures/Justin.pack", TextureAtlas.class);

		// Load level-maps
		assets.load("levels/level-1.png", Pixmap.class);
		assets.load("levels/level-2.png", Pixmap.class);
		assets.load("levels/level-3.png", Pixmap.class);

		// Load sounds
		assets.load("sounds/ball-hit.wav", Sound.class);
		assets.load("sounds/ball-launched.wav", Sound.class);
		assets.load("sounds/bob-die.wav", Sound.class);
		assets.load("sounds/bob-hit.wav", Sound.class);
		assets.load("sounds/bob-jump.wav", Sound.class);
		assets.load("sounds/bob-land.wav", Sound.class);
		assets.load("sounds/bob-shoot.wav", Sound.class);
		assets.load("sounds/bob-shoot-pressed.wav", Sound.class);
		assets.load("sounds/justin-hit.wav", Sound.class);
		assets.load("sounds/justin-die.wav", Sound.class);
		assets.load("sounds/door-opened.wav", Sound.class);
		assets.load("sounds/level-change.wav", Sound.class);
		assets.load("sounds/game-over.wav", Sound.class);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

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
