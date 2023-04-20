package com.javadabl.creativemusicplayer.Util;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import com.javadabl.creativemusicplayer.Models.RadioModel;
import com.javadabl.creativemusicplayer.Models.RadioStationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RadioProvider {
    private Context _Context;

    public RadioProvider(Context _Context) {
        this._Context = _Context;
    }

    public List<RadioModel> getAllRadioGenre() {

        List<RadioModel> listRadio = new ArrayList<>();
        RadioModel radioModel;
        HashSet<String> tempList = new HashSet<>();

        AssetManager assetManager = _Context.getAssets();
        InputStream inputStream;
        try {

            inputStream = assetManager.open("stations.json");
            byte[] bytes;
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            String json = new String(bytes);
            JSONObject reader;
            reader = new JSONObject(json);

            Iterator<String> iterator = reader.keys();
            while (iterator.hasNext()) {
                JSONObject tempObj = reader.getJSONObject(iterator.next());
                JSONArray tempArray = tempObj.getJSONArray("tags");
                for (int i = 0; i < tempArray.length(); i++)
                    tempList.add(tempArray.getString(i));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        List<String> genreList = new ArrayList<>(tempList);
        Collections.sort(genreList);

        for (String s : genreList) {
            radioModel = new RadioModel();
            radioModel.Titles = new ArrayList<>();
            radioModel.Genre = s;
            radioModel.Titles = getAllStationsByGenre(s);
            listRadio.add(radioModel);
        }
        return listRadio;
    }
//////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    private List<String> getAllStationsByGenre(String genre) {
        List<String> StationList = new ArrayList<>();
        AssetManager assetManager = _Context.getAssets();
        InputStream inputStream;
        try {

            inputStream = assetManager.open("stations.json");
            byte[] bytes;
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String json = new String(bytes);
            JSONObject reader;
            reader = new JSONObject(json);

            Iterator<String> iterator = reader.keys();
            while (iterator.hasNext()) {
                String stationName = iterator.next();
                JSONObject tempObj = reader.getJSONObject(stationName);
                if (tempObj.getString("tags").contains(genre))
                    StationList.add(stationName);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        Collections.sort(StationList);
        return StationList;
    }

    //////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    private Uri getHrefByName(String StationName) {
        String HREF = null;
        AssetManager assetManager = _Context.getAssets();
        InputStream inputStream;
        try {

            inputStream = assetManager.open("stations.json");
            byte[] bytes;
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String json = new String(bytes);
            JSONObject reader;
            reader = new JSONObject(json);

            JSONObject tempObj = reader.getJSONObject(StationName);
            HREF = tempObj.getString("href");

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return Uri.parse(HREF);
    }
////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public RadioStationModel getNextStation(String StationName) {
        RadioStationModel radioStationModel = new RadioStationModel();
        AssetManager assetManager = _Context.getAssets();
        InputStream inputStream;
        try {

            inputStream = assetManager.open("stations.json");
            byte[] bytes;
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String json = new String(bytes);
            JSONObject reader;
            reader = new JSONObject(json);


            JSONArray array = reader.names();
            assert array != null;
            for (int i = 0; i < array.length(); i++) {
                if (array.optString(i).contains(StationName)) {
                    if (i + 1 < array.length()) {
                        JSONObject tempObj = reader.getJSONObject(array.getString(i + 1));
                        radioStationModel.Title = array.getString(i + 1);
                        radioStationModel.Genre = tempObj.getString("tags");
                        radioStationModel.Uri = Uri.parse(tempObj.getString("href"));
                        return radioStationModel;
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public RadioStationModel getPrevStation(String StationName) {
        RadioStationModel radioStationModel = new RadioStationModel();
        AssetManager assetManager = _Context.getAssets();
        InputStream inputStream;
        try {

            inputStream = assetManager.open("stations.json");
            byte[] bytes;
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String json = new String(bytes);
            JSONObject reader;
            reader = new JSONObject(json);

            JSONArray array = reader.names();
            assert array != null;
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).contains(StationName)) {
                    if (i - 1 > -1) {
                        JSONObject tempObj = reader.getJSONObject(array.getString(i - 1));
                        radioStationModel.Title = array.getString(i - 1);
                        radioStationModel.Genre = tempObj.getString("tags");
                        radioStationModel.Uri = Uri.parse(tempObj.getString("href"));
                        return radioStationModel;
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public RadioStationModel getRadioStation(String Title, String Genre) {
        RadioStationModel radioStationModel = new RadioStationModel();
        radioStationModel.Uri = getHrefByName(Title);
        radioStationModel.Title = Title;
        radioStationModel.Genre = Genre;
        return radioStationModel;
    }
}
