package com.maarshgames.monsterassault.model;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.maarshgames.monsterassault.model.Bob.State;

public class Justin extends Enemy {

	public static final float SIZE = 0.9f;
	public static final int HIT_POINTS = 40;
	public static final int DAMAGE = 25;

	private static final float ACCELERATION = 10f;
	private static final float GRAVITY = -20f;
	private static final float MAX_VEL = 2f;

	private static final float IDLE_FRAME_DURATION = 0.3f;
	private static final float RUNNING_FRAME_DURATION = 0.1f;
	private static final float ATTACKING_FRAME_DURATION = 0.1f;
	private static final float DYING_FRAME_DURATION = 0.2f;
	private static final float HIT_FRAME_DURATION = 0.2f;

	/** Textures **/
	private static TextureAtlas atlas = null;

	private static TextureRegion jumpLeft;
	private static TextureRegion jumpRight;

	private static Animation idleLeftAnimation;
	private static Animation idleRightAnimation;
	private static Animation walkLeftAnimation;
	private static Animation walkRightAnimation;
	private static Animation fireLeftAnimation;
	private static Animation fireRightAnimation;
	private static Animation hitLeftAnimation;
	private static Animation hitRightAnimation;
	private static Animation dieLeftAnimation;
	private static Animation dieRightAnimation;
	
	private static Sound justinHitSound;
	private static Sound justinDieSound;

	private boolean grounded;
	private boolean initialUpdate = true;
	private Array<Block> collidable = new Array<Block>();

	public Justin(Vector2 position) {
		super(position, SIZE, HIT_POINTS, DAMAGE, EnemyType.JUSTIN);
		this.bounds.width = SIZE / 2f;
		this.bounds.height = SIZE;
		if (atlas == null) {
			loadTextures();
			// Load sounds
			justinHitSound = assets.get("sounds/justin-hit.wav", Sound.class);
			justinDieSound = assets.get("sounds/justin-die.wav", Sound.class);
		}
		this.enemyFrame = idleLeftAnimation.getKeyFrame(stateTime, true);
	}

	private void loadTextures() {
		atlas = assets.get("images/textures/Justin.pack", TextureAtlas.class);

		jumpLeft = atlas.findRegion("justin-jump");
		jumpRight = new TextureRegion(jumpLeft);
		jumpRight.flip(true, false);

		TextureRegion[] idleLeftFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			idleLeftFrames[i] = atlas.findRegion("justin-idle-0" + (i + 1));
		}
		idleLeftAnimation = new Animation(IDLE_FRAME_DURATION, idleLeftFrames);

