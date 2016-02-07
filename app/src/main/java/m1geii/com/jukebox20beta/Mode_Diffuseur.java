package m1geii.com.jukebox20beta;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import m1geii.com.jukebox20beta.Fragments.Fragment_Bibliotheque;
import m1geii.com.jukebox20beta.Fragments.Fragment_Listes_de_Lecture;
import m1geii.com.jukebox20beta.Fragments.Fragment_Music_Playback;

public class Mode_Diffuseur extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_diffuseur);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragmentBibliotheque,fragmentControlLecteur;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fragmentBibliotheque = new Fragment_Bibliotheque();
        fragmentControlLecteur=new Fragment_Music_Playback();
        ft.replace(R.id.mainFrame, fragmentBibliotheque)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ft.replace(R.id.playbackContentControl,fragmentControlLecteur)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mode_diffuseur_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mon_compte_diffuseur) {
            return true;
        }

        else if (id == R.id.reglages_diffuseur) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
  /*  protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }  */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Identification de chaque clique sur les option du drawer
        int id = item.getItemId();

        // Déclaration du fragment de l'interface
        Fragment fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch(id) {

            case R.id.accueil_mode_diffuseur:
                fragment = new Fragment_Bibliotheque();
                ft.replace(R.id.mainFrame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.bibliotheque_mode_diffuseur:
                fragment = new Fragment_Bibliotheque();
                ft.replace(R.id.mainFrame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.mes_listes_de_lecture_mode_diffuseur:
                fragment = new Fragment_Listes_de_Lecture();
                ft.replace(R.id.mainFrame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;

            /*case R.id.diffusion_playlist:
                Intent i=new Intent (Mode_Diffuseur.this,Diffuser_Liste_Playlist.class);
                startActivity(i);
                break;*/

            case R.id.changer_de_mode_mode_diffuseur:
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
