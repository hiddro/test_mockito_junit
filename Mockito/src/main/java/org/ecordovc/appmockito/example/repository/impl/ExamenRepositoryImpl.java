package org.ecordovc.appmockito.example.repository.impl;

import org.ecordovc.appmockito.example.Datos;
import org.ecordovc.appmockito.example.models.Examen;
import org.ecordovc.appmockito.example.repository.ExamenRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
//        return Collections.emptyList();
                /*Arrays.asList(new Examen(5L, "Matemáticas"),
                new Examen(6L, "Lenguajes"),
                new Examen(7L, "Historia"));*/

        try{
            TimeUnit.SECONDS.sleep(5);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return Datos.EXAMENES;
    }
}
