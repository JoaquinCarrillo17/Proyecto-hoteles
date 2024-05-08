package gz.hoteles.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// @EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/auth/signUp", "/auth/login", "/auth", "/usuarios/**", "/roles", "/roles/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**")
                .permitAll() // Rutas públicas
                .antMatchers(HttpMethod.DELETE, "/hoteles/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/habitaciones/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/servicios/{id}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/huespedes/{id}").hasRole("ADMIN")
                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                .and()
                .csrf().disable(); // Deshabilitar CSRF (puedes habilitarlo en producción)

        // Agregar filtro JWT antes del filtro de autenticación de usuario y contraseña
        http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

}
