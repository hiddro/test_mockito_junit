package org.ecordovc.test.springboot.app.utils;

import org.ecordovc.test.springboot.app.models.entities.Banco;
import org.ecordovc.test.springboot.app.models.entities.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
//    public static final Cuenta CUENTA_001 = new Cuenta(1L, "Edward", new BigDecimal("1000"));
//    public static final Cuenta CUENTA_002 = new Cuenta(2L, "Cristina", new BigDecimal("2000"));
//    public static final Banco BANCO = new Banco(1L, "BBVA", 0);

    public static Optional<Cuenta> crearCuenta001(){
        return Optional.of(new Cuenta(1L, "Edward", new BigDecimal("1000")));
    }

    public static Optional<Cuenta> crearCuenta002(){
        return Optional.of(new Cuenta(2L, "Cristina", new BigDecimal("2000")));
    }

    public static Optional<Banco> crearBanco001(){
        return Optional.of(new Banco(1L, "BBVA", 0));
    }
}
