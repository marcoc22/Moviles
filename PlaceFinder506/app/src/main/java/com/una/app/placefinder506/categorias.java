package com.una.app.placefinder506;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by
 * Angélica
 * Bryhan
 * Marco
 * Massiel Mora Rodríguez cedula: 604190071
 */
public class categorias extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    //el imageview es para setear la foto con la cuenta del usuario más adelante
    private ImageView imageViewfoto;
    private GoogleSignInAccount account;
    ObtenerWebService hiloconexionjson;

    private GoogleApiClient googleApiClient;
    private FragmentoMapa fragmentoMapa;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference f = null;
    DatabaseReference m = null;
    DatabaseReference c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
        f = database.getReference("Farmacias");
        m = database.getReference("Macrobioticas");
        c = database.getReference("Clinicas");

    }
    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};
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

    public void  EnviarMensaje(String mensaje){


        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.putExtra("address", " ");
        i.putExtra("sms_body", "Esta es mi ubicación: "+mensaje);
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
            this.fragmentoMapa.ubicarClinicas(fragmentoMapa.getClinicas());
        } else if (id == R.id.nav_farmacia) {
            this.fragmentoMapa.getMiMapa().clear();
            this.fragmentoMapa.ubicacionActual();
            cargarFarmacias();
            this.fragmentoMapa.ubicarFarmacias(fragmentoMapa.getFarmacias());

        }else if (id == R.id.nav_macrobiotica) {
            this.fragmentoMapa.getMiMapa().clear();
            this.fragmentoMapa.ubicacionActual();
            cargarMacrobioticas();
            this.fragmentoMapa.ubicarMacrobioticas(fragmentoMapa.getMacrobioticas());
        }
        else if (id == R.id.nav_msj) {
            //ENVIAR UN MSJ CON LA UBICACIÓN ACTUAL
            //EnviarMensaje();
            hiloconexionjson = new ObtenerWebService();
            Double latitud = this.fragmentoMapa.getLatitud();
            Double longitud = this.fragmentoMapa.getLongitud();
            hiloconexionjson.execute(latitud.toString(),longitud.toString());

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
        }else if (id == R.id.nav_video) {
            Intent intento = new Intent(getApplicationContext(), ActividadYoutube.class);
            startActivity(intento);
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

        c.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fragmentoMapa.getMiMapa().clear();
                fragmentoMapa.setClinicas(new ArrayList<Lugar>());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Lugar  lugar = postSnapshot.getValue(Lugar.class);
                    fragmentoMapa.getClinicas().add(lugar);
                }
                fragmentoMapa.ubicarClinicas(fragmentoMapa.getClinicas());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //this.clinicas.add(new Lugar("Clínica Guararí",9.9981466,-84.121953));
    }
    public void cargarFarmacias(){
        f.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fragmentoMapa.getMiMapa().clear();
                fragmentoMapa.setFarmacias(new ArrayList<Lugar>());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Lugar  lugar = postSnapshot.getValue(Lugar.class);
                    fragmentoMapa.getFarmacias().add(lugar);
                }
                fragmentoMapa.ubicarFarmacias(fragmentoMapa.getFarmacias());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /*
        this.farmacias.add(new Lugar("Farmacia Fischel",9.9682309,-84.1203954));
        this.farmacias.add(new Lugar("Farmacia Sucre San Francisco De Heredia",9.9961119,-84.1285801));
        this.farmacias.add(new Lugar("Farmacia Las Hortensias",9.9891646,-84.1522265));
        this.farmacias.add(new Lugar("Farmacia Santander",9.9846422,-84.1493512));
        */
    }
    public void cargarMacrobioticas(){


        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fragmentoMapa.getMiMapa().clear();
                fragmentoMapa.setMacrobioticas(new ArrayList<Lugar>());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Lugar  lugar = postSnapshot.getValue(Lugar.class);
                    fragmentoMapa.getMacrobioticas().add(lugar);
                }
                fragmentoMapa.ubicarMacrobioticas(fragmentoMapa.getMacrobioticas());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* this.macrobioticas.add(new Lugar("Macrobiótica LEAF Salud Natural",9.997198,-84.1218801));
        this.macrobioticas.add(new Lugar("Macrobiótica Heredia",9.996912,-84.1206612));
        this.macrobioticas.add(new Lugar("Macrobiótica San Cristóbal",9.9846422,-84.1493512));
        */
    }

    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=38.404593,%20-0.529534&sensor=false
            cadena = cadena + params[0];
            cadena = cadena + ",%20";
            cadena = cadena + params[1];
            cadena = cadena + "&sensor=false";


            String devuelve = "";

            URL url = null; // Url de donde queremos obtener información
            try {
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK){


                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("results");   // results es el nombre del campo en el JSON

                    //Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String direccion="SIN DATOS PARA ESA LONGITUD Y LATITUD";
                    if (resultJSON.length()>0){
                        direccion = resultJSON.getJSONObject(0).getString("formatted_address");
                        // dentro del results pasamos a Objeto la seccion formated_address
                    }
                    devuelve = direccion;   // variable de salida que mandaré al onPostExecute para que actualice la UI

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return devuelve;
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            //MensajeOK(aVoid);
            EnviarMensaje(aVoid);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

}
