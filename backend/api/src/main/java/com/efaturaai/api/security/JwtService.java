package com.efaturaai.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final Key key;

  public JwtService(
      @Value(
              "${security.jwt.secret:0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF}")
          String secret) {
    byte[] bytes = Decoders.BASE64.decode(secret);
    this.key = Keys.hmacShaKeyFor(bytes);
  }

  public String generate(
      UUID tenantId, String subject, Map<String, Object> extraClaims, int minutes) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(subject)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plus(minutes, ChronoUnit.MINUTES)))
        .claim("tenant", tenantId.toString())
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
