/**
 * 
 */
package com.maarshgames.monsterassault.utils;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class TextureSetup {

	public static void main(String[] args) {
		 TexturePacker2.process("D:\\Projects\\GitHub\\MonsterAssault\\monster-assault-android\\assets\\images\\BobAndMap", "D:\\Projects\\GitHub\\MonsterAssault\\monster-assault-android\\assets\\images\\textures", "BobAndMap.pack");
		 TexturePacker2.process("D:\\Projects\\GitHub\\MonsterAssault\\monster-assault-android\\assets\\images\\Justin", "D:\\Projects\\GitHub\\MonsterAssault\\monster-assault-android\\assets\\images\\textures", "Justin.pack");
	}
}
