package com.maarshgames.monsterassault;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.maarshgames.monsterassault.screens.GameScreen;
import com.maarshgames.monsterassault.screens.LoadingScreen;
import com.maarshgames.monsterassault.screens.MainMenuScreen;
import com.maarshgames.monsterassault.screens.SplashScreen;

public class MonsterAssault extends Game {

	public static AssetManager assets;

	public SplashScreen splashScreen;
	public MainMenuScreen mainMenuScreen;
	public LoadingScreen loadingScreen;
	public GameScreen gameScreen;

	@Override
	public void create() {
		// create asset manager
		assets = new AssetManager();

		// load assets for splash screen
		assets.load("images/splash.jpg", Texture.class);
		assets.finishLoading();

		// Catch back-key and menu-key press events
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);

		// create screens
		splashScreen = new SplashScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		loadingScreen = new LoadingScreen(this);
		gameScreen = new GameScreen(this);

		// show splash-screen
		setScreen(splashScreen);
	}
}
