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
                .antMatchers("/auth/signUp", "/auth/login", "/auth", "/permisos",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                .permitAll() // Rutas públicas

                // Restrinjo los hoteles
                .antMatchers(HttpMethod.GET, "/hoteles/**")
                .hasAnyRole("HOTELES_R", "HOTELES_W")
                .antMatchers(HttpMethod.GET, "/hoteles")
                .hasAnyRole("HOTELES_R", "HOTELES_W")
                // Restringir los métodos PUT, POST y DELETE a ROLE_HOTELES_W
                .antMatchers(HttpMethod.PUT, "/hoteles/{id}")
                .hasRole("HOTELES_W")
                .antMatchers(HttpMethod.POST, "/hoteles")
                .hasRole("HOTELES_W")
                .antMatchers(HttpMethod.POST, "/hoteles/**")
                .hasRole("HOTELES_W")
                .antMatchers(HttpMethod.DELETE, "/hoteles/{id}")
                .hasRole("HOTELES_W")
                // Restrinjo las habitaciones
                .antMatchers(HttpMethod.GET, "/habitaciones/**")
                .hasAnyRole("HABITACIONES_R", "HABITACIONES_W")
                .antMatchers(HttpMethod.GET, "/habitaciones")
                .hasAnyRole("HABITACIONES_R", "HABITACIONES_W")
                // Restringir los métodos PUT, POST y DELETE a ROLE_HABITACIONES_W
                .antMatchers(HttpMethod.PUT, "/habitaciones/{id}")
                .hasRole("HABITACIONES_W")
                .antMatchers(HttpMethod.POST, "/habitaciones")
                .hasRole("HABITACIONES_W")
                .antMatchers(HttpMethod.POST, "/habitaciones/**")
                .hasRole("HABITACIONES_W")
                .antMatchers(HttpMethod.DELETE, "/habitaciones/{id}")
                .hasRole("HABITACIONES_W")

                // Para Servicios
                .antMatchers(HttpMethod.GET, "/servicios/**")
                .hasAnyRole("SERVICIOS_R", "SERVICIOS_W")
                .antMatchers(HttpMethod.GET, "/servicios")
                .hasAnyRole("SERVICIOS_R", "SERVICIOS_W")
                // Restringir los métodos PUT, POST y DELETE a ROLE_SERVICIOS_W
                .antMatchers(HttpMethod.PUT, "/servicios/{id}")
                .hasRole("SERVICIOS_W")
                .antMatchers(HttpMethod.POST, "/servicios")
                .hasRole("SERVICIOS_W")
                .antMatchers(HttpMethod.POST, "/servicios/**")
                .hasRole("SERVICIOS_W")
                .antMatchers(HttpMethod.DELETE, "/servicios/{id}")
                .hasRole("SERVICIOS_W")

                // Para Huéspedes
                .antMatchers(HttpMethod.GET, "/huespedes/**")
                .hasAnyRole("HUESPEDES_R", "HUESPEDES_W")
                .antMatchers(HttpMethod.GET, "/huespedes")
                .hasAnyRole("HUESPEDES_R", "HUESPEDES_W")
                // Restringir los métodos PUT, POST y DELETE a ROLE_HUESPEDES_W
                .antMatchers(HttpMethod.PUT, "/huespedes/{id}")
                .hasRole("HUESPEDES_W")
                .antMatchers(HttpMethod.POST, "/huespedes")
                .hasRole("HUESPEDES_W")
                .antMatchers(HttpMethod.POST, "/huespedes/**")
                .hasRole("HUESPEDES_W")
                .antMatchers(HttpMethod.DELETE, "/huespedes/{id}")
                .hasRole("HUESPEDES_W")

                // Para Roles
                .antMatchers(HttpMethod.GET, "/roles/**")
                .hasAnyRole("ROLES_R", "ROLES_W")
                .antMatchers(HttpMethod.GET, "/roles")
                .hasAnyRole("ROLES_R", "ROLES_W")
                // Restringir los métodos PUT, POST y DELETE a ROLE_ROLES_W
                .antMatchers(HttpMethod.PUT, "/roles/{id}")
                .hasRole("ROLES_W")
                .antMatchers(HttpMethod.POST, "/roles")
                .hasRole("ROLES_W")
                .antMatchers(HttpMethod.POST, "/roles/**")
                .hasRole("ROLES_W")
                .antMatchers(HttpMethod.DELETE, "/roles/{id}")
                .hasRole("ROLES_W")

                 .antMatchers("/hoteles", "/hoteles/**", "/habitaciones", "/habitaciones/**", "/servicios",
                        "/servicios/**", "/huespedes", "/huespedes**" , "/roles", "/roles**", "/usuarios", "/usuarios/**").authenticated()

                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                .and()
                .csrf().disable(); // Deshabilitar CSRF (puedes habilitarlo en producción)

        // Agregar filtro JWT antes del filtro de autenticación de usuario y contraseña
        http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

}
