package com.codeja.domain;


/*
 * ============================================================
 * OBJETO DE DOMINIO - Empleado
 * ============================================================
 *
 * Este record representa un empleado dentro del dominio
 * de la aplicación.
 *
 * Es el modelo que utiliza la lógica de la aplicación y
 * no depende de ninguna tecnología de persistencia.
 *
 * No contiene anotaciones de JPA ni depende de:
 *
 * - Spring
 * - JPA
 * - Hibernate
 * - PostgreSQL
 *
 * Es diferente de EmpleadoEntity, que pertenece al adaptador
 * de persistencia y se utiliza para trabajar con la base de datos.
 *
 *
 * Empleado
 *     → modelo de dominio
 *
 * EmpleadoEntity
 *     → modelo de persistencia
 *
 * El EmpleadoRepositoryAdapter se encarga de convertir
 * entre ambos modelos.
 *
 *
 * Al ser un record, Empleado es inmutable:
 *
 * una vez creado, sus valores no pueden modificarse.
 */

public record Empleado(
        Long id,
        String nombre) {
}