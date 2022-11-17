package org.ecordovc.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecordovc.test.springboot.app.models.dtos.TransaccionDto;
import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CuentaControllerTestRestTemplateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    @DisplayName("TestTransferir()")
    void testTransferir() throws JsonProcessingException {
        TransaccionDto transaccionDto = TransaccionDto.builder()
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .monto(new BigDecimal("100"))
                .bancoId(1L).build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/cuentas/transferir", transaccionDto, String.class);
        String json = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("Transferencia realizada con Éxito!"));

        JsonNode jsonNode = objectMapper.readTree(json);
        Assertions.assertEquals("Transferencia realizada con Éxito!", jsonNode.path("mensaje").asText());
        Assertions.assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        Assertions.assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
        Assertions.assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("mensaje", "Transferencia realizada con Éxito!");
        response2.put("transaccion", transaccionDto);

        Assertions.assertEquals(objectMapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(2)
    @DisplayName("TestDetalle()")
    void testDetalle(){
        ResponseEntity<Cuenta> respuesta = testRestTemplate.getForEntity("/api/cuentas/1", Cuenta.class);
        Cuenta cuenta = respuesta.getBody();

        Assertions.assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        Assertions.assertNotNull(cuenta);
        Assertions.assertEquals(1L, cuenta.getId());
        Assertions.assertEquals("Edward", cuenta.getPersona());
        Assertions.assertEquals("900.00", cuenta.getSaldo().toPlainString());
        Assertions.assertEquals(new Cuenta(1L, "Edward", new BigDecimal("900.00")), cuenta);

    }

    @Test
    @Order(3)
    @DisplayName("TestListar()")
    void testListar() throws JsonProcessingException{
        ResponseEntity<Cuenta[]> respuesta = testRestTemplate.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());

        Assertions.assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        Assertions.assertEquals(2, cuentas.size());
        Assertions.assertEquals(1L, cuentas.get(0).getId());
        Assertions.assertEquals("Edward", cuentas.get(0).getPersona());
        Assertions.assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());

        Assertions.assertEquals(2L, cuentas.get(1).getId());
        Assertions.assertEquals("Cristina", cuentas.get(1).getPersona());
        Assertions.assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());

        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
        Assertions.assertEquals(1L, jsonNode.get(0).path("id").asLong());
        Assertions.assertEquals("Edward", jsonNode.get(0).path("persona").asText());
        Assertions.assertEquals("900.0", jsonNode.get(0).path("saldo").asText());

        Assertions.assertEquals(2L, jsonNode.get(1).path("id").asLong());
        Assertions.assertEquals("Cristina", jsonNode.get(1).path("persona").asText());
        Assertions.assertEquals("2100.0", jsonNode.get(1).path("saldo").asText());
    }

    @Test
    @Order(4)
    @DisplayName("TestGuardar()")
    void testGuardar(){
        Cuenta cuenta = new Cuenta(null, "Laia", new BigDecimal("3000"));

        ResponseEntity<Cuenta> respuesta = testRestTemplate.postForEntity("/api/cuentas", cuenta, Cuenta.class);

        Assertions.assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        Cuenta response = respuesta.getBody();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(3L, response.getId());
        Assertions.assertEquals("Laia", response.getPersona());
        Assertions.assertEquals("3000", response.getSaldo().toPlainString());
    }

    @Test
    @Order(5)
    @DisplayName("TestEliminar()")
    void testEliminar(){
        ResponseEntity<Cuenta[]> respuesta = testRestTemplate.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
        Assertions.assertEquals(3, cuentas.size());

//        testRestTemplate.delete("/api/cuentas/3");
        //exchange es para todos los metodos get post put delete
//        ResponseEntity<Void> exchange = testRestTemplate.exchange("/api/cuentas/3", HttpMethod.DELETE, null, Void.class);
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 3L);
        ResponseEntity<Void> exchange = testRestTemplate.exchange("/api/cuentas/{id}", HttpMethod.DELETE, null, Void.class, pathVariables);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        Assertions.assertFalse(exchange.hasBody());

        respuesta = testRestTemplate.getForEntity("/api/cuentas", Cuenta[].class);
        cuentas = Arrays.asList(respuesta.getBody());
        Assertions.assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> respuestaDetalle = testRestTemplate.getForEntity("/api/cuentas/3", Cuenta.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, respuestaDetalle.getStatusCode());
        Assertions.assertFalse(respuestaDetalle.hasBody());
    }
}
