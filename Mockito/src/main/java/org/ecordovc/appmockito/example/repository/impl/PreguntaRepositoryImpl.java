package org.ecordovc.appmockito.example.repository.impl;

import org.ecordovc.appmockito.example.Datos;
import org.ecordovc.appmockito.example.repository.PreguntaRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository {
    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");
    }

    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPreguntasPorExamenId");

        try{
            TimeUnit.SECONDS.sleep(2);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Datos.PREGUNTAS;
    }
}
