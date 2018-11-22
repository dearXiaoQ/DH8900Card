package com.yuwei.dh8900card.uhf;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.yuwei.dh8900card.App;
import com.yuwei.dh8900card.R;

import java.util.Map;

public class Util {


	public static SoundPool sp ;
	public static Map<Integer, Integer> suondMap;
	static int soundId = 0;
	static AudioAttributes abs;
	static SoundPool mSoundPoll;
	static int streamId = 0;
	//初始化声音池
	public static void initSoundPool(Context context){
		abs = new AudioAttributes.Builder()
				.setUsage(AudioAttributes.USAGE_MEDIA)
				.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
				.build();

		mSoundPoll =  new SoundPool.Builder()
				.setMaxStreams(100)   //设置允许同时播放的流的最大值
				.setAudioAttributes(abs)   //完全可以设置为null
				.build();

		soundId = mSoundPoll.load(App.AppContext, R.raw.msg, 1);
	}

	//播放声音池声音
	public static  void play(){
		try {
			if (streamId != 0)
				mSoundPoll.stop(streamId);

			if (soundId == 0) {
				Log.i("soundId", "soundId = " + soundId);
				soundId = mSoundPoll.load(App.AppContext, R.raw.msg, 1);
				Thread.sleep(50);
			} else {
				Log.i("soundId", "soundId = " + soundId);
			}

			if (mSoundPoll != null)
				streamId = mSoundPoll.play(soundId, 1, 1, 1, 0, 1);
		} catch (Exception e) {e.printStackTrace();
		}
	}


	public static void destorySound(){
		if(mSoundPoll != null)
			mSoundPoll.release();
	}



}
