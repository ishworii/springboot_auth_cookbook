package com.ishwor.authcookbook.jwt.auth;


import com.ishwor.authcookbook.common.auth.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final long accessMinutes;

    public TokenService(JwtEncoder jwtEncoder,@Value("${jwt.access-minutes : 15}") long accessMinutes){
        this.accessMinutes = accessMinutes;
        this.jwtEncoder = jwtEncoder;
    }

    public String issueAccessToken(AppUser appUser){
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessMinutes * 60);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("authcookbook")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(appUser.getEmail())
                .claim("role", appUser.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
