package com.maarshgames.monsterassault.view;

import static com.maarshgames.monsterassault.MonsterAssault.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.maarshgames.monsterassault.model.Bob;
import com.maarshgames.monsterassault.model.World;

public class GuiRenderer {

	private static final float GUI_CAM_WIDTH = 848f;
	private static final float GUI_CAM_HEIGHT = 480f;

	private static final float HEALTH_BAR_WIDTH = 250f;
	private static final float HEALTH_BAR_HEIGHT = 15f;

	private static final float BAR_UNIT_WIDTH = HEALTH_BAR_WIDTH
			/ Bob.HIT_POINTS;

	private World world;
	private OrthographicCamera guiCam;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private TextureRegion bobFace;
	private Texture pausedScreen;
	private BitmapFont font;

	public GuiRenderer(World world) {
		this.world = world;

		font = assets.get("fonts/villa.fnt", BitmapFont.class);
		bobFace = assets.get("images/textures/BobAndMap.pack",
				TextureAtlas.class).findRegion("bob-face");
		pausedScreen = assets.get("images/game-paused.png", Texture.class);

		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		guiCam = new OrthographicCamera(GUI_CAM_WIDTH, GUI_CAM_HEIGHT);
		guiCam.update();
	}

	public void render(boolean drawFPS, boolean paused) {
		// Draw HUD
		drawHealthBar();
		spriteBatch.setProjectionMatrix(guiCam.combined);
		spriteBatch.begin();
		drawBobFace();
		drawScore();
		if (drawFPS) {
			drawFPS();
		}
		if (paused) {
			spriteBatch.draw(pausedScreen, guiCam.position.x
					- (GUI_CAM_WIDTH / 2), guiCam.position.y
					- (GUI_CAM_HEIGHT / 2), GUI_CAM_WIDTH, GUI_CAM_HEIGHT);
		}
		spriteBatch.end();
	}

	private void drawBobFace() {
		spriteBatch.draw(bobFace, guiCam.position.x - (GUI_CAM_WIDTH / 2) + 15,
				guiCam.position.y + (GUI_CAM_HEIGHT / 2) - 82f);
	}

	private void drawHealthBar() {
		shapeRenderer.setProjectionMatrix(guiCam.combined);

		float x = guiCam.position.x - (GUI_CAM_WIDTH / 2) + 87f;
		float y = guiCam.position.y + (GUI_CAM_HEIGHT / 2) - 37f;
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
		float x = guiCam.position.x + (GUI_CAM_WIDTH / 2)
				- font.getBounds("" + World.score).width - 25;
		float y = guiCam.position.y + (GUI_CAM_HEIGHT / 2) + 10;
		font.draw(spriteBatch, "" + World.score, x, y);
	}

	private void drawFPS() {
		float x = guiCam.position.x + (GUI_CAM_WIDTH / 2)
				- font.getBounds("" + Gdx.graphics.getFramesPerSecond()).width
				- 25;
		float y = guiCam.position.y - (GUI_CAM_HEIGHT / 2)
				+ font.getBounds("" + Gdx.graphics.getFramesPerSecond()).height
				+ 25;
		font.draw(spriteBatch, "" + Gdx.graphics.getFramesPerSecond(), x, y);
	}
}
