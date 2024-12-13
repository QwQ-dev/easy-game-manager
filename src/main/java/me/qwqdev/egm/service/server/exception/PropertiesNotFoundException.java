package me.qwqdev.egm.service.server.exception;

/**
 * Exception thrown when a required properties file is not found.
 *
 * @author qwq-dev
 * @since 2024-12-08 12:40
 */
public class PropertiesNotFoundException extends RuntimeException {
    /**
     * Instantiates a new Properties not found.
     */
    public PropertiesNotFoundException() {
        super("Properties file not found");
    }

    /**
     * Instantiates a new Properties not found.
     *
     * @param message the message
     */
    public PropertiesNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Properties not found.
     *
     * @param message the message
     * @param cause   the cause
     */
    public PropertiesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Properties not found.
     *
     * @param cause the cause
     */
    public PropertiesNotFoundException(Throwable cause) {
        super(cause);
    }
}
