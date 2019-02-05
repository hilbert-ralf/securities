package de.hilbert.securities.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Ralf Hilbert
 * @since 25.01.2019
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.password.guest}")
    private String guestPassword;

    @Value("${security.password.admin}")
    private String adminPassword;

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        // TODO: 25.01.2019 improve security
        UserDetails guest =
                User.withDefaultPasswordEncoder()
                        .username("guest")
                        .password(guestPassword)
                        .roles("GUESTS")
                        .build();

        UserDetails admin =
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password(adminPassword)
                        .roles("ADMINS")
                        .build();

        return new InMemoryUserDetailsManager(guest, admin);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/actuator/*").hasAnyRole("ADMINS")
                .antMatchers("/api/**").hasAnyRole("GUESTS", "ADMINS")
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }
}
