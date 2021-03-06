package dev.teamcyan.dungeoncrafter.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import java.util.ArrayList;

/**
 * Audio Manager Class 
 */
public class AudioManager {
    public Music menuSound;
    public Sound breakStone;
    public Sound tick;
    public Sound alert;
    public Sound jump;
    public Sound warp;
    public ArrayList<Music> ambientMusic = new ArrayList<>();
    public Music ambients;
    long menuSoundID;
    public int curSong;
    boolean tickPlaying = false;
    long warpStatus = -1;
    ArrayList<Long> activeAudio = new ArrayList<>();
    ArrayList<String> playingAudio = new ArrayList<>();

    /**
     * Constructor for Audio Manager
     */
    public AudioManager (){
        this.menuSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound1.mp3"));
        this.breakStone = Gdx.audio.newSound(Gdx.files.internal("sounds/harddig.ogg"));
        this.tick = Gdx.audio.newSound(Gdx.files.internal("sounds/tick.wav"));
        this.ambients = Gdx.audio.newMusic(Gdx.files.internal("sounds/ambients.ogg"));
        this.alert = Gdx.audio.newSound(Gdx.files.internal("sounds/notify.ogg"));
        this.jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump2.wav"));
        this.warp = Gdx.audio.newSound(Gdx.files.internal("sounds/antigrav.wav"));
        // Fix volume levels
        this.menuSound.setVolume((float)0.6);
    }

    /**
     * StartAudio starts to play a specific sound 
     * @param toPlay
     * @return
     */
    public long startAudio(Sound toPlay){
        if(playingAudio.contains(toPlay.toString())){
            return 0;
        }
        long soundID = toPlay.play();
        activeAudio.add(soundID);
        playingAudio.add(toPlay.toString());
        return soundID;
    }

    /**
     * startMusicStr starts to play a specific sound based on title 
     * @param toPlay
     */
    public void startMusicStr(String toPlay){
        if(toPlay == "tick" & tickPlaying == false) {
            tick.play();
            tickPlaying = true;
        }
        if(toPlay == "notify"){
            alert.play();
        }
        if(toPlay == "jump"){
            jump.play();
        }
    }

    /**
     * startMusicStr starts to play a specific sound based on title and sets a maxVol to implement a 
     * fade-in
     * @param toPlay
     * @param maxVol
     */
    public void startMusic(Music toPlay, int maxVol){
        if(!toPlay.isPlaying()) {
            toPlay.setVolume(0);
            toPlay.play();
            toPlay.setLooping(true);
            fadeMusicIn(toPlay, maxVol);
        }
    }

    /**
     * Method to Stop a specific Music
     * @param toStop
     */
    public void stopMusic(Music toStop){
        toStop.stop();
    }

    /**
     * Test Logger
     */
    public void testPrint(){
        System.out.println("Testing function is running");
    }

    /**
     * Plays antigravitational sound
     * @param status
     */
    public void antiGrav(String status) {
        if(status == "play" & warpStatus == -1){
            warpStatus = this.warp.loop();
        }
        if(status == "stop" & warpStatus != -1) {
            this.warp.stop(warpStatus);
            warpStatus = -1;
        }
    }

    /**
     * Stops playing a specific audio
     * @param toStop
     * @param audioID
     */
    public void stopAudio(Sound toStop, long audioID){
        toStop.stop(audioID);
        activeAudio.remove(activeAudio.indexOf(audioID));
    }

    /**
     * Fade out music
     * @param toFade
     */
    public void fadeMusicOut(final Music toFade) {
        Timer timer=new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                float curVolume = toFade.getVolume();
                toFade.setVolume(curVolume - (float)0.01);
            }
        }, 0, (float)0.01, 100);
    }

    /**
     * Method to fade music in
     * @param toFade
     * @param maxVol
     */
    public void fadeMusicIn(final Music toFade, int maxVol) {
        Timer timer=new Timer();
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                float curVolume = toFade.getVolume();
                toFade.setVolume(curVolume + (float)0.01);
            }
        }, 0, (float)0.01, maxVol);
    }


    /**
     * Method to play break block sound
     **/
    public void breakBlock(String blockString){

        this.breakStone.stop();
        if(blockString.contains("brick")) {
            //this.breakStone.play();
            //System.out.println("brick broken");
        }
        if(blockString.contains("clay")){
            //this.breakStone.play();
            //System.out.println("clay broken");
        }

        if(blockString.contains("dirt")){
            breakStone.play();
            //System.out.println("dirt broken");

        }
        if(blockString.contains("gravel")) {
            this.breakStone.play();
            //System.out.println("gravel broken");
        }

        if(blockString.contains("stone")) {
            //this.breakStone.play();
            //System.out.println("stone broken");
        }
    }
}
