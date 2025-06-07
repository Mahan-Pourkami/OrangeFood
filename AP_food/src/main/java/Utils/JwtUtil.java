package Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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


    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    public static  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String extractSubject(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public static boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }



//    public static boolean hasValidToken(String phone) {
//        for (String token : AuthHandler.tokens) {
//            try {
//                Claims claims = Jwts.parserBuilder()
//                        .setSigningKey(SECRET_KEY)
//                        .build()
//                        .parseClaimsJws(token)
//                        .getBody();
//
//                String tokenPhone = claims.get("phone", String.class);
//                boolean isExpired = claims.getExpiration().before(new Date());
//
//                if (phone.equals(tokenPhone) && !isExpired) {
//                    return true;
//                }
//            } catch (Exception e) {
//
//                AuthHandler.tokens.remove(token);
//            }
//        }
//        return false;
//    }

}
