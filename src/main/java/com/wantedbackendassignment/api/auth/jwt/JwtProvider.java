package com.wantedbackendassignment.api.auth.jwt;

import com.wantedbackendassignment.api.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final UserDetailsService userService;
    private final JwtProperties jwtProperties;

    public String generate(JwtVo jwtVo) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", jwtVo.getEmail());
        payloads.put("role", jwtVo.joinRolesToString());

        return generateJwtBuilder(payloads)
                .setSubject("Access Token (" + jwtVo.getEmail() + ")")
                .setExpiration(calculateExpiryDate(jwtProperties.getValidityPeriod()))
                .compact();
    }

    public void setAuthenticationToContext(String accessToken) {
        SecurityContextHolder.getContext().setAuthentication(this.createAuthentication(accessToken));
    }

    public void verify(String token) {
        try {
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSecret());

            Jwts.parser()
                .setSigningKey(apiKeySecretBytes)
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("expired token");
        } catch (MalformedJwtException e) {
            throw new JwtException("malformed token");
        } catch (SignatureException e) {
            throw new JwtException("invalid signature of token");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("invalid token format");
        }
    }

    private Authentication createAuthentication(String accessToken) {
        UserDetails userDetails = userService.loadUserByUsername(this.extractEmail(accessToken));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private JwtBuilder generateJwtBuilder(Map<String, Object> payloads) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", jwtProperties.getAlgorithm());

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forName(jwtProperties.getAlgorithm());

        // 서명에 담을 데이터
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSecret());
        Key signKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(signatureAlgorithm, signKey);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtProperties.getSecret()))
                .parseClaimsJws(token)
                .getBody();
    }

    private String extractEmail(String token) {
        return (String) parseClaims(token).get("email");
    }

    private Date calculateExpiryDate(long validityHour) {
        return Date.from(Instant.now().plus(validityHour, ChronoUnit.HOURS));
    }
}
