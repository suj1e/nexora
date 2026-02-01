package com.nexora.security.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * Authentication provider for SMS-based login.
 *
 * <p>Validates SMS verification code via {@link SmsCodeValidator}
 * and loads user details via {@link SmsUserDetailsService}.
 *
 * @author sujie
 */
@Slf4j
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private SmsCodeValidator smsCodeValidator;

    private SmsUserDetailsService smsUserDetailsService;

    /**
     * Creates a new SMS authentication provider.
     */
    public SmsAuthenticationProvider() {
    }

    /**
     * Creates a new SMS authentication provider with dependencies.
     *
     * @param smsCodeValidator     the SMS code validator
     * @param smsUserDetailsService the SMS user details service
     */
    public SmsAuthenticationProvider(SmsCodeValidator smsCodeValidator,
                                      SmsUserDetailsService smsUserDetailsService) {
        this.smsCodeValidator = smsCodeValidator;
        this.smsUserDetailsService = smsUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(smsCodeValidator, "SmsCodeValidator must not be null");
        Assert.notNull(smsUserDetailsService, "SmsUserDetailsService must not be null");

        SmsAuthenticationToken authRequest = (SmsAuthenticationToken) authentication;

        String phone = authRequest.getPhone();
        String smsCode = authRequest.getSmsCode();

        log.debug("Authenticating SMS code for phone: {}", maskPhone(phone));

        // Validate SMS code
        boolean codeValid = smsCodeValidator.validate(phone, smsCode);
        if (!codeValid) {
            log.warn("Invalid SMS code for phone: {}", maskPhone(phone));
            throw new BadCredentialsException("Invalid SMS verification code");
        }

        // Load user details
        UserDetails userDetails = smsUserDetailsService.loadUserByPhone(phone);

        // Create authenticated token
        SmsAuthenticationToken authenticatedToken = new SmsAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authenticatedToken.setDetails(authRequest.getDetails());

        log.info("Successfully authenticated user via SMS for phone: {}", maskPhone(phone));

        return authenticatedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Set the SMS code validator.
     *
     * @param smsCodeValidator the SMS code validator
     */
    public void setSmsCodeValidator(SmsCodeValidator smsCodeValidator) {
        this.smsCodeValidator = smsCodeValidator;
    }

    /**
     * Set the SMS user details service.
     *
     * @param smsUserDetailsService the SMS user details service
     */
    public void setSmsUserDetailsService(SmsUserDetailsService smsUserDetailsService) {
        this.smsUserDetailsService = smsUserDetailsService;
    }

    /**
     * Mask phone number for logging (show only first 3 and last 4 digits).
     *
     * @param phone the phone number
     * @return the masked phone number
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
