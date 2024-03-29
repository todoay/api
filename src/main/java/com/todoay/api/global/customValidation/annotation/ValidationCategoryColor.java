package com.todoay.api.global.customValidation.annotation;

import com.todoay.api.global.customValidation.validator.CategoryColorValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryColorValidator.class)
public @interface ValidationCategoryColor {
    String message() default "카테고리 색상은 null이거나 공백일 수 없으며 색상 형식을 따라야 합니다. {# 포함 또는 미포함}{숫자 3 또는 4 또는 6 또는 8 자리}";
    Class[] groups() default {};
    Class[] payload() default {};
}
