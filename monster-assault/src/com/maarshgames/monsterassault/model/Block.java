package com.maarshgames.monsterassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {

	public static final float SIZE = 1f;

	public enum Type {
		grassLeft, grassRight, grassCenter, grassMid
	}

	Vector2 position = new Vector2();
	Rectangle bounds = new Rectangle();
	Type type;

	public Block(Vector2 pos, Type type) {
		this.position = pos;
		this.bounds.setX(pos.x);
		this.bounds.setY(pos.y);
		this.bounds.width = SIZE;
		this.bounds.height = SIZE;
		this.type = type;
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
}
