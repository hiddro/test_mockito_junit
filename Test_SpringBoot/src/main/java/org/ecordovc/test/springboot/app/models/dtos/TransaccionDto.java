package org.ecordovc.test.springboot.app.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDto implements Serializable {

    private Long cuentaOrigenId;

    private Long cuentaDestinoId;

    private Long bancoId;

    private BigDecimal monto;
}
