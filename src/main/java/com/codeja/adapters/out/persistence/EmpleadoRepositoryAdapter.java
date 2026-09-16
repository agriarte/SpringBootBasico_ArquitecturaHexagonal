package com.codeja.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.codeja.application.ports.out.EmpleadoRepository;
import com.codeja.domain.Empleado;


/*
 * ============================================================
 * ADAPTADOR DE SALIDA - PERSISTENCIA
 * ============================================================
 *
 * Esta clase es el adaptador que conecta la aplicación con
 * la base de datos mediante Spring Data JPA.
 *
 * Implementa el puerto de salida EmpleadoRepository.
 *
 * El puerto define QUÉ operaciones necesita la aplicación.
 *
 * Este adaptador define CÓMO se realizan esas operaciones
 * utilizando EmpleadoJpaRepository.
 *
 * El adaptador también se encarga de convertir entre los dos
 * modelos utilizados en la aplicación:
 *
 *     Empleado
 *     → objeto de dominio.
 *
 *     EmpleadoEntity
 *     → entidad utilizada por JPA para la persistencia.
 *
 *
 * El resto de la aplicación no conoce directamente:
 *
 * - JPA
 * - Hibernate
 * - PostgreSQL
 *
 * La comunicación queda de esta forma:
 *
 *
 * EmpleadosService
 *        ↓
 * EmpleadoRepository
 *    (puerto de salida)
 *        ↓
 * EmpleadoRepositoryAdapter
 *        ↓
 * EmpleadoJpaRepository
 *        ↓
 *    JPA / Hibernate
 *        ↓
 *    PostgreSQL
 */

@Repository
@Primary
public class EmpleadoRepositoryAdapter implements EmpleadoRepository {


    /*
     * Repositorio proporcionado por Spring Data JPA.
     *
     * Esta interfaz permite realizar las operaciones sobre
     * EmpleadoEntity utilizando métodos como:
     *
     * findAll()
     * findById()
     * save()
     * existsById()
     * deleteById()
     *
     * El Adapter es quien utiliza directamente este repositorio.
     */

    private final EmpleadoJpaRepository repository;


    /*
     * Inyección mediante constructor.
     *
     * Spring proporciona automáticamente la implementación
     * de EmpleadoJpaRepository.
     */

    public EmpleadoRepositoryAdapter(
            EmpleadoJpaRepository repository) {

        this.repository = repository;
    }


    /*
     * ============================================================
     * BUSCAR TODOS LOS EMPLEADOS
     * ============================================================
     *
     * El puerto de salida define:
     *
     *     buscarTodos()
     *
     * Aquí implementamos esa operación utilizando JPA.
     *
     * repository.findAll() devuelve una lista de EmpleadoEntity.
     *
     * Después convertimos cada EmpleadoEntity en un Empleado
     * del dominio.
     *
     * De esta forma, la entidad JPA no sale de este adaptador.
     */

    @Override
    public List<Empleado> buscarTodos() {

        return repository.findAll()
                .stream()
                .map(entity -> new Empleado(
                        entity.getId(),
                        entity.getNombre()))
                .toList();
    }


    /*
     * ============================================================
     * BUSCAR UN EMPLEADO POR ID
     * ============================================================
     *
     * El puerto define:
     *
     *     buscarPorId(Long id)
     *
     * repository.findById() devuelve:
     *
     *     Optional<EmpleadoEntity>
     *
     * El Adapter convierte ese resultado en:
     *
     *     Optional<Empleado>
     *
     * Si el empleado existe:
     *
     *     EmpleadoEntity → Empleado
     *
     * Si no existe:
     *
     *     Optional permanece vacío.
     *
     * El Optional no se convierte aquí en una respuesta HTTP.
     * Esa decisión corresponde al Controller.
     */

    @Override
    public Optional<Empleado> buscarPorId(Long id) {

        return repository.findById(id)
                .map(entity -> new Empleado(
                        entity.getId(),
                        entity.getNombre()
                ));
    }


    /*
     * ============================================================
     * GUARDAR UN EMPLEADO
     * ============================================================
     *
     * El puerto define:
     *
     *     guardar(Empleado empleado)
     *
     * El Service trabaja con un objeto de dominio Empleado,
     * pero JPA necesita una EmpleadoEntity.
     *
     * Por eso el Adapter realiza la conversión:
     *
     *     Empleado
     *        ↓
     * EmpleadoEntity
     *        ↓
     * repository.save()
     *
     * Después de guardar, JPA devuelve la entidad guardada.
     *
     * Esto es especialmente importante al crear un empleado,
     * porque la base de datos genera el ID.
     *
     * Finalmente convertimos otra vez:
     *
     *     EmpleadoEntity
     *        ↓
     *     Empleado
     *
     * De esta forma, el objeto que sale del Adapter vuelve a
     * ser un objeto de dominio.
     */

    @Override
    public Empleado guardar(Empleado empleado) {

        EmpleadoEntity entity = new EmpleadoEntity(
                empleado.id(),
                empleado.nombre()
        );

        EmpleadoEntity entityGuardada =
                repository.save(entity);

        return new Empleado(
                entityGuardada.getId(),
                entityGuardada.getNombre()
        );
    }


    /*
     * ============================================================
     * COMPROBAR SI EXISTE UN EMPLEADO
     * ============================================================
     *
     * El puerto define:
     *
     *     existePorId(Long id)
     *
     * Aquí utilizamos directamente el método equivalente
     * proporcionado por Spring Data JPA:
     *
     *     existsById()
     *
     * El Adapter traduce la operación de nuestro puerto a
     * la operación concreta que proporciona JPA.
     */

    @Override
    public boolean existePorId(Long id) {

        return repository.existsById(id);
    }


    /*
     * ============================================================
     * ELIMINAR UN EMPLEADO
     * ============================================================
     *
     * El puerto define:
     *
     *     eliminarPorId(Long id)
     *
     * El Adapter utiliza el método deleteById() de
     * Spring Data JPA.
     *
     * El Controller y el Service no necesitan saber que
     * internamente se está utilizando deleteById().
     */

    @Override
    public void eliminarPorId(Long id) {

        repository.deleteById(id);
    }
}