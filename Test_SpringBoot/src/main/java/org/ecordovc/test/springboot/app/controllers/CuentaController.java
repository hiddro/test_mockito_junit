package org.ecordovc.test.springboot.app.controllers;

import org.ecordovc.test.springboot.app.models.dtos.TransaccionDto;
import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.ecordovc.test.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> listar(){
        return cuentaService.findAll();
    }

    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> detalle(@PathVariable(name = "id") Long id){
        Cuenta cuenta = null;

        try {
            cuenta = cuentaService.findById(id);
        }catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cuenta);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return cuentaService.save(cuenta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransaccionDto transaccionDto){
        cuentaService.transferir(transaccionDto.getCuentaOrigenId(), transaccionDto.getCuentaDestinoId(), transaccionDto.getBancoId(), transaccionDto.getMonto());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con Éxito!");
        response.put("transaccion", transaccionDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        cuentaService.deleteById(id);
    }
}
