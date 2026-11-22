package com.example.di_ud67_gymfit.model;

/** Situación de un socio en el gimnasio. */
public enum EstadoSocio {
    ACTIVO("Activo"),
    CONGELADO("Congelado"),
    BAJA("De baja");

    private final String texto;

    EstadoSocio(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
