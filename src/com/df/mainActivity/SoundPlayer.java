package com.df.mainActivity;
  
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundPlayer {
 
    private static MediaPlayer music;
    private static SoundPool soundPool;
     
    private static boolean musicSt = true; //音乐开关
    private static boolean soundSt = true; //音效开关
    private static boolean isPause = false; //音效开关
    private static Context context;
     
    private static final int musicId = R.raw.background;
    private static int soundId =0;

    public static void init(Context c)
    {
        context = c;
        initMusic();     
        initSound();
    }
     
    //初始化播放器
    private static void initMusic() {
        music = MediaPlayer.create(context,musicId);
        music.setLooping(true);
    }
    @SuppressLint("UseSparseArrays")
	private static void initSound()
    {
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundId = soundPool.load(context, R.raw.chess, 0);
    }

    public static void playSound()
    {
        if(soundSt == false)
            return;
        else
        	//播放音频，第二个参数为左声道音量;
    		//第三个参数为右声道音量;第四个参数为优先级；
    		//第五个参数为循环次数:0不循环，-1循环;
    		//第六个参数为速率，速率最低0.5最高为2，1代表正常速度  
            soundPool.play(soundId, (float)0.5, (float)0.5, 0, 0, 1);
    }
    
    //暂停音乐
    public static void pauseMusic()
    {
        if(isPause)
            music.pause();
        else
        	music.start();
        	
    }
 
    //播放音乐
    public static void startMusic()
    {
        if(musicSt)
            music.start();
    }
     
    //设置开关状态
    public static void setisPause(boolean isPause) {
        SoundPlayer.isPause = isPause;
    }
    public static void setMusicSt(boolean musicSt) {
        SoundPlayer.musicSt = musicSt;
        if(musicSt)
            music.start();
        else
            music.stop();
    }
    public static void setSoundSt(boolean soundSt) {
        SoundPlayer.soundSt = soundSt;
    }
 
    //获得开关状态
    public static boolean isSoundSt() {
        return soundSt;
    }
    public static boolean isMusicSt() {
        return musicSt;
    }
    public static boolean isPause() {
        return isPause;
    }
}
