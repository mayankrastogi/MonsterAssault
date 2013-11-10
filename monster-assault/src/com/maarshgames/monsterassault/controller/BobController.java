package com.maarshgames.monsterassault.controller;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.maarshgames.monsterassault.MonsterAssault;
import com.maarshgames.monsterassault.model.Ball;
import com.maarshgames.monsterassault.model.Block;
import com.maarshgames.monsterassault.model.Block.Type;
import com.maarshgames.monsterassault.model.Bob;
import com.maarshgames.monsterassault.model.Bob.State;
import com.maarshgames.monsterassault.model.World;
import com.maarshgames.monsterassault.view.WorldRenderer;

public class BobController {

	enum Keys {
		LEFT, RIGHT, JUMP, FIRE
	}

	private static final long LONG_JUMP_PRESS = 300l;
	private static final float ACCELERATION = 20f;
	private static final float GRAVITY = -20f;
	private static final float MAX_JUMP_SPEED = 5.3f;
	private static final float DAMP = 0.90f;
	private static final float MAX_VEL = 4f;

	public static final int BOB_HIT_VIBRATION_DURATION = 200;
	public static final int LEVEL_CHANGE_VIBRATION_DURATION = 200;
	public static final int BOB_DIE_VIBRATION_DURATION = 500;

	private MonsterAssault game;
	private World world;
	private WorldRenderer renderer;
	private Bob bob;
	private long jumpPressedTime;
	private boolean jumpingPressed;
	private boolean fireLongPressed;
	private boolean ballFired;
	private boolean grounded = false;
	private boolean inputDisabled = false;

	public static Sound bobDieSound;
	public static Sound bobHitSound;
	public static Sound bobJumpSound;
	public static Sound bobLandSound;
	public static Sound bobShootSound;
	public static Sound bobShootPressedSound;
	public static Sound levelChangeSound;

