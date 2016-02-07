package m1geii.com.jukebox20beta.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import m1geii.com.jukebox20beta.Ajouter_Musique;
import m1geii.com.jukebox20beta.Model.AdapterChansons;
import m1geii.com.jukebox20beta.Model.BDAdapter;
import m1geii.com.jukebox20beta.Model.Chanson;
import m1geii.com.jukebox20beta.MusicService;
import m1geii.com.jukebox20beta.R;


public class Fragment_contenu_liste_de_lecture extends Fragment {
    String NomListe;
    ArrayList<Chanson> Chansons = new ArrayList<>();
    ArrayList<String> ltitres = new ArrayList<>();
    ArrayList<String> lartistes = new ArrayList<>();
    ArrayList<Integer> lids = new ArrayList<>();

    ArrayAdapter<String> adapter;
    ListView listeMusiquePlaylist;
    EditText NomDeLaNouvelleListe;
    FloatingActionButton btnFloatingAjoutTitre;
    LayoutInflater songInf;
    String id;
    AdapterChansons adaptChanson;
    BDAdapter base;
    Cursor curseurBase;
    Bundle donneesContenuPlaylist=new Bundle();

    // Parti déclaration du service chanson
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    FragmentTransaction ft;
    Fragment fragment;

    public Fragment_contenu_liste_de_lecture() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Récupération des données
        id=getArguments().getString("identifiantPlaylist");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View vueContenuPlaylist=inflater.inflate(R.layout.fragment_contenu_liste_de_lecture, container, false);

        btnFloatingAjoutTitre=(FloatingActionButton)vueContenuPlaylist.findViewById(R.id.bouton_flottant_ajouter_musiques);
        listeMusiquePlaylist=(ListView)vueContenuPlaylist.findViewById(R.id.liste_des_musiques);

        adaptChanson = new AdapterChansons(getActivity(),Chansons);
        listeMusiquePlaylist.setAdapter(adaptChanson);

        // Listener lors du clique sur une musique de la liste de lecture
        listeMusiquePlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicSrv.setList(Chansons);
                musicSrv.setChanson(position);
                musicSrv.playChanson();
            }
        });

        // Gestion du clique long
        registerForContextMenu(listeMusiquePlaylist);

        base = new BDAdapter(getActivity());
        base.ouvrirBase();
        curseurBase = base.ObtenirTousLesTitres(id);
        Chansons.clear();

        if(curseurBase!=null && curseurBase.moveToFirst()){
            do
            {
                String name = curseurBase.getString(1);
                ltitres.add(name);

                String art = curseurBase.getString(2);
                lartistes.add(art);

                String ids = curseurBase.getString(3);
                long idsBis=Long.parseLong(ids);
                lids.add((int) idsBis);

                Chansons.add(new Chanson(idsBis, name, art));
            }
            while(curseurBase.moveToNext());

            curseurBase.close();
        }

        // Ecouteur bouton Ajouter
        btnFloatingAjoutTitre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donneesContenuPlaylist.putString("identifiantPlaylist", id);
                FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
                Intent i = new Intent(getActivity(), Ajouter_Musique.class);
                i.putExtra("IdentifiantListe", id);
                getActivity().getSupportFragmentManager().popBackStack();
                startActivity(i);

                //Fragment fragment;
                //FragmentTransaction ft;
                /*ft = getActivity().getSupportFragmentManager().beginTransaction();

                fragment = new Fragment_Ajout_Musiques();
                fragment.setArguments(donneesContenuPlaylist); // Envoie des infos de la musique au fragment
                ft.replace(R.id.mainFrame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();*/
            }
        });

        return vueContenuPlaylist;
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    /*@Override
    public void onPause(){
        curseurBase.close();
        base.fermer();
        super.onPause();
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onDestroy() {
        if(musicSrv!=null){
            musicSrv.stopSelf();
        }

        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextmenumusiques, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            /*case R.id.AllerAPageAlbum:

                return true;
            case R.id.AllerAPageArtiste:
                return true;*/

            case R.id.SuppressionChanson:
                String l = Chansons.get(info.position).getTitle();
                base.Supprimerm(l);
                Chansons.remove(l);
                Toast.makeText(getActivity().getApplicationContext(),"Supprimée", Toast.LENGTH_SHORT).show();
                donneesContenuPlaylist.putString("identifiantPlaylist", id);

                fragment = new Fragment_contenu_liste_de_lecture();
                fragment.setArguments(donneesContenuPlaylist);
                getActivity().getSupportFragmentManager().popBackStack(); // Pour supprimer le fragment actuel

                // Puis ensuite on relance le fragment en guise de rafraîchissement
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
        }

        return super.onContextItemSelected(item);
    }
}
