package com.una.app.placefinder506;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Marco on 18/4/2017.
 */
public class FragmentoMapa extends Fragment implements OnMapReadyCallback {
    private GoogleMap miMapa;
    private Location location;

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
        ubicacionActual();
    }
    public void ubicacionActual(){
        LatLng milugar = new LatLng(9.971157, -84.129138);
        miMapa.addMarker(new MarkerOptions().position(milugar).title("Escuela de Informática"));
        int zoomLevel = 12;
        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(milugar, zoomLevel));
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
