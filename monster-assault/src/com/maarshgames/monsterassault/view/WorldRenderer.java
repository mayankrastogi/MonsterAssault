package com.maarshgames.monsterassault.view;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.maarshgames.monsterassault.model.Ball;
import com.maarshgames.monsterassault.model.Block;
import com.maarshgames.monsterassault.model.Bob;
import com.maarshgames.monsterassault.model.Bob.State;
import com.maarshgames.monsterassault.model.Enemy;
import com.maarshgames.monsterassault.model.World;

public class WorldRenderer {

	private static final float CAMERA_WIDTH = 12.4f;
	private static final float CAMERA_HEIGHT = 7f;
	public static final float RUNNING_FRAME_DURATION = 0.1f;
	public static final float IDLE_FRAME_DURATION = 0.35f;
	public static final float FIRING_FRAME_DURATION = 0.2f;
	public static final float FIRING_PRESSED_FRAME_DURATION = 0.1f;
	public static final float DYING_FRAME_DURATION = 0.2f;
	public static final float HIT_FRAME_DURATION = 0.2f;

	private World world;
	private OrthographicCamera cam;

	/** for debug rendering **/
	ShapeRenderer debugRenderer = new ShapeRenderer();

	/** Textures **/
	private TextureRegion bobFrame;
	private TextureRegion bobJumpLeft;
	private TextureRegion bobJumpRight;

	/** Animations **/
	private Animation idleLeftAnimation;
	private Animation idleRightAnimation;
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation fireLeftAnimation;
	private Animation fireRightAnimation;
	private Animation firePressedLeftAnimation;
	private Animation firePressedRightAnimation;
	private Animation hitLeftAnimation;
	private Animation hitRightAnimation;
	private Animation dieLeftAnimation;
	private Animation dieRightAnimation;

	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private boolean debug = false;
	// private int width;
	// private int height;
	// private float ppuX; // pixels per unit on the X axis
	// private float ppuY; // pixels per unit on the Y axis
	private float camX;
	private float camY;

	// public void setSize(int w, int h) {
	// this.width = w;
	// this.height = h;
	// // ppuX = (float) width / CAMERA_WIDTH;
	// // ppuY = (float) height / CAMERA_HEIGHT;
	// }

	public boolean isDebug() {
		return debug;
	}

	public OrthographicCamera getCam() {
		return cam;
	}

	public void setCam(OrthographicCamera cam) {
		this.cam = cam;
	}

	public float getCamX() {
		return camX;
	}

	public void setCamX(float camX) {
		this.camX = camX;
		this.cam.position.set(camX, camY, 0);
	}

	public float getCamY() {
		return camY;
	}

	public void setCamY(float camY) {
		this.camY = camY;
		this.cam.position.set(camX, camY, 0);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public WorldRenderer(World world, boolean debug) {
		this.world = world;
		this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
		this.camX = world.getBob().getPosition().x;
		this.camY = world.getBob().getPosition().y;
		this.cam.position.set(camX, camY, 0);
		// this.cam.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f, 0);
		this.cam.update();
		this.debug = debug;
		spriteBatch = new SpriteBatch();
		font = assets.get("fonts/villa.fnt", BitmapFont.class);
		loadTextures();
	}

	private void loadTextures() {
		// Load Bob's Textures
		TextureAtlas atlas = assets.get("images/textures/BobAndMap.pack",
				TextureAtlas.class);

		TextureRegion[] idleLeftFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			idleLeftFrames[i] = atlas.findRegion("bob-idle-0" + (i + 1));
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
			walkLeftFrames[i] = atlas.findRegion("bob-move-0" + (i + 1));
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

		TextureRegion[] fireLeftFrames = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			fireLeftFrames[i] = atlas.findRegion("bob-attack-0" + (i + 1));
		}
		fireLeftAnimation = new Animation(FIRING_FRAME_DURATION, fireLeftFrames);

		TextureRegion[] fireRightFrames = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			fireRightFrames[i] = new TextureRegion(fireLeftFrames[i]);
			fireRightFrames[i].flip(true, false);
		}
		fireRightAnimation = new Animation(FIRING_FRAME_DURATION,
				fireRightFrames);

