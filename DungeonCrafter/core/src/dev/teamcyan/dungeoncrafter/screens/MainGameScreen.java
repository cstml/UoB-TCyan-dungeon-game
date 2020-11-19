package dev.teamcyan.dungeoncrafter.screens;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.utils.Timer;
import dev.teamcyan.dungeoncrafter.DungeonCrafter;
import dev.teamcyan.dungeoncrafter.classes.*;
import dev.teamcyan.dungeoncrafter.classes.Pos;
import sun.rmi.rmic.Main;

public class MainGameScreen extends BaseScreen {

    SpriteBatch batch = new SpriteBatch();
    SpriteBatch hud = new SpriteBatch();
    boolean movingRight = false;
    boolean movingLeft = false;
    boolean movingUp = false;
    boolean movingDown = false;
    boolean zoomIn = false;
    boolean zoomOut = false;
    boolean keyD = false;
    boolean keyA = false;



    Matrix4 uiMatrix;
    private BitmapFont font;
    private BitmapFont timerFont;
    private ArrayList<String> keyInfo;
    private String mouseInfo;
    private float timeLeft;
    private float totTime;
    private GameModel model;

  /**
   * Constructor - 
   * Inherit the game and the model fron the parent class
   */
    public MainGameScreen(DungeonCrafter parent, GameModel model) {
        super(parent, model);
        this.model = model;
    }


