package com.codeja.application.ports.out;

import java.util.List;
import java.util.Optional;

import com.codeja.domain.Empleado;

public interface EmpleadoRepository {

	List<Empleado> buscarTodos();

    Optional<Empleado> buscarPorId(Long id);

    Empleado guardar(Empleado empleado);

    boolean existePorId(Long id);

    void eliminarPorId(Long id);
}