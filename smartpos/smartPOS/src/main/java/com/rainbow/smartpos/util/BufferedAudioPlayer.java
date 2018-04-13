package com.rainbow.smartpos.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2017/11/3.
 */

public class BufferedAudioPlayer extends Thread{

    public BufferedAudioPlayer(Activity ctx)
    {
        if(ctx != null) {
//            running = true;
            this.ctx = ctx;
//            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void loadMusic(String key, int musicRawId) throws Exception {
//        System.out.println("load music: "+key);
        if (running)
            throw new Exception("Cant't load music while AudioPlayer is running");
//        if(soundPool != null) {
//            musics.put(key, soundPool.load(ctx, musicRawId,1));
//        }

        putPlayer(key, musicRawId);
    }

    @Override
    public void start()
    {
        running = true;
        super.start();
    }

    public void run()
    {
        while(running) {
            String audioName = null;
            synchronized(this) {
                if(semphores.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        running = false;
                    }
                } else {
                    audioName = semphores.get(0);
                    semphores.remove(0);
                }
            }
            if(audioName != null &&  !isSilent) {
                play(audioName);
            } /*else {
                System.out.println("Not found music: "+audioName);
            }*/
        }
        if(currentPlayer != null) {
            currentPlayer.waitForIdle();
            currentPlayer.getMediaPlayer().release();
        }
    }

    public void exit()
    {
        running = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public synchronized void playOnce(String name)
    {
//        System.out.println("play "+name);
        if(name != null) {
            semphores.add(name);
            notifyAll();
        }
    }

    public void setSilent(boolean isSilent)
    {
        this.isSilent = isSilent;
    }

    private MediaPlayerHolder initMediaPlayer(Activity context, String name, int rawId) {
        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        final MediaPlayerHolder holder = new MediaPlayerHolder();
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        /**
         * When the beep has finished playing, rewind to queue up another one.
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                System.out.println("Play complete");
                mediaPlayer.seekTo(0);
                holder.setState(1);//ready
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.release();
                holder.setState(0);//fall back to initing state
                return true;
            }
        });

        holder.setMediaPlayer(mediaPlayer);
        holder.setState(0);//initing
        holder.prepare(context, name, rawId);
        return holder;
    }

    private void play(String name) {

//        if(name == null || getPlayer(name) == null)
//            return;
//        if(currentPlayer != null)
//            currentPlayer.waitForIdle();
//        currentPlayer = getPlayer(name);

        if(currentPlayer != null)
            currentPlayer.waitForIdle();
        currentPlayer = getPlayer(name);

        if(currentPlayer != null && currentPlayer.getState() == 1) {//ready
            currentPlayer.setState(2);
            currentPlayer.getMediaPlayer().start();
        } else {
//            if(currentPlayer != null)
//                System.out.println("player "+currentPlayer.getName()+" is not ready: "+currentPlayer.getState());
        }
    }

    private MediaPlayerHolder getPlayer(String name)
    {
//        return players.get(name);

        Integer id = musics.get(name.trim());
        if(id != null) {
            currentPlayer.prepare(ctx, name, id.intValue());
            return currentPlayer;
        }
//        System.out.println("key not exist: "+name);
        return null;
    }

    private void putPlayer(String key, int rawId)
    {
//        MediaPlayerHolder player = initMediaPlayer(ctx, key, rawId);
//        if (player != null)
//            players.put(key, player);
        if(currentPlayer == null)
            currentPlayer = initMediaPlayer(ctx, key, rawId);
        musics.put(key.trim(), rawId);
    }

    private boolean running = false;
    private List<String> semphores = new ArrayList();
    private HashMap<String, Integer> musics = new HashMap<>();
    private Activity ctx;
//    private HashMap<String, MediaPlayerHolder> players = new HashMap<>();
    private MediaPlayerHolder currentPlayer;
    private boolean isSilent = false;

    private static class MediaPlayerHolder
    {
        private int state; //0: initing, 1:ready, 2:playing
        private MediaPlayer player;
        private String name=""; //key

        public MediaPlayer getMediaPlayer() {return player;}
        public void setMediaPlayer(MediaPlayer player) {this.player = player;}
        public synchronized  int getState() {return state;}

        /**
         * when return must check state again before play
         */
        public synchronized  void waitForIdle() {
            while(state == 2) //is playing
                try {
                    wait();
                } catch (InterruptedException e) {
                    state = 0;
                }
        }
        public synchronized  void setState(int state) {
            this.state = state;
            notifyAll();
        }
        public String getName() {return name;}
        public void setName(String name) {this.name = name;}
        public void prepare(Context ctx, String key, int rawId)
        {
            if(key.compareTo(getName()) == 0) return;
            player.reset();
            AssetFileDescriptor file = ctx.getResources().openRawResourceFd(rawId);
            try {
                player.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                float BEEP_VOLUME = 1.0f;
                player.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                player.prepare();
//                System.out.println("media ready");
                setName(key);
                setState(1);//ready
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("initSpecifiedSound", "what happened to init sound? you need to deal it .");
            }
        }
    }
}
