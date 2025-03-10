package com.example.zenworld;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ZenWordData {
    // Catálogo de palabras válidas con y sin acentos
    private HashMap<String, String> paraulesDic =new HashMap<>();

    // Catálogo de longitudes con las palabras de cada longitud
    private TreeMap<Integer, TreeSet<String>> paraulesPerLongitud = new TreeMap<>();

    // Catálogo de soluciones con las soluciones de cada longitud
    private HashMap<Integer, HashSet<String>> solucionsPerLongitud = new HashMap<>();

    // Catálogo de palabras ocultas junto con su posición en la pantalla
    private TreeMap<Integer, HashMap<Integer,String>> paraulesOcultes = new TreeMap<>();

    // Catálogo de soluciones encontradas
    private TreeSet<String> solucionsTrobades = new TreeSet<>();

    public ZenWordData(Context context) {
        Resources res = context.getResources();
        InputStream is = res.openRawResource(R.raw.paraules2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String doblePalabra : line.split(" ")) {
                    char[] doblePalabraArray = doblePalabra.toCharArray();
                    String palabraAcentos = "";
                    String palabraNoAcentos = "";
                    for (int i = 0; i < doblePalabraArray.length; i++) {
                        if (i < doblePalabraArray.length / 2) {
                            palabraAcentos += doblePalabraArray[i];
                        } else if (i > doblePalabraArray.length / 2) {
                            palabraNoAcentos += doblePalabraArray[i];
                        }
                    }
                    if((palabraNoAcentos.length() > 2) && (palabraNoAcentos.length() < 8)) {
                        paraulesDic.put(palabraNoAcentos, palabraAcentos);

                        paraulesPerLongitud.computeIfAbsent(palabraNoAcentos.length(), k -> new TreeSet<>()).add(palabraNoAcentos);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obtenerParaulesConLletresDisponibles(char[] lletres) {
        HashMap<Character, Integer> lletresMap = new HashMap<>();

        // Contar la frecuencia de cada letra en el array de letras
        for (char lletra : lletres) {
            lletresMap.put(lletra, lletresMap.getOrDefault(lletra, 0) + 1);
        }

        Iterator<Integer> iterator = paraulesPerLongitud.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            TreeSet<String> paraules = paraulesPerLongitud.get(key);

            // Crear un iterador para el TreeSet<String>
            Iterator<String> paraulesIterator = paraules.iterator();
            while (paraulesIterator.hasNext()) {
                String paraula = paraulesIterator.next();
                if (contieneUnicamenteLasLletres(paraula, lletresMap)) {
                    solucionsPerLongitud.computeIfAbsent(paraula.length(), k -> new HashSet<>()).add(paraula);
                    System.out.println(paraula);
                }
            }
        }
    }

    private static boolean contieneUnicamenteLasLletres(String paraula, Map<Character, Integer> lletresMap) {
        Map<Character, Integer> paraulaMap = new HashMap<>();

        // Contar la frecuencia de cada letra en la palabra
        for (char lletra : paraula.toCharArray()) {
            paraulaMap.put(lletra, paraulaMap.getOrDefault(lletra, 0) + 1);
        }

        // Verificar que la palabra no tenga más letras que las disponibles en el array
        for (Map.Entry<Character, Integer> entry : paraulaMap.entrySet()) {
            char lletra = entry.getKey();
            int count = entry.getValue();

            if (count > lletresMap.getOrDefault(lletra, 0)) {
                return false;
            }
        }

        return true;
    }

    public boolean elegirPalabrasSolucion(int longitud, int posicion){
        HashSet<String> soluciones = solucionsPerLongitud.get(longitud);
        if (soluciones!=null) {
        Iterator <String> it=soluciones.iterator();

            int i = 0;
            while (it.hasNext()) {
                i++;
                it.next();
            }
            if (i != 0) {
                Iterator<String> ot = soluciones.iterator();
                Random rd = new Random();
                int o = 0;
                for (; o < rd.nextInt(i); o++) {
                    System.out.println(ot.next()+"   iteracion");

                }

                String s = ot.next();
                paraulesOcultes.computeIfAbsent(longitud, k -> new HashMap<>()).put(posicion,s);

                System.out.println("Antes  "+soluciones);
                soluciones.remove(s);
                System.out.println("Despues  "+soluciones);

                return true;
            }
        }
        return false;
    }

    public void resetInfo(){
        solucionsPerLongitud = new HashMap<>();

        paraulesOcultes = new TreeMap<>();

        solucionsTrobades = new TreeSet<>();
    }

    public TreeSet<String> getParaulesPerLongitud(int length) {
        return paraulesPerLongitud.getOrDefault(length, new TreeSet<>());
    }

    public HashSet<String> getSolucions(int length) {
        return solucionsPerLongitud.getOrDefault(length, new HashSet<>());
    }

    public boolean esParaulaOculta(String paraula, int longitud) {
        return (paraulesOcultes.get(longitud)).containsValue(paraula);
    }

    public void addParaulaTrobada(String paraula) {
        solucionsTrobades.add(paraula);
    }

    public void removeParaulaOculta(String paraula, int longitud) {
        paraulesOcultes.get(longitud).values().remove(paraula);
    }
    public void removeParaulaValida(String paraula, int longitud) {
        solucionsPerLongitud.get(longitud).remove(paraula);
    }
    public boolean isValida(String paraula, int longitud){
        return solucionsPerLongitud.get(longitud).contains(paraula);
    }

    public HashMap<Integer, String> getParaulesOcultes (int lletres) {
        return paraulesOcultes.get(lletres);
    }

    public void setElecc(int q, String elecc, int posicion){
        paraulesOcultes.computeIfAbsent(q, k -> new HashMap<>()).put(posicion,elecc);
    }

    public int getParaulesOcultesLength(){
        Iterator <HashMap<Integer,String>> num =  paraulesOcultes.values().iterator();
        int cont=0;
        while(num.hasNext()){
            HashMap<Integer,String> map= num.next();
            Iterator<String> it = map.values().iterator();
            while(it.hasNext()){
                cont++;
                it.next();
            }
        }
        return cont;
    }

    public boolean getNumPalabrasOcultasLeng(int i){
        return paraulesOcultes.containsKey(i);
    }

    public int getSolucionsPerLongitudLength(){
        Iterator <HashSet<String>> num =  solucionsPerLongitud.values().iterator();
        int cont=0;
        while(num.hasNext()){
            HashSet<String> map= num.next();
            Iterator<String> it = map.iterator();
            while(it.hasNext()){
                cont++;
                it.next();
            }
        }
        return cont;
    }

    public String getParaulasAcertadas(){
        return ""+solucionsTrobades;
    }


    public String getPalabraAcento (String palabra){
        return paraulesDic.get(palabra);
    }
}
