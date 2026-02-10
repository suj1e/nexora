package com.nexora.security.sms;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

/**
 * Filter for processing SMS-based authentication requests.
 *
 * <p>Extracts phone number and SMS code from request parameters
 * and creates an {@link SmsAuthenticationToken} for authentication.
 *
 * <p>Default request parameters:
 * <ul>
 *   <li>phone: the phone number</li>
 *   <li>smsCode: the SMS verification code</li>
 * </ul>
 *
 * @author sujie
 */
@Slf4j
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_PROCESSING_URL = "/auth/sms/login";

    private static final String PHONE_PARAMETER = "phone";

    private static final String SMS_CODE_PARAMETER = "smsCode";

    private String phoneParameter = PHONE_PARAMETER;

    private String smsCodeParameter = SMS_CODE_PARAMETER;

    private boolean postOnly = true;

    /**
     * Creates a new SMS authentication filter with default URL.
     */
    public SmsAuthenticationFilter() {
        super(request -> {
            return DEFAULT_LOGIN_PROCESSING_URL.equals(request.getServletPath())
                    && HttpMethod.POST.name().equals(request.getMethod());
        });
    }

    /**
     * Creates a new SMS authentication filter with custom URL.
     *
     * @param loginProcessingUrl the login processing URL
     */
    public SmsAuthenticationFilter(String loginProcessingUrl) {
        super(request -> {
            return loginProcessingUrl.equals(request.getServletPath())
                    && HttpMethod.POST.name().equals(request.getMethod());
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String phone = obtainPhone(request);
        String smsCode = obtainSmsCode(request);

        if (phone == null) {
            phone = "";
        }
        if (smsCode == null) {
            smsCode = "";
        }

        phone = phone.trim();

        log.debug("Attempting SMS authentication for phone: {}", maskPhone(phone));

        SmsAuthenticationToken authRequest = new SmsAuthenticationToken(phone, smsCode);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Get the phone number from the request.
     *
     * @param request the HTTP request
     * @return the phone number
     */
    protected String obtainPhone(HttpServletRequest request) {
        return request.getParameter(phoneParameter);
    }

    /**
     * Get the SMS code from the request.
     *
     * @param request the HTTP request
     * @return the SMS code
     */
    protected String obtainSmsCode(HttpServletRequest request) {
        return request.getParameter(smsCodeParameter);
    }

    /**
     * Set additional details on the authentication token.
     *
     * @param request     the HTTP request
     * @param authRequest the authentication token
     */
    protected void setDetails(HttpServletRequest request, SmsAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    /**
     * Set the phone parameter name.
     *
     * @param phoneParameter the phone parameter name
     */
    public void setPhoneParameter(String phoneParameter) {
        Assert.hasText(phoneParameter, "Phone parameter must not be empty or null");
        this.phoneParameter = phoneParameter;
    }

    /**
     * Set the SMS code parameter name.
     *
     * @param smsCodeParameter the SMS code parameter name
     */
    public void setSmsCodeParameter(String smsCodeParameter) {
        Assert.hasText(smsCodeParameter, "SMS code parameter must not be empty or null");
        this.smsCodeParameter = smsCodeParameter;
    }

    /**
     * Set whether only POST requests are allowed.
     *
     * @param postOnly true if only POST allowed
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
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
