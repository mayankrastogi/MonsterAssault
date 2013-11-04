package com.maarshgames.monsterassault.screens;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.maarshgames.monsterassault.MonsterAssault;

public class MainMenuScreen implements Screen, InputProcessor {

	private MonsterAssault game;

	private int width;
	private int height;

	private Texture background;
	private SpriteBatch spriteBatch;
	private BitmapFont font;

	// Rectangles to store bounds for menu buttons
	private Rectangle playButton;
	private Rectangle exitButton;

	public MainMenuScreen(MonsterAssault game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(background, 0, 0, width, height);
		font.draw(spriteBatch, "Play", playButton.x, playButton.y);
		font.draw(spriteBatch, "Exit", exitButton.x, exitButton.y);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);

		background = assets.get("images/main-menu.jpg", Texture.class);
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		spriteBatch = new SpriteBatch();

		font = assets.get("fonts/villa.fnt", BitmapFont.class);

		float playButtonWidth = font.getBounds("Play").width;
		float exitButtonWidth = font.getBounds("Exit").width;

		float playButtonHeight = font.getBounds("Play").height;
		float exitButtonHeight = font.getBounds("Exit").height;

		playButton = new Rectangle(width / 2f - playButtonWidth / 2f,
				height / 2f - 50f, playButtonWidth, playButtonHeight);
		exitButton = new Rectangle(width / 2f - exitButtonWidth / 2f,
				height / 2f - 100f, exitButtonWidth, exitButtonHeight);
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
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK) {
			Gdx.app.exit();
		} else if (keycode == Keys.ENTER) {
			game.setScreen(game.gameScreen);
		}
		return true;
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
		// TODO handle button touch events
		game.setScreen(game.loadingScreen);
		return true;
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