		TextureRegion[] firePressedLeftFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			firePressedLeftFrames[i] = atlas.findRegion("bob-attack-pressed-0"
					+ (i + 1));
		}
		firePressedLeftAnimation = new Animation(FIRING_PRESSED_FRAME_DURATION,
				firePressedLeftFrames);

		TextureRegion[] firePressedRightFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			firePressedRightFrames[i] = new TextureRegion(
					firePressedLeftFrames[i]);
			firePressedRightFrames[i].flip(true, false);
		}
		firePressedRightAnimation = new Animation(
				FIRING_PRESSED_FRAME_DURATION, firePressedRightFrames);

		TextureRegion[] hitLeftFrames = new TextureRegion[3];
		for (int i = 0; i < 3; i++) {
			hitLeftFrames[i] = atlas.findRegion("bob-hit-0" + (i + 1));
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
			dieLeftFrames[i] = atlas.findRegion("bob-die-0" + (i + 1));
		}
		dieLeftAnimation = new Animation(DYING_FRAME_DURATION, dieLeftFrames);

		TextureRegion[] dieRightFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			dieRightFrames[i] = new TextureRegion(dieLeftFrames[i]);
			dieRightFrames[i].flip(true, false);
		}
		dieRightAnimation = new Animation(DYING_FRAME_DURATION, dieRightFrames);

		bobJumpLeft = atlas.findRegion("bob-jump");
		bobJumpRight = new TextureRegion(bobJumpLeft);
		bobJumpRight.flip(true, false);
	}

	public void render() {
		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		drawBlocksAndEnemies();
		drawBalls();
		drawBob();
		drawScore();
		spriteBatch.end();

		if (debug) {
			drawCollisionBlocks();
			drawDebug();
		}
	}

	private void drawBlocksAndEnemies() {
		List<Block> blocks = new ArrayList<Block>();
		world.populateDrawableItems((int) CAMERA_WIDTH, (int) CAMERA_HEIGHT,
				blocks);
		for (Block block : blocks) {
			// spriteBatch.draw(blockTexture, block.getPosition().x * ppuX,
			// block.getPosition().y * ppuY, Block.SIZE * ppuX, Block.SIZE *
			// ppuY);
			spriteBatch.draw(block.getTexture(), block.getPosition().x,
					block.getPosition().y, Block.SIZE, Block.SIZE);
		}
		for (Enemy enemy : world.getEnemies()) {
			spriteBatch.draw(enemy.getEnemyFrame(), enemy.getPosition().x
					- enemy.getSize() / 4f, enemy.getPosition().y,
					enemy.getSize(), enemy.getSize() * 1.1f);
		}
	}

	private void drawBalls() {
		for (Ball ball : World.balls) {
			spriteBatch.draw(ball.getBallFrame(), ball.getPosition().x,
					ball.getPosition().y, Ball.SIZE, Ball.SIZE * 0.6f);
		}
	}

	private void drawBob() {
		Bob bob = world.getBob();

		if (bob.isHit()) {
			bobFrame = bob.isFacingLeft() ? hitLeftAnimation.getKeyFrame(
					bob.getStateTime(), false) : hitRightAnimation.getKeyFrame(
					bob.getStateTime(), false);
		} else if (bob.getState().equals(State.IDLE)) {
			bobFrame = bob.isFacingLeft() ? idleLeftAnimation.getKeyFrame(
					bob.getStateTime(), true) : idleRightAnimation.getKeyFrame(
					bob.getStateTime(), true);
		} else if (bob.getState().equals(State.WALKING)) {
			bobFrame = bob.isFacingLeft() ? walkLeftAnimation.getKeyFrame(
					bob.getStateTime(), true) : walkRightAnimation.getKeyFrame(
					bob.getStateTime(), true);
		} else if (bob.getState().equals(State.FIRING)) {
			if (bob.getStateTime() <= FIRING_FRAME_DURATION * 3) {
				bobFrame = bob.isFacingLeft() ? fireLeftAnimation.getKeyFrame(
						bob.getStateTime(), false) : fireRightAnimation
						.getKeyFrame(bob.getStateTime(), false);
			} else {
				bobFrame = bob.isFacingLeft() ? firePressedLeftAnimation
						.getKeyFrame(bob.getStateTime() - FIRING_FRAME_DURATION
								* 3, true) : firePressedRightAnimation
						.getKeyFrame(bob.getStateTime() - FIRING_FRAME_DURATION
								* 3, true);
			}
		} else if (bob.getState().equals(State.DYING)) {
			bobFrame = bob.isFacingLeft() ? dieLeftAnimation.getKeyFrame(
					bob.getStateTime(), false) : dieRightAnimation.getKeyFrame(
					bob.getStateTime(), false);
		} else if (bob.getState().equals(State.JUMPING)) {
			bobFrame = bob.isFacingLeft() ? bobJumpLeft : bobJumpRight;
		}
		// spriteBatch.draw(bobFrame, bob.getPosition().x * ppuX,
		// bob.getPosition().y * ppuY, Bob.SIZE * ppuX, Bob.SIZE * ppuY);
		spriteBatch.draw(bobFrame, bob.getPosition().x - Bob.SIZE / 4f,
				bob.getPosition().y, Bob.SIZE, Bob.SIZE * 1.1f);
	}

	private void drawScore() {
		// TODO render score
	}

	private void drawDebug() {
		// render blocks
		List<Block> blocks = new ArrayList<Block>();
		world.populateDrawableItems((int) CAMERA_WIDTH, (int) CAMERA_HEIGHT,
				blocks);
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Line);
		for (Block block : blocks) {
			Rectangle rect = block.getBounds();
			debugRenderer.setColor(new Color(1, 0, 0, 1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		for (Enemy enemy : world.getEnemies()) {
			Rectangle rect = enemy.getBounds();
			debugRenderer.setColor(new Color(0, 0, 0, 1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		for (Ball ball : world.getBalls()) {
			Rectangle rect = ball.getBounds();
			debugRenderer.setColor(new Color(0, 0, 1, 1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		// render Bob
		Bob bob = world.getBob();
		Rectangle rect = bob.getBounds();
		debugRenderer.setColor(new Color(0, 1, 0, 1));
		debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		debugRenderer.end();
	}

	private void drawCollisionBlocks() {
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Filled);
		debugRenderer.setColor(Color.WHITE);
		for (Rectangle rect : world.getCollisionRects()) {
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height,
					Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
		}
		debugRenderer.end();
	}
}
