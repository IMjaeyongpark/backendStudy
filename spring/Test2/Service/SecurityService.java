package jaeyong.Test2.Service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;


public class SecurityService {

    //이러면 안되지만 연습을 위해
    private static final String SECRET_KEY = "oidfnainvmoniofaniofnjxnvknaonvoaidnvojnOVJNDSJVodkvnso";

    //로그인 서비스 던질때 같이
    public String createToken(String subject,long expTime){

        if(expTime<=0){
            throw new RuntimeException("0보다 작아");
        }

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] secretkeybytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingkey = new SecretKeySpec(secretkeybytes, signatureAlgorithm.getJcaName());

        return Jwts.builder().setSubject(subject)
                .signWith(signingkey,signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis()+expTime))
                .compact();
    }


    //토큰 검증하는 메서드를 Boolean 타입으로 반화받아 함께사용
    public String getSubject(String token){
        Claims claim = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claim.getSubject();
    }

}
