package com.neom108.SmartHire.config;

import com.neom108.SmartHire.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    // list of urls that do not require authentication
    private final String[] publicUrl = {"/",
            "/global-search/**",
            "/register",
            "/register/**",
            "/webjars/**",
            "/resources/**",
            "/assets/**",
            "/css/**",
            "/summernote/**",
            "/js/**",
            "/*.css",
            "/*.js",
            "/*.js.map",
            "/fonts**", "/favicon.ico", "/resources/**", "/error"};

    @Autowired
    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean // so that spring security is aware of this function
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(auth ->{
            //no need to login on public urls
            auth.requestMatchers(publicUrl).permitAll();
            //for other urls - login first
            auth.anyRequest().authenticated();
        });

        http.formLogin(form -> form.loginPage("/login").permitAll().successHandler(customAuthenticationSuccessHandler))
                .logout(logout ->{
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                })
                .cors(Customizer.withDefaults())
                .csrf(csrf ->csrf.disable());

        return http.build();
    }

    @Bean // so that spring security is aware of this function
    public AuthenticationProvider authenticationProvider() {// custom authentication provider
        // tell Spring Security how to find our users
        // and how to authenticate passwords

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailsService); // tells Spring security how to retrieve the users from database
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ // custom password encoder
        return new BCryptPasswordEncoder(); // tell spring security how to authenticate passwords (plain text or encryption)
    }
}
