package com.una.app.placefinder506;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by
 * Angélica
 * Bryhan
 * Marco
 * Massiel Mora Rodríguez cedula: 604190071
 */
public class DeveloperInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);
        // A continuación mi código para OnCreate
        Mensaje("Información de Desarrolladores");

    } // Fin del Oncreate de la DeveloperInfo

    public void Mensaje(String msg){getSupportActionBar().setTitle(msg);};

} // [20:01:33] Fin de la Clase DeveloperInfo
