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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock // al usar docallmethoreal se cambia a impl PreguntaRepository y ExamenRepository
    ExamenRepositoryImpl examenRepository;

    @Mock
    PreguntaRepositoryImpl preguntaRepository;

    @Captor
    ArgumentCaptor<Long> captor;

    @InjectMocks
    ExamenServiceImpl service;
//    ExamenService service;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
            // al usar docallmethoreal se cambia a impl PreguntaRepository y ExamenRepository
//        this.examenRepository = Mockito.mock(ExamenRepositoryImpl.class);
//        this.preguntaRepository = Mockito.mock(PreguntaRepositoryImpl.class);
//        this.service = new ExamenServiceImpl(examenRepository, preguntaRepository);
    }

    @Test
    @DisplayName("FindExamenPorNombre()")
    void findExamenPorNombre() {

//        ExamenRepository examenRepository = new ExamenRepositoryImpl();
        /*ExamenRepository examenRepository = Mockito.mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(examenRepository);*/

//        List<Examen> datos = Arrays.asList(new Examen(5L, "Matemáticas"),
//                new Examen(6L, "Lenguajes"),
//                new Examen(7L, "Historia"));

        List<Examen> datos = Arrays.asList(Datos.EXAMENES.toArray(new Examen[0]));

        Mockito.when(examenRepository.findAll()).thenReturn(datos); // nunca llama al metodo real solo lo simula
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matemáticas", examen.orElseThrow().getNombre());
    }

    @Test
    @DisplayName("FindExamenPorNombreListaVacia()")
    void findExamenPorNombreListaVacia() {

        /*ExamenRepository examenRepository = Mockito.mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(examenRepository);*/
        List<Examen> datos = Collections.emptyList();

        Mockito.when(examenRepository.findAll()).thenReturn(datos); // nunca llama al metodo real solo lo simula
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");

        assertFalse(examen.isPresent());
//        assertEquals(5L, examen.orElseThrow().getId());
//        assertEquals("Matemáticas", examen.orElseThrow().getNombre());
    }

    @Test
    @DisplayName("TestPreguntasExamen()")
    void testPreguntasExamen() {
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS); // 5L - anyLong

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(6, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));
    }

    @Test
    @DisplayName("TestPreguntasExamenVerify()")
    void testPreguntasExamenVerify() {
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS); // 5L - anyLong

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(6, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());
    }

    @Test
    @DisplayName("TestNoExisteExamenVerify()")
    void testNoExisteExamenVerify() {
        //given
        Mockito.when(examenRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS); // 5L - anyLong

        //when
        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");

        //then
        assertNull(examen);
        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());
    }

    @Test
    @DisplayName("TestGuardarExamen()")
    void testGuardarExamen() {
        // Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

//        Mockito.when(examenRepository.guardar(Mockito.any(Examen.class))).thenReturn(Datos.EXAMEN);
        Mockito.when(examenRepository.guardar(Mockito.any(Examen.class))).then(new Answer<Examen>(){
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }

        });

