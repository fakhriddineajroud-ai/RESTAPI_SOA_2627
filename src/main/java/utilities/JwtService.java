package utilities;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtService {

    // Clé secrète connue uniquement du serveur (HS512 => au moins 64 caractères)
    private static final byte[] SECRET_KEY =
            "gestion-options-esprit-secret-key-change-me-0123456789-abcdefghijklmnopqrstuvwxyz"
                    .getBytes(StandardCharsets.UTF_8);

    // Durée de vie de l'access token
    public static final long ACCESS_MINUTES = 15L;

    // ===== Génération =====

    public static String generateAccessToken(String username, String issuer) {
        long duree = ACCESS_MINUTES * 60 * 1000;   // en millisecondes
        return buildToken(username, issuer, "access", duree, null);
    }

    private static String buildToken(String username, String issuer, String type,
                                     long dureeMillis, String jti) {
        long now = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)                        // sub : l'utilisateur
                .setIssuer(issuer)                           // iss : l'émetteur
                .setIssuedAt(new Date(now))                  // iat : date d'émission
                .setExpiration(new Date(now + dureeMillis))  // exp : date d'expiration
                .claim("type", type);                        // notre propre claim
        if (jti != null) {
            builder.setId(jti);                              // jti : identifiant unique (partie C)
        }
        return builder.signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
    }

    // ===== Validation =====

    // Lève une exception si la signature est invalide ou si le jeton a expiré
    private static Claims parse(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Valide un access token : signature, expiration et type
    public static Claims validateAccessToken(String token) {
        Claims claims = parse(token);
        if (!"access".equals(claims.get("type"))) {
            throw new IllegalArgumentException("Ce jeton n'est pas un access token");
        }
        return claims;
    }
}
