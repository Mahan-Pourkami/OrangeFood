package Utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.sun.net.httpserver.HttpExchange;

import Model.User;
import Model.Role;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    private final static Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final static Long EXPIRATION_TIME = 15*60L;  /* 15 Minutes */


    public static String generateToken(String phone, String role) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("phone", phone);
        claims.put("role", role);

        return createToken(claims,phone);
    }

    private static String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public Boolean validateToken(String token, String expectedSubject) {
        final String extractedSubject = extractSubject(token);
        return (extractedSubject.equals(expectedSubject) && !isTokenExpired(token));
    }

}
