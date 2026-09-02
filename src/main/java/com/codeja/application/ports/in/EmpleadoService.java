package com.codeja.application.ports.in;

import java.util.List;
import java.util.Optional;

import com.codeja.domain.Empleado;


/*
 * ============================================================
 * PUERTO DE ENTRADA
 * ============================================================
 *
 * Esta interfaz Java representa el puerto de entrada de la
 * aplicación para las operaciones relacionadas con empleados.
 *
 * En arquitectura hexagonal, un "puerto" no es un elemento
 * especial de Java: es el papel arquitectónico que desempeña
 * una interfaz.
 *
 * Define las operaciones que la aplicación ofrece al exterior.
 *
 * Los adaptadores de entrada utilizan este contrato para
 * comunicarse con la aplicación.
 *
 * En este proyecto tenemos dos adaptadores de entrada:
 *
 * - EmpleadosControlador
 *      → REST / JSON / Postman
 *
 * - EmpleadosWebController
 *      → interfaz web / Thymeleaf
 *
 * Ambos utilizan este mismo puerto de entrada.
 *
 * EmpleadosService es la implementación de este puerto.
 *
 * El puerto no conoce:
 *
 * - HTTP
 * - REST
 * - Thymeleaf
 * - JPA
 * - Hibernate
 * - PostgreSQL
 *
 * De esta forma, la capa de aplicación queda desacoplada
 * de las tecnologías utilizadas para entrar en ella.
 */

public interface EmpleadoService {


    /*
     * Obtener todos los empleados.
     */

    List<Empleado> getEmpleados();


    /*
     * Obtener un empleado por su ID.
     *
     * Se utiliza Optional porque el empleado puede existir
     * o no existir.
     */

    Optional<Empleado> getEmpleadoById(long idEmpleado);


    /*
     * Crear un nuevo empleado.
     */

    Empleado crearEmpleado(Empleado empleado);


    /*
     * Modificar un empleado existente.
     */

    boolean modificarEmpleado(
            long idEmpleado,
            Empleado empleadoModificado);


    /*
	 * Eliminar un empleado.
	 *
	 * Devuelve true si se ha eliminado y false si no existía.
	 */

	boolean eliminarEmpleado(long idEmpleado);
}