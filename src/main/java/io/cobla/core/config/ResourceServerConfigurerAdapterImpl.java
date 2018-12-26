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
            .antMatchers( "/nsaction")
            .permitAll()
            .antMatchers( "/v1/address/balance")
            .permitAll()
            .antMatchers( "/wallet/monitor")
            .permitAll()
            .antMatchers( "/test/elastic")
            .permitAll()
            .antMatchers( "/addr/transaction")
            .permitAll()
            .antMatchers( "/elastic/make/transaction")
            .permitAll()
                .antMatchers( "/temp/eth/black")
                .permitAll()
                .antMatchers( "/temp/hum")
                .permitAll()
                .antMatchers( "/temp/hum/result")
                .permitAll()
            //.antMatchers( "/v1/blacklist/wallet_block")
            //.permitAll()
            //.antMatchers( "/wallet")
            //.permitAll()

         .anyRequest().authenticated()
        .and()
        .httpBasic();

    }


}
