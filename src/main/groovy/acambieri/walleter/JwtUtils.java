package acambieri.walleter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public class JwtUtils {
    public static String generateToken(UserDetails user,long tokenValidity,String jwtSecret){
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+ tokenValidity*1000))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }
}
