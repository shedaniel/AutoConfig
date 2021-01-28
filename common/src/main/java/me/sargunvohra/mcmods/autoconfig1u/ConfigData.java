package me.sargunvohra.mcmods.autoconfig1u;

public interface ConfigData {
    default void validatePostLoad() throws ValidationException {
    }
    
    class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
        
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
        
        public ValidationException(Throwable cause) {
            super(cause);
        }
    }
}
