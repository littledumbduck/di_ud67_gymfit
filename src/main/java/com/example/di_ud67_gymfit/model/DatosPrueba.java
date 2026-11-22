package com.example.di_ud67_gymfit.model;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Datos de ejemplo para probar la interfaz sin base de datos.
 * Todo se carga en memoria al arrancar y se pierde al cerrar la aplicación.
 */
public final class DatosPrueba {

    private DatosPrueba() {
    }

    public static final List<String> CUOTAS = List.of("Básica", "Plus", "Premium", "Joven", "Senior");

    public static final List<String> EXTRAS = List.of("Taquilla", "Toalla", "Parking");

    /** Socios que aparecen en la tabla al abrir la aplicación. */
    public static List<Socio> socios() {
        List<Socio> lista = new ArrayList<>();
        lista.add(new Socio("Ana", "Ruiz Martín", LocalDate.of(1999, 3, 12), "Mujer", "Sevilla",
                "Plus", "Yoga", List.of("Taquilla"), "ES91 2100 0418 4502 0005 1332", EstadoSocio.ACTIVO));
        lista.add(new Socio("Luis", "Pérez Gil", LocalDate.of(2001, 7, 21), "Hombre", "Huelva",
                "Básica", null, List.of(), null, EstadoSocio.BAJA));
        lista.add(new Socio("Marta", "López Vega", LocalDate.of(1988, 11, 30), "Mujer", "Sevilla",
                "Premium", "Pilates", List.of("Toalla", "Parking"), null, EstadoSocio.ACTIVO));
        lista.add(new Socio("José", "Navarro Ortiz", LocalDate.of(2005, 2, 2), "Hombre", "Málaga",
                "Joven", "Spinning", List.of(), null, EstadoSocio.ACTIVO));
        lista.add(new Socio("Carmen", "Díaz Romero", LocalDate.of(1958, 9, 17), "Mujer", "Cádiz",
                "Senior", "Aquagym", List.of("Toalla"), null, EstadoSocio.ACTIVO));
        lista.add(new Socio("Javier", "Moreno Sanz", LocalDate.of(1993, 1, 5), "Hombre", "Córdoba",
                "Plus", "Crossfit", List.of("Parking"), null, EstadoSocio.CONGELADO));
        lista.add(new Socio("Lucía", "Fernández Castro", LocalDate.of(2000, 6, 23), "Mujer", "Sevilla",
                "Básica", "Zumba", List.of(), null, EstadoSocio.ACTIVO));
        lista.add(new Socio("Pablo", "Jiménez Ramos", LocalDate.of(1984, 11, 11), null, "Granada",
                "Premium", "Boxeo", List.of("Taquilla", "Parking"), null, EstadoSocio.ACTIVO));
        return lista;
    }

    /** Clases dirigidas: unas 10, como recomiendan las pautas para un desplegable. */
    public static List<String> clases() {
        return ordenar(List.of("Aquagym", "Body Pump", "Boxeo", "Crossfit", "GAP", "Pilates",
                "Spinning", "Stretching", "TRX", "Yoga", "Zumba"));
    }

    /** Provincias por nombre (no por código INE). */
    public static List<String> provincias() {
        return ordenar(List.of("A Coruña", "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila",
                "Badajoz", "Barcelona", "Bizkaia", "Burgos", "Cáceres", "Cádiz", "Cantabria", "Castellón",
                "Ceuta", "Ciudad Real", "Córdoba", "Cuenca", "Gipuzkoa", "Girona", "Granada", "Guadalajara",
                "Huelva", "Huesca", "Illes Balears", "Jaén", "La Rioja", "Las Palmas", "León", "Lleida",
                "Lugo", "Madrid", "Málaga", "Melilla", "Murcia", "Navarra", "Ourense", "Palencia",
                "Pontevedra", "Salamanca", "Santa Cruz de Tenerife", "Segovia", "Sevilla", "Soria",
                "Tarragona", "Teruel", "Toledo", "Valencia", "Valladolid", "Zamora", "Zaragoza"));
    }

    /** Ordena alfabéticamente respetando tildes (Ávila junto a la A). */
    private static List<String> ordenar(List<String> valores) {
        List<String> copia = new ArrayList<>(valores);
        Collator collator = Collator.getInstance(new Locale("es", "ES"));
        collator.setStrength(Collator.PRIMARY);
        copia.sort(collator);
        return copia;
    }
}
