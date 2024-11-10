package gz.hoteles.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/swagger-ui.html**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .csrf().disable();
        return http.build();
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/auth/signUp", "/auth/login", "/auth", "/auth/**", "/permisos", "/roles", "/roles/**",
                        "/usuarios", "/usuarios/**", "/ubicaciones", "/ubicaciones/**", "historicos", "historicos/**",
                        "/hoteles/dynamicFilterAnd",
                        "/habitaciones/dynamicFilterAnd",
                        "/reservas",
                        "/reservas/*",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                .permitAll() // Rutas públicas

                .antMatchers(HttpMethod.GET, "/habitaciones/{id}")
                .permitAll()

                // * Restrinjo los hoteles
                .antMatchers(HttpMethod.GET, "/hoteles/**")
                .hasAnyRole("HOTELES_R", "HOTELES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/hoteles")
                .hasAnyRole("HOTELES_R", "HOTELES_W", "SUPER_ADMIN")
                // * Restringir los métodos PUT, POST y DELETE a ROLE_HOTELES_W
                .antMatchers(HttpMethod.PUT, "/hoteles/{id}")
                .hasAnyRole("HOTELES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/hoteles")
                .hasAnyRole("HOTELES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/hoteles/**")
                .hasAnyRole("HOTELES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/hoteles/{id}")
                .hasAnyRole("HOTELES_W", "SUPER_ADMIN")
                // * Restrinjo las habitaciones
                .antMatchers(HttpMethod.GET, "/habitaciones/**")
                .hasAnyRole("HABITACIONES_R", "HABITACIONES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/habitaciones")
                .hasAnyRole("HABITACIONES_R", "HABITACIONES_W", "SUPER_ADMIN")
                // * Restringir los métodos PUT, POST y DELETE a ROLE_HABITACIONES_W
                .antMatchers(HttpMethod.PUT, "/habitaciones/{id}")
                .hasAnyRole("HABITACIONES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/habitaciones")
                .hasAnyRole("HABITACIONES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/habitaciones/**")
                .hasAnyRole("HABITACIONES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/habitaciones/{id}")
                .hasAnyRole("HABITACIONES_W", "SUPER_ADMIN")

                // * Para Huéspedes
                .antMatchers(HttpMethod.GET, "/huespedes/**")
                .hasAnyRole("HUESPEDES_R", "HUESPEDES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/huespedes")
                .hasAnyRole("HUESPEDES_R", "HUESPEDES_W", "SUPER_ADMIN")
                // * Restringir los métodos PUT, POST y DELETE a ROLE_HUESPEDES_W
                .antMatchers(HttpMethod.PUT, "/huespedes/{id}")
                .hasAnyRole("HUESPEDES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/huespedes")
                .hasAnyRole("HUESPEDES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/huespedes/**")
                .hasAnyRole("HUESPEDES_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/huespedes/{id}")
                .hasAnyRole("HUESPEDES_W", "SUPER_ADMIN")

                // * Para reservas
                .antMatchers(HttpMethod.GET, "/reservas/**")
                .hasAnyRole("RESERVAS_R", "RESERVAS_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/reservas")
                .hasAnyRole("RESERVAS_R", "RESERVAS_W", "SUPER_ADMIN")
                // * Restringir los métodos PUT, POST y DELETE a ROLE_RESERVAS_W
                .antMatchers(HttpMethod.PUT, "/reservas/{id}")
                .hasAnyRole("RESERVAS_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/reservas")
                .hasAnyRole("RESERVAS_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/reservas/**")
                .hasAnyRole("RESERVAS_W", "SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/reservas/{id}")
                .hasAnyRole("RESERVAS_W", "SUPER_ADMIN")

                // * Para Roles
                .antMatchers(HttpMethod.GET, "/roles/**")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/roles")
                .hasRole("SUPER_ADMIN")
                // * Restringir los métodos PUT, POST y DELETE a ROLE_ROLES_W
                .antMatchers(HttpMethod.PUT, "/roles/{id}")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/roles")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/roles/**")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/roles/{id}")
                .hasRole("SUPER_ADMIN")

                .antMatchers(HttpMethod.GET, "/usuarios/**")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/usuarios")
                .hasRole("SUPER_ADMIN")
                // * Restringir los métodos PUT, POST y DELETE a ROLE_ROLES_W
                .antMatchers(HttpMethod.PUT, "/usuarios/{id}")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/usuarios")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.POST, "/usuarios/**")
                .hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/usuarios/{id}")
                .hasRole("SUPER_ADMIN")

                 .antMatchers("/hoteles", "/hoteles/**", "/habitaciones", "/habitaciones/**", "/huespedes", "/huespedes**", "/roles", "/roles**", "/usuarios", "/usuarios/**", "/reservas", "/reservas/**").authenticated()

                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                .and()
                .csrf().disable(); // Deshabilitar CSRF (puedes habilitarlo en producción)

        // Agregar filtro JWT antes del filtro de autenticación de usuario y contraseña
        http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

}
