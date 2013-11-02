package com.maarshgames.monsterassault.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.maarshgames.monsterassault.model.Block;
import com.maarshgames.monsterassault.model.Block.Type;
import com.maarshgames.monsterassault.model.Bob;
import com.maarshgames.monsterassault.model.Enemy;
import com.maarshgames.monsterassault.model.World;
import com.maarshgames.monsterassault.model.Bob.State;

public class WorldRenderer {

	private static final float CAMERA_WIDTH = 12.4f;
	private static final float CAMERA_HEIGHT = 7f;
	private static final float RUNNING_FRAME_DURATION = 0.1f;
	private static final float IDLE_FRAME_DURATION = 0.35f;
	private static final float FIRING_FRAME_DURATION = 0.1f;

	private World world;
	private OrthographicCamera cam;

	/** for debug rendering **/
	ShapeRenderer debugRenderer = new ShapeRenderer();

	/** Textures **/
	private TextureRegion bobFrame;
	private TextureRegion bobJumpLeft;
	private TextureRegion bobJumpRight;

	private TextureRegion blockGrassLeft;
	private TextureRegion blockGrassRight;
	private TextureRegion blockGrassCenter;
	private TextureRegion blockGrassMid;

	/** Animations **/
	private Animation idleLeftAnimation;
	private Animation idleRightAnimation;
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;
	private Animation fireLeftAnimation;
	private Animation fireRightAnimation;

	private SpriteBatch spriteBatch;
	private boolean debug = false;
	private int width;
	private int height;
	private float ppuX; // pixels per unit on the X axis
	private float ppuY; // pixels per unit on the Y axis
	private float camX;
	private float camY;

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
		ppuX = (float) width / CAMERA_WIDTH;
		ppuY = (float) height / CAMERA_HEIGHT;
	}

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
		loadTextures();
	}

	private void loadTextures() {
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("images/textures/BobAndMap.pack"));

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

		TextureRegion[] fireLeftFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			fireLeftFrames[i] = atlas.findRegion("bob-attack-pressed-0"	+(i + 1));
		}
		fireLeftAnimation = new Animation(FIRING_FRAME_DURATION, fireLeftFrames);

		TextureRegion[] fireRightFrames = new TextureRegion[8];
		for (int i = 0; i < 8; i++) {
			fireRightFrames[i] = new TextureRegion(fireLeftFrames[i]);
			fireRightFrames[i].flip(true, false);
		}
		fireRightAnimation = new Animation(FIRING_FRAME_DURATION,
				fireRightFrames);

		bobJumpLeft = atlas.findRegion("bob-jump");
		bobJumpRight = new TextureRegion(bobJumpLeft);
		bobJumpRight.flip(true, false);

		blockGrassLeft = atlas.findRegion("grassLeft");
		blockGrassRight = atlas.findRegion("grassRight");
		blockGrassCenter = atlas.findRegion("grassCenter");
		blockGrassMid = atlas.findRegion("grassMid");
	}

	public void render() {
		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		drawBlocksAndEnemies();
		drawBob();
		spriteBatch.end();
		
		if (debug) {
			drawCollisionBlocks();
			drawDebug();
		}
	}

	private void drawBlocksAndEnemies() {
		List<Block> blocks = new ArrayList<Block>();
		List<Enemy> enemies = new ArrayList<Enemy>();
		world.populateDrawableItems((int) CAMERA_WIDTH, (int) CAMERA_HEIGHT,
				blocks, enemies);
		for (Block block : blocks) {
			// spriteBatch.draw(blockTexture, block.getPosition().x * ppuX,
			// block.getPosition().y * ppuY, Block.SIZE * ppuX, Block.SIZE *
			// ppuY);
			spriteBatch.draw(getBlockTexture(block.getType()),
					block.getPosition().x, block.getPosition().y, Block.SIZE,
					Block.SIZE);
		}
		for (Enemy enemy : enemies) {
			spriteBatch.draw(enemy.getEnemyFrame(), enemy.getPosition().x-enemy.getSize()/4f, enemy.getPosition().y, enemy.getSize(), enemy.getSize()*1.1f);
		}
	}

	private void drawBob() {
		Bob bob = world.getBob();

		if (bob.getState().equals(State.IDLE)) {
			bobFrame = bob.isFacingLeft() ? idleLeftAnimation.getKeyFrame(
					bob.getStateTime(), true) : idleRightAnimation.getKeyFrame(
					bob.getStateTime(), true);
		} else if (bob.getState().equals(State.WALKING)) {
			bobFrame = bob.isFacingLeft() ? walkLeftAnimation.getKeyFrame(
					bob.getStateTime(), true) : walkRightAnimation.getKeyFrame(
					bob.getStateTime(), true);
		} else if (bob.getState().equals(State.FIRING)) {
			bobFrame = bob.isFacingLeft() ? fireLeftAnimation.getKeyFrame(
					bob.getStateTime(), true) : fireRightAnimation.getKeyFrame(
					bob.getStateTime(), true);
		} else if (bob.getState().equals(State.JUMPING)) {
			bobFrame = bob.isFacingLeft() ? bobJumpLeft : bobJumpRight;
		}
		// spriteBatch.draw(bobFrame, bob.getPosition().x * ppuX,
		// bob.getPosition().y * ppuY, Bob.SIZE * ppuX, Bob.SIZE * ppuY);
		spriteBatch.draw(bobFrame, bob.getPosition().x - Bob.SIZE / 4f,
				bob.getPosition().y, Bob.SIZE, Bob.SIZE * 1.1f);
	}

	private void drawDebug() {
		// render blocks
		List<Block> blocks = new ArrayList<Block>();
		List<Enemy> enemies = new ArrayList<Enemy>();
		world.populateDrawableItems((int) CAMERA_WIDTH, (int) CAMERA_HEIGHT,
				blocks, enemies);
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Line);
		for (Block block : blocks) {
			Rectangle rect = block.getBounds();
			debugRenderer.setColor(new Color(1, 0, 0, 1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		for (Enemy enemy : enemies) {
			Rectangle rect = enemy.getBounds();
			debugRenderer.setColor(new Color(0, 0, 0, 1));
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

	private TextureRegion getBlockTexture(Type blockType) {
		switch (blockType) {
		case grassLeft:
			return blockGrassLeft;
		case grassRight:
			return blockGrassRight;
		case grassCenter:
			return blockGrassCenter;
		case grassMid:
			return blockGrassMid;
		default:
			return null;
		}
	}
}
