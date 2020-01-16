package com.gmail.quiz.config;

import com.gmail.quiz.service.UserDetailsServiceImpl;
import com.gmail.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Qualifier("myUserDetailsService")
//    private UserDetailsService userDetailsService;
//
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public WebSecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers(HttpMethod.GET, "/js/**").permitAll()
                    .antMatchers("/registration").permitAll()
                    .antMatchers("/quiz").authenticated()
                    .antMatchers("/api/question/validate").authenticated()
                    .antMatchers("/questions", "/newQuestion", "/question/**", "/users", "/user/**", "/api/user/all").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/api/question/{id:[\\d+]}").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and()
//                 One reason to override most of the defaults in Spring Security is to hide the fact that
//                 the application is secured with Spring Security and minimize the information a potential attacker
//                 knows about the application.
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
//                    .failureUrl("/login?error=true")
                    .defaultSuccessUrl("/")
                    .usernameParameter("userName").permitAll()
                    .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .and()
                .exceptionHandling().accessDeniedPage("/errors/accessDenied");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

//        private DataSource dataSource;

//        @Autowired
//    public WebSecurityConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

//        @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .passwordEncoder(passwordEncoder())
//                .usersByUsernameQuery("SELECT user_name, password, enabled FROM usr WHERE user_name=?")
//                .authoritiesByUsernameQuery("SELECT u.user_name, ur.roles FROM usr u INNER JOIN user_role ur ON u.id = ur.user_id WHERE u.user_name=?");
//    }


//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//        return new BCryptPasswordEncoder();
//    }

}
