package it.workstocks.security.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpiringMap;

@Component
@Slf4j
public class JWTUtil {
	
	private ExpiringMap<String, String> tokenBlacklistMap;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_CREATED = "iat";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_AUTHORITIES = "roles";

    private static final String CLAIM_KEY_NOT_IGNORE_EXP = "n";
    private static final String CLAIM_KEY_YES_IGNORE_EXP = "y";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    @PostConstruct
	public void initMap() {
		this.tokenBlacklistMap = ExpiringMap.builder()
                .variableExpiration()
                .maxSize(1000)
                .build();
	}

    //generazione token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        List<String> auth = userDetails.getAuthorities().stream().map(role -> role.getAuthority()).collect(Collectors.toList());
        claims.put(CLAIM_KEY_AUTHORITIES, auth);
        claims.put(CLAIM_KEY_AUDIENCE, CLAIM_KEY_NOT_IGNORE_EXP);

        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.HS256, secret).compact();
    }
    ////////

    //settare scadenza token
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    //check scadenza token
    private Boolean isTokenExpired(String token) {
	   final Date expiration = getExpirationDateFromToken(token);
	   return expiration.before(new Date());
    }

    //ottenere tutti i parametri del token
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
        	claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (IllegalArgumentException e) {
			throw new JwtException("JWT Claims is empty");
		}
        return claims;
    }

    //ottenere scadenza token
    private Date getExpirationDateFromToken(String token) {
        Date expiration;
        final Claims claims = getClaimsFromToken(token);
        expiration = claims.getExpiration();
        return expiration;
    }

    //ottenere username dal token
    public String getUsernameFromToken(String token) {
        String username;
        final Claims claims = getClaimsFromToken(token);
        username = claims.getSubject();
        return username;
    }

    //ottenere audience token (nel nostro caso audience è: ignoraScadenza (Y|N) )
    private String getAudienceFromToken(String token) {
        String audience;
        final Claims claims = getClaimsFromToken(token);
        audience = (String) claims.get(CLAIM_KEY_AUDIENCE);
        return audience;
    }

    //check validità del token: utente corrispondente, token non scaduto (oppure scadenza non impostata) e valido (non in blacklist)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) 
        		&& (!isTokenExpired(token) || ignoreTokenExpiration(token))
        		&& !isTokenInvalid(token));
    }

    //check se il la scadenza del token è ignorata
    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (CLAIM_KEY_YES_IGNORE_EXP.equals(audience));
    }
    
    ////////// blacklist
    
    public void invalidateToken(String token) {
		String userEmail = getUsernameFromToken(token);
        if (tokenBlacklistMap.containsKey(token)) {
            log.debug(String.format("Logout token for user [%s] is already invalidated", userEmail));
        } else {
            Date tokenExpiryDate = getExpirationDateFromToken(token);
            long ttlForToken = getTTLForToken(tokenExpiryDate);
            log.debug(String.format("Token invalidated set for [%s] with a TTL of [%s] seconds. Token is due expiry at [%s]", userEmail, ttlForToken, tokenExpiryDate));
            tokenBlacklistMap.put(token, userEmail, ttlForToken, TimeUnit.SECONDS);
        }
    }
	
	private long getTTLForToken(Date date) {
        long secondAtExpiry = date.toInstant().getEpochSecond();
        long secondAtLogout = Instant.now().getEpochSecond();
        return Math.max(0, secondAtExpiry - secondAtLogout);
	}
	
	private boolean isTokenInvalid(String token) {
        return (tokenBlacklistMap.get(token) != null);
    }
}
