package Utils;

import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME_MS = 15 * 60 * 1000;
    private static  ArrayList<String> black_list = new ArrayList<>();


    public static String generateToken(String phone, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        claims.put("role", role);
        return createToken(claims, phone);
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    public static String extractTokenFromBearer(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return bearerToken;
    }

    private static Claims extractAllClaims(String token) throws ExpiredJwtException, SignatureException {
        String pureToken = extractTokenFromBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(pureToken)
                .getBody();
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
            throws ExpiredJwtException, SignatureException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String extractSubject(String token) throws ExpiredJwtException, SignatureException {
        return extractClaim(token, Claims::getSubject);
    }

    public static boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean validateToken(String token) {
        try {
            final String phone = extractClaim(token, claims -> claims.get("phone", String.class));
            return (!isTokenExpired(token) && !black_list.contains(token));
        } catch (Exception ex) {
            return false;
        }
    }

    public static void expireToken(String token) {
        black_list.add(token);
    }

    public static String extractRole(String token)  {
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class);

        return role;
    }


    public static String get_token_from_server(HttpExchange exchange) throws IOException {
        try{
            String header = exchange.getRequestHeaders().getFirst("Authorization");

            if(!header.startsWith(BEARER_PREFIX)){
                return null;
            }
            else {
                return header.substring(7);
            }
        }
        catch(Exception e){
            return null;
        }
    }

}

