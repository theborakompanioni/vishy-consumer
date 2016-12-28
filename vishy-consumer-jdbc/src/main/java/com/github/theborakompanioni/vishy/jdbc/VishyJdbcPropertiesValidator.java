package com.github.theborakompanioni.vishy.jdbc;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.google.common.base.Preconditions.checkArgument;

public class VishyJdbcPropertiesValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return VishyJdbcProperties.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        checkArgument(supports(target.getClass()), "Unsupported type.");

        VishyJdbcProperties properties = (VishyJdbcProperties) target;

        // if tableName is `null`, a default table name is applied
        if (properties.getTableName() != null && !isValidTableName(properties.getTableName())) {
            errors.rejectValue("tableName", "tableName.invalid", "Invalid table name.");
        }
        if (properties.getJdbcUrl() == null || Strings.isNullOrEmpty(properties.getJdbcUrl())) {
            errors.rejectValue("jdbcUrl", "jdbcUrl.empty", "Empty jdbc url.");
        }
    }

    private boolean isValidTableName(String tableName) {
        return CharMatcher.javaLetterOrDigit().or(CharMatcher.anyOf("._")).matchesAllOf(tableName);
    }
}
