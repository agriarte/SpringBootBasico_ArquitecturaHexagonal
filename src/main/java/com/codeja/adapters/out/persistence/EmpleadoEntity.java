package com.codeja.adapters.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


/*
 * ============================================================
 * ENTIDAD DE PERSISTENCIA - EmpleadoEntity
 * ============================================================
 *
 * Esta clase representa la entidad que se utiliza para
 * almacenar los empleados en la base de datos.
 *
 * Pertenece al adaptador de salida de persistencia.
 *
 * No es el objeto de dominio Empleado.
 *
 * En la arquitectura hexagonal tenemos dos modelos diferentes:
 *
 *     Empleado
 *     → objeto de dominio utilizado por la aplicación.
 *
 *     EmpleadoEntity
 *     → entidad utilizada por JPA para trabajar con la
 *       base de datos.
 *
 * El EmpleadoRepositoryAdapter se encarga de convertir
 * entre ambos modelos.
 *
 * Empleado
 *     ↕
 * RepositoryAdapter
 *     ↕
 * EmpleadoEntity
 *     ↕
 * JPA / Hibernate
 *     ↕
 * Base de datos
 */

@Entity
public class EmpleadoEntity {


    /*
     * ============================================================
     * IDENTIFICADOR
     * ============================================================
     *
     * @Id indica que este campo es la clave primaria de la
     * entidad y, por tanto, de la tabla correspondiente.
     */

    @Id

    /*
     * El valor del ID será generado automáticamente.
     *
     * GenerationType.IDENTITY indica que la generación del
     * identificador se delega en la base de datos.
     *
     * En PostgreSQL, al insertar un nuevo empleado, la base
     * de datos genera el ID.
     */

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * Campo que contiene el nombre del empleado.
     *
     * JPA utilizará este atributo para almacenar el nombre
     * en la columna correspondiente de la tabla.
     */

    private String nombre;


    /*
     * ============================================================
     * CONSTRUCTOR VACÍO
     * ============================================================
     *
     * JPA necesita un constructor sin argumentos para poder
     * crear instancias de esta entidad.
     *
     * No es el constructor que utilizamos normalmente para
     * crear un empleado desde nuestra aplicación.
     */

    public EmpleadoEntity() {
    }


    /*
     * ============================================================
     * CONSTRUCTOR CON DATOS
     * ============================================================
     *
     * Permite crear una EmpleadoEntity indicando su ID y nombre.
     *
     * El RepositoryAdapter utiliza este constructor cuando
     * convierte un objeto de dominio Empleado en una entidad
     * que JPA puede guardar.
     *
     * Ejemplo:
     *
     * Empleado
     *     ↓
     * new EmpleadoEntity(empleado.id(), empleado.nombre())
     *     ↓
     * JPA
     *     ↓
     * Base de datos
     */

    public EmpleadoEntity(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }


    /*
     * ============================================================
     * GETTERS Y SETTERS
     * ============================================================
     *
     * JPA utiliza estos métodos para acceder y modificar
     * los valores de la entidad.
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}