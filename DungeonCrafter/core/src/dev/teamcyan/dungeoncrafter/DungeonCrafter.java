package dev.teamcyan.dungeoncrafter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import dev.teamcyan.dungeoncrafter.classes.GMap;
import dev.teamcyan.dungeoncrafter.classes.GameModel;
import dev.teamcyan.dungeoncrafter.screens.BaseScreen;
import dev.teamcyan.dungeoncrafter.screens.InventoryScreen;
import dev.teamcyan.dungeoncrafter.screens.MainGameScreen;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import sun.applet.Main;

public class DungeonCrafter extends Game {

	public static final int WIDTH = 720;
	public static final int HEIGHT = 720;

	public SpriteBatch batch;

	private GameModel model;
	private boolean debugMode = true;
	private AssetManager assetManager;
	private ObjectMap<Class<? extends BaseScreen>, BaseScreen> screens = new ObjectMap<>();

	@Override
	public void create () {
		VisUI.load();

		// Initialize the asset manager
		assetManager = new AssetManager();
		assetManager.load("textures.atlas", TextureAtlas.class);
		assetManager.finishLoading();

		this.setDebugOn(true);
		this.model = new GameModel();
		loadScreens();
		newGame();
		//batch = new SpriteBatch();
		//this.setScreen(new MainGameScreen(this, new GMap(this.WIDTH, this.HEIGHT)));
	}

	@Override
	public void dispose () {
		// Dispose of the view
		setScreen(null);
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
	 * Convenience method to safely load textures. If the texture isn't found, a blank one is created and the error is logged.
	 * @param textureName The name of the image that is being looked up.
	 * @return
	 */
	public TextureAtlas.AtlasRegion getAtlasRegion(String textureName) {
		try {
			return assetManager.get("textures.atlas", TextureAtlas.class).findRegion(textureName);
		} catch(Exception e) {
			Gdx.app.error(getClass().getCanonicalName(), "Couldn't get managed texture.", e);
			return getEmptyAtlasRegion();
		}
	}
	public TextureAtlas.AtlasRegion getEmptyAtlasRegion() {
		return new TextureAtlas.AtlasRegion(new TextureRegion(new Texture(new Pixmap(1,1, Pixmap.Format.RGBA8888))));
	}

	//	  Create a new game, starting with the story screen
	public void newGame() {
		model.startNewGame(this);
		this.changeScreen(MainGameScreen.class);
	}

	// === Debug Logic === //
	public boolean isDebugOn() {
		return debugMode;
	}
	public DungeonCrafter setDebugOn(boolean on) {
		this.debugMode = on;
		Gdx.app.setLogLevel(on ? Application.LOG_DEBUG : Application.LOG_INFO);
		return this;
	}

	// Screen Management
	public void changeScreen(Class<? extends BaseScreen> key) {
		this.setScreen(screens.get(key));
		//handle(new GameEvent("SCREEN_CHANGE").set("SCREEN", screens.get(key)));
	}

	public void loadScreens() {
		screens.put(InventoryScreen.class, new InventoryScreen(this, model));
		screens.put(MainGameScreen.class, new MainGameScreen(this, model));
	}
	/*@Override
	public void render () {
		super.render();
	}*/

}
