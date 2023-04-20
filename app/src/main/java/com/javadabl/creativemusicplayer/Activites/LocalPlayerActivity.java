package com.javadabl.creativemusicplayer.Activites;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.javadabl.creativemusicplayer.Models.MusicModel;
import com.javadabl.creativemusicplayer.Player.MediaSource;
import com.javadabl.creativemusicplayer.R;
import com.javadabl.creativemusicplayer.Util.MusicProvider;

public class LocalPlayerActivity extends AppCompatActivity {

    private TextView _textviewMusicTitle;
    private ImageView _imgviewMuiscImage;
    private TextView _textviewMusicArtist;
    private TextView _textviewMusicAlbum;
    private TextView _textviewMusicGenre;
    private TextView _textviewRepeat;
    private TextView _textviewDuration1;
    private TextView _textviewDuration2;
    private ImageButton _btnRepeat;
    private SeekBar _SeekBar;
    private SeekBar _VolBar;
    private FrameLayout _FrameLayoutBtnPlay;
    private boolean _AvtivityLife = true;
    public static boolean _NeedRefrsh = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        BindViews();
        SetupLayout();
        CheckRefresh();
        LoadSeekBar();
    }

    private void SetupLayout() {
        MusicModel _MusicModel = MainActivity._Player._MusicModel;
        MusicProvider music = new MusicProvider(this);
        _MusicModel.Genre = music.getGenrebyID(_MusicModel.ID);
        _MusicModel.AlbumCover = music.getArtWorkbyID(_MusicModel.ID);
        _textviewMusicTitle.setText(_MusicModel.Title);
        if (_MusicModel.AlbumCover != null)
            _imgviewMuiscImage.setImageBitmap(_MusicModel.AlbumCover);
        else
            _imgviewMuiscImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_albumcover));
        _textviewMusicArtist.setText(_MusicModel.Artist);
        _textviewMusicAlbum.setText(_MusicModel.Album);
        _textviewMusicGenre.setText(_MusicModel.Genre);
        _SeekBar.setMax(MainActivity._Player.getDuration());
        _textviewDuration2.setText(LoadTimeTexts(MainActivity._Player.getDuration()));
        if (MainActivity._Player.ISPlaying())
            _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_pause));
        else
            _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_play));
    }

    private void LoadSeekBar() {
        final Handler mainHandler = new Handler();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                final int currentPosition = msg.what;
                _SeekBar.setProgress(currentPosition);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        _textviewDuration1.setText(LoadTimeTexts(currentPosition));
                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!MainActivity._Player.CheckNull()) {
                    try {
                        Message message = new Message();
                        message.what = MainActivity._Player.getCurrentPosition();
                        handler.handleMessage(message);
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private String LoadTimeTexts(int time) {
        String timelable = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timelable = min + ":";
        if (sec < 10) timelable += "0";
        timelable += sec;

        return timelable;
    }

    private void CheckRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (_AvtivityLife) {
                    try {
                        if (_NeedRefrsh)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SetupLayout();
                                    _NeedRefrsh = false;
                                }
                            });

                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void BindViews() {
        _textviewMusicTitle = findViewById(R.id.textview_nowplaying_songtitle);
        _textviewMusicArtist = findViewById(R.id.textview_nowplaying_ArtistValue);
        _textviewMusicAlbum = findViewById(R.id.textview_nowplaying_AlbumValue);
        _textviewMusicGenre = findViewById(R.id.textview_nowplaying_GenreValue);
        _textviewRepeat = findViewById(R.id.textview_NowPlaying_Repeat);
        _textviewDuration1 = findViewById(R.id.textview_nowplaying_dur1);
        _textviewDuration2 = findViewById(R.id.textview_nowplaying_dur2);
        _btnRepeat = findViewById(R.id.btn_NowPlaying_Repeat);
        _imgviewMuiscImage = findViewById(R.id.imgview_nowplaying_albumcover);
        _SeekBar = findViewById(R.id.seekBar_nowplaying_progress);
        _VolBar = findViewById(R.id.seekbar_nowplaying_vol);
        _FrameLayoutBtnPlay = findViewById(R.id.LayoutFrame_NowPlaying_btnPlay);
        Button _btnPlayPause = findViewById(R.id.btn_nowplaying_playpause);
        Button _btnForeward = findViewById(R.id.btn_nowplaying_forward);
        Button _btnBackward = findViewById(R.id.btn_nowplaying_backward);
        Button _btnStop = findViewById(R.id.btn_nowplaying_stop);

        switch (MainActivity._Player.getRepeatMode()) {
            case MediaSource.NoRepeat:
                _textviewRepeat.setText("No Repeat");
                break;
            case MediaSource.RepeatSong:
                _textviewRepeat.setText("Repeat Song");
                break;
            case MediaSource.RepeatAll:
                _textviewRepeat.setText("Repeat All");
        }

        _btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (MainActivity._Player.getRepeatMode()) {
                    case MediaSource.NoRepeat:
                        MainActivity._Player.setRepeatMode(MediaSource.RepeatSong);
                        _textviewRepeat.setText("Repeat Song");
                        break;
                    case MediaSource.RepeatSong:
                        MainActivity._Player.setRepeatMode(MediaSource.RepeatAll);
                        _textviewRepeat.setText("Repeat All");
                        break;
                    case MediaSource.RepeatAll:
                        MainActivity._Player.setRepeatMode(MediaSource.NoRepeat);
                        _textviewRepeat.setText("No Repeat");
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(getString(R.string.PrefKey_Repeat), MainActivity._Player.getRepeatMode()).apply();
                    }
                }).start();
            }
        });

        _btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity._Player.ISPlaying()) {
                    MainActivity._Player.Pause();
                    _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_play));
                } else {
                    MainActivity._Player.Start();
                    _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_pause));
                }
            }
        });

        _btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_play));
                MainActivity._Player.Stop();
            }
        });

        _btnForeward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity._Player.Foreward();
            }
        });
        _btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity._Player.Backward();
            }
        });

        _SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MainActivity._Player.seekTo(progress);
                    _SeekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        _VolBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = progress / 100f;
                MainActivity._Player.setVolume(vol, vol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        _AvtivityLife = false;
        super.onDestroy();
    }
}