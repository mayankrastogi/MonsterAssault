package com.maarshgames.monsterassault.model;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.maarshgames.monsterassault.controller.LevelLoader;
import com.maarshgames.monsterassault.model.Enemy.EnemyState;

public class World {

	/** Our player controlled hero **/
	public static Bob bob;
	/** A world has a level through which Bob needs to go through **/
	public static Level level;

	public static int score = 0;

	public static Array<Enemy> enemies = new Array<Enemy>();

	public static Array<Ball> balls = new Array<Ball>();

	/** The collision boxes **/
	public Array<Rectangle> collisionRects = new Array<Rectangle>();

	public World() {
		createWorld();
	}

	private void createWorld() {

		level = LevelLoader.loadLevel(1);
		bob = new Bob(level.getSpawnPosition());
	}

	// Getters -----------

	public Array<Rectangle> getCollisionRects() {
		return collisionRects;
	}

	public Bob getBob() {
		return bob;
	}

	public Level getLevel() {
		return level;
	}

	public Array<Enemy> getEnemies() {
		return enemies;
	}

	public Array<Ball> getBalls() {
		return balls;
	}

	public static void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}

	public static void removeEnemy(Enemy enemy) {
		enemies.removeValue(enemy, false);
	}

	public static void addBall(Ball ball) {
		balls.add(ball);
	}

	public static void removeBall(Ball ball) {
		balls.removeValue(ball, false);
	}

	/** Return only the blocks that need to be drawn **/

	public void populateDrawableItems(int width, int height, List<Block> blocks) {
		int x = (int) bob.getPosition().x - width;
		int y = (int) bob.getPosition().y - height;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		int x2 = x + 2 * width;
		int y2 = y + 2 * height;
		if (x2 >= level.getWidth()) {
			x2 = level.getWidth() - 1;
		}
		if (y2 >= level.getHeight()) {
			y2 = level.getHeight() - 1;
		}

		Block block;
		Enemy enemy;
		for (int col = x; col <= x2; col++) {
			for (int row = y; row <= y2; row++) {
				block = level.getBlocks()[col][row];
				if (block != null) {
					blocks.add(block);
				}
				enemy = level.getEnemies()[col][row];
				if (enemy != null) {
					if (enemy.getState().equals(EnemyState.INACTIVE)) {
						enemy.setState(EnemyState.IDLE);
						World.addEnemy(enemy);
					}
				}
			}
		}
	}
}