		TextureRegion[] idleRightFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			idleRightFrames[i] = new TextureRegion(idleLeftFrames[i]);
			idleRightFrames[i].flip(true, false);
		}
		idleRightAnimation = new Animation(IDLE_FRAME_DURATION, idleRightFrames);

		TextureRegion[] walkLeftFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			walkLeftFrames[i] = atlas.findRegion("justin-move-0" + (i + 1));
		}
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,
				walkLeftFrames);

		TextureRegion[] walkRightFrames = new TextureRegion[6];

		for (int i = 0; i < 6; i++) {
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,
				walkRightFrames);

		TextureRegion[] fireLeftFrames = new TextureRegion[4];
		for (int i = 0; i < 4; i++) {
			fireLeftFrames[i] = atlas.findRegion("justin-attack-0" + (i + 1));
		}
		fireLeftAnimation = new Animation(ATTACKING_FRAME_DURATION,
				fireLeftFrames);

		TextureRegion[] fireRightFrames = new TextureRegion[4];
		for (int i = 0; i < 4; i++) {
			fireRightFrames[i] = new TextureRegion(fireLeftFrames[i]);
			fireRightFrames[i].flip(true, false);
		}
		fireRightAnimation = new Animation(ATTACKING_FRAME_DURATION,
				fireRightFrames);

		TextureRegion[] hitLeftFrames = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			hitLeftFrames[i] = atlas.findRegion("justin-hit-0" + (i + 1));
		}
		hitLeftAnimation = new Animation(HIT_FRAME_DURATION, hitLeftFrames);

		TextureRegion[] hitRightFrames = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			hitRightFrames[i] = new TextureRegion(hitLeftFrames[i]);
			hitRightFrames[i].flip(true, false);
		}
		hitRightAnimation = new Animation(HIT_FRAME_DURATION, hitRightFrames);

		TextureRegion[] dieLeftFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			dieLeftFrames[i] = atlas.findRegion("justin-die-0" + (i + 1));
		}
		dieLeftAnimation = new Animation(DYING_FRAME_DURATION, dieLeftFrames);

		TextureRegion[] dieRightFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			dieRightFrames[i] = new TextureRegion(dieLeftFrames[i]);
			dieRightFrames[i].flip(true, false);
		}
		dieRightAnimation = new Animation(DYING_FRAME_DURATION, dieRightFrames);
	}

	private void updateEnemyFrame() {
		if (hit) {
			enemyFrame = facingLeft ? hitLeftAnimation.getKeyFrame(stateTime,
					false) : hitRightAnimation.getKeyFrame(stateTime, false);
		} else if (state.equals(EnemyState.IDLE)) {
			enemyFrame = facingLeft ? idleLeftAnimation.getKeyFrame(stateTime,
					true) : idleRightAnimation.getKeyFrame(stateTime, true);
		} else if (state.equals(EnemyState.WALKING)) {
			enemyFrame = facingLeft ? walkLeftAnimation.getKeyFrame(stateTime,
					true) : walkRightAnimation.getKeyFrame(stateTime, true);
		} else if (state.equals(EnemyState.ATTACKING)) {
			enemyFrame = facingLeft ? fireLeftAnimation.getKeyFrame(stateTime,
					true) : fireRightAnimation.getKeyFrame(stateTime, true);
		} else if (state.equals(EnemyState.JUMPING)) {
			enemyFrame = facingLeft ? jumpLeft : jumpRight;
		} else if (state.equals(EnemyState.DYING)) {
			enemyFrame = facingLeft ? dieLeftAnimation.getKeyFrame(stateTime,
					false) : dieRightAnimation.getKeyFrame(stateTime, false);
		}
	}

	@Override
	public void update(float delta) {
		if (!state.equals(EnemyState.INACTIVE)) {
			stateTime += delta;

			// check if enemy enemy is dead
			if (hitPoints <= 0 && !state.equals(EnemyState.DYING)) {
				setState(EnemyState.DYING);
				// Play dying sound
				justinDieSound.play();
			}

			// If enemy is dead, remove him and update score
			if (state.equals(EnemyState.DYING)
					&& stateTime >= DYING_FRAME_DURATION * 7) {
				World.score += HIT_POINTS;
				World.removeEnemy(this);
				return;
			}

			Bob bob = World.bob;

			if (initialUpdate) {
				facingLeft = true;
				state = EnemyState.WALKING;
				initialUpdate = false;
			}

			if (grounded && state.equals(EnemyState.JUMPING)
					&& !state.equals(EnemyState.IDLE)) {
				setState(EnemyState.IDLE);
			}

			if (hit) {
				// Stop movement if enemy is hit
				acceleration.x = 0;
				velocity.x = 0;
				if (stateTime >= HIT_FRAME_DURATION * 3) {
					hit = false;
				}
			} else {
				// Setting initial horizontal acceleration
				if (state.equals(EnemyState.DYING)) {
					acceleration.x = 0;
					velocity.x = 0;
				} else {
					acceleration.x = facingLeft ? -ACCELERATION : ACCELERATION;
				}
			}

			// Setting initial vertical acceleration
			acceleration.y = GRAVITY;

			// Convert acceleration to frame time
			acceleration.scl(delta);

			// apply acceleration to change velocity
			velocity.add(acceleration.x, acceleration.y);

			// checking collisions with the surrounding blocks depending on
			// velocity
			checkCollisionWithBlocks(delta);

			// check collision with Bob
			if (!bob.isHit() && !bob.getState().equals(State.DYING)
					&& !state.equals(EnemyState.DYING)
					&& bob.getBounds().overlaps(bounds)) {
				bob.hit(damage);
			}

			// ensure terminal velocity is not exceeded
			if (velocity.x > MAX_VEL) {
				velocity.x = MAX_VEL;
			}
			if (velocity.x < -MAX_VEL) {
				velocity.x = -MAX_VEL;
			}

			updateEnemyFrame();
		}
	}

	private void checkCollisionWithBlocks(float delta) {
		// scale velocity to frame units
		velocity.scl(delta);

		// Obtain the rectangle from the pool instead of instantiating it
		Rectangle enemyRect = rectPool.obtain();
		// set the rectangle to bob's bounding box
		enemyRect.set(bounds.x, bounds.y, bounds.width, bounds.height);

		// we first check the movement on the horizontal X axis
		int startX, endX;
		int startY = (int) bounds.y;
		int endY = (int) (bounds.y + bounds.height);
		// if Justin is heading left then we check if he collides with the block
		// on his left we check the block on his right otherwise
		if (velocity.x < 0) {
			startX = endX = (int) Math.floor(bounds.x + velocity.x);
		} else {
			startX = endX = (int) Math.floor(bounds.x + bounds.width
					+ velocity.x);
		}

		// get the block(s) Justin can collide with
		populateCollidableBlocks(startX, startY, endX, endY);

		// simulate Justin's movement on the X
		enemyRect.x += velocity.x;

		// if Justin collides, make his horizontal velocity 0 and change
		// direction
		for (Block block : collidable) {
			if (block == null)
				continue;
			if (enemyRect.overlaps(block.getBounds())) {
				velocity.x = 0;
				facingLeft = !facingLeft;
				break;
			}
		}

		// reset the x position of the collision box
		enemyRect.x = position.x;

		// the same thing but on the vertical Y axis
		startX = (int) bounds.x;
		endX = (int) (bounds.x + bounds.width);
		if (velocity.y < 0) {
			startY = endY = (int) Math.floor(bounds.y + velocity.y);
		} else {
			startY = endY = (int) Math.floor(bounds.y + bounds.height
					+ velocity.y);
		}

		populateCollidableBlocks(startX, startY, endX, endY);

		enemyRect.y += velocity.y;

		for (Block block : collidable) {
			if (block == null)
				continue;
			if (enemyRect.overlaps(block.getBounds())) {
				if (velocity.y < 0) {
					grounded = true;
				}
				velocity.y = 0;
				break;
			}
		}
		// reset the collision box's position on Y
		enemyRect.y = position.y;

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

	@Override
	public void hit(int damage) {
		if (!hit && !state.equals(EnemyState.DYING)) {
			hit = true;
			stateTime = 0;
			World.score += (hitPoints > damage) ? damage : hitPoints;
			hitPoints -= damage;
			// Play hit sound
			justinHitSound.play();
		}
	}
}
