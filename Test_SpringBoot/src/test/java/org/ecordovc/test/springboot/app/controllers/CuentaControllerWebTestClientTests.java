package org.ecordovc.test.springboot.app.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecordovc.test.springboot.app.models.dtos.TransaccionDto;
import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CuentaControllerWebTestClientTests {

    @Autowired
    private WebTestClient webTestClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    @DisplayName("TestTransferir()")
    void testTransferir() throws Exception{

        //Given
        TransaccionDto transaccionDto = TransaccionDto.builder()
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .bancoId(1L)
                .monto(new BigDecimal("100"))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con Éxito!");
        response.put("transaccion", transaccionDto);

        //When
        webTestClient.post()
//                .uri("http://localhost:8080/api/cuentas/transferir")
                .uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transaccionDto)
                .exchange() // de aca en adelante es la respuesta es como un flag
        //Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()// cuando no indicas ni un dato por defecto es byte
        //con lambda
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        Assertions.assertEquals("Transferencia realizada con Éxito!", json.path("mensaje").asText());
                        Assertions.assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        Assertions.assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        Assertions.assertEquals("100", json.path("transaccion").path("monto").asText());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                })
        // normalmente
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(Matchers.is("Transferencia realizada con Éxito!"))
                .jsonPath("$.mensaje").value(valor -> {
                    Assertions.assertEquals("Transferencia realizada con Éxito!", valor);
                })
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con Éxito!")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(transaccionDto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    @DisplayName("TestDetalle()")
    void testDetalle() throws Exception{
        Cuenta cuenta = new Cuenta(1L, "Edward", new BigDecimal("900"));

        webTestClient.get()
                .uri("api/cuentas/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Edward")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(3)
    @DisplayName("TestDetalle2()")
    void testDetalle2(){
        webTestClient.get()
                .uri("api/cuentas/2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> { // mejor para una declaracion de clase para el response
                    Cuenta cuenta = response.getResponseBody();
                    Assertions.assertEquals("Cristina", cuenta.getPersona());
                    Assertions.assertEquals("2100.00", cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(4)
    @DisplayName("TestListar()")
    void testListar(){
        webTestClient.get()
                .uri("api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Edward")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900) // jsonpath se usa entero
                .jsonPath("$[1].persona").isEqualTo("Cristina")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(Matchers.hasSize(2));
    }

    @Test
    @Order(5)
    @DisplayName("TestListar2()")
    void testListar2(){
        webTestClient.get()
                .uri("api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> { // con consumewidth se compara los monto en cadena
                    List<Cuenta> cuentas = response.getResponseBody();
                    Assertions.assertNotNull(cuentas);
                    Assertions.assertEquals(2, cuentas.size());

                    Assertions.assertEquals(1, cuentas.get(0).getId());
                    Assertions.assertEquals("Edward", cuentas.get(0).getPersona());
                    Assertions.assertEquals("900.0", cuentas.get(0).getSaldo().toPlainString());
                    Assertions.assertEquals(2, cuentas.get(1).getId());
                    Assertions.assertEquals("Cristina", cuentas.get(1).getPersona());
                    Assertions.assertEquals("2100.0", cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)
                .value(Matchers.hasSize(2));
    }

    @Test
    @Order(6)
    @DisplayName("TestGuardar()")
    void testGuardar(){
        //Given
        Cuenta cuenta = new Cuenta(null, "Laia", new BigDecimal("3000"));

        //When
        webTestClient.post()
                .uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
        //then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Laia")
                .jsonPath("$.persona").value(Matchers.is("Laia"))
                .jsonPath("$.saldo").isEqualTo(3000);
    }

    @Test
    @Order(7)
    @DisplayName("TestGuardar2()")
    void testGuardar2(){
        //Given
        Cuenta cuenta = new Cuenta(null, "Catsby", new BigDecimal("3500"));

        //When
        webTestClient.post()
                .uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                //then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                   Cuenta c = response.getResponseBody();
                   Assertions.assertNotNull(c);

                   Assertions.assertEquals(4, c.getId());
                   Assertions.assertEquals("Catsby", c.getPersona());
                   Assertions.assertEquals("3500", c.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(8)
    @DisplayName("TestEliminar()")
    void testEliminar(){
        webTestClient.get()
                .uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        webTestClient.delete()
                .uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        webTestClient.get()
                .uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        webTestClient.get()
                .uri("/api/cuentas/3")
                .exchange()
//                .expectStatus().is5xxServerError(); // antes del cambio en el controller para detalle
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}
