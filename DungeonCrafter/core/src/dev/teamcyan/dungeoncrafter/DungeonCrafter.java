package dev.teamcyan.dungeoncrafter;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import dev.teamcyan.dungeoncrafter.classes.GameModel;
import dev.teamcyan.dungeoncrafter.screens.*;
import dev.teamcyan.dungeoncrafter.classes.AudioManager;
import dev.teamcyan.dungeoncrafter.classes.KeyListener;

/**
 * Controller of the MVC-pattern
 */
public class DungeonCrafter extends Game {


	private AssetManager assetManager;

	/**
	 * Width of the game window
	 */
	public static final int WIDTH = 720;
	/**
	 * Height of the game window
	 */
	public static final int HEIGHT = 720;
	/**
	 * global rate of zoom changes
	 */
	public static final float ZOOM_FACTOR = 0.1f;
	public float totTime;
	public double startZoom;

	public AudioManager audioManager;
	public KeyListener keyListener;

	/**
	 * The model of the game, which holds the state of the current game. Model of the MVC-pattern.
	 */
	private GameModel model;
	private boolean debugMode = true;

	/**
	 * List of available screens of the game. View of the MVC-pattern.
	 */
	private ObjectMap<Class<? extends BaseScreen>, BaseScreen> screens = new ObjectMap<>();


	/**
	 * Convenience method to safely load assets
	 */
   public void init_asset_manager()
   {
		assetManager = new AssetManager();
		assetManager.load("spritesheets/sprites.atlas", TextureAtlas.class);
		assetManager.finishLoading();
   }

	/**
	 * Creates new game, including keyListener, audioManager.
	 * Loads all game screens.
	 */
	@Override
	public void create () 
   {
		this.setDebugOn(true);
		this.model = new GameModel();
		VisUI.load();
      
	   // Initialise the audio manager
	   audioManager = new AudioManager();

	   // Initialise the key listener
	   keyListener = new KeyListener();
	   init_asset_manager();
	   loadScreens();
	   newGame();
	}

	/**
	 * Disposes all screens and GameModel, when game is exited.
	 */
	@Override
	public void dispose () 
   {
		setScreen(null); // Dispose of the view
		for (ObjectMap.Entry<Class<? extends BaseScreen>,BaseScreen> var : screens)
		{
			var.value.dispose();
		}
		screens.clear();
		if(VisUI.isLoaded()) VisUI.dispose();

		// Dispose of the model
		model.dispose();
	}

	/**
    * Convenience method to safely load textures. If the texture isn't found, a
    * blank one is created and the error is logged.
	 * @param textureName The name of the image that is being looked up.
	 * @return TextureAtlas.AtlasRegion
	 */
	public TextureAtlas.AtlasRegion getAtlasRegion(String textureName) {
		try 
      {
			return assetManager.get("spritesheets/sprites.atlas", TextureAtlas.class)
           .findRegion(textureName);
		} 
      catch(Exception e) 
      {
			Gdx.app.error(
             getClass()
             .getCanonicalName(), "Couldn't get managed texture.", e);
			return getEmptyAtlasRegion();
		}
	}

	/**
	 * Convenience method to provide getAtlasRegion() with empty empty AtlasRegion in case of error.
	 * @return TextureAtlas.AtlasRegion
	 */
	public TextureAtlas.AtlasRegion getEmptyAtlasRegion() {
		return new TextureAtlas
        .AtlasRegion(
            new TextureRegion(
              new Texture(
                new Pixmap(1,1, Pixmap.Format.RGBA8888))));
	}

	/**
	 * Starts a new session that begins with the Main Menu Screen.
	 */
	public void newGame() {
		this.changeScreen(MainMenuScreen.class);
	}

	/**
	 * @return boolean
	 */
	public boolean isDebugOn() {
		return debugMode;
	}

	/**
	 * Set the debugging mode for amount of logging information printed.
	 * @param on boolean to set debugMode to
	 * @return DungeonCrafter
	 */
	public DungeonCrafter setDebugOn(boolean on) {
		this.debugMode = on;
		Gdx.app.setLogLevel(on ? Application.LOG_DEBUG : Application.LOG_INFO);
		return this;
	}

	/**
	 * Switches between Screens of the game.
	 * @param key The Screen class to set the current screen to
	 */
	public void changeScreen(Class<? extends BaseScreen> key) {
		this.setScreen(screens.get(key));
		//handle(new GameEvent("SCREEN_CHANGE").set("SCREEN", screens.get(key)));
		if(key.getName() != "dev.teamcyan.dungeoncrafter.screens.MainMenuScreen" &
				key.getName() != "dev.teamcyan.dungeoncrafter.screens.DifficultyScreen" &
		key.getName() != "dev.teamcyan.dungeoncrafter.screens.SettingsScreen" &
				key.getName() != "dev.teamcyan.dungeoncrafter.screens.CreditsScreen")
		{
			audioManager.fadeMusicOut(audioManager.menuSound);
		}
		if(key.getName() == "dev.teamcyan.dungeoncrafter.screens.MainMenuScreen"){
			audioManager.fadeMusicOut(audioManager.ambients);
			audioManager.stopMusic(audioManager.ambients);
		}
	}

	/**
	 * Instantiates all necessary screens of the game session.
	 */
	public void loadScreens() {
		screens.put(InventoryScreen.class, new InventoryScreen(this, model));
		screens.put(MainGameScreen.class, new MainGameScreen(this, model));
		screens.put(MainMenuScreen.class, new MainMenuScreen(this, model));
		screens.put(SettingsScreen.class, new SettingsScreen(this, model));
		screens.put(DifficultyScreen.class, new DifficultyScreen(this, model));
		screens.put(GameOverScreen.class, new GameOverScreen(this, model));
		screens.put(CreditsScreen.class, new CreditsScreen(this, model));
	}

	/**
	 * Restart the game by overriding all screens and GameModel, and setting current screen to DifficultyScreen
	 */
	public void restartGame() {
		this.screens = new ObjectMap<>();
		loadScreens();
		model.startNewGame(this);
		this.changeScreen(DifficultyScreen.class);
	}

	/**
	 * Get the model of the current game session.
	 * @return GameModel
	 */
	public GameModel getModel() {
		return this.model;
	}
}
