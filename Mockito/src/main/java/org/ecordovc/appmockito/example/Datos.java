package org.ecordovc.appmockito.example;

import org.ecordovc.appmockito.example.models.Examen;

import java.util.*;

public class Datos {
    public final static List<Examen> EXAMENES = Arrays.asList(new Examen(5L, "Matemáticas"),
            new Examen(6L, "Lenguajes"),
            new Examen(7L, "Historia"));

    public final static List<Examen> EXAMENES_ID_NULL = Arrays.asList(new Examen(null, "Matemáticas"),
            new Examen(null, "Lenguajes"),
            new Examen(null, "Historia"));

    public final static List<Examen> EXAMENES_ID_NEGATIVOS = Arrays.asList(new Examen(-5L, "Matemáticas"),
            new Examen(-6L, "Lenguajes"),
            new Examen(-7L, "Historia"));
    public final static List<String> PREGUNTAS = Arrays.asList("aritmética", "integrales", "derivadas",
            "trigonometría", "geometría", "calculo");

    public final static Examen EXAMEN = new Examen(8L, "Física");
}
