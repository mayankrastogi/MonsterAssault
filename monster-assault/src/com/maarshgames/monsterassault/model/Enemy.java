package com.maarshgames.monsterassault.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public abstract class Enemy {

	public enum EnemyType {
		JUSTIN, XYZ
	}

	public enum EnemyState {
		INACTIVE, IDLE, WALKING, JUMPING, ATTACKING, DYING
	}

	Vector2 position;
	Vector2 acceleration;
	Vector2 velocity;
	Rectangle bounds;
	int hitPoints;
	int damage;
	EnemyState state;
	boolean facingLeft;
	float stateTime;
	EnemyType enemyType;
	TextureRegion enemyFrame;
	float size;
	boolean hit;

	// This is the rectangle pool used in collision detection
	// Good to avoid instantiation each frame
	protected Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};

	public Enemy(Vector2 position, float size, int hitPoints, int damage,
			EnemyType enemyType) {
		acceleration = new Vector2();
		velocity = new Vector2();
		bounds = new Rectangle();
		this.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.size = size;
		this.hitPoints = hitPoints;
		this.damage = damage;
		this.enemyType = enemyType;
		enemyFrame = null;
		facingLeft = true;
		state = EnemyState.INACTIVE;
		stateTime = 0;
		hit = false;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public int getHitPoints() {
		return hitPoints;
	}

	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}

	public int getDamage() {
		return damage;
	}

	public EnemyState getState() {
		return state;
	}

	public void setState(EnemyState newState) {
		this.state = newState;
		this.stateTime = 0;
	}

	public float getStateTime() {
		return stateTime;
	}

	public EnemyType getEnemyType() {
		return enemyType;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public TextureRegion getEnemyFrame() {
		return enemyFrame;
	}

	public void setEnemyFrame(TextureRegion enemyFrame) {
		this.enemyFrame = enemyFrame;
	}

	public float getSize() {
		return size;
	}

	public abstract void update(float delta);

	public abstract void hit(int damage);
}
