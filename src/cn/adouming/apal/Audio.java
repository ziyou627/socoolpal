// This string is autogenerated by ChangeAppSettings.sh, do not change spaces amount
package cn.adouming.apal;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;
import java.io.*;
import java.nio.ByteBuffer;
import android.util.Log;
import java.lang.Thread;
import org.socool.pal.R;


class AudioThread implements DemoGLSurfaceView.ICallBack {

	private Activity mParent;
	private AudioTrack mAudio;
	private byte[] mAudioBuffer;
	private float volume ;
	private float stepVolume;
	
	
	public AudioThread(Activity parent)
	{
		mParent = parent;
		mAudio = null;
		mAudioBuffer = null;
		
		nativeAudioInitJavaCallbacks();
	}
	public void IncVolume()
	{
		if(volume < AudioTrack.getMaxVolume())
		{
			volume = volume + stepVolume;
			mAudio.setStereoVolume(volume, volume);
		}
		
	}
	public void DecVolume()
	{
		if(volume > AudioTrack.getMinVolume())
		{
			volume = volume - stepVolume;
			mAudio.setStereoVolume(volume, volume);
		}
	}
	public int fillBuffer()
	{
		
		mAudio.write( mAudioBuffer, 0, mAudioBuffer.length );
		return 1;
	}
	
	public byte[] initAudio(int rate, int channels, int encoding, int bufSize)
	{
			if( mAudio == null )
			{
					channels = ( channels == 1 ) ? AudioFormat.CHANNEL_CONFIGURATION_MONO : 
													AudioFormat.CHANNEL_CONFIGURATION_STEREO;
					encoding = ( encoding == 1 ) ? AudioFormat.ENCODING_PCM_16BIT :
													AudioFormat.ENCODING_PCM_8BIT;

					if( AudioTrack.getMinBufferSize( rate, channels, encoding ) > bufSize )
						bufSize = AudioTrack.getMinBufferSize( rate, channels, encoding );

					mAudioBuffer = new byte[bufSize];

					mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, 
												rate,
												channels,
												encoding,
												bufSize,
												AudioTrack.MODE_STREAM );
					mAudio.play();
					volume = (AudioTrack.getMaxVolume() - AudioTrack.getMinVolume()) /2;
					mAudio.setStereoVolume(volume, volume);
					stepVolume = (AudioTrack.getMaxVolume() - AudioTrack.getMinVolume()) / 10;
			}
			return mAudioBuffer;
	}
	
	public int deinitAudio()
	{
		if( mAudio != null )
		{
			mAudio.stop();
			mAudio.release();
			mAudio = null;
		}
		mAudioBuffer = null;
		return 1;
	}
	
	public int initAudioThread()
	{
		// Make audio thread priority higher so audio thread won't get underrun
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		return 1;
	}
	
	private native int nativeAudioInitJavaCallbacks();
}

