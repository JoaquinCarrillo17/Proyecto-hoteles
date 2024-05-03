package gz.hoteles.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import gz.hoteles.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public String createToken(Usuario usuario) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 400000); // 10 mins de validez, esta puesto en 40 segundos para pruebas

        Map<String, Object> claims = crearClaims(usuario);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Map<String, Object> crearClaims(Usuario usuario){
        Map<String, Object> claims = new HashMap<>();
		LinkedList<String> roles = new LinkedList<String>();

        for (String rol: usuario.getRoles()) {
            roles.add(rol);
        }

		claims.put("sub", usuario.getUsername());
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
