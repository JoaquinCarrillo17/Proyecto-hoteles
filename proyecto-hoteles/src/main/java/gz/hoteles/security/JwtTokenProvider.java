package gz.hoteles.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gz.hoteles.entities.Permiso;
import gz.hoteles.entities.Rol;
import gz.hoteles.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public String createToken(Usuario usuario) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 1200000); // ! 2 mins, ahora mismo esta con un cero de mas

        Map<String, Object> claims = crearClaims(usuario);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Map<String, Object> crearClaims(Usuario usuario){
        Map<String, Object> claims = new HashMap<>();
		Set<String> roles = new HashSet<String>();

        for (Rol rol : usuario.getRoles()) {
            for (Permiso p : rol.getPermisos()) {
                roles.add("ROLE_" + p.getNombre());
            }
        }

		claims.put("sub", usuario.getUsername());
        claims.put("id", usuario.getId());
		claims.put("roles", roles);

		return claims;
    }

    public String extraerToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims validateToken(String token) {
        return  Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

}
