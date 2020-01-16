package com.changgou.system;

import io.jsonwebtoken.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * Created by zps on 2020/1/9 10:59
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestBcrypt {
    @Test
    public void Bycrpt() {
        //获取盐
        String gensalt = BCrypt.gensalt();
        System.out.println("盐：" + gensalt);
        String hashpw = BCrypt.hashpw("123456", gensalt);
        System.out.println("加密之后：" + hashpw);
        boolean checkpw = BCrypt.checkpw("123456", "$2a$10$ofq7LJ6QIaRmNFEUylZQQOhS6VG5qHr/wkcKXHiTb4MedJM1qUFb2");
        System.out.println(checkpw);
    }

    @Test
    public void JJWTTest() {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId("66")            //设置id
                .setIssuedAt(new Date())    //设置签发日期
                .setSubject("笙歌")          //设置主题
                .claim("roles", "admin")     //自定义信息
                .claim("company", "heima")
                //  .setExpiration(new Date())  //设置过期时间,令牌一创建就过期！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
                .signWith(SignatureAlgorithm.HS256, "root");    //设置签名 使用HS256算法，并设置SecretKey(字符串)


        //构建 并返回一个字符串
        String compact = jwtBuilder.compact();
        System.out.println(compact);

        JwtParser root = Jwts.parser().setSigningKey("root");
        Claims body = root.parseClaimsJws(compact).getBody();
        System.out.println(body);

    }
}
