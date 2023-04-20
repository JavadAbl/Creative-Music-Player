package com.javadabl.creativemusicplayer.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javadabl.creativemusicplayer.Adapters.OnMessageReadListener;
import com.javadabl.creativemusicplayer.Models.ArtistModel;
import com.javadabl.creativemusicplayer.Models.MusicModel;
import com.javadabl.creativemusicplayer.Player.MediaSource;
import com.javadabl.creativemusicplayer.R;
import com.javadabl.creativemusicplayer.Util.MusicProvider;

import java.util.List;


public class MusicList extends Fragment {

    private OnMessageReadListener _onMessageReadListener = new OnMessageReadListener() {
        @Override
        public void OnReadMessage(MusicModel MusicModel, int Source) {

        }
    };


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = ((Activity) context);
        _onMessageReadListener = ((OnMessageReadListener) activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_music_list, container, false);

        final RecyclerView _recyclerView = view.findViewById(R.id.RecycleMusiclist);
        RecyclerView.LayoutManager _layoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        _recyclerView.addItemDecoration(itemDecoration);
        _recyclerView.setHasFixedSize(true);
        _recyclerView.setHasTransientState(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<MusicModel> musicModels = new MusicProvider(getContext()).GetAllMusic();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _recyclerView.setAdapter(new MusiclistAdaptor(musicModels));
                    }
                });
            }
        }).start();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    class MusiclistAdaptor extends RecyclerView.Adapter<MusiclistAdaptor.MyViewHolder> {

        List<MusicModel> _listMusic;

        public MusiclistAdaptor(List<MusicModel> _list) {
            this._listMusic = _list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.items_musiclist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            holder._textartist.setText(_listMusic.get(position).Artist);
            holder._textname.setText(_listMusic.get(position).Title);
            holder._textCounter.setText(position + ":");
            holder.musicID = _listMusic.get(position).ID;
        }

        @Override
        public int getItemCount() {
            return _listMusic.size();
           // return 10;
        }

        private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView _textname;
            TextView _textartist;
            TextView _textCounter;
            LinearLayout _layout;
            int musicID;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                _textname = itemView.findViewById(R.id.musiclist_text1);
                _textartist = itemView.findViewById(R.id.musiclist_text2);
                _textCounter = itemView.findViewById(R.id.textview_Musiclist_Counter);
                _layout = itemView.findViewById(R.id.LayoutLinear_MusicList);
                _layout.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                for (MusicModel musicModel : _listMusic)
                    if (musicModel.ID == musicID) {
                        _onMessageReadListener.OnReadMessage(musicModel, MediaSource.LocalSource_FromMusicList);
                        return;
                    }
            }
        }
    }
}
