package gz.hoteles.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.extraerToken(request);
        if (token != null) {
            Claims claims;
            try {
               claims = jwtTokenProvider.validateToken(token); // Compruebo si esta caducado
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "El token está caducado"); 
                return; // ! No para aqui, sigue por OncePerRequestFilter y da forbidden
            }
            @SuppressWarnings("unchecked")
            ArrayList<String> roles = (ArrayList<String>) claims.get("roles");
            ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            // Consulta si la operación está protegida por rol
            for (String rol : roles) {
                authorities.add(new SimpleGrantedAuthority(rol));
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.get("sub"), null,
                    authorities);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

}
