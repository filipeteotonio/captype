package br.ironspark.captype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the fields as a CaptureField so that it can be evaluated and have its
 * metadata exported
 * 
 * @author filipemendonca
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CaptureField {
	public String name() default "";

	public String displayName() default "";

	public int displayOrder() default 0;
}
