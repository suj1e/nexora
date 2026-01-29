package com.nexora.security.sms;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication token for SMS-based login.
 *
 * <p>Supports two states:
 * <ul>
 *   <li>Unauthenticated: Created with phone number and SMS code</li>
 *   <li>Authenticated: Created after successful validation with user details and authorities</li>
 * </ul>
 *
 * @author sujie
 */
public class SmsAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an unauthenticated authentication token.
     *
     * @param phone  the phone number
     * @param smsCode the SMS verification code
     */
    public SmsAuthenticationToken(Object phone, Object smsCode) {
        super(phone, smsCode);
    }

    /**
     * Creates an authenticated authentication token.
     *
     * @param principal   the authenticated user principal
     * @param credentials the credentials (typically null after authentication)
     * @param authorities the granted authorities
     */
    public SmsAuthenticationToken(Object principal, Object credentials,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    /**
     * Get the phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return getPrincipal() != null ? getPrincipal().toString() : null;
    }

    /**
     * Get the SMS code.
     *
     * @return the SMS code
     */
    public String getSmsCode() {
        return getCredentials() != null ? getCredentials().toString() : null;
    }
}
