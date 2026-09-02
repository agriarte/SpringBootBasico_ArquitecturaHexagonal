package com.codeja.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;


/*
 * ============================================================
 * REPOSITORIO JPA
 * ============================================================
 *
 * Esta interfaz permite acceder a la base de datos utilizando
 * Spring Data JPA.
 *
 * Extiende JpaRepository, que proporciona automáticamente
 * operaciones habituales como:
 *
 * - findAll()
 * - findById()
 * - save()
 * - existsById()
 * - deleteById()
 *
 * JpaRepository trabaja directamente con EmpleadoEntity,
 * que es la entidad de persistencia.
 *
 * Esta interfaz pertenece al adaptador de salida de persistencia.
 *
 * El resto de la aplicación no utiliza directamente esta
 * interfaz.
 *
 * EmpleadoRepositoryAdapter es quien la utiliza y adapta
 * sus operaciones al puerto de salida EmpleadoRepository.
 *
 *
 * EmpleadoRepositoryAdapter
 *          ↓
 * EmpleadoJpaRepository
 *          ↓
 *     JPA / Hibernate
 *          ↓
 *      PostgreSQL
 */

public interface EmpleadoJpaRepository
        extends JpaRepository<EmpleadoEntity, Long> {
}
