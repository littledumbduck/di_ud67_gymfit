package com.example.di_ud67_gymfit.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Socio del gimnasio. Solo vive en memoria: no hay base de datos. */
public class Socio {

    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;        // opcional
    private String provincia;
    private String cuota;
    private String clase;       // opcional
    private List<String> extras;
    private String iban;        // opcional
    private EstadoSocio estado;

    public Socio(String nombre, String apellidos, LocalDate fechaNacimiento, String sexo, String provincia,
                 String cuota, String clase, List<String> extras, String iban, EstadoSocio estado) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.provincia = provincia;
        this.cuota = cuota;
        this.clase = clase;
        this.extras = new ArrayList<>(extras);
        this.iban = iban;
        this.estado = estado;
    }

    /** Copia independiente, usada para poder deshacer una edición. */
    public Socio copiar() {
        return new Socio(nombre, apellidos, fechaNacimiento, sexo, provincia, cuota, clase, extras, iban, estado);
    }

    /** Sobrescribe los datos de este socio con los de otro. */
    public void copiarDe(Socio otro) {
        nombre = otro.nombre;
        apellidos = otro.apellidos;
        fechaNacimiento = otro.fechaNacimiento;
        sexo = otro.sexo;
        provincia = otro.provincia;
        cuota = otro.cuota;
        clase = otro.clase;
        extras = new ArrayList<>(otro.extras);
        iban = otro.iban;
        estado = otro.estado;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getCuota() { return cuota; }
    public void setCuota(String cuota) { this.cuota = cuota; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public List<String> getExtras() { return extras; }
    public void setExtras(List<String> extras) { this.extras = new ArrayList<>(extras); }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public EstadoSocio getEstado() { return estado; }
    public void setEstado(EstadoSocio estado) { this.estado = estado; }
}
