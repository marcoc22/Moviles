package com.una.app.placefinder506;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Marco, Massiel,Angélica,Bryhan on 18/4/2017.
 */
public class FragmentoMapa extends Fragment implements OnMapReadyCallback {
    private GoogleMap miMapa;
    private Location location;
    private Marker marcador;
    double latitud = 0.0;
    double longitud = 0.0;
    Context aux2;
    public Context getAux2() {
        return aux2;
    }

    public void setAux2(Context aux2) {
        this.aux2 = aux2;
    }



    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public GoogleMap getMiMapa() {
        return miMapa;
    }

    public void setMiMapa(GoogleMap miMapa) {
        this.miMapa = miMapa;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        miMapa = googleMap;
        //si pongo este método se cae
        miUbicacion();
        //ubicacionActual();
    }
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
        marcador = miMapa.addMarker(new MarkerOptions().position(milugar).title("Mi posición Actual"));
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




    public void ubicarFarmacias(List<Lugar> lugares){
        for(int i=0;i<lugares.size();i++){
            Lugar lugar = lugares.get(i);
            LatLng milugar = new LatLng(lugar.getLatitud(), lugar.getLongitud());
            miMapa.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.markergreen))
                    .anchor(0.0f, 1.0f)
                    .position(milugar)
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

}