//        Examen examen = service.guardar(Datos.EXAMEN);
        //when
        Examen examen = service.guardar(newExamen);

        //then
        assertNotNull(examen);
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        Mockito.verify(examenRepository).guardar(Mockito.any(Examen.class));
        Mockito.verify(preguntaRepository).guardarVarias(Mockito.anyList());
    }

    @Test
    @DisplayName("TestManejoExecption()")
    void testManejoExecption(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL); // EXAMENES
        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.isNull())).thenThrow(IllegalArgumentException.class); // Mockito.anyLong()

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matemáticas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.isNull()); // Mockito.anyLong()
    }

    @Test
    @DisplayName("TestArgumentMatchers()")
    void testArgumentMatchers(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        Mockito.verify(examenRepository).findAll();
//        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.argThat(arg -> arg != null && arg.equals(5L)));
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.argThat(arg -> arg != null && arg >= 5L));
//        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.eq(5L));
//        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(5L);
    }

    @Test
    @DisplayName("TestArgumentMatchers2()")
    void testArgumentMatchers2(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.argThat(new MiArgsMatchers()));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long>{
        private Long aLong;

        @Override
        public boolean matches(Long aLong) {
            this.aLong = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "Es para un mensaje perzonalisado de error que imprime mockito en caso de que falle el Test " +
                    aLong + " debe ser un entero positivo";
        }
    }

    @Test
    @DisplayName("TestArgumentCaptor()")
    void testArgumentCaptor(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
//        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matemáticas");

//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); // se borra si se declara @Captor
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    @DisplayName("TestDoThrow()")
    void testDoThrow(){
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        // permite el then o thenreturn porque devuelve algo en cambio si ponemos dothrow no espera nada
//        Mockito.when(preguntaRepository.guardarVarias()).thenThrow(IllegalArgumentException.class);

        // en caso te devuelve algo vacio seria
        Mockito.doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(Mockito.anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    @DisplayName("TestDoAnswer()")
    void testDoAnswer(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
//        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS);
        Mockito.doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Datos.PREGUNTAS : Collections.emptyList(); //null se cambio por el collections
        }).when(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertTrue(examen.getPreguntas().contains("geometría"));
        assertEquals(6, examen.getPreguntas().size());

        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());
    }

    @Test
    @DisplayName("TestDoAnswerGuardarExamen()")
    void testDoAnswerGuardarExamen() {
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        Mockito.doAnswer(new Answer<Examen>(){
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }

        }).when(examenRepository).guardar(Mockito.any(Examen.class));

        Examen examen = service.guardar(newExamen);

        assertNotNull(examen);
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        Mockito.verify(examenRepository).guardar(Mockito.any(Examen.class));
        Mockito.verify(preguntaRepository).guardarVarias(Mockito.anyList());
    }

    @Test
    @DisplayName("TestDoCallRealMethod()")
    void testDoCallRealMethod(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
//        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(Datos.PREGUNTAS);
        Mockito.doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
    }

    @Test
    @DisplayName("TestSpy()")
    void testSpy(){
        ExamenRepository examenRepository = Mockito.spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = Mockito.spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmética");
//        Mockito.when(preguntaRepository.findPreguntasPorExamenId(Mockito.anyLong())).thenReturn(preguntas); // Datos.PREGUNTAS
        Mockito.doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size()); // 6
        assertTrue(examen.getPreguntas().contains("aritmética"));

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(Mockito.anyLong());
    }

    @Test
    @DisplayName("TestOrdenDeInvocaciones()")
    void testOrdenDeInvocaciones(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguajes");

        InOrder inOrder = Mockito.inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    @DisplayName("TestOrdenDeInvocaciones2()")
    void testOrdenDeInvocaciones2(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguajes");

        InOrder inOrder = Mockito.inOrder(examenRepository, preguntaRepository);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);

        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    @DisplayName("TestNumeroDeInvocaciones()")
    void testNumeroDeInvocaciones(){
        Mockito.when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        Mockito.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        Mockito.verify(preguntaRepository, Mockito.times(1)).findPreguntasPorExamenId(5L);
        Mockito.verify(preguntaRepository, Mockito.atLeast(1)).findPreguntasPorExamenId(5L); // atleast como minimo 1 vez
        Mockito.verify(preguntaRepository, Mockito.atLeastOnce()).findPreguntasPorExamenId(5L);
        Mockito.verify(preguntaRepository, Mockito.atMost(10)).findPreguntasPorExamenId(5L); // maximo 10 pero en si solo enviamos 1
        Mockito.verify(preguntaRepository, Mockito.atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    @DisplayName("TestNumeroInvocaciones3()")
    void testNumeroInvocaciones3(){
        Mockito.when(examenRepository.findAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPreguntas("Matemáticas");

        Mockito.verify(preguntaRepository, Mockito.never()).findPreguntasPorExamenId(5L);
        Mockito.verifyNoInteractions(preguntaRepository);

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(examenRepository, Mockito.times(1)).findAll();
        Mockito.verify(examenRepository, Mockito.atLeast(1)).findAll();
        Mockito.verify(examenRepository, Mockito.atLeastOnce()).findAll();
        Mockito.verify(examenRepository, Mockito.atMost(10)).findAll();
        Mockito.verify(examenRepository, Mockito.atMostOnce()).findAll();
    }
}