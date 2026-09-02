package com.codeja.adapters.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeja.application.ports.in.EmpleadoService;
import com.codeja.domain.Empleado;


/*
 * ============================================================
 * ADAPTADOR DE ENTRADA - REST
 * ============================================================
 *
 * Este Controller es un adaptador de entrada de la aplicación.
 *
 * Su función es recibir peticiones HTTP y comunicarse con el
 * puerto de entrada EmpleadoService.
 *
 * En este proyecto se utiliza principalmente para comprobar
 * y probar la API mediante URLs, navegador o Postman.
 *
 * Ejemplos:
 *
 * GET    /empleados
 * GET    /empleados/3
 * POST   /empleados
 * PUT    /empleados/3
 * DELETE /empleados/3
 *
 * El Controller se encarga de:
 *
 * - Recibir la petición HTTP.
 * - Obtener los datos de la URL o del cuerpo de la petición.
 * - Llamar al Service.
 * - Convertir el resultado en una respuesta HTTP.
 *
 * El Controller no contiene la lógica de negocio ni accede
 * directamente a la base de datos.
 *
 * En arquitectura hexagonal, este Controller es un
 * ADAPTADOR DE ENTRADA.
 */
@RestController
@RequestMapping("/empleados")
public class EmpleadosControlador {


    // ============================================================
    // TEST
    // ============================================================
    //
    // Endpoint utilizado para comprobar rápidamente que el
    // Controller funciona y que Spring está respondiendo.
    //
    // La ruta completa será:
    //
    // GET /empleados/test
    //
    // Spring convierte automáticamente el objeto Empleado
    // en JSON.
    
    @GetMapping("/test")
    public Empleado getNuevoEmpleado() {

        return new Empleado(
                null,
                "Pedro"
        );
    }


    // ============================================================
    // INYECCIÓN DEL PUERTO DE ENTRADA
    // ============================================================
    //
    // El Controller depende del puerto de entrada
    // EmpleadoService, no directamente de la clase
    // EmpleadosService.
    //
    // EmpleadoService define las operaciones que la aplicación
    // ofrece al exterior.
    //
    // EmpleadosService es la implementación concreta de ese
    // puerto, pero el Controller no necesita conocerla.
    
    private final EmpleadoService empleadosService;

    public EmpleadosControlador(EmpleadoService empleadosService) {
        this.empleadosService = empleadosService;
    }


    /*
     * ============================================================
     * OBTENER TODOS LOS EMPLEADOS
     * ============================================================
     *
     * La ruta completa será:
     *
     * GET /empleados
     *
     * @GetMapping sin una ruta adicional utiliza la ruta base
     * definida en @RequestMapping("/empleados").
     *
     * El Controller solicita al Service la lista de empleados.
     *
     * El Controller no accede directamente al repositorio
     * ni a la base de datos.
     *
     * No necesitamos Optional porque estamos solicitando una
     * lista completa.
     *
     * Si existen empleados:
     *     → devuelve la lista.
     *
     * Si no existen empleados:
     *     → devuelve una lista vacía [].
     *
     * Spring convierte automáticamente List<Empleado>
     * en un array JSON.
     *
     * Ejemplo:
     *
     * GET /empleados
     *
     * Respuesta:
     *
     * [
     *     {
     *         "id": 1,
     *         "nombre": "Juan"
     *     },
     *     {
     *         "id": 2,
     *         "nombre": "María"
     *     }
     * ]
     */

    @GetMapping
    public List<Empleado> getLista() {

        return empleadosService.getEmpleados();
    }


    /*
     * ============================================================
     * OBTENER UN EMPLEADO POR ID
     * ============================================================
     *
     * La ruta completa será:
     *
     * GET /empleados/{idEmpleado}
     *
     * @PathVariable recoge el valor de {idEmpleado} de la URL.
     *
     * Ejemplo:
     *
     * GET /empleados/3
     *
     * El Controller solicita al Service el empleado indicado.
     *
     * El Service devuelve Optional<Empleado> porque el empleado
     * puede existir o no existir.
     *
     * Si el Optional contiene un empleado:
     *     → map() lo transforma en ResponseEntity<Empleado>.
     *     → devuelve HTTP 200 OK.
     *
     * Si el Optional está vacío:
     *     → devuelve HTTP 404 NOT FOUND.
     *
     * El Optional se utiliza como resultado intermedio.
     *
     * El tipo que finalmente devuelve el método es:
     *
     * ResponseEntity<Empleado>
     */

