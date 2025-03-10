/*
Creado por: Sergi Villalonga, Lucas Sabater y Marcos Socías
 */

package com.example.zenworld;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ZenWordData zenWordData;
    private String paraulaTriada;
    private int wordLength;
    private int widthDisplay, heightDisplay;
    private String respostaTmp = "";

    private HashMap<String,Integer> posicion = new HashMap<>();

    private char [] lletresCercle;

    private TextView[][] textViewsArray = null;

    private Button [] botonsCercle = new Button[7];

    private int posicion3;
    private boolean palabrasDesveladas [] = {false, false, false, false, false};

    private int longitud;

    private int soluciones;

    private int bonus = 0;

    private int descobert;

    private int ocultas;

    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar ZenWordData
        zenWordData = new ZenWordData(this);

        botonsCercle[0] = findViewById(R.id.button1);
        botonsCercle[1] = findViewById(R.id.button2);
        botonsCercle[2] = findViewById(R.id.button3);
        botonsCercle[3] = findViewById(R.id.button4);
        botonsCercle[4] = findViewById(R.id.button5);
        botonsCercle[5] = findViewById(R.id.button6);
        botonsCercle[6] = findViewById(R.id.button7);

        // Obtener información de la pantalla
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        // Seleccionar palabra aleatoria y palabras ocultas
        reiniciar(null);
    }

    //  Borrar de la pantalla las casillas de las letras escondidas actuales
    //  Cambiar de color los elementos de la pantalla (círculo y letras escondidas)
    //  Mostrar las nuevas palabras escondidas
    //  Reiniciar todas las variables necesarias para comenzar una nueva partida
    public void reiniciar(View view) {
        Random random = new Random();
        wordLength = random.nextInt(5) + 3;
        ocultas = 0;
        descobert = 0;
        posicion3=1;
        soluciones=1;

        posicion = new HashMap<>();

        for(int i = 0; i < palabrasDesveladas.length; i++) {
            palabrasDesveladas[i] = false;
        }

        zenWordData.resetInfo();

        // ClearText para que todos los botones estén disponibles y
        // no aparezca la respuesta temporal de la anterior ronda

        clearText(null);

        paraulaTriada = seleccionarParaulaLlarga();

        zenWordData.setElecc(wordLength, paraulaTriada, posicion3);

        lletresCercle = new char[wordLength];
        for(int i = 0; i < paraulaTriada.length(); i++) {
            lletresCercle[i] = paraulaTriada.charAt(i);
        }

        zenWordData.obtenerParaulesConLletresDisponibles(lletresCercle);
        total =zenWordData.getSolucionsPerLongitudLength();
        if(textViewsArray!=null){
            limpiarArrayTV();
        }

        textViewsArray = new TextView[5][];
        creacionHuecosPalabras();

        // Poner invisibles todos los botones
        for(int i = 0; i < botonsCercle.length; i++) {
            botonsCercle[i].setVisibility(View.GONE);
        }

        // Poner visibles los botones que queramos mostrar

        botonesUsables();

        random(null);

        actualizarTextViewArriba();
    }


    private void limpiarArrayTV() {
        // Verificar que textViewsArray no sea nulo
        ConstraintLayout layout = findViewById(R.id.constraintLayout);
        if (textViewsArray != null) {
            for (int i = 0; i < textViewsArray.length; i++) {
                if (textViewsArray[i] != null) {
                    for (int j = 0; j < textViewsArray[i].length; j++) {
                        if (textViewsArray[i][j] != null) {
                            layout.removeView(textViewsArray[i][j]);
                        }
                    }
                }
            }
        }
    }

    public void botonesUsables(){
        for (int i=0;i<botonsCercle.length;i++){
            botonsCercle[i].setEnabled(true);
        }

        for(int i = 0; i < lletresCercle.length; i++) {
            botonsCercle[i].setVisibility(View.VISIBLE);
            botonsCercle[i].setText(String.valueOf(lletresCercle[i]));
        }
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setEnabled(true);

        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        imageButton3.setEnabled(true);

        Button bonus = findViewById(R.id.bonusButton);
        bonus.setEnabled(true);

        Button clear = findViewById(R.id.button8);
        clear.setTextColor(Color.BLACK);
        clear.setEnabled(true);

        Button send = findViewById(R.id.button9);
        send.setEnabled(true);
        send.setTextColor(Color.BLACK);
    }

    private void creacionHuecosPalabras () {

        int color = seleccionColor();

        int longitudesPalabras = wordLength;
        int long2 = zenWordData.getSolucionsPerLongitudLength();
        System.out.println("long2 "+long2);

        int iteraciones = (long2 < 5) ? long2 : 5;

        for(int i=0;i<iteraciones;i++){
            if(longitudesPalabras <= 3){
                zenWordData.elegirPalabrasSolucion(3, posicion3);
                posicion3++;
            }else{
                if(!zenWordData.elegirPalabrasSolucion(longitudesPalabras, posicion3)) {
                    i--;
                }
            }
            if(longitudesPalabras > 3) {
                longitudesPalabras--;
            }
        }

        longitudesPalabras = wordLength;
        posicion3=1;
        longitud=zenWordData.getParaulesOcultesLength();

        System.out.println("\nlong "+longitud);
        textViewsArray[0] = new TextView[longitudesPalabras];
        textViewsArray[0]=crearFilaTextViews(R.id.guideLine5,longitudesPalabras, color);
        posicion.put(zenWordData.getParaulesOcultes(longitudesPalabras).get(posicion3),0);
        if(longitudesPalabras > 3) {
            longitudesPalabras--;
        }else{
            posicion3++;
        }

        int existe = 1;

        for(int i = 1; i < longitud; i++) {

            if(zenWordData.getNumPalabrasOcultasLeng(longitudesPalabras)){

                textViewsArray[i] = new TextView[longitudesPalabras];

                switch(existe) {
                    case 1: textViewsArray[1]=crearFilaTextViews(R.id.guideLine4,longitudesPalabras, color); break;
                    case 2: textViewsArray[2]=crearFilaTextViews(R.id.guideLine3,longitudesPalabras, color); break;
                    case 3: textViewsArray[3]=crearFilaTextViews(R.id.guideLine2,longitudesPalabras, color); break;
                    case 4: textViewsArray[4]=crearFilaTextViews(R.id.guideLineStart,longitudesPalabras, color); break;
                }
                posicion.put(zenWordData.getParaulesOcultes(longitudesPalabras).get(posicion3), existe);
                existe++;
                soluciones++;
            } else {
                i--;
            }

            if(longitudesPalabras > 3) {
                longitudesPalabras--;
            }else{
                posicion3++;
            }
        }
        posicion3=1;
    }

    private int seleccionColor() {
        ImageView cercle = findViewById(R.id.imageViewCercle);

        Random random = new Random();
        int color;

        switch(random.nextInt(5)) {
            case 0: color = Color.rgb(235,139,0); cercle.setImageResource(R.drawable.cerclegroc); break; // GROC
            case 2: color = Color.rgb(85, 194, 112); cercle.setImageResource(R.drawable.cercleverd); break;  // VERD
            case 3: color = Color.rgb(146, 92, 167); cercle.setImageResource(R.drawable.cerclepurpura); break; // PÚRPURA
            case 4: color = Color.rgb(62, 22, 225); cercle.setImageResource(R.drawable.cercleblau); break; // BLAU
            default: color = Color.rgb(251, 109, 114); cercle.setImageResource(R.drawable.cerclerosa); break; // ROSA (per defecte)
        }

        return color;
    }

    private String seleccionarParaulaLlarga() {
        List<String> paraules = new ArrayList<>(zenWordData.getParaulesPerLongitud(wordLength));
        Collections.shuffle(paraules);
        return paraules.isEmpty() ? "" : paraules.get(0);
    }

    public void setLletra(View view) {
        Button button = (Button) view;
        String lletra = button.getText().toString();
        respostaTmp += lletra;
        TextView textView = findViewById(R.id.textView);
        textView.setText(respostaTmp);

        button.setEnabled(false);
    }

    public void bonus(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Encertades:  "+descobert+";  possibles:  " + total);
        builder.setMessage("La llista de trobades"+zenWordData.getParaulasAcertadas());
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void random(View view) {
        Random random= new Random();
        char a;
        for (int i = lletresCercle.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            a = lletresCercle[index];
            lletresCercle[index] = lletresCercle[i];
            lletresCercle[i] = a;
        }
        botonesUsables();
        clearText(null);
    }

    @SuppressLint("SetTextI18n")
    public TextView[] crearFilaTextViews(int guia, int lletres, int color) {
        TextView[] textViews = new TextView[lletres];
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        String paraula = zenWordData.getParaulesOcultes(lletres).get(posicion3);
        System.out.println(lletres);
        System.out.println(paraula+"   check");

        for (int i = 0; i < lletres; i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setText("" + paraula.charAt(i));

            textView.setTextColor(color);
            // Crear borde para el TextView
            GradientDrawable border = new GradientDrawable();
            border.setColor(color); // fondo blanco
            border.setStroke(5, Color.rgb(240,0,120)); // borde negro

            textView.setBackground(border);
            textView.setTextSize(25);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Centrar el texto

            constraintLayout.addView(textView);

            // Ajustar las dimensiones de los TextView para que sean "cuadraditos"
            int size = 130;
            constraintSet.constrainWidth(textView.getId(), size);
            constraintSet.constrainHeight(textView.getId(), size);

            if (i == 0) {
                constraintSet.connect(textView.getId(), ConstraintSet.START, guia, ConstraintSet.START);
            } else {
                constraintSet.connect(textView.getId(), ConstraintSet.START, textViews[i - 1].getId(), ConstraintSet.END, 15);
            }

            constraintSet.connect(textView.getId(), ConstraintSet.TOP, guia, ConstraintSet.BOTTOM, 15);

            textViews[i] = textView;
        }

        int margen = (widthDisplay - ((lletres * 130) + (lletres - 1) * 8))/2;

        constraintSet.connect(textViews[0].getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, margen);

        constraintSet.applyTo(constraintLayout);
        return textViews;
    }

    public void clearText(View view) {
        respostaTmp = "";
        TextView textView = findViewById(R.id.textView);
        textView.setText(respostaTmp);
        for (int i=0;i<botonsCercle.length;i++){
            botonsCercle[i].setEnabled(true);
        }
    }

    @SuppressLint("SetTextI18n")
    public void sendText(View view) {
        TextView textView = findViewById(R.id.textView);
        String paraulaIntroduida = textView.getText().toString();
        int longitudPal = paraulaIntroduida.length();
        textView.setText("");
        if(paraulaIntroduida.isEmpty() || longitudPal<3){
            mostraMissatge("Paraula no vàlida.", false);
        }else {
            if (zenWordData.esParaulaOculta(paraulaIntroduida, longitudPal)) {
                descobert++;

                zenWordData.addParaulaTrobada(paraulaIntroduida);
                zenWordData.removeParaulaOculta(paraulaIntroduida, longitudPal);

                ocultas++;

                for (int i = 0; i < textViewsArray[posicion.get(paraulaIntroduida)].length; i++) {
                    if (textViewsArray[posicion.get(paraulaIntroduida)][i]!=null) {
                        textViewsArray[posicion.get(paraulaIntroduida)][i].setTextColor(Color.WHITE);
                        palabrasDesveladas[posicion.get(paraulaIntroduida)] = true;
                    }
                }

                if(ocultas==soluciones) {
                    ganar();
                }else{
                    mostraMissatge("Has descobert una paraula amagada!", true);
                }

            } else if (zenWordData.isValida(paraulaIntroduida, longitudPal)) {
                    descobert++;
                    zenWordData.addParaulaTrobada(zenWordData.getPalabraAcento(paraulaIntroduida));
                    zenWordData.removeParaulaValida(paraulaIntroduida, longitudPal);
                    bonus++;
                    Button boton = findViewById(R.id.bonusButton);
                    boton.setText("" + bonus);
                    mostraMissatge("Paraula vàlida! Tens un bonus.", false);

            } else {
                //TreeSet<String> asd = zenWordData.getSolucionsTrobades();
                //if(asd.contains(zenWordData.getPalabraAcento(paraulaIntroduida))){


                   //mostraMissatge("Els dos sabem que aquesta paraula ja l'has posat... >:C", false);

                //}else{
                    mostraMissatge("Paraula no vàlida.", false);
                //}

            }

            actualizarTextViewArriba();
        }

        clearText(null);
    }

    private void ganar(){
        for (int i=0;i<botonsCercle.length;i++){
            botonsCercle[i].setEnabled(false);
            botonsCercle[i].setVisibility(View.GONE);
        }
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setEnabled(false);

        ImageButton imageButton3 = findViewById(R.id.imageButton3);
        imageButton3.setEnabled(false);

        Button bonus = findViewById(R.id.bonusButton);
        bonus.setEnabled(false);

        Button clear = findViewById(R.id.button8);
        clear.setEnabled(false);

        Button send = findViewById(R.id.button9);
        send.setEnabled(false);

        mostraMissatge("Has guanyat, ets un guanyador ;)", false);
    }
    private void mostraMissatge(String s, boolean llarg) {
        Context context = getApplicationContext();
        int duration = llarg ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    public void ajuda(View view) {
        if (bonus >= 5) {

            boolean letraDesvelada = false;

            for (int i = 0; i < longitud; i++) {
                if (!palabrasDesveladas[i] && textViewsArray[i] != null) {
                    if (textViewsArray[i].length > 0 && textViewsArray[i][0] != null) {
                        // Reveal the first letter of the hidden word
                        textViewsArray[i][0].setTextColor(Color.WHITE);
                        palabrasDesveladas[i] = true;
                        letraDesvelada = true;
                        break;
                    }
                }
            }

            if (!letraDesvelada) {
                mostraMissatge("No hi ha paraules ocultes per desvelar!", false);
            } else {
                bonus -= 5;
                Button boton = findViewById(R.id.bonusButton);
                boton.setText("" + bonus);
            }
        } else {
            mostraMissatge("No tens suficients bonus!", false);
        }
    }

    private void actualizarTextViewArriba () {
        TextView textView = findViewById(R.id.textViewArriba);

        textView.setText("Encertades:  "+descobert+";  possibles:  " + total + ". La llista de trobades"+zenWordData.getParaulasAcertadas());
    }
}
