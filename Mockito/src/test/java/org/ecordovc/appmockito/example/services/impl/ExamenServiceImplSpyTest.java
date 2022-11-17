package org.ecordovc.appmockito.example.services.impl;

import org.ecordovc.appmockito.example.Datos;
import org.ecordovc.appmockito.example.models.Examen;
import org.ecordovc.appmockito.example.repository.ExamenRepository;
import org.ecordovc.appmockito.example.repository.PreguntaRepository;
import org.ecordovc.appmockito.example.repository.impl.ExamenRepositoryImpl;
import org.ecordovc.appmockito.example.repository.impl.PreguntaRepositoryImpl;
import org.ecordovc.appmockito.example.services.ExamenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    ExamenRepositoryImpl examenRepository;

    @Spy
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service;

    @Test
    @DisplayName("TestSpy()")
    void testSpy(){
        List<String> preguntas = Arrays.asList("aritmética");
        Mockito.doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size()); // 6
        assertTrue(examen.getPreguntas().contains("aritmética"));

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());
    }
}