package it.workstocks.validator.vatnumber;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = CustomVatNumberValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface VatNumberConstraint {
    String message() default "{loginAndRegister.vatNumberValidation}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

