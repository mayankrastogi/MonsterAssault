package com.maarshgames.monsterassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.maarshgames.monsterassault.model.Enemy.EnemyState;

public class Bob {

	public enum State {
		IDLE, WALKING, JUMPING, FIRING, DYING
	}

	public static final float SIZE = 0.9f; // half a unit
	public static final int HIT_POINTS = 100;
	public static final int DAMAGE = 15;

	Vector2 position = new Vector2();
	Vector2 acceleration = new Vector2();
	Vector2 velocity = new Vector2();
	Rectangle bounds = new Rectangle();
	int hitPoints = HIT_POINTS;
	int damage = DAMAGE;
	State state = State.IDLE;
	boolean facingLeft = true;
	float stateTime = 0;
	boolean longJump = false;
	boolean hit = false;

	public Bob(Vector2 position) {
		this.position = position;
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		this.bounds.height = SIZE;
		this.bounds.width = SIZE / 2f;
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

	public State getState() {
		return state;
	}

	public void setState(State newState) {
		this.state = newState;
		this.stateTime = 0;
	}

	public float getStateTime() {
		return stateTime;
	}

	public boolean isLongJump() {
		return longJump;
	}

	public void setLongJump(boolean longJump) {
		this.longJump = longJump;
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

	public void update(float delta) {
		stateTime += delta;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public void hit(int damage) {
		if (!hit && !state.equals(EnemyState.DYING)) {
			hit = true;
			stateTime = 0;
			hitPoints -= damage;
		}
	}
}
