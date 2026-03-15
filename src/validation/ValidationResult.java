package validation;

public class ValidationResult {

    private final boolean valid;
    private final String message;

    /***
     * A setter altering the validity of the method
     * 
     * @param valid - set the property 'valid' with the input
     * @param message - set the message of the result with 'message'
     */
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    /***
     *  A method to get the value of this ValidationResult's valid property
     * @return validity
     */
    public boolean isValid() {
        return valid;
    }

    /***
     *  A method to get the string of this ValidationResult's message
     * @return message
     */
    public String getMessage() {
        return message;
    }
}
