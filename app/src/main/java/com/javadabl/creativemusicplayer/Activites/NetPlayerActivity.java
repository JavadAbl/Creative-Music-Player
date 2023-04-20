package com.javadabl.creativemusicplayer.Activites;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.javadabl.creativemusicplayer.R;

public class NetPlayerActivity extends AppCompatActivity {

    //  private RadioStationModel _RadioStationModel = MainActivity._RadioStationModel;
    private FrameLayout _FrameLayoutBtnPlay;
    private TextView _textviewTitle, _textviewGenre;
    public static boolean _NeedRefresh = false;
    private boolean _ActivityLife = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_player);

        BindViews();
        CheckRefresh();
        SetupLayout();
    }

    private void CheckRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (_ActivityLife) {
                    try {
                        if (_NeedRefresh)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SetupLayout();
                                }
                            });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void SetupLayout() {
        _textviewTitle.setText(MainActivity._Player._RadioStationModel.Title);
        _textviewGenre.setText(MainActivity._Player._RadioStationModel.Genre);
        if (MainActivity._Player.ISPlaying())
            _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_pause));
        else
            _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_play));
    }


    private void BindViews() {
        Button stop, play, backward, foreward;
        stop = findViewById(R.id.btn_Netplaying_stop);
        play = findViewById(R.id.btn_Netplaying_playpause);
        backward = findViewById(R.id.btn_Netplaying_backward);
        foreward = findViewById(R.id.btn_Netplaying_forward);
        _textviewTitle = findViewById(R.id.txt_NetPlaying_Title);
        _FrameLayoutBtnPlay = findViewById(R.id.LayoutFrame_NetPlaying_btnPlay);
        _textviewGenre = findViewById(R.id.txt_NetPlaying_Genre);

        play.setOnClickListener(new View.OnClickListener() {
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

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _FrameLayoutBtnPlay.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_action_play));
                MainActivity._Player.Stop();
            }
        });

        foreward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity._Player.Foreward();
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity._Player.Backward();
            }
        });
    }

    @Override
    protected void onDestroy() {
        _ActivityLife = false;
        super.onDestroy();
    }
}