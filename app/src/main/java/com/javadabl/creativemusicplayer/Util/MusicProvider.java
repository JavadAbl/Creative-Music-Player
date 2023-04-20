package com.javadabl.creativemusicplayer.Util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.javadabl.creativemusicplayer.Models.AlbumModel;
import com.javadabl.creativemusicplayer.Models.ArtistModel;
import com.javadabl.creativemusicplayer.Models.MusicModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
@SuppressLint("Range")
public class MusicProvider {
    private Context _context;
    private final Uri _externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final String _ISMUSIC = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    private final String[] _AllProj = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION
    };
    private final String _Title = MediaStore.Audio.Media.TITLE;
    private final String _Artist = MediaStore.Audio.Media.ARTIST;
    private final String _Album = MediaStore.Audio.Media.ALBUM;
    private final String _ID = MediaStore.Audio.Media._ID;
    private final String _Duration = MediaStore.Audio.Media.DURATION;

    public MusicProvider(Context context) {
        this._context = context;
    }

    public List<MusicModel> GetAllMusic() {
        List<MusicModel> listMusic = new ArrayList<>();

        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj, _ISMUSIC, null, MediaStore.Audio.Media.TITLE);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    listMusic.add(GetMusicModel(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        /*
        Arrays.sort(_musiclist, new Comparator<String[]>() {
            @Override
            public int compare(String[] first, String[] second) {
                // compare the first element
                int comparedTo = first[0].compareTo(second[0]);
                // if the first element is same (result is 0), compare the second element
                if (comparedTo == 0) return first[1].compareTo(second[1]);
                else return comparedTo;
            }
        }); */
        return listMusic;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<AlbumModel> getAllAlbums() {

        List<AlbumModel> albumModelList = new ArrayList<>();
        AlbumModel albumModel;

        ContentResolver resolver = _context.getContentResolver();
        String[] proj = {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.NUMBER_OF_SONGS};
        Cursor cursor = _context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
                , proj, null, null, MediaStore.Audio.Albums.ALBUM);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                    albumModel = new AlbumModel();
                    albumModel.ID = id;
                    albumModel.Album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                    albumModel.MusicCount = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

                    final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, id);
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    try {
                        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(resolver.openFileDescriptor(uri, "r").getFileDescriptor(), null, options);
                        albumModel.AlbumCover = bitmap;
                    } catch (FileNotFoundException e) {
                        albumModel.AlbumCover = null;
                        e.printStackTrace();
                    }
                    albumModelList.add(albumModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return albumModelList;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<ArtistModel> getAllArtists() {
        List<ArtistModel> listArtist = new ArrayList<>();
        ArtistModel artistModel;
        String[] proj = {MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS, MediaStore.Audio.Artists._ID};
        Cursor cursor = _context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, proj,
                null, null, MediaStore.Audio.Artists.ARTIST);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    artistModel = new ArtistModel();
                    artistModel.Artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                    artistModel.MusicCount = cursor.getShort(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                    artistModel.ID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                    listArtist.add(artistModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return listArtist;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getAlbumNamebyID(int id) {
        String albumName = null;
        String[] proj = {MediaStore.Audio.Media.ALBUM};
        Cursor cursor = _context.getContentResolver().query(_externalUri, proj,
                _ISMUSIC + " and " + MediaStore.Audio.Media._ID + "==" + id, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            cursor.close();
        }
        return albumName;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<MusicModel> getAllMusicbyAlbumID(int AlbumID) {
        List<MusicModel> listmusic = new ArrayList<>();

        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj, _ISMUSIC + " and "
                + MediaStore.Audio.Media.ALBUM_ID + " ==" + AlbumID, null, MediaStore.Audio.Media.TITLE);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                listmusic.add(GetMusicModel(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listmusic;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<MusicModel> getAllMusicbyArtistID(int ArtistID) {
        List<MusicModel> listMusic = new ArrayList<>();

        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj, _ISMUSIC + " and " +
                MediaStore.Audio.Media.ARTIST_ID + " ==" + ArtistID, null, MediaStore.Audio.Media.TITLE);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listMusic.add(GetMusicModel(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return listMusic;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

/*
    public void test() {
        ContentResolver resolver = _context.getContentResolver();
        Cursor cursor = _context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.ALBUM + "=?", new String[]{"World of Warcraft Wrath of the Lich King Soundtrack"}, null);
        cursor.moveToFirst();
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        BitmapFactory.Options options = new BitmapFactory.Options();
        MediaMetadataRetriever meta = new MediaMetadataRetriever();
        try {
            meta.setDataSource(resolver.openFileDescriptor(uri, "r").getFileDescriptor());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = meta.getEmbeddedPicture();
        Bitmap ss = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        int s = 2;
    }
*/

    public int getMusicIDbyName(String MusicName) {
        int MusicID = -1;
        String[] proj = {MediaStore.Audio.Media._ID};
        Cursor cursor = _context.getContentResolver().query(_externalUri, proj, _ISMUSIC
                + " and " + MediaStore.Audio.Media.TITLE + "=?", new String[]{MusicName}, null);
        if (cursor != null && cursor.moveToFirst()) {
            MusicID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            cursor.close();
        }
        return MusicID;
    }

    public List<Integer> getAllMusicID(int SortOrder) {
        List<Integer> ListID = new ArrayList<>();
        String[] proj = {MediaStore.Audio.Media._ID};
        String sort;
        switch (SortOrder) {
            case 0:
                sort = MediaStore.Audio.Media.TITLE;
                break;
            case 1:
                sort = MediaStore.Audio.Media.ALBUM;
                break;
            case 2:
                sort = MediaStore.Audio.Media.ARTIST;
                break;
            default:
                sort = MediaStore.Audio.Media.TITLE;
        }
        Cursor cursor = _context.getContentResolver().query(_externalUri, proj, _ISMUSIC, null, sort);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ListID.add(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return ListID;
    }

    public String getTitlebyID(int ID) {
        String MusicTitle = "Unknown Title";
        String[] proj = {MediaStore.Audio.Media.TITLE};
        Cursor cursor = _context.getContentResolver().query(_externalUri, proj, _ISMUSIC
                + " and " + MediaStore.Audio.Media._ID + "==" + ID, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            MusicTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            cursor.close();
        }
        return MusicTitle;
    }

    public String getArtistbyID(int ID) {
        String MusicArtist = "Unknown Artist";
        String[] proj = {MediaStore.Audio.Media.ARTIST};
        Cursor cursor = _context.getContentResolver().query(_externalUri, proj, _ISMUSIC
                + " and " + MediaStore.Audio.Media._ID + "==" + ID, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            MusicArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            cursor.close();
        }
        return MusicArtist;
    }

    public String getGenrebyID(int ID) {
        String Genre = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Uri uri = ContentUris.withAppendedId(_externalUri, ID);
        try {
            retriever.setDataSource(_context, uri);
            Genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        } catch (Exception ignored) {
        }
        if (Genre == null)
            Genre = "Unknown Genre";
        return Genre;
    }

    public Bitmap getArtWorkbyID(int ID) {
        ContentResolver resolver = _context.getContentResolver();
        int albumID = -1;
        String[] proj = {MediaStore.Audio.Media.ALBUM_ID};
        Cursor cursor = resolver.query(_externalUri, proj, _ISMUSIC + " and " + MediaStore.Audio.Media._ID + "==" + ID, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            cursor.close();
        }
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumID);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFileDescriptor(resolver.openFileDescriptor(uri, "r").getFileDescriptor(), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
        /*
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Uri uri = ContentUris.withAppendedId(_externalUri, ID);
        retriever.setDataSource(_context, uri);
        byte[] bytes = retriever.getEmbeddedPicture();
        if (bytes != null)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        else return null;
         */
    }
///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("Recycle")
    public MusicModel getNextMusic(int ID) {
        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj,
                _ISMUSIC, null, _Title);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex(_ID)) == ID)
                    if (cursor.moveToNext()) {
                        MusicModel musicModel = GetMusicModel(cursor);
                        cursor.close();
                        return musicModel;
                    }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("Recycle")
    public MusicModel getPrevMusic(int ID) {

        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj,
                _ISMUSIC, null, _Title);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndex(_ID)) == ID)
                    if (cursor.moveToPrevious()) {
                        MusicModel musicModel = GetMusicModel(cursor);
                        cursor.close();
                        return musicModel;
                    } else return null;
            } while (cursor.moveToNext());
            cursor.close();
        }
        return null;
    }


    private MusicModel GetMusicModel(Cursor cursor) {
        MusicModel musicModel;

        musicModel = new MusicModel();
        int musicID = cursor.getInt(cursor.getColumnIndex(_ID));
        musicModel.ID = musicID;
        musicModel.Title = cursor.getString(cursor.getColumnIndex(_Title));
        musicModel.Artist = cursor.getString(cursor.getColumnIndex(_Artist));
        musicModel.Album = cursor.getString(cursor.getColumnIndex(_Album));
        musicModel.Duration = cursor.getInt(cursor.getColumnIndex(_Duration));
        musicModel.Uri = ContentUris.withAppendedId(_externalUri, musicID);

        return musicModel;
    }

    public MusicModel getFirstMusic() {
        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj, _ISMUSIC, null, _Title);
        if (cursor != null && cursor.moveToFirst()) {
            MusicModel musicModel = GetMusicModel(cursor);
            cursor.close();
            return musicModel;
        }
        return null;
    }

    public MusicModel getMusic(int initializeDSongID) {
        Cursor cursor = _context.getContentResolver().query(_externalUri, _AllProj, _ISMUSIC +" and " +_ID + "==" + initializeDSongID, null, _Title);
        if (cursor != null && cursor.moveToFirst()) {
            MusicModel musicModel = GetMusicModel(cursor);
            cursor.close();
            return musicModel;
        }
        return null;
    }
}
