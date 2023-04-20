package com.javadabl.creativemusicplayer.Player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.javadabl.creativemusicplayer.Activites.LocalPlayerActivity;
import com.javadabl.creativemusicplayer.Activites.NetPlayerActivity;
import com.javadabl.creativemusicplayer.Models.MusicModel;
import com.javadabl.creativemusicplayer.Models.RadioStationModel;
import com.javadabl.creativemusicplayer.Util.MusicProvider;
import com.javadabl.creativemusicplayer.Util.RadioProvider;

import java.io.IOException;

public class Player {

    private int _RepeatMode = 0;
    private MediaPlayer _mediaPlayer = new MediaPlayer();
    private Context _Context;
    private Uri _uri;
    private boolean _ISPrepared = false;
    private boolean _StartWhenPrepard;
    public int _Source;
    public MusicModel _MusicModel;
    public RadioStationModel _RadioStationModel;

    public Player() {
        FinishListener();
    }

    private void FinishListener() {
        _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (_RepeatMode) {
                    case 0:
                        _StartWhenPrepard = false;
                        Prepare();
                        break;
                    case 1:
                        _StartWhenPrepard = true;
                        Prepare();
                        break;
                    case 2:
                        Foreward();
                }
            }
        });
    }

    public void Setup(Context Context, MusicModel musicModel, boolean StartWhenPrepard, int Source) {
        _StartWhenPrepard = StartWhenPrepard;
        _Source = Source;
        _MusicModel = musicModel;
        _uri = musicModel.Uri;
        _Context = Context;
        _ISPrepared = false;
        Prepare();
    }

    public void SetupRadio(Context Context, RadioStationModel radioStationModel, boolean StartWhenPrepard, int Source) {
        _StartWhenPrepard = StartWhenPrepard;
        _Source = Source;
        _RadioStationModel = radioStationModel;
        _uri = radioStationModel.Uri;
        _Context = Context;
        _ISPrepared = false;
        Prepare();
    }

    public void Prepare() {
        _mediaPlayer.reset();
        try {
            _mediaPlayer.setDataSource(_Context, _uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        _mediaPlayer.prepareAsync();
        _mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                _ISPrepared = true;
                if (_StartWhenPrepard)
                    Start();
            }
        });
    }


    public void Start() {
        if (_ISPrepared)
            _mediaPlayer.start();
    }


    public void Pause() {
        if (_ISPrepared)
            _mediaPlayer.pause();
    }

    public void Stop() {
        _mediaPlayer.stop();
        _ISPrepared = false;
        _mediaPlayer.reset();
        _StartWhenPrepard = false;
        Prepare();
    }

    public void Release() {
        _mediaPlayer.release();
        _mediaPlayer = null;
    }

    public void seekTo(int progress) {
        _mediaPlayer.seekTo(progress);
    }

    public void setVolume(float vol, float vol1) {
        _mediaPlayer.setVolume(vol, vol1);
    }

    public boolean CheckNull() {
        return _mediaPlayer == null;
    }

    public int getCurrentPosition() {
        return _mediaPlayer.getCurrentPosition();
    }

    public boolean ISPlaying() {
        return _mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return _mediaPlayer.getDuration();
    }

    public void Foreward() {
        if (_Source == MediaSource.NetSource_FromRadioList) {
            RadioStationModel radioStationModel = new RadioProvider(_Context).getNextStation(_RadioStationModel.Title);
            if (radioStationModel != null) {
                SetupRadio(_Context, radioStationModel, true, _Source);
                NetPlayerActivity._NeedRefresh = true;
            } else
                Toast.makeText(_Context, "There is no next music in the list..", Toast.LENGTH_SHORT).show();
        } else {
            MusicModel musicModel = new MusicProvider(_Context).getNextMusic(_MusicModel.ID);
            if (musicModel != null) {
                Setup(_Context, musicModel, true, _Source);
                LocalPlayerActivity._NeedRefrsh = true;
            } else
                Toast.makeText(_Context, "There is no next music in the list..", Toast.LENGTH_SHORT).show();
        }
    }

    public void Backward() {
        if (_Source == MediaSource.NetSource_FromRadioList) {
            RadioStationModel radioStationModel = new RadioProvider(_Context).getPrevStation(_RadioStationModel.Title);
            if (radioStationModel != null) {
                SetupRadio(_Context, radioStationModel, true, _Source);
                NetPlayerActivity._NeedRefresh = true;
            } else
                Toast.makeText(_Context, "There is no previus music in the list..", Toast.LENGTH_SHORT).show();
        } else {
            MusicModel musicModel = new MusicProvider(_Context).getPrevMusic(_MusicModel.ID);
            if (musicModel != null) {
                Setup(_Context, musicModel, true, _Source);
                LocalPlayerActivity._NeedRefrsh = true;
            } else
                Toast.makeText(_Context, "There is no next previus in the list..", Toast.LENGTH_SHORT).show();
        }
    }

    public int getRepeatMode() {
        return _RepeatMode;
    }

    public void setRepeatMode(int RepeatMode) {
        _RepeatMode = RepeatMode;
    }
}
