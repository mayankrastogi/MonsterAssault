package com.maarshgames.monsterassault.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.maarshgames.monsterassault.MonsterAssault;
import com.maarshgames.monsterassault.controller.BobController;
import com.maarshgames.monsterassault.model.Ball;
import com.maarshgames.monsterassault.model.Block.Type;
import com.maarshgames.monsterassault.model.Bob;
import com.maarshgames.monsterassault.model.Enemy;
import com.maarshgames.monsterassault.model.World;
import com.maarshgames.monsterassault.view.WorldRenderer;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

public class GameScreen implements Screen, InputProcessor {

	private static final float GUI_CAM_WIDTH = 848f;
	private static final float GUI_CAM_HEIGHT = 480f;
	private static final float ACCELEROMETER_THRESHOLD = 0.8f;
	private static final float HEALTH_BAR_WIDTH = 250f;
	private static final float HEALTH_BAR_HEIGHT = 15f;

	private static final float BAR_UNIT_WIDTH = HEALTH_BAR_WIDTH
			/ Bob.HIT_POINTS;

	private MonsterAssault game;
	private World world;
	private WorldRenderer renderer;
	private BobController controller;
	private Sound doorOpenedSound;

	private OrthographicCamera guiCam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private BitmapFont font;

	private int width, height;

	private int touchFire, touchJump;
	private int levelNumber = 1;

	public GameScreen(MonsterAssault game) {
		this.game = game;
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int level) {
		this.levelNumber = level;
	}

	@Override
	public void show() {
		font = assets.get("fonts/villa.fnt", BitmapFont.class);
		doorOpenedSound = assets.get("sounds/door-opened.wav", Sound.class);
		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();

		world = new World(levelNumber);
		renderer = new WorldRenderer(world, false);
		controller = new BobController(game, world, renderer);
		guiCam = new OrthographicCamera(GUI_CAM_WIDTH, GUI_CAM_HEIGHT);
		guiCam.update();
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.48f, 0.83f, 0.9f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// If all enemies are dead, open the exit door
		if (World.enemiesLeft == 0
				&& World.level.getDoor().getType().equals(Type.DOOR_CLOSED)) {
			World.level.getDoor().setType(Type.DOOR_OPENED);
			// Play door-opened sound
			doorOpenedSound.play();
		}
		for (Enemy enemy : world.getEnemies()) {
			enemy.update(delta);
		}
		for (Ball ball : world.getBalls()) {
			ball.update(delta);
		}
		processAccelerometerInput();
		controller.update(delta);
		renderer.render();

		// Draw HUD
		drawHealthBar();
		drawScore();
	}

	private void drawHealthBar() {
		shapeRenderer.setProjectionMatrix(guiCam.combined);

		float x = guiCam.position.x - (GUI_CAM_WIDTH / 2) + 25;
		float y = guiCam.position.y + (GUI_CAM_HEIGHT / 2) - 25f;
		int bobHP = world.getBob().getHitPoints();
		float barWidth = (bobHP > 0) ? BAR_UNIT_WIDTH * bobHP : BAR_UNIT_WIDTH;

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0.65f, 0, 0, 1);
		shapeRenderer.rect(x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
		shapeRenderer.setColor(1, 0, 0, 1);
		shapeRenderer.rect(x, y, barWidth, HEALTH_BAR_HEIGHT);
		shapeRenderer.end();

	}

	private void drawScore() {
		spriteBatch.setProjectionMatrix(guiCam.combined);
		spriteBatch.begin();
		float x = guiCam.position.x + (GUI_CAM_WIDTH / 2)
				- font.getBounds("" + World.score).width - 25;
		float y = guiCam.position.y + (GUI_CAM_HEIGHT / 2) - 10;
		font.draw(spriteBatch, "" + World.score, x, y);
		spriteBatch.end();
	}

	private void processAccelerometerInput() {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return;

		float y = Gdx.input.getAccelerometerY();
		if (Math.abs(y) > ACCELEROMETER_THRESHOLD) {
			if (y < 0) {
				controller.rightReleased();
				controller.leftPressed();
			} else {
				controller.leftReleased();
				controller.rightPressed();
			}
		} else {
			controller.leftReleased();
			controller.rightReleased();
		}
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.touchFire = width / 4;
		this.touchJump = (3 * width) / 4;
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

	// * InputProcessor methods ***************************//

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.LEFT)
			controller.leftPressed();
		if (keycode == Keys.RIGHT)
			controller.rightPressed();
		if (keycode == Keys.Z)
			controller.jumpPressed();
		if (keycode == Keys.X)
			controller.firePressed();
		if (keycode == Keys.D)
			renderer.setDebug(!renderer.isDebug());
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.LEFT)
			controller.leftReleased();
		if (keycode == Keys.RIGHT)
			controller.rightReleased();
		if (keycode == Keys.Z)
			controller.jumpReleased();
		if (keycode == Keys.X)
			controller.fireReleased();
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			levelNumber = 1;
			world.clear();
			game.setScreen(game.mainMenuScreen);
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;

		if (x < touchFire) {
			controller.firePressed();
		}
		if (x > touchJump) {
			controller.jumpPressed();
		}
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (!Gdx.app.getType().equals(ApplicationType.Android))
			return false;
		if (x < touchFire) {
			controller.fireReleased();
		}
		if (x > touchJump) {
			controller.jumpReleased();
		}
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

}