	private Vector2 camAcceleration;
	private Vector2 camVelocity;
	private boolean scrollUp = false;
	private float scrollTo;

	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};

	// Ball pool
	private Pool<Ball> ballPool = new Pool<Ball>() {
		@Override
		protected Ball newObject() {
			return new Ball();
		}
	};

	static Map<Keys, Boolean> keys = new HashMap<BobController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};

	// Blocks that Bob can collide with any given frame
	private Array<Block> collidable = new Array<Block>();

	public BobController(MonsterAssault game, World world,
			WorldRenderer renderer) {
		this.game = game;
		this.world = world;
		this.renderer = renderer;
		this.bob = world.getBob();
		this.camAcceleration = new Vector2(0, ACCELERATION);
		this.camVelocity = new Vector2();

		bobDieSound = assets.get("sounds/bob-die.wav", Sound.class);
		bobHitSound = assets.get("sounds/bob-hit.wav", Sound.class);
		bobJumpSound = assets.get("sounds/bob-jump.wav", Sound.class);
		bobLandSound = assets.get("sounds/bob-land.wav", Sound.class);
		bobShootSound = assets.get("sounds/bob-shoot.wav", Sound.class);
		bobShootPressedSound = assets.get("sounds/bob-shoot-pressed.wav",
				Sound.class);
		levelChangeSound = assets.get("sounds/level-change.wav", Sound.class);
	}

	// ** Key presses and touches **************** //

	public void leftPressed() {
		keys.get(keys.put(Keys.LEFT, true));
	}

	public void rightPressed() {
		keys.get(keys.put(Keys.RIGHT, true));
	}

	public void jumpPressed() {
		keys.get(keys.put(Keys.JUMP, true));
	}

	public void firePressed() {
		keys.get(keys.put(Keys.FIRE, true));
	}

	public void leftReleased() {
		keys.get(keys.put(Keys.LEFT, false));
	}

	public void rightReleased() {
		keys.get(keys.put(Keys.RIGHT, false));
	}

	public void jumpReleased() {
		keys.get(keys.put(Keys.JUMP, false));
		jumpingPressed = false;
	}

	public void fireReleased() {
		keys.get(keys.put(Keys.FIRE, false));
		if (fireLongPressed) {
			bob.setState(State.IDLE);
		}
	}

	private void releaseAllKeys() {
		leftReleased();
		rightReleased();
		jumpReleased();
		fireReleased();
	}

	/** The main update method **/
	public void update(float delta) {
		// simply updates the state time
		bob.update(delta);

		// check if bob is dead
		if (bob.getHitPoints() <= 0 && !bob.getState().equals(State.DYING)) {
			bob.setState(State.DYING);
			Gdx.input.vibrate(BOB_DIE_VIBRATION_DURATION);
			inputDisabled = true;
			// Play Bob die sound
			bobDieSound.play();
		}

		// If bob is dead, show game over screen
		if (bob.getState().equals(State.DYING)
				&& bob.getStateTime() >= WorldRenderer.DYING_FRAME_DURATION * 7) {
			showGameOverScreen();
			return;
		}

		// Processing the input - setting the states of Bob
		processInput();

		// If Bob is grounded then reset the state to IDLE
		if (grounded && bob.getState().equals(State.JUMPING)) {
			bob.setState(State.IDLE);
		}

		if (bob.isHit()) {
			// Disable input processing
			inputDisabled = true;

			// Stop movement if Bob is hit
			bob.getAcceleration().x = 0;
			if (bob.getStateTime() >= WorldRenderer.HIT_FRAME_DURATION * 3) {
				bob.setHit(false);
				inputDisabled = false;
			}
		}

		// Setting initial vertical acceleration
		bob.getAcceleration().y = GRAVITY;

		// Convert acceleration to frame time
		bob.getAcceleration().scl(delta);

		// apply acceleration to change velocity
		bob.getVelocity().add(bob.getAcceleration().x, bob.getAcceleration().y);

		// checking collisions with the surrounding blocks depending on Bob's
		// velocity
		checkCollisionWithBlocks(delta);

		// apply damping to halt Bob nicely
		if (bob.getAcceleration().x == 0) {
			bob.getVelocity().x *= DAMP;
		}

		// ensure terminal velocity is not exceeded
		if (bob.getVelocity().x > MAX_VEL) {
			bob.getVelocity().x = MAX_VEL;
		}
		if (bob.getVelocity().x < -MAX_VEL) {
			bob.getVelocity().x = -MAX_VEL;
		}

	}

	private void showGameOverScreen() {
		game.gameOverScreen.setScore(World.score);
		world.clear();
		game.gameScreen.setLevelNumber(1);
		game.setScreen(game.gameOverScreen);
	}

	/** Collision checking **/
	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units
		bob.getVelocity().scl(delta);

		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle bobRect = rectPool.obtain();
		// set the rectangle to bob's bounding box
		bobRect.set(bob.getBounds().x, bob.getBounds().y,
				bob.getBounds().width, bob.getBounds().height);

		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) bob.getBounds().y;
		int endY = (int) (bob.getBounds().y + bob.getBounds().height);
		// if Bob is heading left then we check if he collides with the block on
		// his left
		// we check the block on his right otherwise
		if (bob.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(bob.getBounds().x
					+ bob.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(bob.getBounds().x
					+ bob.getBounds().width + bob.getVelocity().x);
		}

		// get the block(s) bob can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate bob's movement on the X
		bobRect.x += bob.getVelocity().x;

		// clear collision boxes in world
		world.getCollisionRects().clear();

		// if bob collides, make his horizontal velocity 0
		for (Block block : collidable) {
			if (block == null || block.getType().equals(Type.DOOR_CLOSED))
				continue;
			if (bobRect.overlaps(block.getBounds())) {
				if (block.getType().equals(Type.DOOR_OPENED)) {
					goToNextLevel();
				}
				bob.getVelocity().x = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}

		// reset the x position of the collision box
		bobRect.x = bob.getPosition().x;

		// the same thing but on the vertical Y axis
		startX = (int) bob.getBounds().x;
		endX = (int) (bob.getBounds().x + bob.getBounds().width);
		if (bob.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(bob.getBounds().y
					+ bob.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(bob.getBounds().y
					+ bob.getBounds().height + bob.getVelocity().y);
		}

		populateCollidableBlocks(startX, startY, endX, endY);

		bobRect.y += bob.getVelocity().y;

		for (Block block : collidable) {
			if (block == null || block.getType().equals(Type.DOOR_CLOSED))
				continue;
			if (bobRect.overlaps(block.getBounds())) {
				if (block.getType().equals(Type.DOOR_OPENED)) {
					goToNextLevel();
				}
				if (bob.getVelocity().y < 0) {
					if (!grounded) {
						grounded = true;
						scrollUp = true;
						scrollTo = bob.getPosition().y;
						camVelocity.set(0, MAX_JUMP_SPEED);

						// Play sound when bob hits ground
						bobLandSound.play();
					}
				}
				bob.getVelocity().y = 0;
				world.getCollisionRects().add(block.getBounds());
				break;
			}
		}
		// reset the collision box's position on Y
		bobRect.y = bob.getPosition().y;

		// update Bob's position
		bob.getPosition().add(bob.getVelocity());
		bob.getBounds().x = bob.getPosition().x;
		bob.getBounds().y = bob.getPosition().y;

		// update Cam's Position
		renderer.setCamX(bob.getPosition().x);
		if (scrollUp) {
			camAcceleration.y = ACCELERATION;
			camAcceleration.scl(delta);
			camVelocity.add(0, camAcceleration.y);
			camVelocity.scl(delta);
			if (renderer.getCamY() < scrollTo) {
				renderer.setCamY(renderer.getCamY() + camVelocity.y);
			} else {
				renderer.setCamY(scrollTo);
				scrollUp = false;
			}
			camVelocity.scl(1 / delta);
		}
		if (bob.getPosition().y < renderer.getCamY()) {
			renderer.setCamY(bob.getPosition().y);
		}
		renderer.getCam().update();

		// un-scale velocity (not in frame time)
		bob.getVelocity().scl(1 / delta);

	}

	private void goToNextLevel() {
		if (game.gameScreen.getLevelNumber() == MonsterAssault.NUMBER_OF_LEVELS) {
			// Bonus score on game-completion
			World.score += 500;
			showGameOverScreen();
		} else {
			game.gameScreen
					.setLevelNumber(game.gameScreen.getLevelNumber() + 1);
			// Bonus score on level-completion
			World.score += 100;
			releaseAllKeys();
			game.setScreen(game.loadingScreen);
		}
		// Play level change sound and vibrate
		levelChangeSound.play();
		Gdx.input.vibrate(LEVEL_CHANGE_VIBRATION_DURATION);
	}

	/**
	 * populate the collidable array with the blocks found in the enclosing
	 * coordinates
	 **/
	private void populateCollidableBlocks(int startX, int startY, int endX,
			int endY) {
		collidable.clear();
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				if (x >= 0 && x < world.getLevel().getWidth() && y >= 0
						&& y < world.getLevel().getHeight()) {
					collidable.add(world.getLevel().getBlock(x, y));
				}
			}
		}
	}

	/** Change Bob's state and parameters based on input controls **/
	private boolean processInput() {
		if (!inputDisabled) {
			if (keys.get(Keys.JUMP)) {
				if (!bob.getState().equals(State.JUMPING)
						&& !bob.getState().equals(State.FIRING)) {
					jumpingPressed = true;
					jumpPressedTime = System.currentTimeMillis();
					bob.setState(State.JUMPING);
					bob.getVelocity().y = MAX_JUMP_SPEED;
					grounded = false;
					// Play jumping sound
					bobJumpSound.play();
				} else {
					if (jumpingPressed
							&& ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
						jumpingPressed = false;
					} else {
						if (jumpingPressed) {
							bob.getVelocity().y = MAX_JUMP_SPEED;
						}
					}
				}
			}
			if (keys.get(Keys.FIRE) && !bob.getState().equals(State.JUMPING)) {
				// fire is pressed
				if (!bob.getState().equals(State.FIRING)) {
					bob.setState(State.FIRING);
					ballFired = false;
					fireLongPressed = false;
					// Play Bob shoot sound
					bobShootSound.play();
				} else if (!fireLongPressed
						&& bob.getStateTime() >= WorldRenderer.FIRING_FRAME_DURATION * 3) {
					fireLongPressed = true;
					ballFired = false;
				}
				bob.getAcceleration().x = 0;
			} else if (keys.get(Keys.LEFT)
					&& !bob.getState().equals(State.FIRING)) {
				// left is pressed
				bob.setFacingLeft(true);
				if (!bob.getState().equals(State.JUMPING)
						&& !bob.getState().equals(State.WALKING)) {
					bob.setState(State.WALKING);
				}
				bob.getAcceleration().x = -ACCELERATION;
			} else if (keys.get(Keys.RIGHT)
					&& !bob.getState().equals(State.FIRING)) {
				// left is pressed
				bob.setFacingLeft(false);
				if (!bob.getState().equals(State.JUMPING)
						&& !bob.getState().equals(State.WALKING)) {
					bob.setState(State.WALKING);
				}
				bob.getAcceleration().x = ACCELERATION;
			} else {
				if (!bob.getState().equals(State.JUMPING)
						&& !bob.getState().equals(State.FIRING)
						&& !bob.getState().equals(State.IDLE)) {
					bob.setState(State.IDLE);
				}
				bob.getAcceleration().x = 0;

			}
			// Handle shooting
			if (bob.getState().equals(State.FIRING)) {
				if (!fireLongPressed) {
					if (!ballFired
							&& bob.getStateTime() >= WorldRenderer.FIRING_FRAME_DURATION) {
						shootBall();
						ballFired = true;
					} else if (bob.getStateTime() > WorldRenderer.FIRING_FRAME_DURATION * 3) {
						bob.setState(State.IDLE);
					}
				} else {
					float stateTimeMod = (bob.getStateTime() - WorldRenderer.FIRING_FRAME_DURATION * 3)
							% (WorldRenderer.FIRING_PRESSED_FRAME_DURATION * 8);
					if ((stateTimeMod >= WorldRenderer.FIRING_PRESSED_FRAME_DURATION * 3 && stateTimeMod < WorldRenderer.FIRING_PRESSED_FRAME_DURATION * 4)
							|| (stateTimeMod >= WorldRenderer.FIRING_PRESSED_FRAME_DURATION * 7 && stateTimeMod < WorldRenderer.FIRING_PRESSED_FRAME_DURATION * 8)) {
						if (!ballFired) {
							shootBall();
							ballFired = true;
							// Play Bob shoot pressed sound
							bobShootPressedSound.play();
						}
					} else {
						ballFired = false;
					}
				}
			}
		}
		return false;
	}

	private void shootBall() {
		// Obtain a ball from pool and add it to world
		Ball ball = ballPool.obtain();
		if (bob.isFacingLeft()) {
			ball.setBall(bob.getPosition().x - Bob.SIZE / 2f,
					bob.getPosition().y + Bob.SIZE / 4f, true);
		} else {
			ball.setBall(bob.getPosition().x + Bob.SIZE / 4f,
					bob.getPosition().y + Bob.SIZE / 4f, false);
		}
		World.addBall(ball);
		// Play ball launched sound
		Ball.ballLaunchedSound.play();
	}

}
