package com.nexora.web.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Common web auto-configuration.
 *
 * <p>Automatically enables:
 * <ul>
 *   <li>Global exception handler - handles all exceptions and returns unified {@link com.nexora.common.api.Result} format</li>
 * </ul>
 *
 * @author sujie
 */
@AutoConfiguration
@ConditionalOnWebApplication
@ComponentScan(basePackageClasses = {GlobalExceptionHandler.class})
public class CommonWebAutoConfiguration {

}
