package com.nexora.web.autoconfigure;

import com.nexora.web.aspect.ResponseWrapperAspect;
import com.nexora.web.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Common web auto-configuration.
 *
 * <p>Automatically enables:
 * <ul>
 *   <li>Response wrapper AOP - wraps all controller responses in {@link com.nexora.web.model.Result}</li>
 *   <li>Global exception handler - handles all exceptions and returns unified format</li>
 * </ul>
 *
 * @author sujie
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackageClasses = {ResponseWrapperAspect.class, GlobalExceptionHandler.class})
public class CommonWebAutoConfiguration {

}
