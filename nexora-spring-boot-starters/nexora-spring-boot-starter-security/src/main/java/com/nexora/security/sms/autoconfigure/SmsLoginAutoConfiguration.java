package com.nexora.security.sms.autoconfigure;

import com.nexora.security.sms.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Auto-configuration for SMS login feature.
 *
 * <p>Registers {@link SmsProperties} for configuration binding.
 *
 * <p>To enable SMS login, set:
 * <pre>
 * nexora.security.sms.enabled=true
 * </pre>
 *
 * <p>Usage:
 * <pre>
 * &#64;Configuration
 * &#64;EnableWebSecurity
 * public class SecurityConfig {
 *
 *     &#64;Autowired
 *     private SmsProperties smsProperties;
 *
 *     &#64;Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         http.apply(SmsLoginSecurityConfigurer.smsLogin())
 *             .smsUserDetailsService(userDetailsService)
 *             .smsCodeValidator(codeValidator)
 *             .loginProcessingUrl(smsProperties.getLoginProcessingUrl());
 *
 *         http.authorizeHttpRequests(auth -&gt; auth
 *             .requestMatchers("/auth/sms/send").permitAll()
 *             .anyRequest().authenticated()
 *         );
 *         return http.build();
 *     }
 *
 *     // Implement SMS sending
 *     &#64;RestController
 *     &#64;RequestMapping("/auth/sms")
 *     public class SmsController {
 *         &#64;PostMapping("/send")
 *         public Result&lt;Void&gt; send(@RequestParam String phone) {
 *             // Generate code
 *             String code = RandomStringUtils.randomNumeric(
 *                 smsProperties.getCodeLength()
 *             );
 *
 *             // Store code with expiration
 *             String key = "sms:code:" + phone;
 *             redisTemplate.opsForValue().set(
 *                 key, code,
 *                 smsProperties.getCodeExpiration()
 *             );
 *
 *             // Send SMS (user implements this)
 *             smsService.send(phone, code);
 *
 *             return Result.ok();
 *         }
 *     }
 * }
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "nexora.security.sms", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SmsProperties.class)
public class SmsLoginAutoConfiguration {

    public SmsLoginAutoConfiguration() {
        log.info("SMS login auto-configuration enabled");
    }
}
