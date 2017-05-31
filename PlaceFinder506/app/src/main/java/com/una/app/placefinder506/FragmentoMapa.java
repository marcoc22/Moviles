package com.una.app.placefinder506;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.LocationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco, Massiel,Angélica,Bryhan on 18/4/2017.
 */
public class FragmentoMapa extends Fragment implements OnMapReadyCallback {
    private GoogleMap miMapa;
    private Location location;
    private Marker marcador;
    private Lugar lugarSeleccionado;
    List<Lugar> clinicas;
    List<Lugar> farmacias;
    List<Lugar> macrobioticas;
    double latitud = 0.0;
    double longitud = 0.0;
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
        lugarSeleccionado = new Lugar();
        //si pongo este método se cae
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
                Button btnEditar = (Button) getActivity().findViewById(R.id.btnEditar);
                Button btnEliminar = (Button) getActivity().findViewById(R.id.btnEliminar);
                if (btnIr.getVisibility()==btnIr.VISIBLE){
                    btnIr.setVisibility(btnIr.INVISIBLE);} else {btnIr.setVisibility(btnIr.VISIBLE);}
                if (btnEditar.getVisibility()==btnEditar.VISIBLE){
                    btnEditar.setVisibility(btnEditar.INVISIBLE);} else {btnEditar.setVisibility(btnEditar.VISIBLE);}
                if (btnEliminar.getVisibility()==btnEliminar.VISIBLE){
                    btnEliminar.setVisibility(btnEliminar.INVISIBLE);} else {btnEliminar.setVisibility(btnEliminar.VISIBLE);}
                return false;
            }
        });
        OnclickDelButton(R.id.btnIr);
        OnclickDelButton(R.id.btnEditar);
        OnclickDelButton(R.id.btnEliminar);

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

                    case R.id.btnIr:
                        Mensaje("metodo ruta aquí");
                        Mensaje(lugarSeleccionado.getNombre()+","+lugarSeleccionado.getLatitud()+","+lugarSeleccionado.getLongitud());
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

                        //Mensaje(lugarSeleccionado.getNombre()+","+lugarSeleccionado.getLatitud()+","+lugarSeleccionado.getLongitud());
                        break;

                    case R.id.btnEliminar:
                        //Mensaje(lugarSeleccionado.getNombre()+","+lugarSeleccionado.getLatitud()+","+lugarSeleccionado.getLongitud());

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

}
