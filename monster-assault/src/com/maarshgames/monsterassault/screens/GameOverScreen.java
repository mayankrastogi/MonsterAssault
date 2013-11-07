package com.maarshgames.monsterassault.screens;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maarshgames.monsterassault.MonsterAssault;

public class GameOverScreen implements Screen, InputProcessor {

	private MonsterAssault game;

	private int width;
	private int height;

	private SpriteBatch spriteBatch;
	private Texture background;
	private BitmapFont font;
	private int score;

	public GameOverScreen(MonsterAssault game) {
		this.game = game;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(background, 0, 0, width, height);
		font.draw(spriteBatch, "" + score,
				width / 2f - font.getBounds("" + score).width / 2f,
				height / 2f - 80);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void show() {
		background = assets.get("images/game-over.jpg", Texture.class);
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		spriteBatch = new SpriteBatch();

		font = assets.get("fonts/villa.fnt", BitmapFont.class);
		
		// Play game-over sound
		assets.get("sounds/game-over.wav", Sound.class).play();

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void resume() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ENTER)
			game.setScreen(game.mainMenuScreen);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		game.setScreen(game.mainMenuScreen);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
