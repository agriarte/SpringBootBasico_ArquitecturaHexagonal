package com.codeja.adapters.in.web;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codeja.application.ports.in.EmpleadoService;
import com.codeja.domain.Empleado;


/*
 * ============================================================
 * ADAPTADOR DE ENTRADA - INTERFAZ WEB
 * ============================================================
 *
 * Este Controller es un adaptador de entrada de la aplicación.
 *
 * Se utiliza para gestionar las peticiones HTTP relacionadas
 * con la interfaz web de la aplicación.
 *
 * A diferencia de @RestController, @Controller no devuelve
 * directamente objetos JSON.
 *
 * Sus métodos devuelven el nombre de una vista Thymeleaf.
 *
 * La ruta base será:
 *
 * /web/empleados
 *
 * Ejemplos:
 *
 * GET /web/empleados
 * GET /web/empleados/nuevo
 * GET /web/empleados/editar/3
 *
 * El Controller se encarga de:
 *
 * - Recibir la petición HTTP.
 * - Obtener los datos necesarios de la URL.
 * - Solicitar información al Service.
 * - Preparar el Model con los datos que necesita la vista.
 * - Indicar qué plantilla Thymeleaf debe mostrar.
 *
 * El Controller no contiene la lógica de negocio ni accede
 * directamente a la base de datos.
 *
 * Este Controller y EmpleadosControlador son dos adaptadores
 * de entrada diferentes que utilizan el mismo puerto de entrada
 * EmpleadoService.
 */
@Controller
@RequestMapping("/web/empleados")
public class EmpleadosWebController {


    /*
     * ============================================================
     * PUERTO DE ENTRADA
     * ============================================================
     *
     * El Controller depende del puerto de entrada
     * EmpleadoService.
     *
     * No depende directamente de la implementación
     * EmpleadosService.
     *
     * De esta forma, el Controller conoce únicamente las
     * operaciones que la aplicación ofrece al exterior.
     */

    private final EmpleadoService empleadosService;

    public EmpleadosWebController(EmpleadoService empleadosService) {
        this.empleadosService = empleadosService;
    }


    /*
     * ============================================================
     * MOSTRAR LISTA DE EMPLEADOS
     * ============================================================
     *
     * La ruta completa será:
     *
     * GET /web/empleados
     *
     * El Controller solicita la lista de empleados al Service.
     *
     * Después añade la lista al Model con el nombre
     * "listaEmpleados".
     *
     * El Model puede entenderse como un contenedor de datos
     * que se envía a la vista para que Thymeleaf pueda utilizarlos.
     *
     * En la plantilla se podrá acceder a esos datos mediante:
     *
     * ${listaEmpleados}
     *
     * return "empleados" indica que Thymeleaf debe cargar:
     *
     * templates/empleados.html
     */

    @GetMapping
    public String mostrarEmpleados(Model model) {

        model.addAttribute(
                "listaEmpleados",
                empleadosService.getEmpleados()
        );

        return "empleados";
    }


    /*
     * ============================================================
     * MOSTRAR FORMULARIO PARA CREAR UN EMPLEADO
     * ============================================================
     *
     * La ruta completa será:
     *
     * GET /web/empleados/nuevo
     *
     * Esta petición únicamente solicita mostrar el formulario.
     *
     * No necesita consultar ningún empleado.
     *
     * return "nuevo-empleado" indica que Thymeleaf debe cargar:
     *
     * templates/nuevo-empleado.html
     */

    @GetMapping("/nuevo")
    public String nuevoEmpleado() {

        return "nuevo-empleado";
    }


    /*
     * ============================================================
     * MOSTRAR FORMULARIO PARA MODIFICAR UN EMPLEADO
     * ============================================================
     *
     * La ruta completa será:
     *
     * GET /web/empleados/editar/{idEmpleado}
     *
     * Ejemplo:
     *
     * GET /web/empleados/editar/3
     *
     * @PathVariable obtiene el ID del empleado de la URL.
     *
     * El Controller solicita al Service que busque el empleado.
     *
     * El Service devuelve Optional<Empleado> porque el empleado
     * puede existir o no existir.
     *
     * Si existe:
     *
     *     → se añade el Empleado al Model.
     *     → se muestra la vista editar-empleado.html.
     *
     * Si no existe:
     *
     *     → se redirige a la lista de empleados.
     *
     * El Controller decide qué vista o redirección HTTP realizar.
     */

    @GetMapping("/editar/{idEmpleado}")
    public String mostrarFormularioEdicion(
            @PathVariable long idEmpleado,
            Model model) {

        Optional<Empleado> empleado =
                empleadosService.getEmpleadoById(idEmpleado);

        if (empleado.isEmpty()) {

            return "redirect:/web/empleados";
        }

        model.addAttribute(
                "empleado",
                empleado.get()
        );

        return "editar-empleado";
    }
}