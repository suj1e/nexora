package com.nexora.security.sms;

/**
 * Service interface for validating SMS verification codes.
 *
 * <p>Users must implement this interface to provide SMS code validation.
 * This typically involves checking against a cache (e.g., Redis) where
 * the verification code was stored after sending.
 *
 * <p>Implementation example with Redis:
 * <pre>
 * &#64;Bean
 * public SmsCodeValidator smsCodeValidator(RedisTemplate&lt;String, String&gt; redisTemplate) {
 *     return (phone, code) -&gt; {
 *         String key = "sms:code:" + phone;
 *         String stored = redisTemplate.opsForValue().get(key);
 *         if (Objects.equals(stored, code)) {
 *             redisTemplate.delete(key);  // One-time use
 *             return true;
 *         }
 *         return false;
 *     };
 * }
 * </pre>
 *
 * @author sujie
 */
@FunctionalInterface
public interface SmsCodeValidator {

    /**
     * Validate SMS verification code for the given phone number.
     *
     * @param phone the phone number
     * @param code  the SMS verification code to validate
     * @return true if the code is valid, false otherwise
     */
    boolean validate(String phone, String code);
}
