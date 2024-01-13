package com.test.one.service;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {
	
	
		/*
		 * 
			The JwtTokenService class is a service responsible for handling JSON Web Tokens (JWTs) 
			in a Spring application. It provides methods to generate, extract information from, 
			and validate JWTs.
		 */
	

        public String SECRET_KEY = "sAMPLEkEY";
        private Date CURRENT_TIME = new Date(System.currentTimeMillis());  //the token is valid from now
        private Date EXPIRATION_TIME = new Date(System.currentTimeMillis() + 1000 * 60 * 60 *24*7); //the token expiry period


        public String extractUsername(String token){      //we start by extracting the token
            return extractClaim(token, Claims::getSubject);
        }
        // END OF EXTRACT USERNAME FROM TOKEN METHOD.

        public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    // END OF EXTRACT EXPIRATION DATE FROM TOKEN METHOD.

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // END OF EXTRACT CLAIMS FROM TOKEN METHOD.

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build().parseClaimsJws(token).getBody();
    }
    // END OF EXTRACT ALL CLAIMS FROM TOKEN METHOD.

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // END OF GET SIGNING KEY METHOD.

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    // END OF CHECK IF TOKEN IS EXPIRED FROM TOKEN METHOD.

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    // END OF GENERATE TOKEN METHOD.

    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(CURRENT_TIME)
                .setExpiration(EXPIRATION_TIME)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // END OF CREATE JWT TOKEN METHOD.

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // END OF VALIDATE TOKEN METHOD.

}
// END OF JWT TOKEN GENERATOR / SERVICE.