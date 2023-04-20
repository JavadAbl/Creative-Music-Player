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

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistList extends Fragment {

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

    public ArtistList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.recycle_Artistlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ArtistModel> artistModels = new MusicProvider(getContext()).getAllArtists();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new ArtistlistAdaptor(artistModels));
                    }
                });
            }
        }).start();
        return view;
    }

    class ArtistlistAdaptor extends RecyclerView.Adapter<ArtistlistAdaptor.MyViewHolder> {

        List<ArtistModel> _listArtist;
        MusicProvider _Music = new MusicProvider(getContext());

        public ArtistlistAdaptor(List<ArtistModel> _list) {
            this._listArtist = _list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.items_artistlist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder._textartist.setText(_listArtist.get(position).Artist);
            holder._textcount.setText(_listArtist.get(position).MusicCount + " Songs");
            holder._textnumber.setText(position + ": ");
            holder.artistID = _listArtist.get(position).ID;
            holder._recycle.setLayoutManager(new LinearLayoutManager(getContext()));
            holder._recycle.setAdapter(null);
        }

        @Override
        public int getItemCount() {
            return _listArtist.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            TextView _textartist;
            TextView _textcount;
            TextView _textnumber;
            RecyclerView _recycle;
            LinearLayout _layout;
            int artistID;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                _textartist = itemView.findViewById(R.id.text_Artistlist1);
                _textcount = itemView.findViewById(R.id.text_Artistlist2);
                _textnumber = itemView.findViewById(R.id.textview_Artistlist_Counter);
                _recycle = itemView.findViewById(R.id.recycle_Artistlist_items);
                _layout = itemView.findViewById(R.id.LayoutLinear_Artistlist);

                _layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_recycle.getAdapter() == null) {
                            for (int i = 0; i < _listArtist.size(); i++) {
                                if (_listArtist.get(i).ID == artistID)
                                    _recycle.setAdapter(new ArtistItemsListAdaptor(_listArtist.get(i).ID));
                            }
                        } else _recycle.setAdapter(null);
                    }
                });
            }
        }
/////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////

        private class ArtistItemsListAdaptor extends RecyclerView.Adapter<ArtistItemsListAdaptor.MyViewHolder> {

            private List<MusicModel> _listMusic;

            ArtistItemsListAdaptor(int ArtistID) {
                _listMusic = _Music.getAllMusicbyArtistID(ArtistID);
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.items_artistlist_eachartist, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder._textitems.setText(_listMusic.get(position).Title);
                holder.musicID = _listMusic.get(position).ID;
            }

            @Override
            public int getItemCount() {
                return _listMusic.size();
            }

            private class MyViewHolder extends RecyclerView.ViewHolder {
                TextView _textitems;
                int musicID;

                public MyViewHolder(@NonNull View itemView) {
                    super(itemView);
                    _textitems = itemView.findViewById(R.id.text_Artistlist_items);
                    _textitems.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (MusicModel musicModel : _listMusic)
                                if (musicModel.ID == musicID) {
                                    _onMessageReadListener.OnReadMessage(musicModel, MediaSource.LocalSource_FromArtistList);
                                    return;
                                }
                        }
                    });
                }
            }
        }
    }
}
