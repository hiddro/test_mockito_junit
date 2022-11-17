package org.ecordovc.appmockito.example.repository;

import org.ecordovc.appmockito.example.models.Examen;

import java.util.List;

public interface ExamenRepository {

    Examen guardar(Examen examen);

    List<Examen> findAll();
}
