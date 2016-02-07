package m1geii.com.jukebox20beta;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import m1geii.com.jukebox20beta.Model.Chanson;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Chanson> chansons;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private String titreChanson="",artisteChanson="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;
    private boolean musicPlaying=false;

    // Variable pour le contrôle de boutons de la notification
    private static final String ACTION_TOGGLE_PLAYBACK = "m1geii.com.jukebox20beta.TOGGLE_PLAYBACK";
    private static final String ACTION_PREV = "m1geii.com.jukebox20beta.PREV";
    private static final String ACTION_NEXT = "m1geii.com.jukebox20beta.NEXT";


    public void onCreate(){
        // Pour mettre à jour l'interface

        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
        rand=new Random();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }


    @Override
    public void onCompletion(MediaPlayer player) {
        if(player.getCurrentPosition()>0){
            player.reset();
            playNext();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        player.start();
        Intent notIntent = new Intent(this, Mode_Diffuseur.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_drawer_playlists)
                //.setVisibility(Notification.VISIBILITY_PUBLIC)
                //.setUsesChronometer(true)
                .setTicker(titreChanson)
                .setOngoing(true)
                .setContentTitle(titreChanson)
                .setContentText(artisteChanson);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);*/

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_drawer_playlists)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentTitle(titreChanson)
                .setTicker(titreChanson)
                .setContentText(artisteChanson)
                .setColor(0x2196f3)
                .setContentIntent(pendInt)
                .setAutoCancel(true);
                        // Add some playback controls
                //.addAction(R.drawable.previous, "prev",retreivePlaybackAction(3))
                //.addAction(R.drawable.play, "pause", retreivePlaybackAction(1))
                //.addAction(R.drawable.next, "next", retreivePlaybackAction(2));

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFY_ID, mBuilder.build());
    }

    // Setter de l'ArrayList du service
    public void setList(ArrayList<Chanson> lesChansons){
      if(chansons != null)
            chansons.clear();
        chansons = new ArrayList<>();
        for(int i = 0; i<lesChansons.size(); i++)
        {
            Chanson chanson=new Chanson(lesChansons.get(i).getID(),lesChansons.get(i).getTitle(),lesChansons.get(i).getArtist());
            chansons.add(chanson);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    // IBinder permet au client de demander des choses à un service
    public class MusicBinder extends Binder {
        // Méthode permettant de renvoyer le service
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playChanson(){

        Intent intent = new Intent("m1geii.com.jukebox20beta");

        player.reset();
        //get song
        Chanson playChanson = chansons.get(songPosn);
        titreChanson= playChanson.getTitle();
        artisteChanson=playChanson.getArtist();
        //get id
        long currChanson = playChanson.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currChanson);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
        musicPlaying=true;

        // Envoie des informations
        intent.putExtra("titre",titreChanson);
        intent.putExtra("artiste",artisteChanson);
        intent.putExtra("id",String.valueOf(currChanson));
        sendBroadcast(intent);
        }


    // Fonction servant à récupérer les données de la chanson actuellement joué
    public Chanson getCurrentSong(){

        if(chansons!=null){
        Chanson playedSong;
        playedSong=chansons.get(songPosn);

        return playedSong;
        }

        else
            return new Chanson(0,"Titre","Artiste");
    }

    // Fonction de detection de lecture
    public boolean ifPlaying(){ return musicPlaying; }
    public void setChanson(int songIndex){
        songPosn=songIndex;
    }

    // Récupère la position courant de la lecture en cours
    public int getPosn(){
        return player.getCurrentPosition();
    }

    // Récupère la durée de la chanson en cours
    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
        musicPlaying=false;
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
        musicPlaying=true;
    }

    public void playPrev(){
        if(chansons!=null) {
            songPosn--;
            if (songPosn < 0) songPosn = chansons.size() - 1;
            playChanson();
            musicPlaying = true;
        }
    }

    public void playNext(){
        if(chansons!=null) {
            // Si on est en music aléatoire
            if (shuffle) {
                int newChanson = songPosn;
                while (newChanson == songPosn) {
                    newChanson = rand.nextInt(chansons.size());
                }
                songPosn = newChanson;
            } else {
                songPosn++;
                if (songPosn >= chansons.size()) songPosn = 0;
            }
            playChanson();
            musicPlaying = true;
        }
    }

    @Override
    public void onDestroy() {

        //stopForeground(true);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        player.reset();
        return false;
    }

    // Notification commande du lecteur
    private PendingIntent retreivePlaybackAction(final int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this,MusicService.class);
        switch (which){
            case 1:
                // Play and pause
                action = new Intent(MusicService.ACTION_TOGGLE_PLAYBACK);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks
                action = new Intent(MusicService.ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent(MusicService.ACTION_PREV);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 3, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }
}