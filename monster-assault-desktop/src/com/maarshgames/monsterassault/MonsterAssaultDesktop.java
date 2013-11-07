package com.maarshgames.monsterassault;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class MonsterAssaultDesktop {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Monster Assault";
		config.width = 848;
		config.height = 480;
		config.useGL20 = true;
		config.addIcon("icons/icon_128.png", FileType.Internal);
		config.addIcon("icons/icon_48.png", FileType.Internal);
		config.addIcon("icons/icon_32.png", FileType.Internal);
		config.addIcon("icons/icon_16.png", FileType.Internal);

		new LwjglApplication(new MonsterAssault(), config);
	}

}
