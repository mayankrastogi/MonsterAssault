package com.maarshgames.monsterassault.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.maarshgames.monsterassault.controller.BobController;
import com.maarshgames.monsterassault.model.Enemy;
import com.maarshgames.monsterassault.model.World;
import com.maarshgames.monsterassault.view.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

	private World world;
	private WorldRenderer renderer;
	private BobController controller;

	private int width, height;

	private int touchLeft, touchRight, touchJump;

	@Override
	public void show() {
		world = new World();
		renderer = new WorldRenderer(world, false);
		controller = new BobController(world, renderer);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.48f, 0.83f, 0.9f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		for(Enemy enemy: world.getEnemies()) {
			enemy.update(delta);
		}
		controller.update(delta);
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
		renderer.setSize(width, height);
		this.width = width;
		this.height = height;
		this.touchLeft = width / 8;
		this.touchRight = width / 4;
		this.touchJump = (7 * width) / 8;
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
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
		if (x < touchLeft) {
			controller.rightReleased();
			controller.leftPressed();
		}
		if (x > touchLeft && x < touchRight) {
			controller.leftReleased();
			controller.rightPressed();
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
		if (x < touchLeft) {
			controller.leftReleased();
		}
		if (x > touchLeft && x < touchRight) {
			controller.rightReleased();
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
