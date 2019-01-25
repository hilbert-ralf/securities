package de.hilbert.securities.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        // TODO: 25.01.2019 improve security
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("guest")
                        .password(guestPassword)
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
