package com.cedraz.sas.exams.app.exception;

import com.cedraz.sas.exams.app.config.PropertiesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Optional;

@Component
public class ApplicationException {

    private static PropertiesConfig propertiesConfig;

    @Autowired
    public ApplicationException(PropertiesConfig propertiesConfig) {
        ApplicationException.propertiesConfig = propertiesConfig;
    }

    public static RuntimeException throwException(String messageTemplate, String... args) { //TODO: Remove if never used
        return new RuntimeException(format(messageTemplate, args));
    }

    public static RuntimeException throwException(EntityType entityType, ExceptionType exceptionType, String... args) {
        String messageTemplate = getMessageTemplate(entityType, exceptionType);
        return throwException(exceptionType, messageTemplate, args);
    }

    public static RuntimeException throwExceptionWithId(EntityType entityType, ExceptionType exceptionType, String id, String... args) { //TODO: Remove if never used
        String messageTemplate = getMessageTemplate(entityType, exceptionType).concat(".").concat(id);
        return throwException(exceptionType, messageTemplate, args);
    }

    public static RuntimeException throwExceptionWithTemplate(EntityType entityType, ExceptionType exceptionType, String messageTemplate, String... args) { //TODO: Remove if never used
        return throwException(exceptionType, messageTemplate, args);
    }

    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateEntityException extends RuntimeException {
        public DuplicateEntityException(String message) {
            super(message);
        }
    }

    private static RuntimeException throwException(ExceptionType exceptionType, String messageTemplate, String... args) {
        if (ExceptionType.ENTITY_NOT_FOUND.equals(exceptionType)) {
            return new EntityNotFoundException(format(messageTemplate, args));
        } else if (ExceptionType.DUPLICATE_ENTITY.equals(exceptionType)) {
            return new DuplicateEntityException(format(messageTemplate, args));
        }
        return new RuntimeException(format(messageTemplate, args));
    }

    private static String getMessageTemplate(EntityType entityType, ExceptionType exceptionType) {
        return entityType.name().concat(".").concat(exceptionType.getValue()).toLowerCase();
    }

    private static String format(String template, String... args) {
        Optional<String> templateContent = Optional.ofNullable(propertiesConfig.getConfigValue(template));
        return templateContent.map(s -> MessageFormat.format(s, args)).orElseGet(() -> String.format(template, args));
    }

}