package pl.dev.news.devnewsservice.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.dev.news.devnewsservice.security.TokenAuthenticationProvider;
import pl.dev.news.devnewsservice.security.TokenFilter;
import pl.dev.news.devnewsservice.security.TokenValidator;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static pl.dev.news.controller.api.AuthApi.activatePath;
import static pl.dev.news.controller.api.AuthApi.basePath;
import static pl.dev.news.controller.api.AuthApi.refreshTokenPath;
import static pl.dev.news.controller.api.AuthApi.resendPath;
import static pl.dev.news.controller.api.AuthApi.signInPath;
import static pl.dev.news.controller.api.AuthApi.signUpPath;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final TokenAuthenticationProvider tokenAuthenticationProvider;
    private final TokenValidator tokenValidator;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf()
                .disable();

        httpSecurity
                .cors()
                .and();

        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity
                .addFilterBefore(new TokenFilter(tokenValidator), BasicAuthenticationFilter.class);

        httpSecurity
                .authenticationProvider(tokenAuthenticationProvider);

        httpSecurity.authorizeRequests()
                .antMatchers(POST, signInPath).permitAll()
                .antMatchers(POST, signUpPath).permitAll()
                .antMatchers(POST, refreshTokenPath).permitAll()
                .antMatchers(GET, activatePath).permitAll()
                .antMatchers(GET, resendPath).permitAll()
                .antMatchers(OPTIONS, "/**").permitAll()

                .antMatchers(basePath + "/**").authenticated();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(tokenAuthenticationProvider);
    }
}
