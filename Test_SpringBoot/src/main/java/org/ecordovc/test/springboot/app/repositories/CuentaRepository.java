package org.ecordovc.test.springboot.app.repositories;

import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

//    @Query("select c from Cuenta c where c.persona=?1") // opcional ya con la definicion de jpa lo busca
    Optional<Cuenta> findByPersona(String persona);

//    List<Cuenta> findAll();

//    Cuenta findById(Long id);

//    void update(Cuenta cuenta);
}
