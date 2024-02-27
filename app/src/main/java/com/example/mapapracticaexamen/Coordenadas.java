package com.example.mapapracticaexamen;

public class Coordenadas {

    private String key;
    private String latitud;
    private String longitud;

    public Coordenadas() {
    }

    public Coordenadas(String key, String latitud, String longitud) {
        this.key = key;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

}
