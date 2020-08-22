package br.ironspark.captype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the entity as a CaptureEntity in order to be scanned and have its
 * metadata exported
 * 
 * @author filipemendonca
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CaptureEntity {
	public String name() default "";
	
	public DisplayNameEstrategy displayNameEstrategy() default DisplayNameEstrategy.DEFAULT;
}
