package org.ecordovc.test.springboot.app.services;

import org.ecordovc.test.springboot.app.models.entities.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<Cuenta> findAll();

     Cuenta save(Cuenta cuenta);

     void deleteById(Long id);

    Cuenta findById(Long id);

    int revisarTotalTransferencia(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, Long bancoId, BigDecimal monto);
}
