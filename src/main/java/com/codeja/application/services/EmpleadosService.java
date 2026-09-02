package com.codeja.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.codeja.application.ports.in.EmpleadoService;
import com.codeja.application.ports.out.EmpleadoRepository;
import com.codeja.domain.Empleado;


/*
 * ============================================================
 * SERVICIO DE APLICACIÓN
 * ============================================================
 *
 * Esta clase contiene la lógica de aplicación relacionada
 * con los empleados.
 *
 * Implementa el puerto de entrada EmpleadoService, que define
 * las operaciones que la aplicación ofrece al exterior.
 *
 * Para acceder a los datos utiliza el puerto de salida
 * EmpleadoRepository.
 *
 * El Service conoce el contrato del repositorio, pero no conoce
 * su implementación concreta.
 *
 * Por tanto, no sabe si los datos se obtienen mediante:
 *
 * - JPA
 * - Hibernate
 * - PostgreSQL
 * - otra base de datos
 * - una implementación en memoria
 * - etc.
 *
 * El Service tampoco conoce:
 *
 * - HTTP
 * - REST
 * - Thymeleaf
 *
 * De esta forma, la lógica de aplicación queda desacoplada
 * de las tecnologías utilizadas para entrar o salir de ella.
 *
 *
 * En la arquitectura hexagonal:
 *
 * Adaptador de entrada
 *          ↓
 * EmpleadoService
 *    (puerto de entrada)
 *          ↓
 * EmpleadosService
 *          ↓
 * EmpleadoRepository
 *    (puerto de salida)
 *          ↓
 * Adaptador de persistencia
 */
@Service
public class EmpleadosService implements EmpleadoService {


    /*
     * Puerto de salida que permite al Service solicitar
     * operaciones de persistencia.
     *
     * El Service trabaja con la interfaz EmpleadoRepository,
     * no directamente con EmpleadoRepositoryAdapter ni con JPA.
     */

    private final EmpleadoRepository empleadoRepository;


    /*
     * Inyección mediante constructor.
     *
     * Spring proporciona la implementación del puerto de salida.
     */

    public EmpleadosService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }


    /*
     * ============================================================
     * OBTENER TODOS LOS EMPLEADOS
     * ============================================================
     *
     * El Service solicita los empleados al puerto de salida.
     *
     * No sabe cómo se obtienen los datos.
     *
     * La implementación del puerto se encuentra en el adaptador
     * de persistencia.
     *
     * El resultado es una lista de objetos de dominio Empleado.
     */

    @Override
    public List<Empleado> getEmpleados() {

        return empleadoRepository.buscarTodos();
    }


    /*
     * ============================================================
     * OBTENER UN EMPLEADO POR ID
     * ============================================================
     *
     * El Service solicita al puerto de salida la búsqueda
     * del empleado indicado.
     *
     * El puerto devuelve Optional porque el empleado puede
     * existir o no existir.
     *
     * El Service devuelve ese Optional al adaptador de entrada.
     *
     * La decisión de convertir esta situación en una respuesta
     * HTTP (por ejemplo, 200 OK o 404 NOT FOUND) corresponde
     * al Controller, no al Service.
     */

    @Override
    public Optional<Empleado> getEmpleadoById(long idEmpleado) {

        return empleadoRepository.buscarPorId(idEmpleado);
    }


    /*
     * ============================================================
     * CREAR UN EMPLEADO
     * ============================================================
     *
     * El ID no lo proporciona el cliente.
     *
     * El Service crea un nuevo objeto de dominio sin ID.
     *
     * Después solicita al puerto de salida que lo guarde.
     *
     * El adaptador de persistencia será el encargado de convertir
     * el Empleado en la entidad que necesita JPA.
     */

    @Override
    public Empleado crearEmpleado(Empleado empleado) {

        Empleado nuevoEmpleado = new Empleado(
                null,
                empleado.nombre()
        );

        return empleadoRepository.guardar(nuevoEmpleado);
    }


    /*
     * ============================================================
     * MODIFICAR UN EMPLEADO
     * ============================================================
     *
     * Primero comprobamos si el empleado existe.
     *
     * Empleado es un record y, por tanto, es inmutable.
     *
     * Por eso no modificamos el objeto existente.
     * Creamos un nuevo Empleado conservando su ID y utilizando
     * el nuevo nombre recibido.
     *
     * Después solicitamos al puerto de salida que guarde
     * el empleado actualizado.
     *
     * Devuelve:
     *
     *     true  → si el empleado existía y se ha modificado.
     *     false → si el empleado no existía.
     */

    @Override
    public boolean modificarEmpleado(
            long idEmpleado,
            Empleado empleadoModificado) {

        Optional<Empleado> empleado =
                empleadoRepository.buscarPorId(idEmpleado);

        if (empleado.isEmpty()) {
            return false;
        }

        Empleado empleadoExistente = empleado.get();

        Empleado empleadoActualizado = new Empleado(
                empleadoExistente.id(),
                empleadoModificado.nombre()
        );

        empleadoRepository.guardar(empleadoActualizado);

        return true;
    }


    /*
     * ============================================================
     * ELIMINAR UN EMPLEADO
     * ============================================================
     *
     * Primero comprobamos si el empleado existe.
     *
     * Si no existe:
     *
     *     → devolvemos false.
     *
     * Si existe:
     *
     *     → solicitamos al puerto de salida que lo elimine.
     *     → devolvemos true.
     *
     * El Controller utiliza este resultado para decidir qué
     * respuesta HTTP debe devolver.
     *
     * Por ejemplo:
     *
     *     true  → HTTP 204 NO CONTENT
     *     false → HTTP 404 NOT FOUND
     */

    @Override
    public boolean eliminarEmpleado(long idEmpleado) {

        if (!empleadoRepository.existePorId(idEmpleado)) {
            return false;
        }

        empleadoRepository.eliminarPorId(idEmpleado);

        return true;
    }
}