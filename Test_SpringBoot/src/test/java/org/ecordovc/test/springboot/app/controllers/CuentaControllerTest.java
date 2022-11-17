package org.ecordovc.test.springboot.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecordovc.test.springboot.app.models.dtos.TransaccionDto;
import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.ecordovc.test.springboot.app.services.CuentaService;
import org.ecordovc.test.springboot.app.utils.Datos;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("TestDetalle()")
    void testDetalle() throws Exception{
        // Given
        Mockito.when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta001().orElseThrow());

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.persona").value("Edward"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value("1000"));

        Mockito.verify(cuentaService).findById(1L);
    }

    @Test
    @DisplayName("TestTransferir()")
    void testTransferir() throws Exception{

        // Given
        TransaccionDto transaccionDto = TransaccionDto.builder()
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .monto(new BigDecimal("100"))
                .bancoId(1L)
                .build();

        System.out.println(objectMapper.writeValueAsString(transaccionDto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con Éxito!");
        response.put("transaccion", transaccionDto);

        System.out.println(objectMapper.writeValueAsString(response));

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaccionDto)))
        // Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensaje").value("Transferencia realizada con Éxito!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaccion.cuentaOrigenId").value(transaccionDto.getCuentaOrigenId()))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("TestListar()")
    void testListar() throws Exception{
        //Given
        List<Cuenta> cuentas = Arrays.asList(Datos.crearCuenta001().orElseThrow(),
                Datos.crearCuenta002().orElseThrow());

        Mockito.when(cuentaService.findAll()).thenReturn(cuentas);

        //When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
        //Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].persona").value("Edward"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].persona").value("Cristina"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].saldo").value("1000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].saldo").value("2000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(cuentas)));

        Mockito.verify(cuentaService).findAll();
    }

    @Test
    @DisplayName("TestGuardar()")
    void testGuardar() throws Exception{

        // Given
        Cuenta cuenta = new Cuenta(null, "Laia", new BigDecimal("3000"));
//        Mockito.when(cuentaService.save(Mockito.any())).thenReturn(cuenta);
        Mockito.when(cuentaService.save(Mockito.any())).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cuenta)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.persona", Matchers.is("Laia")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo", Matchers.is(3000)));

        // Then
        Mockito.verify(cuentaService).save(Mockito.any());
    }
}