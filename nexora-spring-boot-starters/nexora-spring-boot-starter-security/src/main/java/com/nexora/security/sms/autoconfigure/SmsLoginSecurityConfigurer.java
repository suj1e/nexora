package com.nexora.security.sms.autoconfigure;

import com.nexora.security.sms.SmsAuthenticationFilter;
import com.nexora.security.sms.SmsAuthenticationProvider;
import com.nexora.security.sms.SmsCodeValidator;
import com.nexora.security.sms.SmsUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * Security configurer for SMS-based login.
 *
 * <p>Usage example:
 * <pre>
 * &#64;Configuration
 * &#64;EnableWebSecurity
 * public class SecurityConfig {
 *
 *     &#64;Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         http.apply(SmsLoginSecurityConfigurer.smsLogin())
 *             .smsUserDetailsService(userDetailsService)
 *             .smsCodeValidator(codeValidator);
 *
 *         http.authorizeHttpRequests(auth -&gt; auth
 *             .requestMatchers("/auth/sms/send").permitAll()
 *             .anyRequest().authenticated()
 *         );
 *         return http.build();
 *     }
 * }
 * </pre>
 *
 * @author sujie
 */
@Slf4j
public class SmsLoginSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private SmsUserDetailsService smsUserDetailsService;

    private SmsCodeValidator smsCodeValidator;

    private String loginProcessingUrl = "/auth/sms/login";

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    /**
     * Create a new configurer instance.
     */
    private SmsLoginSecurityConfigurer() {
    }

    /**
     * Create a new configurer with default settings.
     *
     * @return the configurer builder
     */
    public static SmsLoginSecurityConfigurer smsLogin() {
        return new SmsLoginSecurityConfigurer();
    }

    @Override
    public void init(HttpSecurity http) {
        Assert.notNull(smsUserDetailsService, "SmsUserDetailsService must not be null");
        Assert.notNull(smsCodeValidator, "SmsCodeValidator must not be null");

        log.info("Initializing SMS login security with processing URL: {}", loginProcessingUrl);
    }

    @Override
    public void configure(HttpSecurity http) {
        SmsAuthenticationFilter filter = new SmsAuthenticationFilter(loginProcessingUrl);

        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        if (authenticationSuccessHandler != null) {
            filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        } else {
            SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
            successHandler.setDefaultTargetUrl("/");
            filter.setAuthenticationSuccessHandler(successHandler);
        }

        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setDefaultFailureUrl("/login?error");
        filter.setAuthenticationFailureHandler(failureHandler);

        SmsAuthenticationProvider provider = new SmsAuthenticationProvider();
        provider.setSmsCodeValidator(smsCodeValidator);
        provider.setSmsUserDetailsService(smsUserDetailsService);

        http.authenticationProvider(provider);
        http.addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);

        log.info("Configured SMS login filter and provider");
    }

    /**
     * Set the SMS user details service.
     *
     * @param smsUserDetailsService the SMS user details service
     * @return this configurer for chaining
     */
    public SmsLoginSecurityConfigurer smsUserDetailsService(SmsUserDetailsService smsUserDetailsService) {
        this.smsUserDetailsService = smsUserDetailsService;
        return this;
    }

    /**
     * Set the SMS code validator.
     *
     * @param smsCodeValidator the SMS code validator
     * @return this configurer for chaining
     */
    public SmsLoginSecurityConfigurer smsCodeValidator(SmsCodeValidator smsCodeValidator) {
        this.smsCodeValidator = smsCodeValidator;
        return this;
    }

    /**
     * Set the login processing URL.
     *
     * @param loginProcessingUrl the login processing URL
     * @return this configurer for chaining
     */
    public SmsLoginSecurityConfigurer loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        return this;
    }

    /**
     * Set the authentication success handler.
     *
     * @param authenticationSuccessHandler the authentication success handler
     * @return this configurer for chaining
     */
    public SmsLoginSecurityConfigurer authenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }
}
