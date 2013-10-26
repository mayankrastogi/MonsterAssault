package com.maarshgames.monsterassault;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.maarshgames.monsterassault.MonsterAssault;

public class MonsterAssaultDesktop {

	public static void main(String[] args) {
		new LwjglApplication(new MonsterAssault(), "Monster Assault", 480, 320,
				true);
	}

}
