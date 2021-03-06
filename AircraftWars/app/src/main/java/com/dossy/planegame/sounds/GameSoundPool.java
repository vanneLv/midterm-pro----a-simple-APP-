package com.dossy.planegame.sounds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.dossy.planegame.MainActivity;
import com.dossy.planegame.myplane.R;

import java.util.HashMap;

/**
 * 游戏音乐
 */
public class GameSoundPool {
    private MainActivity mainActivity;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> map;

    @SuppressLint("UseSparseArrays")
    public GameSoundPool(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        map = new HashMap<Integer, Integer>();
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        //初始化对象，第一个参数是允许有多少个声音流同时播放,第2个参数是声音类型,第三个参数是声音的品质
    }

    /**
     * 初始化游戏音乐
     */
    public void initGameSound() {
        map.put(1, soundPool.load(mainActivity, R.raw.shoot, 1));
        map.put(2, soundPool.load(mainActivity, R.raw.explosion, 1));
        map.put(3, soundPool.load(mainActivity, R.raw.explosion2, 1));
        map.put(4, soundPool.load(mainActivity, R.raw.explosion3, 1));
        map.put(5, soundPool.load(mainActivity, R.raw.bigexplosion, 1));
        map.put(6, soundPool.load(mainActivity, R.raw.get_goods, 1));
        map.put(7, soundPool.load(mainActivity, R.raw.button, 1));
        //map.put(8, soundPool.load(mainActivity, R.raw.game, 1));
        //map.put(9, soundPool.load(mainActivity, R.raw.game2, 1));
    }


    public void playSound(int sound, int loop) {
        AudioManager am = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
        float volume = 0.4f;
        soundPool.play(map.get(sound), volume, volume, 1, loop, 1.0f);
    }
}
