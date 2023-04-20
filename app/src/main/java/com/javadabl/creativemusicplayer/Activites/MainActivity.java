package com.javadabl.creativemusicplayer.Activites;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.javadabl.creativemusicplayer.Adapters.MyPagerAdaptor;
import com.javadabl.creativemusicplayer.Adapters.OnMessageReadListener;
import com.javadabl.creativemusicplayer.Fragments.AlbumList;
import com.javadabl.creativemusicplayer.Fragments.ArtistList;
import com.javadabl.creativemusicplayer.Fragments.MusicList;
import com.javadabl.creativemusicplayer.Fragments.RadioList;
import com.javadabl.creativemusicplayer.Models.MusicModel;
import com.javadabl.creativemusicplayer.Models.RadioStationModel;
import com.javadabl.creativemusicplayer.Player.MediaSource;
import com.javadabl.creativemusicplayer.Player.Player;
import com.javadabl.creativemusicplayer.R;
import com.javadabl.creativemusicplayer.Util.MusicProvider;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMessageReadListener, RadioList.RadioMessageReadListener {
    private Toolbar _toolbar;
    private TabLayout _tablayout;
    private ViewPager _pager;
    private Button _btnNowPlaying;
    public int _InitializeDSongID;
    public static Player _Player = new Player();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViews();
        setSupportActionBar(_toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.LayoutLinear_MainActivity_NowplayingContainer, new MusicList()).commit();

        String languageToLoad = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
            _Player.setRepeatMode(sharedPreferences.getInt(getString(R.string.PrefKey_Repeat), 0));
            _InitializeDSongID = sharedPreferences.getInt(getString(R.string.PrefKey_SongID), -1);
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (!checkIfAlreadyhavePermission())
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    else InitializePlayer();
                else
                    InitializePlayer();
            });
        }).start();


        MyPagerAdaptor myPagerAdaptor = new MyPagerAdaptor(getSupportFragmentManager(), 1);

        MusicList musicList = new MusicList();
        myPagerAdaptor.AddFragment(musicList, "All Songs");

        AlbumList albumList = new AlbumList();
        myPagerAdaptor.AddFragment(albumList, "Albums");

        ArtistList artistList = new ArtistList();
        myPagerAdaptor.AddFragment(artistList, "Artists");

        RadioList radioList = new RadioList();
        myPagerAdaptor.AddFragment(radioList, "Radio");
        _pager.setAdapter(myPagerAdaptor);
        _tablayout.setupWithViewPager(_pager);
        _tablayout.getTabAt(0).setIcon(R.mipmap.ic_musiclist);
        _tablayout.getTabAt(1).setIcon(R.mipmap.ic_albumlist);
        _tablayout.getTabAt(2).setIcon(R.mipmap.ic_artistlist);
        _tablayout.getTabAt(3).setIcon(R.mipmap.ic_radiolist);

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    InitializePlayer();
                } else {
                    Toast.makeText(this, "Application need this permission for loading songs..", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void InitializePlayer() {
        MusicModel musicModel;
        if (_InitializeDSongID == -1)
            musicModel = new MusicProvider(this).getFirstMusic();
        else musicModel = new MusicProvider(this).getMusic(_InitializeDSongID);

        if (musicModel != null) {
            _Player.Setup(this, musicModel, false, MediaSource.LocalSource_FromMusicList);
            _btnNowPlaying.setText("Now Playing: " + musicModel.Title);
        } else {
            Toast.makeText(this, "There are no music in the device..", Toast.LENGTH_LONG).show();
            _btnNowPlaying.setEnabled(false);
        }
    }

    public void NowPlaying_Click(View view) {
        if (_Player._Source == MediaSource.NetSource_FromRadioList) {
            Intent intent = new Intent(this, NetPlayerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LocalPlayerActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void OnReadMessage(MusicModel MusicModel, int Source) {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.PrefKey_SongID), _Player._MusicModel.ID).apply();
        }).start();

        _Player.Setup(this, MusicModel, true, Source);
        _btnNowPlaying.setText("Now Playing: " + MusicModel.Title);
        Intent intent = new Intent(this, LocalPlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void OnRadioReadMessage(RadioStationModel radioStationModel, int Source) {
        _Player.SetupRadio(this, radioStationModel, true, Source);
        _btnNowPlaying.setText("Radio Playing");
        Intent intent = new Intent(this, NetPlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_URLOpen:
                URLDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void URLDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText text = new EditText(MainActivity.this);
        text.setHint("Enter URL...");
        text.setHintTextColor(Color.GRAY);
        builder
                .setTitle("Open URL")
                .setMessage("Enter the URL you want to open")
                .setView(text)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //_URL = text.getText().toString();
                        //MadeNowPlaying();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void BindViews() {
        _toolbar = findViewById(R.id.toolbar);
        _tablayout = findViewById(R.id.tablayout_main);
        _pager = findViewById(R.id.pager_main);
        _btnNowPlaying = findViewById(R.id.btn_MainActivity_NowPlaying);
    }


    @Override
    protected void onDestroy() {
        _Player.Release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
