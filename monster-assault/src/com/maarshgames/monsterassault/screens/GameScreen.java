package com.maarshgames.monsterassault.screens;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.maarshgames.monsterassault.MonsterAssault;
import com.maarshgames.monsterassault.controller.BobController;
import com.maarshgames.monsterassault.model.Ball;
import com.maarshgames.monsterassault.model.Block.Type;
import com.maarshgames.monsterassault.model.Bob.State;
import com.maarshgames.monsterassault.model.Enemy;
import com.maarshgames.monsterassault.model.World;
import com.maarshgames.monsterassault.view.GuiRenderer;
import com.maarshgames.monsterassault.view.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

	private static final float ACCELEROMETER_MOVE_THRESHOLD = 0.4f;
	private static final float ACCELEROMETER_MAX_ACCELERATION_THRESHOLD = 1.5f;
	private static final float ACCELEROMETER_ADJUST = BobController
			.getMaximumVelocity() / ACCELEROMETER_MAX_ACCELERATION_THRESHOLD;

	private MonsterAssault game;
	private World world;
	private WorldRenderer worldRenderer;
	private GuiRenderer guiRenderer;
	private BobController controller;
	private Sound doorOpenedSound;

	private int touchFire, touchJump;
	private int levelNumber = 1;
	private boolean paused = false;

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
		doorOpenedSound = assets.get("sounds/door-opened.wav", Sound.class);

		world = new World(levelNumber);
		worldRenderer = new WorldRenderer(world, false);
		guiRenderer = new GuiRenderer(world);
		controller = new BobController(game, world, worldRenderer);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.48f, 0.83f, 0.9f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Restrict max. value of delta to 1/60 of a second so that Bob
		// doesn't fall off
		delta = Math.min(delta, 1f / 60f);

		// If game is not paused, update world objects
		if (!paused) {
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
			controller.update(delta, getMaxVelocityFromAccelerometerInput());
		} else {
			if (Gdx.input.justTouched()) {
				// if screen is touched while paused, resume the game
				Gdx.input.setInputProcessor(this);
				paused = false;
			} else if (Gdx.input.isKeyPressed(Keys.BACK)
					|| Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				// if back key is pressed exit to main menu
				levelNumber = 1;
				world.clear();
				paused = false;
				game.setScreen(game.mainMenuScreen);
			}
		}
		worldRenderer.render();
		guiRenderer.render(worldRenderer.isDebug(), paused);
	}

	private float getMaxVelocityFromAccelerometerInput() {
		if (!Gdx.app.getType().equals(ApplicationType.Android)
				|| world.getBob().getState().equals(State.DYING))
			return BobController.getMaximumVelocity();

		float y = Gdx.input.getAccelerometerY();

		if (Math.abs(y) < ACCELEROMETER_MOVE_THRESHOLD) {
			// If accelerometer is below movement threshold, just change Bob's
			// direction of facing
			if (y < 0) {
				world.getBob().setFacingLeft(true);
			} else {
				world.getBob().setFacingLeft(false);
			}
			controller.leftReleased();
			controller.rightReleased();
			return 0;
		} else {
			if (y < 0) {
				controller.rightReleased();
				controller.leftPressed();
			} else {
				controller.leftReleased();
				controller.rightPressed();
			}
			// if accelerometer is above movement threshold but below max
			// velocity threshold, return calculated velocity; else return
			// maximum allowed velocity
			return (Math.abs(y) - ACCELEROMETER_MOVE_THRESHOLD < ACCELEROMETER_MAX_ACCELERATION_THRESHOLD) ? (Math
					.abs(y) - ACCELEROMETER_MOVE_THRESHOLD)
					* ACCELEROMETER_ADJUST : BobController.getMaximumVelocity();
		}
	}

	@Override
	public void resize(int width, int height) {
		this.touchFire = width / 4;
		this.touchJump = (3 * width) / 4;
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		paused = true;
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
			worldRenderer.setDebug(!worldRenderer.isDebug());
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
			paused = true;
			Gdx.input.setInputProcessor(null);
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
