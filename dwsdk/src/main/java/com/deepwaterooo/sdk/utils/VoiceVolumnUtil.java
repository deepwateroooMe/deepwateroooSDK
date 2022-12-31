package com.deepwaterooo.sdk.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

/**
 * 获取设置系统（音乐）音量的封装类
 */
public class VoiceVolumnUtil {
    private final String TAG = "VoiceVolumeUtil";

    private static AudioManager _AudioManager;
    private static Context _Context;

    private static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    // 音量广播监听
    private static MyVolumeReceiver mVolumeReceiver;
    // 音量变化监听触发事件
    private VoiceVolumnChangedIntereface _VoiceVolumnChangedIntereface; // <<<<<<<<<<<<<<<<<<<< 

    // 初始化
    public void VolumeCallbackInit(VoiceVolumnChangedIntereface voiceVolumeChangedIntereface){
        Log.d(TAG, "VolumeCallbackInit() ");
        _Context = getActivity(); // <<<<<<<<<<<<<<<<<<<< 
        _AudioManager =(AudioManager) _Context.getSystemService(Context.AUDIO_SERVICE);

// 用且仅用 这个接口,来实现对游戏端的音量变化广播监听
// 它工作，it works!　但是感觉不够好        
// 这个接口的桥接设计方式,感觉不够好,与我现系统安卓SDK与Unity交互的相互调用设计不合
// 这里最好是不再用接口的方式[unity中是可以实现实例化一个这样的接口,但是不方便维护源码,会影响将来游戏项目的整合移植],改用什么呢?        
        _VoiceVolumnChangedIntereface = voiceVolumeChangedIntereface; 
    }

    // 通话音量
    public int GetCallVoiceMax(){
        return  _AudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) ;
    }
    // 通话音量
    public int GetCallVoiceMin(){
        // AudioManager.getStreamMinVolume要求 API 28，要求太高，先注掉，返回 0
        // return  _AudioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL) ;
        return 0;
    }
    // 通话音量
    public int GetCallVoiceCurrentValue(){
        return  _AudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) ;
    }

    // 系统音量
    public int GetSystemVoiceMax(){
        return  _AudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) ;
    }
    // 系统音量
    public int GetSystemVoiceMin(){
        // AudioManager.getStreamMinVolume要求 API 28，要求太高，先注掉，返回 0
        // return  _AudioManager.getStreamMinVolume(AudioManager.STREAM_SYSTEM) ;
        return 0;
    }
    // 系统音量
    public int GetSystemVoiceCurrentValue(){
        return  _AudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) ;
    }
    // 铃声音量
    public int GetRingVoiceMax(){
        return  _AudioManager.getStreamMaxVolume(AudioManager.STREAM_RING) ;
    }
    // 铃声音量
    public int GetRingVoiceMin(){
        // AudioManager.getStreamMinVolume要求 API 28，要求太高，先注掉，返回 0
        // return  _AudioManager.getStreamMinVolume(AudioManager.STREAM_RING) ;
        return 0;
    }
    // 铃声音量
    public int GetRingVoiceCurrentValue(){
        return  _AudioManager.getStreamVolume(AudioManager.STREAM_RING) ;
    }
    // 音乐音量
    public int GetMusicVoiceMax(){
        return  _AudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ;
    }
    // 音乐音量
    public int GetMusicVoiceMin(){
        // AudioManager.getStreamMinVolume要求 API 28，要求太高，先注掉，返回 0
        // return  _AudioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) ;
        return  0;
    }
    // 音乐音量
    public int GetMusicVoiceCurrentValue(){
        return  _AudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;
    }
    // 增加音乐音量
    public void AddMusicVoiceVolumn(int value){
        int addValue = (GetMusicVoiceCurrentValue() + value) ;
        // 防止音量值越界
        addValue = addValue > GetMusicVoiceMax() ? GetMusicVoiceMax():addValue;
        _AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,addValue,AudioManager.FLAG_PLAY_SOUND);
    }
    // 减少音乐音量
    // @param value
    public void ReduceMusicVoiceVolumn(int value){
        int reduceValue = (GetMusicVoiceCurrentValue() - value) ;
        // 防止音量值越界
        reduceValue = reduceValue < GetMusicVoiceMin() ?GetMusicVoiceMin():reduceValue;
        _AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,reduceValue,AudioManager.FLAG_PLAY_SOUND);
    }
    // 设置音乐音量大小
    // @param value
    public void SetMusicVoiceVolumn(int value){
        // 防止越界
        if(value<GetMusicVoiceMin())value = GetMusicVoiceMin();
        if(value>GetMusicVoiceMax())value = GetMusicVoiceMax();
        _AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,value,AudioManager.FLAG_PLAY_SOUND);
    }

    // 注册广播监听
    public void registerVolumeReceiver() {
        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        _Context.registerReceiver(mVolumeReceiver, filter);
    }
    // 取消注册广播监听
    public static void unregisterVolumeReceiver() {
        if (mVolumeReceiver != null) _Context.unregisterReceiver(mVolumeReceiver);
    }
    // 音量变化广播类
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isReceiveVolumeChange(intent) == true) {
                int currVolume = _AudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                // Toast.makeText(context, currVolume + " ", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onReceive:isReceiveVolumeChange currVolume "+currVolume);
                if (_VoiceVolumnChangedIntereface != null)
                    _VoiceVolumnChangedIntereface.VoiceVolumnChanged(currVolume);
            }
        }
    }
    // 判断是否是音乐音量变化（音量键改变的音量）
    private boolean isReceiveVolumeChange(Intent intent) {
        return intent.getAction() != null
            && intent.getAction().equals(ACTION_VOLUME_CHANGED)
            && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC;
    }

    // 设置一个 Activity 参数
    private Activity _unityActivity;
    // 通过反射获取 Unity 的 Activity 的上下文
    Activity getActivity() { // 上面初始化的时候,会需要这个上下文 
        if (_unityActivity == null) {
            try {
                Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
                Activity activity = (Activity) classtype.getDeclaredField("currentActivity").get(classtype);
                _unityActivity = activity;
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            } catch (IllegalAccessException e){
                e.printStackTrace();
            } catch (NoSuchFieldException e){
                e.printStackTrace();
            }
        }
        return _unityActivity;
    }
}