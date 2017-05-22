package com.una.app.placefinder506;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;

public class categorias extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    //el imageview es para setear la foto con la cuenta del usuario más adelante
    private ImageView imageViewfoto;
    private GoogleSignInAccount account;


    private GoogleApiClient googleApiClient;
    private FragmentoMapa fragmentoMapa;
    List<Lugar> clinicas;
    List<Lugar> farmacias;
    List<Lugar> macrobioticas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        clinicas = new ArrayList<>();
        farmacias = new ArrayList<>();
        macrobioticas = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        FragmentManager fm = getFragmentManager();
        fragmentoMapa = new FragmentoMapa();
        //actualiza el fragmento con el deseado
        fm.beginTransaction().replace(R.id.contentFrame,fragmentoMapa).commit();
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
        TextView textViewcorreo = (TextView) findViewById(R.id.textViewcorreo);
        TextView textViewnombre = (TextView) findViewById(R.id.textViewnombre);
        if(account!=null){
            //setea los datos con el correo y el nombre del usuario correspondiente
            textViewnombre.setText(account.getDisplayName());
            textViewcorreo.setText(account.getEmail());
        }
        getMenuInflater().inflate(R.menu.categorias, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.nav_view) {//revisar!
//           Intent intento = new Intent(getApplicationContext(), nformacionAyuda.class);
//           startActivity(intento);
//           return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void  EnviarMensaje(){
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address", " ");
        i.putExtra("sms_body", "Esta es mi ubicación: ");
        i.setType("vnd.android-dir/mms-sms");
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_clinica) {
            this.fragmentoMapa.getMiMapa().clear();
            this.fragmentoMapa.ubicacionActual();
            cargarClinicias();
            this.fragmentoMapa.ubicarClinicas(this.clinicas);
        } else if (id == R.id.nav_farmacia) {
            this.fragmentoMapa.getMiMapa().clear();
            this.fragmentoMapa.ubicacionActual();
            cargarFarmacias();
            this.fragmentoMapa.ubicarFarmacias(this.farmacias);

        }else if (id == R.id.nav_macrobiotica) {
            this.fragmentoMapa.getMiMapa().clear();
            this.fragmentoMapa.ubicacionActual();
            cargarMacrobioticas();
            this.fragmentoMapa.ubicarMacrobioticas(this.macrobioticas);
        }
        else if (id == R.id.nav_msj) {
            //ENVIAR UN MSJ CON LA UBICACIÓN ACTUAL
            EnviarMensaje();

        } else if (id == R.id.nav_send) {
            //enviar un correo (gmail) a los desarrolladores
            Intent intento = new Intent(Intent.ACTION_SEND);
            intento.setData(Uri.parse("mailto:"));
            String[] to = { "marcoc22@hotmail.es" , "massiel.mora.rodriguez@est.una.ac.cr" , "brarodriguezm@hotmail.com" , "angelicamrs16@gmail.com"};
            String[] cc = { "massimoro029@gmail.com" };
            intento.putExtra(Intent.EXTRA_EMAIL, to);
            intento.putExtra(Intent.EXTRA_CC, cc);
            intento.putExtra(Intent.EXTRA_SUBJECT, "Informe sobre PlaceFinder506");
            intento.putExtra(Intent.EXTRA_TEXT, " ");
            intento.setType("message/rfc822");
            startActivity(Intent.createChooser(intento, "Email"));



        } else if (id == R.id.nav_creditos) {
            Intent intento = new Intent(getApplicationContext(), DeveloperInfo.class);
            startActivity(intento);
        } else if (id == R.id.nav_salir) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        googleInScreen();
                    }else{
                        Toast.makeText(getApplicationContext(),"No se pudo cerrar sesión",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            account = result.getSignInAccount();

        }else{
            googleInScreen();
        }
    }

    private void googleInScreen() {
        Intent intent = new Intent(this,Actividad01.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void cargarClinicias(){
        this.clinicas.add(new Lugar("Clínica Guararí",9.9981466,-84.121953));
    }
    public void cargarFarmacias(){
        this.farmacias.add(new Lugar("Farmacia Fischel",9.9682309,-84.1203954));
        this.farmacias.add(new Lugar("Farmacia Sucre San Francisco De Heredia",9.9961119,-84.1285801));
        this.farmacias.add(new Lugar("Farmacia Las Hortensias",9.9891646,-84.1522265));
        this.farmacias.add(new Lugar("Farmacia Santander",9.9846422,-84.1493512));
    }
    public void cargarMacrobioticas(){
        this.macrobioticas.add(new Lugar("Macrobiótica LEAF Salud Natural",9.997198,-84.1218801));
        this.macrobioticas.add(new Lugar("Macrobiótica Heredia",9.996912,-84.1206612));
        this.macrobioticas.add(new Lugar("Macrobiótica San Cristóbal",9.9846422,-84.1493512));
    }
}
