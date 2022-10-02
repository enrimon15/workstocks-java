package it.workstocks.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import it.workstocks.security.UserDetailsServiceImpl;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtTokenUtil;

    @Value("${jwt.token.header}")
    private String tokenHeader;

    @Value("${jwt.token.prefix}")
    private String tokenPrefix;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    // filtro per autenticare l'utente tramite il jwt token
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String requestHeader = request.getHeader(this.tokenHeader);
        UserDetails userDetails = null;
        String authToken = null;

        if (requestHeader != null && requestHeader.startsWith(tokenPrefix)) {
            authToken = requestHeader.substring(tokenPrefix.length() + 1);
            try {
                String userName = jwtTokenUtil.getUsernameFromToken(authToken);
                if (userName != null) {
                    userDetails = userDetailsService.loadUserByUsername(userName);
                }
            } catch(UsernameNotFoundException e) {
            	logger.error("JWT: User not found", e);
            	throw e;
            }  catch (JwtException e) {
                logger.error("JWT: Token Expired", e);
                throw e;
            } catch (Exception e) {
                logger.error("JWT: Generic Error", e);
                throw e;
            }
        }

        if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
