package com.nexora.security.sms;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service interface for loading user details by phone number.
 *
 * <p>Users must implement this interface to provide user lookup
 * for SMS-based authentication.
 *
 * <p>Usage example:
 * <pre>
 * &#64;Bean
 * public SmsUserDetailsService smsUserDetailsService(UserRepository userRepository) {
 *     return phone -&gt; {
 *         User user = userRepository.findByPhone(phone)
 *             .orElseThrow(() -&gt; new UsernameNotFoundException("User not found"));
 *         return User.withUsername(user.getUsername())
 *             .password(user.getPassword())
 *             .roles("USER")
 *             .build();
 *     };
 * }
 * </pre>
 *
 * @author sujie
 */
@FunctionalInterface
public interface SmsUserDetailsService {

    /**
     * Load user details by phone number.
     *
     * @param phone the phone number
     * @return the user details
     * @throws UsernameNotFoundException if user not found
     */
    UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException;
}
