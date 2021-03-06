package info.smart_tools.smartactors.feature_management.feature_manager_actor.exception;

/**
 * Exception for {@link info.smart_tools.smartactors.feature_management.feature_manager_actor.FeatureManagerActor}
 */
public class FeatureManagementException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FeatureManagementException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public FeatureManagementException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FeatureManagementException(final Throwable cause) {
        super(cause);
    }
}
