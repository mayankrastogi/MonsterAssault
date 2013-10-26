package com.maarshgames.monsterassault;

import com.badlogic.gdx.Game;
import com.maarshgames.monsterassault.screens.GameScreen;

public class MonsterAssault extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}

}
