package org.ecordovc.test.springboot.app;

import org.ecordovc.test.springboot.app.exceptions.DineroInsuficienteException;
import org.ecordovc.test.springboot.app.models.entities.Banco;
import org.ecordovc.test.springboot.app.models.entities.Cuenta;
import org.ecordovc.test.springboot.app.repositories.BancoRepository;
import org.ecordovc.test.springboot.app.repositories.CuentaRepository;
import org.ecordovc.test.springboot.app.services.CuentaService;
import org.ecordovc.test.springboot.app.utils.Datos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

//	@Mock // 1
	@MockBean // 2
	CuentaRepository cuentaRepository;

//	@Mock // 1
	@MockBean // 2
	BancoRepository bancoRepository;

//	@InjectMocks // 1
	@Autowired // 2
	CuentaService cuentaService;
//	CuentaServiceImpl cuentaService; // 1
//	CuentaService cuentaService;

	@BeforeEach
	void setup(){
//		cuentaRepository = Mockito.mock(CuentaRepository.class);
//		bancoRepository = Mockito.mock(BancoRepository.class);
//
//		cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);

//		Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
//		Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//		Datos.BANCO.setTotalTransferencia(0);
	}

	@Test
	@DisplayName("ContextLoads()")
	void contextLoads() {
		Mockito.when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		Mockito.when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		Mockito.when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco001());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		Assertions.assertEquals("1000", saldoOrigen.toPlainString());
		Assertions.assertEquals("2000", saldoDestino.toPlainString());

		cuentaService.transferir(1L, 2L, 1L, new BigDecimal("100"));

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		Assertions.assertEquals("900", saldoOrigen.toPlainString());
		Assertions.assertEquals("2100", saldoDestino.toPlainString());

		int total = cuentaService.revisarTotalTransferencia(1L);

		Assertions.assertEquals(1, total);

		Mockito.verify(cuentaRepository, Mockito.times(3)).findById(1L);
		Mockito.verify(cuentaRepository, Mockito.times(3)).findById(2L);
		Mockito.verify(cuentaRepository, Mockito.times(2)).save(Mockito.any(Cuenta.class));

		Mockito.verify(bancoRepository, Mockito.times(2)).findById(1L);
		Mockito.verify(bancoRepository).save(Mockito.any(Banco.class));

		Mockito.verify(cuentaRepository, Mockito.times(6)).findById(Mockito.anyLong());
		Mockito.verify(cuentaRepository, Mockito.never()).findAll();
	}

	@Test
	@DisplayName("ContextLoads2()")
	void contextLoads2() {
		Mockito.when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		Mockito.when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		Mockito.when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco001());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		Assertions.assertEquals("1000", saldoOrigen.toPlainString());
		Assertions.assertEquals("2000", saldoDestino.toPlainString());

		Assertions.assertThrows(DineroInsuficienteException.class, () -> {
			cuentaService.transferir(1L, 2L, 1L, new BigDecimal("1200"));
		});

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		Assertions.assertEquals("1000", saldoOrigen.toPlainString());
		Assertions.assertEquals("2000", saldoDestino.toPlainString());

		int total = cuentaService.revisarTotalTransferencia(1L);

		Assertions.assertEquals(0, total);

		Mockito.verify(cuentaRepository, Mockito.times(3)).findById(1L);
		Mockito.verify(cuentaRepository, Mockito.times(2)).findById(2L);
		Mockito.verify(cuentaRepository, Mockito.never()).save(Mockito.any(Cuenta.class));

		Mockito.verify(bancoRepository, Mockito.times(1)).findById(1L);
		Mockito.verify(bancoRepository, Mockito.never()).save(Mockito.any(Banco.class));

		Mockito.verify(cuentaRepository, Mockito.times(5)).findById(Mockito.anyLong());
		Mockito.verify(cuentaRepository, Mockito.never()).findAll();
	}

	@Test
	@DisplayName("ContextLoads3()")
	void contextLoads3() {
		Mockito.when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());

		Cuenta cuenta1 = cuentaService.findById(1L);
		Cuenta cuenta2 = cuentaService.findById(1L);

		Assertions.assertSame(cuenta1, cuenta2);
		Assertions.assertTrue(cuenta1 == cuenta2);
		Assertions.assertEquals("Edward", cuenta1.getPersona());
		Assertions.assertEquals("Edward", cuenta2.getPersona());

		Mockito.verify(cuentaRepository, Mockito.times(2)).findById(1L);
	}

	@Test
	@DisplayName("TestFindAll()")
	void testFindAll() {
		//Given
		List<Cuenta> datos = Arrays.asList(Datos.crearCuenta001().orElseThrow(),
				Datos.crearCuenta002().orElseThrow());

		Mockito.when(cuentaRepository.findAll()).thenReturn(datos);

		//When
		List<Cuenta> cuentas = cuentaService.findAll();

		//Then
		Assertions.assertFalse(cuentas.isEmpty());
		Assertions.assertEquals(2, cuentas.size());
		Assertions.assertTrue(cuentas.contains(Datos.crearCuenta002().orElseThrow()));

		Mockito.verify(cuentaRepository).findAll();
	}

	@Test
	@DisplayName("TestSave()")
	void testSave() {
		// Given
		Cuenta cuentaSave= new Cuenta(null, "Laia", new BigDecimal("3000"));
		Mockito.when(cuentaRepository.save(Mockito.any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		//When
		Cuenta cuenta = cuentaService.save(cuentaSave);

		//Then
		Assertions.assertEquals("Laia", cuenta.getPersona());
		Assertions.assertEquals(3, cuenta.getId());
		Assertions.assertEquals("3000", cuenta.getSaldo().toPlainString());

		Mockito.verify(cuentaRepository).save(Mockito.any());
	}
}
