package com.wavy.validator;

import com.wavy.utils.ValidationUtil;
import  javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

/**
 *  IsMobileValidator
 *  手机号码校验器
 * Created by WavyPeng on 2018/5/11.
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required) {
            return ValidationUtil.isMobile(value);
        }else {
            if(StringUtils.isEmpty(value)) {
                return true;
            }else {
                return ValidationUtil.isMobile(value);
            }
        }
    }

}
