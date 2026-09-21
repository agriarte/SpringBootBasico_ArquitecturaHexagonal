package com.codeja.adapters.in.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /*
     * ==============================================================
     * CADENA DE FILTROS DE SEGURIDAD
     * ==============================================================
     *
     * SecurityFilterChain define cómo Spring Security debe
     * proteger las peticiones HTTP de nuestra aplicación.
     *
     * Dentro de este método configuramos:
     *
     * - Autorización (quién puede acceder a cada URL).
     * - Protección CSRF.
     * - Autenticación por formulario de login (navegador).
     * - Autenticación Basic Auth (Postman y clientes REST).
     * - Logout.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

            /*
             * ==========================================================
             * AUTORIZACIÓN DE LAS PETICIONES
             * ==========================================================
             *
             * authorizeHttpRequests() permite definir quién puede
             * acceder a cada URL y mediante qué método HTTP.
             *
             * Las reglas se evalúan EN ORDEN y se aplica la primera que
             * coincide. Por eso anyRequest() debe ir siempre la última.
             *
             * authenticated()
             * ----------------
             * El usuario debe estar autenticado, ya sea mediante el
             * formulario de login o mediante Basic Auth.
             *
             * hasRole("ADMIN")
             * ----------------
             * El usuario debe estar autenticado y tener la autoridad
             * ROLE_ADMIN.
             *
             * .roles("ADMIN") en UserDetails crea internamente
             * la autoridad ROLE_ADMIN.
             */
            .authorizeHttpRequests(authorizeRequests -> {

                /*
                 * ======================================================
                 * TOKEN CSRF
                 * ======================================================
                 *
                 * GET /csrf permite a los clientes obtener el token
                 * CSRF que deberán enviar posteriormente en las
                 * peticiones POST, PUT o DELETE.
                 *
                 * Este endpoint está definido en la propia aplicación
                 * (Spring Security no lo proporciona por defecto).
                 *
                 * Esta URL debe estar permitida porque el cliente
                 * necesita poder obtener el token antes de utilizarlo.
                 */
                authorizeRequests
                    .requestMatchers("/csrf")
                    .permitAll();


                /*
                 * ======================================================
                 * CONSULTA DE EMPLEADOS
                 * ======================================================
                 *
                 * Las peticiones GET a /empleados y /empleados/**
                 * requieren autenticación.
                 *
                 * Tanto USER como ADMIN pueden consultar empleados.
                 *
                 * Las peticiones GET no necesitan token CSRF.
                 */
                authorizeRequests
                    .requestMatchers(
                        HttpMethod.GET,
                        "/empleados",
                        "/empleados/**"
                    )
                    .authenticated();


                /*
                 * ======================================================
                 * CREACIÓN DE EMPLEADOS
                 * ======================================================
                 *
                 * Las peticiones POST solo pueden ser realizadas
                 * por usuarios que tengan el rol ADMIN.
                 */
                authorizeRequests
                    .requestMatchers(
                        HttpMethod.POST,
                        "/empleados",
                        "/empleados/**"
                    )
                    .hasRole("ADMIN");


                /*
                 * ======================================================
                 * MODIFICACIÓN DE EMPLEADOS
                 * ======================================================
                 *
                 * Las peticiones PUT solo pueden ser realizadas
                 * por usuarios que tengan el rol ADMIN.
                 */
                authorizeRequests
                    .requestMatchers(
                        HttpMethod.PUT,
                        "/empleados",
                        "/empleados/**"
                    )
                    .hasRole("ADMIN");


                /*
                 * ======================================================
                 * ELIMINACIÓN DE EMPLEADOS
                 * ======================================================
                 *
                 * Las peticiones DELETE solo pueden ser realizadas
                 * por usuarios que tengan el rol ADMIN.
                 */
                authorizeRequests
                    .requestMatchers(
                        HttpMethod.DELETE,
                        "/empleados",
                        "/empleados/**"
                    )
                    .hasRole("ADMIN");


                /*
                 * ======================================================
                 * INTERFAZ WEB
                 * ======================================================
                 *
                 * Todas las URLs que comienzan por /web/
                 * requieren autenticación.
                 *
                 * Esto protege tanto las páginas HTML como las
                 * acciones auxiliares de la interfaz web.
                 */
                authorizeRequests
                    .requestMatchers("/web/**")
                    .authenticated();


                /*
                 * ======================================================
                 * RESTO DE PETICIONES
                 * ======================================================
                 *
                 * Cualquier petición que no haya coincidido con
                 * ninguna de las reglas anteriores queda permitida.
                 *
                 * Esto puede ser útil, por ejemplo, para recursos
                 * públicos de la aplicación.
                 *
                 * ATENCIÓN: al ser permitAll(), cualquier ruta nueva que
                 * no se añada a las reglas anteriores quedará PÚBLICA.
                 * Al crear nuevos endpoints hay que acordarse de
                 * protegerlos aquí. En una aplicación real lo habitual
                 * es usar anyRequest().authenticated() (o denyAll()) y
                 * abrir solo lo necesario.
                 */
                authorizeRequests
                    .anyRequest()
                    .permitAll();
            })


            /*
             * ==========================================================
             * PROTECCIÓN CSRF
             * ==========================================================
             *
             * Spring Security activa CSRF por defecto.
             *
             * Las peticiones que pueden modificar datos, como:
             *
             * POST
             * PUT
             * DELETE
             *
             * deben incluir un token CSRF válido.
             *
             * Utilizamos aquí la configuración estándar de Spring
             * Security y no personalizamos el repositorio del token:
             * el token se guarda en la sesión (cookie JSESSIONID).
             *
             * Los clientes pueden obtener el token mediante:
             *
             * GET /csrf
             *
             * y enviarlo posteriormente en la cabecera indicada
             * por Spring Security (por defecto, X-CSRF-TOKEN).
             *
             * Si el token falta, es incorrecto o no corresponde a la
             * sesión actual, Spring responde 403 ANTES de comprobar los
             * roles, incluso con el usuario admin.
             *
             * - Interfaz web: Thymeleaf incluye el token automáticamente
             *   en los formularios.
             * - Postman: hay que enviarlo manualmente en la cabecera
             *   X-CSRF-TOKEN, obtenido con GET /csrf desde Postman y
             *   manteniendo la misma cookie JSESSIONID.
             */
            .csrf(Customizer.withDefaults())


            /*
             * ==========================================================
             * FORMULARIO DE LOGIN (navegador)
             * ==========================================================
             *
             * Habilita la autenticación mediante formulario.
             *
             * Como no hemos creado una página de login propia,
             * Spring Security proporciona una página automáticamente
             * en /login.
             *
             * permitAll() permite acceder al formulario aunque el
             * usuario todavía no esté autenticado.
             *
             * defaultSuccessUrl() indica a dónde se redirige tras un
             * login correcto. Si el usuario intentó antes entrar a una
             * URL protegida, Spring lo devuelve a esa URL; si no, lo
             * envía a /web/empleados.
             */
            .formLogin(formLogin -> formLogin
                .defaultSuccessUrl("/web/empleados")
                .permitAll()
            )


            /*
             * ==========================================================
             * BASIC AUTH (necesario para Postman y clientes REST)
             * ==========================================================
             *
             * Habilita la autenticación HTTP Basic: el cliente envía
             * en cada petición la cabecera
             *
             * Authorization: Basic base64(usuario:contraseña)
             *
             * ¿Por qué es necesario?
             * ----------------------
             * formLogin() solo sabe autenticar a un navegador: espera
             * que el usuario rellene el formulario de /login y mantiene
             * después la sesión con la cookie JSESSIONID.
             *
             * Postman no rellena formularios, y aunque enviara la
             * cabecera Authorization, Spring la ignoraría si httpBasic
             * no estuviera activado.
             *
             * Sin esta línea, una petición de Postman sin sesión recibe
             * un 302 hacia /login, Postman sigue la redirección y
             * muestra un 200 con el HTML del formulario en lugar del
             * JSON. Con httpBasic activado, la misma petición sin
             * credenciales recibe un 401 (WWW-Authenticate: Basic), y
             * con credenciales válidas se autentica y llega al
             * controlador.
             *
             * ¿Afecta al navegador?
             * ---------------------
             * No. Spring elige cómo responder según el tipo de cliente
             * (cabecera Accept): un navegador (text/html) sigue siendo
             * redirigido al formulario de /login, y los clientes REST
             * reciben el 401. Ambos mecanismos conviven en la misma
             * cadena de filtros.
             *
             * Importante
             * ----------
             * - Basic Auth solo AUTENTICA. No sustituye a CSRF: los
             *   POST, PUT y DELETE siguen necesitando la cabecera
             *   X-CSRF-TOKEN y la cookie JSESSIONID (ver sección CSRF).
             * - Las credenciales viajan codificadas en Base64, no
             *   cifradas. Fuera de un entorno de pruebas debe usarse
             *   HTTPS.
             */
            .httpBasic(Customizer.withDefaults())


            /*
             * ==========================================================
             * LOGOUT
             * ==========================================================
             *
             * Spring Security proporciona el endpoint POST /logout
             * para cerrar la sesión. Al ser POST, también requiere
             * el token CSRF (Thymeleaf lo incluye en el formulario).
             *
             * Después de cerrar sesión se redirige a:
             *
             * /web/empleados
             *
             * Como esa URL requiere autenticación, el usuario será
             * enviado posteriormente al formulario de login.
             */
            .logout(logout -> logout
                .logoutSuccessUrl("/web/empleados")
            );


        /*
         * Construye la cadena de filtros de seguridad que utilizará
         * Spring Security.
         */
        return httpSecurity.build();
    }


    /*
     * ==============================================================
     * USUARIOS
     * ==============================================================
     *
     * Define los usuarios que pueden autenticarse, tanto por
     * formulario como por Basic Auth.
     *
     * En este proyecto los usuarios se almacenan EN MEMORIA.
     *
     * Por tanto, no se guardan en PostgreSQL.
     *
     * Son usuarios de demostración, solo para uso didáctico: en una
     * aplicación real las credenciales no deben estar en el código.
     *
     * El PasswordEncoder se recibe como parámetro y Spring lo
     * inyecta automáticamente.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        /*
         * ==========================================================
         * USUARIO NORMAL
         * ==========================================================
         *
         * username: user
         * password: password
         * rol: USER
         *
         * Puede consultar empleados.
         *
         * No puede crear, modificar ni eliminar.
         */
        UserDetails usuario = User
            .withUsername("user")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();


        /*
         * ==========================================================
         * USUARIO ADMINISTRADOR
         * ==========================================================
         *
         * username: admin
         * password: admin
         * rol: ADMIN
         *
         * Puede consultar, crear, modificar y eliminar empleados.
         */
        UserDetails admin = User
            .withUsername("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN")
            .build();


        /*
         * InMemoryUserDetailsManager almacena ambos usuarios
         * en memoria mientras la aplicación está funcionando.
         *
         * Los usuarios desaparecen al detener la aplicación.
         */
        return new InMemoryUserDetailsManager(usuario, admin);
    }


    /*
     * ==============================================================
     * CODIFICADOR DE CONTRASEÑAS
     * ==============================================================
     *
     * BCrypt se utiliza para codificar las contraseñas.
     *
     * No almacenamos las contraseñas directamente en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}