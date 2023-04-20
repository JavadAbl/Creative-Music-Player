package com.javadabl.creativemusicplayer.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.javadabl.creativemusicplayer.Adapters.OnMessageReadListener;
import com.javadabl.creativemusicplayer.Models.ArtistModel;
import com.javadabl.creativemusicplayer.Models.MusicModel;
import com.javadabl.creativemusicplayer.Models.RadioModel;
import com.javadabl.creativemusicplayer.Models.RadioStationModel;
import com.javadabl.creativemusicplayer.Player.MediaSource;
import com.javadabl.creativemusicplayer.R;
import com.javadabl.creativemusicplayer.Util.MusicProvider;
import com.javadabl.creativemusicplayer.Util.RadioProvider;

import java.util.List;


public class RadioList extends Fragment {

    private RadioMessageReadListener _onMessageReadListener;

    public interface RadioMessageReadListener {

        void OnRadioReadMessage(RadioStationModel radioStationModel, int Source);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = ((Activity) context);
        _onMessageReadListener = ((RadioMessageReadListener) activity);
    }

    public RadioList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radio_list, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.RecycleRadiolist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<RadioModel> radioModels = new RadioProvider(getContext()).getAllRadioGenre();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new RadioListAdapter(radioModels));
                    }
                });
            }
        }).start();
        return view;
    }

    private class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.MyViewHolder> {
        private List<RadioModel> _genreList;

        private RadioListAdapter(List<RadioModel> _genreList) {
            this._genreList = _genreList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_radiolist, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.genreTilte.setText(_genreList.get(position).Genre);
            holder.genre = _genreList.get(position).Genre;
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            holder.recyclerView.setAdapter(null);
        }

        @Override
        public int getItemCount() {
            return _genreList.size();
        }


        private class MyViewHolder extends RecyclerView.ViewHolder {
            TextView genreTilte;
            RecyclerView recyclerView;
            String genre;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                genreTilte = itemView.findViewById(R.id.textview_Radiolist_GenreTitle);
                recyclerView = itemView.findViewById(R.id.recycle_Radiolist_items);

                genreTilte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recyclerView.getAdapter() == null)
                            for (RadioModel radioModel : _genreList) {
                                if (radioModel.Genre.contains(genre)) {
                                    recyclerView.setAdapter(new RadioListItemsAdapter(radioModel));
                                    return;
                                }
                            }
                        else recyclerView.setAdapter(null);
                    }
                });
            }
        }
////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////

        private class RadioListItemsAdapter extends RecyclerView.Adapter<RadioListItemsAdapter.MyViewHolder> {

            private RadioModel _radioModel;

            private RadioListItemsAdapter(RadioModel radioModel) {
                _radioModel = radioModel;
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_radiolist_eachgenre, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.stationName.setText(_radioModel.Titles.get(position));
                holder.title = _radioModel.Titles.get(position);
                holder.genre = _radioModel.Genre;
            }

            @Override
            public int getItemCount() {
                return _radioModel.Titles.size();
            }


            private class MyViewHolder extends RecyclerView.ViewHolder {
                TextView stationName;
                String title;
                String genre;

                private MyViewHolder(@NonNull View itemView) {
                    super(itemView);
                    stationName = itemView.findViewById(R.id.textview_Radiolist_StationName);

                    stationName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RadioStationModel radioStationModel = new RadioProvider(getContext()).getRadioStation(title, genre);
                            _onMessageReadListener.OnRadioReadMessage(radioStationModel, MediaSource.NetSource_FromRadioList);
                        }
                    });
                }
            }
        }
    }
}
