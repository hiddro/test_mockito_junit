package org.ecordovc.test.springboot.app;

import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.ecordovc.test.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
public class IntegracionJpaTests {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    @DisplayName("TestFindById()")
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);

        Assertions.assertTrue(cuenta.isPresent());
        Assertions.assertEquals("Edward", cuenta.orElseThrow().getPersona());
    }

    @Test
    @DisplayName("TestFindByPersona()")
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Edward");

        Assertions.assertTrue(cuenta.isPresent());
        Assertions.assertEquals("Edward", cuenta.orElseThrow().getPersona());
        Assertions.assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    @DisplayName("TestFindByPersonaThrowException()")
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");

        Assertions.assertThrows(NoSuchElementException.class, () -> cuenta.orElseThrow());
        Assertions.assertFalse(cuenta.isPresent());
    }

    @Test
    @DisplayName("TestFindAll()")
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();

        Assertions.assertFalse(cuentas.isEmpty());
        Assertions.assertEquals(2, cuentas.size());
    }

    @Test
    @DisplayName("TestSave()")
    void testSave(){
        //Given
        Cuenta cuenta = new Cuenta(null, "Laia", new BigDecimal("3000"));

        //When
        Cuenta cuentaSave = cuentaRepository.save(cuenta);

//        Cuenta cuentaPersona = cuentaRepository.findByPersona("Laia").orElseThrow();
//        Cuenta cuentaPersona = cuentaRepository.findById(cuentaSave.getId()).orElseThrow();

        //Then
        Assertions.assertEquals("Laia", cuentaSave.getPersona()); // cuentaPersona
        Assertions.assertEquals("3000", cuentaSave.getSaldo().toPlainString()); // cuentaPersona
//        Assertions.assertEquals(3, cuentaPersona.getId());
    }

    @Test
    @DisplayName("TestUpdate()")
    void testUpdate(){
        //Given
        Cuenta cuenta = new Cuenta(null, "Laia", new BigDecimal("3000"));

        //When
        Cuenta cuentaSave = cuentaRepository.save(cuenta);

        //Then
        Assertions.assertEquals("Laia", cuentaSave.getPersona());
        Assertions.assertEquals("3000", cuentaSave.getSaldo().toPlainString());

        cuenta.setSaldo(new BigDecimal("4000"));
        Cuenta cuentaUpdate = cuentaRepository.save(cuentaSave);

        //Then
        Assertions.assertEquals("Laia", cuentaUpdate.getPersona());
        Assertions.assertEquals("4000", cuentaUpdate.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("TestDelete()")
    void testDelete(){
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();

        Assertions.assertEquals("Cristina", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findById(2L).orElseThrow();
        });

        Assertions.assertEquals(1, cuentaRepository.findAll().size());
    }
}
