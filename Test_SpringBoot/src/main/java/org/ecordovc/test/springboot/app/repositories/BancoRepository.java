package org.ecordovc.test.springboot.app.repositories;

import org.ecordovc.test.springboot.app.models.entities.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {
//    List<Banco> findAll();
//
//    Banco findById(Long id);
//
//    void update(Banco banco);
}
