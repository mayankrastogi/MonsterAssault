package com.maarshgames.monsterassault.model;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {

	public static final float SIZE = 1f;

	public static TextureRegion grassLeft;
	public static TextureRegion grassRight;
	public static TextureRegion grassCenter;
	public static TextureRegion grassMid;
	public static TextureRegion doorClosed;
	public static TextureRegion doorOpened;

	public enum Type {
		GRASS_LEFT, GRASS_RIGHT, GRASS_CENTER, GRASS_MID, DOOR_CLOSED, DOOR_OPENED
	}

	Vector2 position = new Vector2();
	Rectangle bounds = new Rectangle();
	Type type;
	TextureRegion texture;

	public Block(Vector2 pos, Type type) {
		this.position = pos;
		this.bounds.setX(pos.x);
		this.bounds.setY(pos.y);
		this.bounds.width = SIZE;
		this.bounds.height = SIZE;
		this.type = type;
		this.texture = getTexture(type);
	}

	public Vector2 getPosition() {
		return position;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
		this.texture = getTexture(type);
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}

	public static TextureRegion getTexture(Type blockType) {
		switch (blockType) {
		case GRASS_LEFT:
			return grassLeft;
		case GRASS_RIGHT:
			return grassRight;
		case GRASS_CENTER:
			return grassCenter;
		case GRASS_MID:
			return grassMid;
		case DOOR_CLOSED:
			return doorClosed;
		case DOOR_OPENED:
			return doorOpened;
		default:
			return null;
		}
	}

	public static void loadTextures(TextureAtlas atlas) {
		grassLeft = atlas.findRegion("grassLeft");
		grassRight = atlas.findRegion("grassRight");
		grassCenter = atlas.findRegion("grassCenter");
		grassMid = atlas.findRegion("grassMid");
		doorClosed = atlas.findRegion("door-closed");
		doorOpened = atlas.findRegion("door-opened");
	}
}