    /**
     *Initialise the Game Menu Screen
     * */
    @Override
    public void init() {

        this.model.setCameraZoom(0.5f);

        model.getPlayer().getPosition().setX(model.getMap().getMapPixelWidth()/2);
        model.getPlayer().getPosition().setY(model.getMap().getMapPixelHeight()/2);

        model.getPebble().getPosition().setX(model.getMap().getMapPixelWidth()/2+10);
        model.getPebble().getPosition().setY(model.getMap().getMapPixelHeight()/2+10);

        model.getEnemy().getPosition().setX(model.getMap().getMapPixelWidth()/2+20);
        model.getEnemy().getPosition().setY(model.getMap().getMapPixelHeight()/2+20);

        font = new BitmapFont();
        timerFont = new BitmapFont();
        keyInfo = new ArrayList<String>();
        mouseInfo = "";
        timeLeft = 60;
        totTime = 60;

        // Create countdown variable for overlay
        Timer timer=new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                timeLeft = timeLeft - (float)0.1;
                // Attempting to add interactive audio - need to fix audio buffer error
                //if(timeLeft < 0.9 * totTime){
                //    MainGameScreen.super.controller.audioManager.startMusicStr(
                //            "tick");
                //}
            }
        }, 0, (float)0.1, (int)totTime*10);

    }


    @Override
    public void draw(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        model.getCamera().zoom = (1 - ((totTime - timeLeft)/(totTime*10)));
        // Cap max and min zoom levels
        if(zoomIn) {
            model.getCamera().zoom = (float) Math.max(0.4, (model.getCamera().zoom - DungeonCrafter.ZOOM_FACTOR));
        }
        if(zoomOut) {
            model.getCamera().zoom = (float) Math.min(3, (model.getCamera().zoom + DungeonCrafter.ZOOM_FACTOR));
        }
        if(keyD) {
            System.out.println("D");
            TiledMapTileLayer.Cell curCell = model.getMap().getBlock(new Pos(
                    model.getPlayer().getPosition().getX(),
                    model.getPlayer().getPosition().getY()));

            model.getMap().interactBlock(new Pos(
                  model.getPlayer().getPosition().getX(),
                  model.getPlayer().getPosition().getY()));

            // Attempting to add interactive audio effects - need to resolve audio buffer error
            //if(curCell != null) {
            //    if (curCell.getTile().getId() == 1)
            //        super.controller.audioManager.startMusic(super.controller.audioManager.breakStone);
            //}
        }
        if(keyA) {
            System.out.println("D");
            model.getMap().digBlock(new Pos(
                  model.getPlayer().getPosition().getX(),
                  model.getPlayer().getPosition().getY()));
        }

        TiledMapTileLayer layer = (TiledMapTileLayer)model
                                .getMap()
                                .getTiledMap()
                                .getLayers()
                                .get("Tile Layer 1");

        model.getPlayer().setRegion(controller.keyListener);

        model.getCamera().translate(
                (model.getPlayer().getPosition().getX() - model.getPlayer().setX(
                        layer, movingLeft, movingRight)) * (-1),
                (model.getPlayer().getPosition().getY() - model.getPlayer().setY(
                        layer, movingUp, movingDown)) * (-1), 0);

        model.getPebble().setRegion();
        model.getPebble().setX(layer, model.getPlayer().getPosition());
        model.getPebble().setY(layer);

        model.getEnemy().setRegion();
        model.getEnemy().setX(layer, model.getPlayer().getPosition());
        model.getEnemy().setY(layer);

        model.getMap().getMapRenderer().setView(model.getCamera());
        model.getMap().getMapRenderer().render();
        batch.setProjectionMatrix(model.getCamera().combined);

        batch.begin();

        if(uiMatrix == null){
            uiMatrix = model.getCamera().combined.cpy();
        }


        model.getCamera().update();

        font.setColor(1,1,1,1);

        GEPlayer player = model.getPlayer();
        batch.draw(
            player.getRegion(),
            player.getPosition().getX(), 
            player.getPosition().getY(), 
            player.getRegion().getRegionWidth(), 
            player.getRegion().getRegionHeight()); 

        GEPebble pebble = model.getPebble();
        batch.draw(pebble.getRegion(), pebble.getPosition().getX(), pebble.getPosition().getY(), pebble.getRegion().getRegionWidth(),pebble.getRegion().getRegionHeight()); // this will be diffrent when you have nummbers at end eg player_1, player_2

        GEEnemy enemy = model.getEnemy();
        batch.draw(enemy.getRegion(), enemy.getPosition().getX(), enemy.getPosition().getY(), enemy.getRegion().getRegionWidth(), enemy.getRegion().getRegionHeight());

        font.setColor(1,1,1,1);
        float redAmount = (float)(totTime - timeLeft) / (float)totTime;
        float greenAmount = 1-redAmount;
        timerFont.setColor(redAmount, greenAmount, 0, 1); // Fade font from green to red as time runs out
        System.out.println(redAmount);
        System.out.println(greenAmount);
        timerFont.getData().setScale(2);
        font.draw(batch, mouseInfo, player.getPosition().getX(), player.getPosition().getY()+150);
        font.draw(batch, "Mouse XY:", player.getPosition().getX(), player.getPosition().getY()+170);
        font.draw(batch, "Keys active:", player.getPosition().getX(), player.getPosition().getY()+200);

        //for(int i = 0; i < keyInfo.size(); i++)
        //{
        //    font.draw(batch, super.controller.keyListener.activeKeys.get(i), player.getPosition().getX()+80+(i*10), player.getPosition().getY()+200);
        //}
        timerFont.draw(batch, Float.toString(timeLeft).substring(0, 4), player.getPosition().getX() - 120, player.getPosition().getY() + 130);
        batch.end();
        model.getCamera().update();

    }

    @Override
    public void resize(int width, int height) {
      model.getCamera().viewportWidth = width;
      model.getCamera().viewportHeight = height;
      model.getCamera().position.set(model.getMap().getMapPixelWidth()/2f, model.getMap().getMapPixelHeight()/2f, 0); //by default model.getCamera() position on (0,0,0)
      model.getCamera().update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        hud.dispose();
        model.getMap().getTiledMap().dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        super.controller.keyListener.keyDownListener(keycode);

        if(keycode == Input.Keys.LEFT) 
            movingLeft = true;
        
        if(keycode == Input.Keys.RIGHT) {
            movingRight = true;
        }
        if(keycode == Input.Keys.UP) {
            movingUp = true;
        }
        if(keycode == Input.Keys.DOWN) {
            movingDown = true;
        }
        if(keycode == Input.Keys.I) {
            zoomIn = true;
        }
        if(keycode == Input.Keys.O) {
            zoomOut = true;
        }
        if(keycode == Input.Keys.D)
            keyD = true;

        if(keycode == Input.Keys.A) 
            keyA = true;

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        super.controller.keyListener.keyUpListener(keycode);

        if(keycode == Input.Keys.RIGHT)
            movingRight = false;

        if(keycode == Input.Keys.LEFT)
            movingLeft = false;

        if(keycode == Input.Keys.UP)
            movingUp = false;

        if(keycode == Input.Keys.DOWN)
            movingDown = false;

        if(keycode == Input.Keys.I)
            zoomIn = false;

        if(keycode == Input.Keys.O)
            zoomOut = false;

        if(keycode == Input.Keys.D) 
            keyD = false;

        if(keycode == Input.Keys.A) 
            keyA = false;

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        super.controller.keyListener.mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
