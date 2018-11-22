package com.yuwei.dh8900card.uhf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.magicrf.uhfreaderlib.reader.UhfReader;

public class ScreenStateReceiver extends BroadcastReceiver {

	private UhfReader reader ;
	@Override
	public void onReceive(Context context, Intent intent) {
		reader = UhfReader.getInstance();
		//∆¡¡¡
//		if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
//			reader.powerOn();
//			Log.i("ScreenStateReceiver", "screen on");
//			
//		}//∆¡√
//		else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
//			reader.powerOff();
//			Log.i("ScreenStateReceiver", "screen off");
//		}

	}

}