    @GetMapping("/{idEmpleado}")
    public ResponseEntity<Empleado> empleadoByID(
            @PathVariable long idEmpleado) {

        return empleadosService.getEmpleadoById(idEmpleado)

                // Si existe el empleado, devuelve HTTP 200 OK.
                .map(ResponseEntity::ok)

                // Si no existe, devuelve HTTP 404 NOT FOUND.
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /*
     * ============================================================
     * CREAR UN NUEVO EMPLEADO
     * ============================================================
     *
     * La ruta completa será:
     *
     * POST /empleados
     *
     * @PostMapping sin una ruta adicional utiliza la ruta base
     * definida en @RequestMapping("/empleados").
     *
     * @RequestBody recibe los datos enviados en el cuerpo de
     * la petición HTTP.
     *
     * Spring convierte automáticamente el JSON recibido en
     * un objeto Empleado.
     *
     * Ejemplo de petición desde Postman:
     *
     * POST /empleados
     *
     * {
     *     "nombre": "Pedro"
     * }
     *
     * El Controller entrega el Empleado al Service.
     *
     * El Service se encarga de crear el nuevo objeto de dominio
     * y solicitar al puerto de salida que lo guarde.
     *
     * El Controller no accede directamente a JPA ni a la base
     * de datos.
     *
     * ResponseEntity permite indicar explícitamente el estado
     * HTTP de la respuesta.
     *
     * HTTP 201 CREATED indica que se ha creado correctamente
     * un nuevo recurso.
     */

    @PostMapping
    public ResponseEntity<Empleado> crearEmpleado(
            @RequestBody Empleado empleado) {

        Empleado nuevoEmpleado =
                empleadosService.crearEmpleado(empleado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoEmpleado);
    }


    /*
     * ============================================================
     * ELIMINAR UN EMPLEADO
     * ============================================================
     *
     * La ruta completa será:
     *
     * DELETE /empleados/{idEmpleado}
     *
     * @PathVariable recoge el valor de {idEmpleado} de la URL.
     *
     * Ejemplo:
     *
     * DELETE /empleados/3
     *
     * El Controller solicita al Service que elimine el empleado.
     *
     * El Service devuelve:
     *
     *     true  → si el empleado existía y se eliminó.
     *     false → si el empleado no existía.
     *
     * El Controller utiliza ese resultado para decidir qué
     * respuesta HTTP devolver.
     *
     * Si se elimina correctamente:
     *
     *     → HTTP 204 NO CONTENT
     *
     * Si no existe:
     *
     *     → HTTP 404 NOT FOUND
     *
     * El Controller se ocupa de la respuesta HTTP.
     * La forma concreta de eliminar el empleado pertenece
     * al Service y al adaptador de persistencia.
     */

    @DeleteMapping("/{idEmpleado}")
    public ResponseEntity<Void> eliminarEmpleado(
            @PathVariable long idEmpleado) {

        boolean eliminado =
                empleadosService.eliminarEmpleado(idEmpleado);

        if (eliminado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }


    /*
     * ============================================================
     * MODIFICAR UN EMPLEADO
     * ============================================================
     *
     * La ruta completa será:
     *
     * PUT /empleados/{idEmpleado}
     *
     * @PathVariable recoge el ID del empleado que queremos
     * modificar.
     *
     * @RequestBody recibe los nuevos datos del empleado.
     *
     * Ejemplo de petición desde Postman:
     *
     * PUT /empleados/3
     *
     * {
     *     "nombre": "José García"
     * }
     *
     * El Controller entrega al Service:
     *
     * - el ID del empleado que queremos modificar.
     * - los nuevos datos recibidos en el cuerpo.
     *
     * El Service se encarga de realizar la modificación.
     *
     * El Service devuelve:
     *
     *     true  → si el empleado existe y se modifica.
     *     false → si el empleado no existe.
     *
     * Si existe:
     *
     *     → HTTP 200 OK
     *
     * Si no existe:
     *
     *     → HTTP 404 NOT FOUND
     */

    @PutMapping("/{idEmpleado}")
    public ResponseEntity<Empleado> modificarEmpleado(
            @PathVariable long idEmpleado,
            @RequestBody Empleado empleadoModificado) {

        if (empleadosService.modificarEmpleado(
                idEmpleado, empleadoModificado)) {

            return ResponseEntity.ok(empleadoModificado);
        }

        return ResponseEntity.notFound().build();
    }
}