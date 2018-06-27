package io.cobla.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
public class ResourceServerConfigurerAdapterImpl extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception {
       http.headers().frameOptions().disable();
        http.authorizeRequests()
            .antMatchers( "/malware")
            .permitAll()
            .antMatchers( "/addr/transaction")
            .permitAll()
         .anyRequest().authenticated()
        .and()
        .httpBasic();

    }


}
