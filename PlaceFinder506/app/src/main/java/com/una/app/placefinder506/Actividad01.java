package com.una.app.placefinder506;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Actividad01 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad01);

        // A continuación mi código para OnCreate
        Mensaje("Bienvenido Actividad 01");

        OnclickDelButton(R.id.btnLogin);
        OnclickDelButton(R.id.btnApp);

    } // Fin del Oncreate de la Actividad 01

    public void Mensaje(String msg){getSupportActionBar().setTitle(msg);};

    public void OnclickDelButton(int ref) {

        // Ejemplo  OnclickDelButton(R.id.MiButton);
        // 1 Doy referencia al Button
        View view =findViewById(ref);
        Button miButton = (Button) view;
        //  final String msg = miButton.getText().toString();
        // 2.  Programar el evento onclick
        miButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // if(msg.equals("Texto")){Mensaje("Texto en el botón ");};
                switch (v.getId()) {

                    case R.id.btnLogin:
                        Intent intento = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intento);

                        break;

                    case R.id.btnApp:
                        intento = new Intent(getApplicationContext(), categorias.class);
                        startActivity(intento);

                        break;
                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton

} // [18:25:04] Fin de la Clase Actividad 01
