package m1geii.com.jukebox20beta.Diffuseur;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import m1geii.com.jukebox20beta.Model.Chanson;
import m1geii.com.jukebox20beta.MusicService;
import m1geii.com.jukebox20beta.R;

public class InterfacePartage extends AppCompatActivity {
    ArrayList image_details = null;         // Liste statique
    ArrayList array_liste_constante = null; // Liste se mettant à jour
    ArrayList<Chanson> array_chansons;
    Button partager;
    IntentFilter connectionfilter;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel wifiDirectChannel;
    ServerSocket socketserver = null;
    // Variable pour l'envoie de Socket
    DataOutputStream [] oos = new DataOutputStream[70];
    DataInputStream  [] in  = new DataInputStream[70];
    Socket [] socket = new Socket[70];
    int numbreClientConnecte = 0;
     ListView lv1;

    // Partie déclaration du service
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;


    /////////////////////////////////

    public android.os.Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.d_activity_interface_partage);
        setTitle("Serveur");

        // récupération de la valeur
        Bundle objetbunble  = this.getIntent().getExtras();
        String InfoPasse= objetbunble.getString("nomListe");

        ///////////// Ajout code Hugues /////////////
        FloatingActionButton boutonLancerMusique=(FloatingActionButton)findViewById(R.id.bouton_flottant_lancer_musique);
        array_chansons=new ArrayList<>();

        // ****** recuperation de la liste ******
        image_details = new CreerListBDD(InterfacePartage.this).getList(InfoPasse) ;
        array_liste_constante = new CreerListBDD(InterfacePartage.this).getList(InfoPasse) ;

        //connectionfilter = new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        connectionfilter = new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION); // Indique si  l'Ã©tat de la connexion Wi-Fi peer to peer a changÃ©

        initializeWiFiDirect();
        lv1 = (ListView) findViewById(R.id.liste_musiques_votes);
        lv1.setAdapter(new CustomListAdapter(this, image_details));

        /*lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewItem ligneSelect=(NewItem)lv1.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Ligne select: "+ligneSelect.getId()+" "+ligneSelect.getTitre(),Toast.LENGTH_SHORT).show();
            }
        });*/
        partager = (Button) findViewById(R.id.bouton_partager_liste);
   /*     partager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String msg1 = new ConvertirListToString().convertirToString(image_details);

                if (image_details != null) {
                        for(int i=0 ;i<numbreClientConnecte;i++) {
                            socket[0].isClosed();
                                    Log.i("msg",socket[0].isClosed()+"");
                            if(oos[i]!=null)
                                new EnvoyerMsg(oos[i], msg1).start();

                        }
                }

            }
        });*/

        /////////////////// Code ajouté Hugues ///////////////////
        boutonLancerMusique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerListeChansons(array_liste_constante);
                musicSrv.setList(array_chansons);
                musicSrv.setChanson(0);
                musicSrv.playChanson();
            }
        });
        registerReceiver(connectionChangedReceiver, connectionfilter);
    }

    /////// Méthode de recréation d'une arrayList de chansons DEBUT ///////
    private void creerListeChansons(ArrayList liste_recup){
        for(int i=0;i<liste_recup.size();i++){
            NewItem ligne_recup=(NewItem)lv1.getItemAtPosition(i);
            array_chansons.add(new Chanson(Long.valueOf(ligne_recup.getId()),ligne_recup.getTitre(),ligne_recup.getArtist()));
        }
    }

    /////// Méthode de recréation d'une arrayList de chansons FIN ///////

    /////// Connexion au service de musique DEBUT ///////

    //Connexion au service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    /////// Connexion au service de musique FIN ///////

    /////// Début cycle pour le service  ///////

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(InterfacePartage.this, MusicService.class);
            this.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            this.startService(playIntent);
        }
    }

    /////// Cycle de vie pour le service  ///////


    BroadcastReceiver connectionChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract the NetworkInfo
            String extraKey = WifiP2pManager.EXTRA_NETWORK_INFO;
            NetworkInfo networkInfo = intent.getParcelableExtra(extraKey);

            // Check if we're connected
            if (networkInfo.isConnected()) {
                wifiP2pManager.requestConnectionInfo(wifiDirectChannel,
                        new WifiP2pManager.ConnectionInfoListener() {
                            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                // If the connection is established
                                    if (info.groupFormed) {
                                        if (info.isGroupOwner) {
                                            Log.i("msg :",numbreClientConnecte+"");
                                            try {
                                                    socketserver = new ServerSocket(1030);
                                                    new CreerSocketServer(socketserver, InterfacePartage.this).initiateServerSocket();
                                            }catch (Exception e){
                                                Log.e("msg", Log.getStackTraceString(e));
                                            }
                                        }
                                    }

                                    }



                            //}
                        });
            }
            else
            {
                Log.d("msg", "Wi-Fi Direct Disconnected");
            }
        }
    };
    private void initializeWiFiDirect() {
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

          wifiDirectChannel = wifiP2pManager.initialize(this, getMainLooper(),
                new WifiP2pManager.ChannelListener() {
                    public void onChannelDisconnected() {
                        initializeWiFiDirect();
                    }
                }
        );
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            if(socketserver!=null){
                socketserver.close();
                for(int i=0;i<numbreClientConnecte;i++){
                    if(socket[i]!=null)
                        socket[i].close();
                    if(oos[i]!=null)
                        oos[i].close();
                    if(in[i]!=null)
                        in[i].close();
                }
            }


        } catch (IOException e)
        {
            Log.e("msg", Log.getStackTraceString(e));

        }

        if(musicSrv!=null){
            musicSrv.stopSelf();
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Si vous quittez la page, vous allez perdre la connexion")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface arg0, int arg1) {
                      //  Intent i=new Intent(InterfacePartage.this,Serveur.class);
                      //  startActivity(i);
                        finish();
                    }
                }).create().show();
    }

}

