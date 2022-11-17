package org.ecordovc.cursotest.example.models;

import jdk.jfr.Enabled;
import org.ecordovc.cursotest.example.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // con estos los metodos after y before All ya no necesitan ser static
class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;

    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){ // testinfo y testreporter pueden estar dentro de cualquier clase
        this.cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("Iniciando el Metodo.");
        System.out.println("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() +
                " con las etiquetas " + testInfo.getTags()); // esto puede estar en cada metodo
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() +
                " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown(){
        System.out.println("Finalizando el Metodo de Prueba.");
    }

    @BeforeAll
    static void beforeAll(){
        System.out.println("Inicializando la clase Test.");
    }
    @AfterAll
    static void afterAll(){
        System.out.println("Finalizando el Test.");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("CuentaTestNombreSaldo")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("TestNombreCuenta()")
        void testNombreCuenta(){
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("Tiene tag cuenta");
            }

            //afirmar valor esperado y expectativa
            String cadEsperada = "Edward";
            String cadReal = cuenta.getPersona();
            //esos mensajes saldran cuando el assert salga con error
//        assertNotNull(cadReal, "La cuenta no puede ser nula");
            assertNotNull(cadReal, () -> "La cuenta no puede ser nula");

            //Assertions.assertEquals
            //los asserts sirven para comparar valores esperados y reales
            assertEquals(cadEsperada, cadReal, () -> "El nombre de la cuenta no es el que se esperaba: se esperaba " + cadEsperada
                    + " sim embargo fue: " + cadReal);
            assertTrue(cadReal.equals(cadEsperada), () -> "Nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("TestSaldoCuenta()")
        void testSaldoCuenta(){
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());

            //me dice si el valor cumple con la condicion y espera false
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("TestReferenciaCuenta()")
        void testReferenciaCuenta() {

            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("8900.9997")); // es como un valor real
            Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("8900.9997")); // valor esperado

            //assertNotEquals(cuenta2, cuenta1);
            assertEquals(cuenta2, cuenta1);
        }
    }

    @Nested
    @DisplayName("CuentaOperacionesTest")
    class CuentaOperacionesTest{
        @Test
        @Tag("cuenta")
        @DisplayName("TestDebitoCuenta()")
        void testDebitoCuenta() {
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        @Tag("cuenta")
        @DisplayName("TestCreditoCuenta()")
        void testCreditoCuenta() {
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        @Tag("cuenta")
        @Tag("banco")
        @DisplayName("TestTransferirDineroCuenta()")
        void testTransferirDineroCuenta() {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Edward", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("BBVA");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }
    }

    @Test
    @Tag("cuenta")
    @Tag("error")
    @DisplayName("TestDineroInsuficienteExceptionCuenta()")
    void testDineroInsuficienteExceptionCuenta() {
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> { // primero va la clase que quiero devolver y luego va el lambda con la logica o metodo
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    @Tag("cuenta")
    @Tag("banco")
    @DisplayName("TestRelacionBancoCuentas()")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Edward", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("BBVA");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        //el assertAll ejecuta todos lso codigos con o sin errores
        assertAll( //cuando es una linea se puede omitir las llaves
                () -> {assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());},
                () -> {assertEquals("3000", cuenta1.getSaldo().toPlainString());},
                () -> {assertEquals(2, banco.getCuentas().size());},
                () -> {assertEquals("BBVA", cuenta1.getBanco().getNombre());},
                () -> {assertEquals("Edward", banco.getCuentas()
                        .stream()
                        .filter(nom -> nom.getPersona().equals("Edward"))
                        .findFirst()
                        .get()
                        .getPersona());},
                () -> {assertTrue(banco.getCuentas()
                        .stream()
                        .anyMatch(nom -> nom.getPersona().equals("Edward")));}
        );

//        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
//        assertEquals("3000", cuenta1.getSaldo().toPlainString());
//        assertEquals(2, banco.getCuentas().size());
//        assertEquals("BBVA", cuenta1.getBanco().getNombre());
//        assertEquals("Edward", banco.getCuentas()
//                .stream()
//                .filter(nom -> nom.getPersona().equals("Edward"))
//                .findFirst()
//                .get()
//                .getPersona());
//
//        assertTrue(banco.getCuentas()
//                .stream()
//                .anyMatch(nom -> nom.getPersona().equals("Edward")));
    }

    @Nested
    @DisplayName("SistemaOperativoTest")
    class SistemaOperativoTest{
        @Test
        @DisplayName("TestSoloWindows()")
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows(){

        }

        @Test
        @DisplayName("TestSoloLinuxMac()")
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac(){

        }

        @Test
        @DisplayName("TestNoWindows()")
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows(){

        }

        @Test
        @DisplayName("TestNoLinuxMac()")
        @DisabledOnOs({OS.LINUX, OS.MAC})
        void testNoLinuxMac(){

        }
    }

    @Nested
    @DisplayName("JavaVersionTest")
    class JavaVersionTest{
        @Test
        @DisplayName("TestSoloJdk8()")
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJdk8(){

        }

        @Test
        @DisplayName("TestSoloJdk11()")
        @EnabledOnJre(JRE.JAVA_11)
        void testSoloJdk11(){

        }

        @Test
        @DisplayName("TestNoJdk8()")
        @DisabledOnJre(JRE.JAVA_8)
        void testNoJdk8(){

        }

        @Test
        @DisplayName("TestNoJdk11()")
        @DisabledOnJre(JRE.JAVA_11)
        void testNoJdk11(){

        }
    }

    @Nested
    @DisplayName("SystemPropertiesTest")
    class SystemPropertiesTest{
        @Test
        @DisplayName("ImprimirSystemProperties()")
        void imprimirSystemProperties(){
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "11.0.16.1")
        @DisplayName("TestJavaVersion()")
        void testJavaVersion(){

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = "amd32")
        @DisplayName("TestSolo64()")
        void testSolo64(){

        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = "amd32")
        @DisplayName("TestNoSolo64()")
        void testNoSolo64(){

        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "educc0r")
        @DisplayName("TestUsername()")
        void testUsername(){

        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev") //como no existe esta deshabilitada
        // para configurar en configuraciones  -ea -DENV=dev
        // -D significa que configuraremos una system properties
        @DisplayName("TestDev()")
        void testDev(){

        }
    }

    @Nested
    @DisplayName("VariablesAmbienteTest")
    class VariablesAmbienteTest{
        @Test
        @DisplayName("ImprimirVariablesAmbiente()")
        void imprimirVariablesAmbiente(){
            Map<String, String> getEnv =  System.getenv();
            getEnv.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "WINDOWPATH", matches = "2")
        @DisplayName("TestWindowPath()")
        void testWindowPath(){

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "DESKTOP_SESSION", matches = "zorin")
        @DisplayName("TestDesktopSession()")
        void testDesktopSession(){

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        @DisplayName("TestEnv()")
        void testEnv(){

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        @DisplayName("TestEnvProd()")
        void testEnvProd(){

        }
    }

    @Test
    @DisplayName("TestSaldoCuentaDev()")
    void testSaldoCuentaDev(){
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
        boolean esDev = "dev".equals(System.getProperty("ENV"));

        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());

        //me dice si el valor cumple con la condicion y espera false
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("TestSaldoCuentaDev2()")
    void testSaldoCuentaDev2(){
//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
        boolean esDev = "dev".equals(System.getProperty("ENV"));

        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());

            //me dice si el valor cumple con la condicion y espera false
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    // se forma como un nodo y dentro estan las pruebas con la cantidad dada
    @RepeatedTest(value = 5, name = "{displayName} - Repetición numero {currentRepetition} de {totalRepetitions}")
    @DisplayName("TestDebitoCuentaRepeated()")
    void testDebitoCuentaRepeated(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 3){
            System.out.println("Estamos en la repetición " + info.getCurrentRepetition());
        }

//        Cuenta cuenta = new Cuenta("Edward", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param") // si se pone a la clase es como ponerle uno por uno a las clases dentro de la clase
    // configuraciones edit configurations cambiar class por tags
    @Nested
    @DisplayName("PruebasParametrizadasTest")
    class PruebasParametrizadasTest{
        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
//    @ValueSource(ints = {100, 200, 300, 500, 700, 1000})
        @DisplayName("TestDebitoCuentaValueSource()")
        void testDebitoCuentaValueSource(String monto) { // int monto
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000"})
        @DisplayName("TestDebitoCuentaCsvSource()")
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,Jhon,Andres", "250,200,maria,Maria", "300,300,Pepe,Pepe", "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000,1000,Edward,Edward"})
        @DisplayName("TestDebitoCuentaCsvSource2()")
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getPersona());
            assertNotNull(cuenta.getSaldo());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("TestDebitoCuentaCsvFileSource()")
        void testDebitoCuentaCsvFileSource(String monto) {
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        @DisplayName("TestDebitoCuentaCsvFileSource2()")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getPersona());
            assertNotNull(cuenta.getSaldo());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    @DisplayName("TestDebitoCuentaMethodSource()")
    void testDebitoCuentaMethodSource(String monto) {
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    private static List<String> montoList(){
        return Arrays.asList("100", "200", "300", "500", "700", "1000");
    }

    @Nested
    @Tag("timeout")
    @DisplayName("EjemploTimeOutTest")
    class EjemploTimeOutTest{
        @Test
        @Timeout(1)
        @DisplayName("TestPruebaTimeOut()")
        void testPruebaTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS) // por defecto el unit es segundos
        @DisplayName("TestPruebaTimeOut2()")
        void testPruebaTimeOut2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        @Test
        @DisplayName("TestTimeOutAssertions()")
        void testTimeOutAssertions(){
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(500);
            });
        }
    }

    @Test
    @Disabled
    @DisplayName("TestPruebaFile()")
    void testPruebaFile(){
        fail(); // esto hace que automaticamente falle al entrar al metodo
        Base64 base64 = new Base64();
        File file = new File("src/test/resources/test.txt");

//        long bytes = file.length();
//
//        long kilobytes = (bytes / 1024);
//        long megabytes = (kilobytes / 1024);
//
//        System.out.println(megabytes);

        byte[] fileArray = new byte[(int) file.length()];
        InputStream inputStream;
        String encodedFile = "";



        try {
            inputStream = new FileInputStream(file);
            inputStream.read(fileArray);
            encodedFile = base64.encodeToString(fileArray);
        } catch (Exception e) {
            // Manejar Error
        }
        System.out.println(encodedFile);
    }
}