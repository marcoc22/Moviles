package com.una.app.placefinder506;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.LocationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.R.attr.type;
import static com.una.app.placefinder506.R.id.btnAutomovil;
import static com.una.app.placefinder506.R.id.btnCaminando;
import static com.una.app.placefinder506.R.id.btnIr;

/**
 * Created by
 * Angélica
 * Bryhan
 * Marco
 * Massiel Mora Rodríguez cedula: 604190071
 */

public class FragmentoMapa extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap miMapa;
    private Marker marcador;
    private Lugar lugarSeleccionado;
    List<Lugar> clinicas;
    List<Lugar> farmacias;
    List<Lugar> macrobioticas;

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    double latitud = 0.0;
    double longitud = 0.0;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;



    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;



    public GoogleMap getMiMapa() {
        return miMapa;
    }

    public void setMiMapa(GoogleMap miMapa) {
        this.miMapa = miMapa;
    }

    public List<Lugar> getClinicas() {
        return clinicas;
    }

    public void setClinicas(List<Lugar> clinicas) {
        this.clinicas = clinicas;
    }

    public List<Lugar> getFarmacias() {
        return farmacias;
    }

    public void setFarmacias(List<Lugar> farmacias) {
        this.farmacias = farmacias;
    }

    public List<Lugar> getMacrobioticas() {
        return macrobioticas;
    }

    public void setMacrobioticas(List<Lugar> macrobioticas) {
        this.macrobioticas = macrobioticas;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        miMapa = googleMap;
        clinicas = new ArrayList<>();
        farmacias = new ArrayList<>();
        macrobioticas = new ArrayList<>();
        MarkerPoints = new ArrayList<>();
        lugarSeleccionado = new Lugar();

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        miMapa.setMyLocationEnabled(true);
        miUbicacion();
        //ubicacionActual();
        miMapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition(); //
                lugarSeleccionado.setLatitud(position.latitude);
                lugarSeleccionado.setLongitud(position.longitude);
                switch (marker.getSnippet()){
                    case "Clinica":
                        lugarSeleccionado.setTipo(Lugar.CLINICA);
                        break;
                    case "Farmacia":
                        lugarSeleccionado.setTipo(Lugar.FARMACIA);
                        break;
                    case "Macrobiotica":
                        lugarSeleccionado.setTipo(Lugar.MACROBIOTICA);
                        break;
                    default:
                        break;
                }
                lugarSeleccionado.setNombre(marker.getTitle());
                Button btnIr = (Button) getActivity().findViewById(R.id.btnIr);
                Button btnLlamar = (Button) getActivity().findViewById(R.id.btnLlamar);
                Button btnEditar = (Button) getActivity().findViewById(R.id.btnEditar);
                Button btnEliminar = (Button) getActivity().findViewById(R.id.btnEliminar);
                Button btnCaminando = (Button) getActivity().findViewById(R.id.btnCaminando);
                Button btnAutomovil = (Button) getActivity().findViewById(R.id.btnAutomovil);
                if (btnLlamar.getVisibility()==btnLlamar.VISIBLE){
                    btnLlamar.setVisibility(btnLlamar.INVISIBLE);} else {btnLlamar.setVisibility(btnLlamar.VISIBLE);}
                if (btnIr.getVisibility()==btnIr.VISIBLE){
                    btnCaminando.setVisibility(btnCaminando.INVISIBLE);
                    btnIr.setVisibility(btnIr.INVISIBLE);
                } else {
                    btnIr.setVisibility(btnIr.VISIBLE);
                }
                if (btnEditar.getVisibility()==btnEditar.VISIBLE){
                    btnEditar.setVisibility(btnEditar.INVISIBLE);} else {btnEditar.setVisibility(btnEditar.VISIBLE);}
                if (btnEliminar.getVisibility()==btnEliminar.VISIBLE){
                    btnEliminar.setVisibility(btnEliminar.INVISIBLE);} else {btnEliminar.setVisibility(btnEliminar.VISIBLE);}


                return false;
            }
        });
        OnclickDelButton(btnIr);
        OnclickDelButton(R.id.btnLlamar);
        OnclickDelButton(R.id.btnEditar);
        OnclickDelButton(R.id.btnEliminar);
        OnclickDelButton(R.id.btnCaminando);
        OnclickDelButton(R.id.btnAutomovil);

        miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                // que hacer después de una larga presión en la pantalla
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.registro, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton("Agregar",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Mensaje("Itentando agregar Lugar");
                        EditText nombre = (EditText) dialoglayout.findViewById(R.id.txtNombre);
                        EditText telefono = (EditText) dialoglayout.findViewById(R.id.txtTelefono);
                        EditText correo = (EditText) dialoglayout.findViewById(R.id.txtCorreo);
                        EditText pagina = (EditText) dialoglayout.findViewById(R.id.txtPagina);
                        Spinner sp = (Spinner) dialoglayout.findViewById(R.id.spinner);
                        Lugar l = new Lugar(nombre.getText().toString(),latLng.latitude,latLng.longitude,telefono.getText().toString(),correo.getText().toString(),pagina.getText().toString(),sp.getSelectedItemPosition());
                        agregarLugar(l,l);
                        dialog.dismiss();//Called dismiss here but dialog doesnt closes
                    }
                });
                builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();//Called dismiss here but dialog doesnt closes
                    }
                });

                builder.setView(dialoglayout);
                builder.setTitle("Registro de Lugar");
                builder.show();
                Mensaje("LongClick");

            }
        });
    }
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void agregarLugar(Lugar l,Lugar l2)
    {
        DatabaseReference myRef = null ;
        switch(l.getTipo())
        {
            case Lugar.MACROBIOTICA:
                myRef = database.getReference("Macrobioticas");
                break;
            case Lugar.FARMACIA:
                myRef = database.getReference("Farmacias");
                break;
            case Lugar.CLINICA:
                myRef = database.getReference("Clinicas");
                break;
        }
        myRef.child(l.crearClave()).setValue(l2);
        //Mensaje("Agregando " + l.getNombre() + " En Lat:" + l.getLatitud() + " Lon: " + l.getLongitud());
    }



    public void OnclickDelButton(int ref) {

        // Ejemplo  OnclickDelButton(R.id.MiButton);
        // 1 Doy referencia al Button
        View view = getActivity().findViewById(ref);
        Button miButton = (Button) view;
        //  final String msg = miButton.getText().toString();
        // 2.  Programar el evento onclick
        miButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if(msg.equals("Texto")){Mensaje("Texto en el botón ");};
                Lugar lugarObtenido = null;
                List<Lugar> lugaresGeneral = new ArrayList<Lugar>();
                switch (v.getId()) {

                    case btnIr:

                        Lugar origin = new Lugar();
                        origin.setLatitud(latitud);
                        origin.setLongitud(longitud);
                        Lugar dest = lugarSeleccionado;

                        // Getting URL to the Google Directions API
                        String url = getUrl(origin, dest);
                        Log.d("onMapClick", url.toString());
                        FetchUrl FetchUrl = new FetchUrl();

                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);
                        //move map camera
                        LatLng originPoint = new LatLng(dest.getLatitud(),dest.getLongitud());
                        miMapa.moveCamera(CameraUpdateFactory.newLatLng(originPoint));
                        miMapa.animateCamera(CameraUpdateFactory.zoomTo(16));
                        Button btnCaminando = (Button) getActivity().findViewById(R.id.btnCaminando);
                        btnCaminando.setVisibility(btnCaminando.VISIBLE);
                        break;
                    case R.id.btnLlamar:
                        Lugar lugarEncontrado = null;
                        if(lugarSeleccionado.getTipo()==Lugar.MACROBIOTICA){
                            lugaresGeneral = macrobioticas;
                        }else if(lugarSeleccionado.getTipo()==Lugar.FARMACIA){
                            lugaresGeneral = farmacias;
                        }else if(lugarSeleccionado.getTipo()==Lugar.CLINICA){
                            lugaresGeneral = clinicas;
                        }
                        for(int i=0;i<lugaresGeneral.size();i++){
                            lugarObtenido = lugaresGeneral.get(i);
                            if(lugarObtenido.getLatitud() == lugarSeleccionado.getLatitud() && lugarObtenido.getLongitud() == lugarSeleccionado.getLongitud()){
                                lugarEncontrado = lugarObtenido;
                            }
                        }
                        if(lugarEncontrado!=null){
                            MarcarNumero(lugarEncontrado.getTelefono());
                        }else{
                            MensajeOK("Lugar no existe");
                        }

                        break;

                    case R.id.btnEditar:
                        //Mensaje("metodo editar aqui");
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View dialoglayout = inflater.inflate(R.layout.registro, null);
                        final EditText nombre = (EditText) dialoglayout.findViewById(R.id.txtNombre);
                        final EditText telefono = (EditText) dialoglayout.findViewById(R.id.txtTelefono);
                        final EditText correo = (EditText) dialoglayout.findViewById(R.id.txtCorreo);
                        final EditText pagina = (EditText) dialoglayout.findViewById(R.id.txtPagina);
                        final Spinner sp = (Spinner) dialoglayout.findViewById(R.id.spinner);

                        if(lugarSeleccionado.getTipo()==Lugar.MACROBIOTICA){
                            lugaresGeneral = macrobioticas;
                        }else if(lugarSeleccionado.getTipo()==Lugar.FARMACIA){
                            lugaresGeneral = farmacias;
                        }else if(lugarSeleccionado.getTipo()==Lugar.CLINICA){
                            lugaresGeneral = clinicas;
                        }
                        for(int i=0;i<lugaresGeneral.size();i++){
                            lugarObtenido = lugaresGeneral.get(i);
                            if(lugarObtenido.getLatitud() == lugarSeleccionado.getLatitud() && lugarObtenido.getLongitud() == lugarSeleccionado.getLongitud()){
                                nombre.setText(lugarObtenido.getNombre());
                                telefono.setText(lugarObtenido.getTelefono());
                                correo.setText(lugarObtenido.getCorreo());
                                pagina.setText(lugarObtenido.getPaginaWeb());
                                sp.setSelection(lugarObtenido.getTipo());
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setPositiveButton("Actualizar",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lugarSeleccionado.setNombre(nombre.getText().toString());
                                lugarSeleccionado.setTelefono(telefono.getText().toString());
                                lugarSeleccionado.setCorreo(correo.getText().toString());
                                lugarSeleccionado.setPaginaWeb(pagina.getText().toString());
                                lugarSeleccionado.setTipo(sp.getSelectedItemPosition());
                                agregarLugar(lugarSeleccionado,lugarSeleccionado);
                                MensajeOK(sp.getSelectedItem().toString() + " actualizada");
                                dialog.dismiss();//Called dismiss here but dialog doesnt closes
                            }
                        });
                        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();//Called dismiss here but dialog doesnt closes
                            }
                        });

                        builder.setView(dialoglayout);
                        builder.setTitle("Edición de Lugar");
                        builder.show();
                        break;

                    case R.id.btnEliminar:

                        if(lugarSeleccionado.getTipo()==Lugar.MACROBIOTICA){
                            lugaresGeneral = macrobioticas;
                        }else if(lugarSeleccionado.getTipo()==Lugar.FARMACIA){
                            lugaresGeneral = farmacias;
                        }else if(lugarSeleccionado.getTipo()==Lugar.CLINICA){
                            lugaresGeneral = clinicas;
                        }
                        for(int i=0;i<lugaresGeneral.size();i++){
                            lugarObtenido = lugaresGeneral.get(i);
                            if(lugarObtenido.getLatitud() == lugarSeleccionado.getLatitud() && lugarObtenido.getLongitud() == lugarSeleccionado.getLongitud()){
                                agregarLugar(lugarObtenido,null);
                                MensajeOK("Lugar eliminar");
                            }
                        }
                        break;
                    case R.id.btnCaminando:
                        origin = new Lugar();
                        origin.setLatitud(latitud);
                        origin.setLongitud(longitud);
                        dest = lugarSeleccionado;
                        build_retrofit_and_get_response(v,"WALKING",dest.getLatitud(),dest.getLongitud());
                        break;
                    case R.id.btnAutomovil:
                        origin = new Lugar();
                        origin.setLatitud(latitud);
                        origin.setLongitud(longitud);
                        dest = lugarSeleccionado;
                        build_retrofit_and_get_response(v,"DRIVING",dest.getLatitud(),dest.getLongitud());
                        break;
                    default:
                        break;
                }// fin de casos
            }// fin del onclick
        });
    }//fin del onclick del boton

    public void Mensaje(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};
    public void ubicacionActual(){
        LatLng milugar = new LatLng(9.971157, -84.129138);
        miMapa.addMarker(new MarkerOptions().position(milugar).title("Escuela de Informática"));
        int zoomLevel = 12;
        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(milugar, zoomLevel));
    }

    public void addMarcador(double lat, double lng) {
        LatLng milugar = new LatLng(lat, lng);
        CameraUpdate miubicacion = CameraUpdateFactory.newLatLngZoom(milugar, 16);
        if (marcador != null) marcador.remove();
        //marcador = miMapa.addMarker(new MarkerOptions().position(milugar).title("Mi posición Actual"));
        miMapa.animateCamera(miubicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {

            latitud = location.getLatitude();
            longitud = location.getLongitude();
            addMarcador(latitud, longitud);
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion() {
        //aux2.getApplicationContext() esto debería ser this
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locListener);
        }catch(Exception e){
            String mensaje = e.getMessage();
        }
    }

    public void MarcarNumero(String numero){
        Intent i = new
                Intent(android.content.Intent.ACTION_DIAL,
                Uri.parse("tel:" + numero));
        startActivity(i);
    };
    public void MensajeOK(String msg){
        View v1 = getActivity().getWindow().getDecorView().getRootView();
        AlertDialog.Builder builder1 = new AlertDialog.Builder( v1.getContext());
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {} });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        ;};

    public void ubicarFarmacias(List<Lugar> lugares){
        for(int i=0;i<lugares.size();i++){
            Lugar lugar = lugares.get(i);
            LatLng milugar = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            miMapa.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markergreen))
                    .anchor(0.0f, 1.0f)
                    .position(milugar)
                    .snippet("Farmacia")
                    .title(lugar.getNombre())
            );
        }

    }
    public void ubicarClinicas(List<Lugar> lugares){
        for(int i=0;i<lugares.size();i++){
            Lugar lugar = lugares.get(i);
            LatLng milugar = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            miMapa.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerblue))
                    .anchor(0.0f, 1.0f)
                    .position(milugar)
                    .snippet("Clinica")
                    .title(lugar.getNombre())
            );
        }
    }
    public void ubicarMacrobioticas(List<Lugar> lugares){
        for(int i=0;i<lugares.size();i++){
            Lugar lugar = lugares.get(i);
            LatLng milugar = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            miMapa.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerorange))
                    .anchor(0.0f, 1.0f)
                    .position(milugar)
                    .snippet("Macrobiotica")
                    .title(lugar.getNombre())
            );
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmento_mapa, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentoMapa);
        fragment.getMapAsync(this);

    }
    private void build_retrofit_and_get_response(final View v,String type, double destLatitude, double destLongitude ) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", latitud + "," + longitud,destLatitude + "," + destLongitude, type);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {

                try {
                    //Remove previous line from map

                    // This loop will go through all the results and add marker on each location.

                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        //String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        String texto = "Distancia: "+distance+"\n" + "Tiempo: "+time;

                        Snackbar.make(v, texto, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }
    private String getUrl(Lugar origin,Lugar dest) {

        // Origin of route
        String str_origin = "origin=" + origin.getLatitud() + "," + origin.getLongitud();

        // Destination of route
        String str_dest = "destination=" + dest.getLatitud() + "," + dest.getLongitud();


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                miMapa.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) getActivity());
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = miMapa.addMarker(markerOptions);

        //move map camera
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        miMapa.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        miMapa.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

}
