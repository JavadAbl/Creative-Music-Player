package com.javadabl.creativemusicplayer.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javadabl.creativemusicplayer.Adapters.OnMessageReadListener;
import com.javadabl.creativemusicplayer.Models.AlbumModel;
import com.javadabl.creativemusicplayer.Models.MusicModel;
import com.javadabl.creativemusicplayer.Player.MediaSource;
import com.javadabl.creativemusicplayer.R;
import com.javadabl.creativemusicplayer.Util.MusicProvider;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumList extends Fragment {
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

    public AlbumList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);

        final RecyclerView _recyclerView = view.findViewById(R.id.recycle_Albumlist);
        RecyclerView.LayoutManager _layoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        _recyclerView.addItemDecoration(itemDecoration);
        _recyclerView.setHasFixedSize(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<AlbumModel> albumModels = new MusicProvider(getContext()).getAllAlbums();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _recyclerView.setAdapter(new AlbumlistAdaptor(albumModels));
                    }
                });
            }
        }).start();
        return view;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    class AlbumlistAdaptor extends RecyclerView.Adapter<AlbumlistAdaptor.MyViewHolder> {

        private List<AlbumModel> _listalbum;
        private MusicProvider _Music = new MusicProvider(getContext());

        public AlbumlistAdaptor(List<AlbumModel> listalbum) {
            this._listalbum = listalbum;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.items_albumlist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder._textalbum.setText(_listalbum.get(position).Album);
            holder._textcount.setText(_listalbum.get(position).MusicCount + " Songs");
            if (_listalbum.get(position).AlbumCover != null)
                holder._imageview.setImageBitmap(_listalbum.get(position).AlbumCover);
            else
                holder._imageview.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_albumcover));
            holder._albumID = _listalbum.get(position).ID;
            holder._recycle.setLayoutManager(new LinearLayoutManager(getContext()));
            holder._recycle.setAdapter(null);
        }

        @Override
        public int getItemCount() {
            return _listalbum.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView _imageview;
            TextView _textalbum;
            TextView _textcount;
            RecyclerView _recycle;
            LinearLayout _layout;
            int _albumID;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                _textalbum = itemView.findViewById(R.id.text_Albumlist);
                _textcount = itemView.findViewById(R.id.text_Albumlist_count);
                _imageview = itemView.findViewById(R.id.image_Albumlist);
                _recycle = itemView.findViewById(R.id.recycle_Albumlist_item);
                _layout = itemView.findViewById(R.id.LayoutLinear_Albumlist);

                _layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_recycle.getAdapter() == null) {
                            for (int i = 0; i < _listalbum.size(); i++) {
                                if (_listalbum.get(i).ID == _albumID)
                                    _recycle.setAdapter(new AlbumItemsListAdaptor(_listalbum.get(i).ID));
                            }
                        } else _recycle.setAdapter(null);
                    }
                });
            }
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

        private class AlbumItemsListAdaptor extends RecyclerView.Adapter<AlbumItemsListAdaptor.MyViewHolder> {
            private List<MusicModel> _listMusic;

            public AlbumItemsListAdaptor(int AlbumID) {
                _listMusic = new MusicProvider(getContext()).getAllMusicbyAlbumID(AlbumID);
            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.items_albumlist_eachalbum, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.textitems.setText(_listMusic.get(position).Title);
                holder.musicID = _listMusic.get(position).ID;
            }

            @Override
            public int getItemCount() {
                return _listMusic.size();
            }

            private class MyViewHolder extends RecyclerView.ViewHolder {
                private TextView textitems;
                private int musicID;

                MyViewHolder(@NonNull View itemView) {
                    super(itemView);
                    textitems = itemView.findViewById(R.id.text_Albumlist_items);

                    textitems.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < _listMusic.size(); i++)
                                if (_listMusic.get(i).ID == musicID) {
                                    _onMessageReadListener.OnReadMessage(_listMusic.get(i), MediaSource.LocalSource_FromAlbumList);
                                    return;
                                }
                        }
                    });
                }
            }
        }
    }
}
