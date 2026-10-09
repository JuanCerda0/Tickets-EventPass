package dev.eventpass.tickets.service;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.eventpass.tickets.model.Rol;
import dev.eventpass.tickets.security.UsuarioAutenticado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {

    private final SecretKey claveFirma;
    private final String emisor;

    public JwtTokenService(
        @Value("${app.jwt.secret-base64}") String claveBase64,
        @Value("${app.jwt.issuer}") String emisor
    ) {
        this.claveFirma = Keys.hmacShaKeyFor(Base64.getDecoder().decode(claveBase64));
        this.emisor = emisor;
    }

    public UsuarioAutenticado validarYLeer(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(claveFirma)
            .requireIssuer(emisor)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return new UsuarioAutenticado(
            Long.valueOf(claims.getSubject()),
            claims.get("email", String.class),
            Rol.valueOf(claims.get("rol", String.class))
        );
    }
}
