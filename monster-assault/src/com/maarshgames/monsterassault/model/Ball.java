package com.maarshgames.monsterassault.model;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.maarshgames.monsterassault.model.Enemy.EnemyState;

public class Ball {

	public enum BallState {
		LAUNCHED, MOVING, HIT
	}

	public static final float SIZE = 0.7f;
	public static final float MAX_VELOCITY = 8f;
	public static final float ACCELERATION = 20f;
	public static final float LAUNCHED_FRAME_DURATION = 0.15f;
	public static final float MOVING_FRAME_DURATION = 0.2f;
	public static final float HIT_FRAME_DURATION = 0.1f;

	Vector2 position;
	Vector2 acceleration;
	Vector2 velocity;
	Rectangle bounds;
	BallState state;
	TextureRegion ballFrame;
	boolean facingLeft;
	float stateTime;

	private static TextureAtlas atlas = null;

	private static Animation launchedLeftAnimation;
	private static Animation launchedRightAnimation;
	private static Animation movingLeftAnimation;
	private static Animation movingRightAnimation;
	private static Animation hitLeftAnimation;
	private static Animation hitRightAnimation;

	private Array<Block> collidable = new Array<Block>();

	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};

	public Ball(Vector2 position, boolean facingLeft) {
		this.acceleration = new Vector2();
		this.velocity = new Vector2();
		this.position = position;
		this.bounds = new Rectangle(position.x, position.y, SIZE, SIZE * 0.6f);
		this.state = BallState.LAUNCHED;
		this.facingLeft = facingLeft;
		if (atlas == null) {
			loadTextures();
		}
		updateBallFrame();
	}

	private void loadTextures() {
		atlas = assets
				.get("images/textures/BobAndMap.pack", TextureAtlas.class);

		TextureRegion[] launchedLeftFrames = new TextureRegion[4];
		for (int i = 0; i < 4; i++) {
			launchedLeftFrames[i] = atlas.findRegion("bob-ball-0" + (i + 1));
		}
		launchedLeftAnimation = new Animation(LAUNCHED_FRAME_DURATION,
				launchedLeftFrames);

		TextureRegion[] launchedRightFrames = new TextureRegion[4];
		for (int i = 0; i < 4; i++) {
			launchedRightFrames[i] = new TextureRegion(launchedLeftFrames[i]);
			launchedRightFrames[i].flip(true, false);
		}
		launchedRightAnimation = new Animation(LAUNCHED_FRAME_DURATION,
				launchedRightFrames);

		TextureRegion[] movingLeftFrames = new TextureRegion[2];
		for (int i = 4; i < 6; i++) {
			movingLeftFrames[i - 4] = atlas.findRegion("bob-ball-0" + (i + 1));
		}
		movingLeftAnimation = new Animation(MOVING_FRAME_DURATION,
				movingLeftFrames);

		TextureRegion[] movingRightFrames = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			movingRightFrames[i] = new TextureRegion(movingLeftFrames[i]);
			movingRightFrames[i].flip(true, false);
		}
		movingRightAnimation = new Animation(MOVING_FRAME_DURATION,
				movingRightFrames);

		TextureRegion[] hitLeftFrames = new TextureRegion[4];
		for (int i = 6; i < 9; i++) {
			hitLeftFrames[i - 6] = atlas.findRegion("bob-ball-0" + (i + 1));
		}
		hitLeftFrames[3] = atlas.findRegion("bob-ball-10");
		hitLeftAnimation = new Animation(HIT_FRAME_DURATION, hitLeftFrames);

		TextureRegion[] hitRightFrames = new TextureRegion[4];
		for (int i = 0; i < 4; i++) {
			hitRightFrames[i] = new TextureRegion(hitLeftFrames[i]);
			hitRightFrames[i].flip(true, false);
		}
		hitRightAnimation = new Animation(HIT_FRAME_DURATION, hitRightFrames);
	}

	private void updateBallFrame() {
		switch (state) {
		case LAUNCHED:
			ballFrame = facingLeft ? launchedLeftAnimation.getKeyFrame(
					stateTime, true) : launchedRightAnimation.getKeyFrame(
					stateTime, true);
			break;
		case MOVING:
			ballFrame = facingLeft ? movingLeftAnimation.getKeyFrame(stateTime,
					true) : movingRightAnimation.getKeyFrame(stateTime, true);
			break;
		case HIT:
			ballFrame = facingLeft ? hitLeftAnimation.getKeyFrame(stateTime,
					true) : hitRightAnimation.getKeyFrame(stateTime, true);
			break;
		}
	}

	public void update(float delta) {
		stateTime += delta;

		if (state.equals(BallState.HIT)) {
			if (stateTime >= HIT_FRAME_DURATION * 4) {
				World.removeBall(this);
				return;
			}
		} else {
			acceleration.x = facingLeft ? -ACCELERATION : ACCELERATION;

			if (state.equals(BallState.LAUNCHED)
					&& stateTime >= LAUNCHED_FRAME_DURATION * 4) {
				setState(BallState.MOVING);
			}

			// Setting initial vertical acceleration
			acceleration.y = 0;

			// Convert acceleration to frame time
			acceleration.scl(delta);

			// apply acceleration to change velocity
			velocity.add(acceleration.x, acceleration.y);

			// checking collisions with the surrounding blocks depending on
			// velocity
			checkCollisionWithBlocks(delta);

			// check collision with enemies
			for (Enemy enemy : World.enemies) {
				if (enemy.getBounds().overlaps(bounds)
						&& !enemy.getState().equals(EnemyState.DYING)) {
					enemy.hit(World.bob.getDamage());
					velocity.x = 0;
					setState(BallState.HIT);
				}
			}
			// ensure terminal velocity is not exceeded
			if (velocity.x > MAX_VELOCITY) {
				velocity.x = MAX_VELOCITY;
			}
			if (velocity.x < -MAX_VELOCITY) {
				velocity.x = -MAX_VELOCITY;
			}
		}

		updateBallFrame();
	}

	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units
		velocity.scl(delta);

		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle enemyRect = rectPool.obtain();
		// set the rectangle to ball's bounding box
		enemyRect.set(bounds.x, bounds.y, bounds.width, bounds.height);

		// check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) bounds.y;
		int endY = (int) (bounds.y + bounds.height);
		// if ball is heading left then we check if it collides with the block
		// on its left, we check the block on its right otherwise
		if (velocity.x < 0) {
			startX = endX = (int) Math.floor(bounds.x + velocity.x);
		} else {
			startX = endX = (int) Math.floor(bounds.x + bounds.width
					+ velocity.x);
		}

		// get the block(s) ball can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate ball's movement on the X
		enemyRect.x += velocity.x;

		// if ball collides, make its horizontal velocity 0 and change state
		for (Block block : collidable) {
			if (block == null)
				continue;
			if (enemyRect.overlaps(block.getBounds())) {
				velocity.x = 0;
				setState(BallState.HIT);
				break;
			}
		}

		// reset the x position of the collision box
		enemyRect.x = position.x;

		// update position
		position.add(velocity);
		bounds.x = position.x;
		bounds.y = position.y;

		// un-scale velocity (not in frame time)
		velocity.scl(1 / delta);
	}

	private void populateCollidableBlocks(int startX, int startY, int endX,
			int endY) {
		collidable.clear();
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				if (x >= 0 && x < World.level.getWidth() && y >= 0
						&& y < World.level.getHeight()) {
					collidable.add(World.level.getBlock(x, y));
				}
			}
		}
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public BallState getState() {
		return state;
	}

	public void setState(BallState state) {
		this.state = state;
		stateTime = 0;
	}

	public TextureRegion getBallFrame() {
		return ballFrame;
	}

	public void setBallFrame(TextureRegion ballFrame) {
		this.ballFrame = ballFrame;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

}
