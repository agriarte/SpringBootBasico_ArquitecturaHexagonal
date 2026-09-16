package com.codeja.adapters.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.codeja.application.ports.out.EmpleadoRepository;
import com.codeja.domain.Empleado;

@Repository

public class EmpleadoRepositoryMemoria implements EmpleadoRepository {

    private final List<Empleado> empleados = new ArrayList<>();

    @Override
    public List<Empleado> buscarTodos() {
        return empleados;
    }

    @Override
    public Optional<Empleado> buscarPorId(Long id) {
        return empleados.stream()
                .filter(e -> e.id().equals(id))
                .findFirst();
    }

    @Override
    public Empleado guardar(Empleado empleado) {
        empleados.add(empleado);
        return empleado;
    }

    @Override
    public boolean existePorId(Long id) {
        return empleados.stream()
                .anyMatch(e -> e.id().equals(id));
    }

    @Override
    public void eliminarPorId(Long id) {
        empleados.removeIf(e -> e.id().equals(id));
    }
}