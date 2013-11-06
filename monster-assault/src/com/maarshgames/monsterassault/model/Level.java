package com.maarshgames.monsterassault.model;

import com.badlogic.gdx.math.Vector2;

public class Level {

	private int width;
	private int height;
	private Block[][] blocks;
	private Enemy[][] enemies;
	private Vector2 spawnPosition;
	private Block door;
	private int numberOfEnemies;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Block[][] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[][] blocks) {
		this.blocks = blocks;
	}

	public Enemy[][] getEnemies() {
		return enemies;
	}

	public void setEnemies(Enemy[][] enemies) {
		this.enemies = enemies;
	}

	public Block getBlock(int x, int y) {
		return blocks[x][y];
	}

	public Enemy getEnemy(int x, int y) {
		return enemies[x][y];
	}

	public Vector2 getSpawnPosition() {
		return spawnPosition;
	}

	public void setSpawnPosition(Vector2 spawnPosition) {
		this.spawnPosition = spawnPosition;
	}

	public Block getDoor() {
		return door;
	}

	public void setDoor(Block doorBlock) {
		this.door = doorBlock;
	}

	public int getNumberOfEnemies() {
		return numberOfEnemies;
	}

	public void setNumberOfEnemies(int numberOfEnemies) {
		this.numberOfEnemies = numberOfEnemies;
	}
}
