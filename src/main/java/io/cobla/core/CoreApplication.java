package io.cobla.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableAuthorizationServer  // API 서버 인증, 권한 설정
@EnableResourceServer
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
@Configuration
public class CoreApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        logger.debug("$$$$$$$$$$$3:"+application.properties().toString());
        return application.sources(CoreApplication.class);
    }


    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
