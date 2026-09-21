package com.codeja.adapters.in.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    /*
     * Devuelve el token CSRF asociado a la sesión actual.
     *
     * Los clientes que necesiten realizar POST, PUT o DELETE
     * pueden obtener primero el token mediante:
     *
     * GET /csrf
     */
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }
}