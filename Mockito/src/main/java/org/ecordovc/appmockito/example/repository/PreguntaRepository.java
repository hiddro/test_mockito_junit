package org.ecordovc.appmockito.example.repository;

import java.util.List;

public interface PreguntaRepository {

    void guardarVarias(List<String> preguntas);

    List<String> findPreguntasPorExamenId(Long id);
}
