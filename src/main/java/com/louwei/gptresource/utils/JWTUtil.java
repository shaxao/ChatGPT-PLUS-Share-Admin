package com.louwei.gptresource.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Data
public class JWTUtil {
  private static final String SECRET_KEY = "mhchat-2000314";
  private static final long EXPIRE_TOKEN = 1000 * 60 * 60 * 30;
  private static final String USER_NAME = "username";
  private static final String ISSUER = "admin";

  public static String token(String username) {
      Date date = new Date();
      Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
      String token = JWT.create()
              .withIssuer(ISSUER)
              .withIssuedAt(date)
              .withExpiresAt(new Date(date.getTime() + EXPIRE_TOKEN))
              .withClaim(USER_NAME, username)
              .sign(algorithm);
      log.info("jwt generated user={}", username);
      return token;
  }

  public static boolean verify(String token) {
      try {
          Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
          JWTVerifier jwtVerifier = JWT.require(algorithm)
                  .withIssuer(ISSUER)
                  .build();
          jwtVerifier.verify(token);
          return true;
      } catch (Exception e) {
          log.error("jwt校验失败-{}", e);
          return false;
      }
  }


    public static String getUsernameFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            String username = jwt.getClaim(USER_NAME).asString();
            return username;
        } catch (JWTVerificationException exception){
            log.error("Token verification failed.", exception);
            return null;
        }
    }

//    public static void main(String[] args) {
//        String token = token("mhchat");
//        System.out.println("token: " + token);
//        boolean verify = verify(token);
//        System.out.println("verify: " + verify);
//        String usernameFromToken = getUsernameFromToken(token);
//        System.out.println(usernameFromToken);
//    }
}
