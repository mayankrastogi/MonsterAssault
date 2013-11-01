package com.maarshgames.monsterassault.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.maarshgames.monsterassault.model.Block;
import com.maarshgames.monsterassault.model.Enemy;
import com.maarshgames.monsterassault.model.Justin;
import com.maarshgames.monsterassault.model.Block.Type;
import com.maarshgames.monsterassault.model.Level;

/**
 * Created with IntelliJ IDEA. User: tamas Date: 26/03/2013 Time: 15:30 To
 * change this template use File | Settings | File Templates.
 */
public class LevelLoader {

	private static final String LEVEL_PREFIX = "levels/level-";

	private static final int EMPTY = 0xffffff; // white
	private static final int BLOCK_LEFT = 0xb5e61d; // lime
	private static final int BLOCK_RIGHT = 0xfff200; // yellow
	private static final int BLOCK_MID = 0x22b14c; // green
	private static final int BLOCK_CENTER = 0x000000; // black
	private static final int START_POS = 0x3f48cc; // blue
	private static final int JUSTIN = 0xed1c24; // red
	private static final int ENEMY_2 = 0xffaec9; // pink
	private static final int EXIT = 0x7f7f7f; // grey
	private static final int CHECKPOINT = 0xa34aa4; // purple

	public static Level loadLevel(int number) {
		Level level = new Level();

		// Loading the png into a Pixmap
		Pixmap pixmap = new Pixmap(Gdx.files.internal(LEVEL_PREFIX + number
				+ ".png"));

		// setting the size of the level based on the size of the pixmap
		level.setWidth(pixmap.getWidth());
		level.setHeight(pixmap.getHeight());

		// creating the backing blocks array
		Block[][] blocks = new Block[level.getWidth()][level.getHeight()];
		Enemy[][] enemies = new Enemy[level.getWidth()][level.getHeight()];
		for (int col = 0; col < level.getWidth(); col++) {
			for (int row = 0; row < level.getHeight(); row++) {
				blocks[col][row] = null;
				enemies[col][row] = null;
			}
		}

		for (int row = 0; row < level.getHeight(); row++) {
			for (int col = 0; col < level.getWidth(); col++) {
				int pixel = (pixmap.getPixel(col, row) >>> 8) & 0xffffff;
				int iRow = level.getHeight() - 1 - row;

				switch (pixel) {
				case BLOCK_LEFT:
					blocks[col][iRow] = new Block(new Vector2(col, iRow),
							Type.grassLeft);
					break;
				case BLOCK_RIGHT:
					blocks[col][iRow] = new Block(new Vector2(col, iRow),
							Type.grassRight);
					break;
				case BLOCK_MID:
					blocks[col][iRow] = new Block(new Vector2(col, iRow),
							Type.grassMid);
					break;
				case BLOCK_CENTER:
					blocks[col][iRow] = new Block(new Vector2(col, iRow),
							Type.grassCenter);
					break;
				case START_POS:
					level.setSpawnPosition(new Vector2(col, iRow));
					break;
				case EXIT:
					level.setExitPosition(new Vector2(col, iRow));
					break;
				// case CHECKPOINT:
				// level.setCheckpointPosition(new Vector2(col, iRow));
				// break;
				case JUSTIN:
					enemies[col][iRow] = new Justin(new Vector2(col, iRow));
					break;
				// case ENEMY_2:
				// level.setEnemySpawnPosition(new Vector2(col, iRow),
				// EnemyType.enemy2);
				// break;
				}
			}
		}

		// setting the blocks
		level.setBlocks(blocks);
		level.setEnemies(enemies);
		return level;
	}

}
