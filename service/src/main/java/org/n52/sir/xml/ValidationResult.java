
package org.n52.sir.xml;

import java.util.ArrayList;
import java.util.Collection;

import scala.actors.threadpool.Arrays;

public class ValidationResult {

    private boolean validated;

    private Collection<String> validationFailures;

    public ValidationResult(boolean validated, Collection<String> validationFailures) {
        super();
        this.validated = validated;
        this.validationFailures = validationFailures;
    }

    public ValidationResult(boolean validated) {
        this(validated, new ArrayList<String>());
    }

    public ValidationResult(boolean validated, Exception e) {
        this(validated, e.getMessage());
    }

    @SuppressWarnings("unchecked")
    public ValidationResult(boolean validated, String message) {
        this(validated, Arrays.asList(new String[] {message}));
    }

    public boolean isValidated() {
        return this.validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Collection<String> getValidationFailures() {
        return this.validationFailures;
    }

    public void setValidationFailures(Collection<String> validationFailures) {
        this.validationFailures = validationFailures;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ValidationResult [validated=");
        builder.append(this.validated);
        builder.append(", ");
        if (this.validationFailures != null) {
            builder.append("validationFailures=");
            builder.append(this.validationFailures);
        }
        builder.append("]");
        return builder.toString();
    }

    public String getValidationFailuresAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The document is NOT valid:\n");
        for (String string : this.validationFailures) {
            sb.append(string);
            sb.append("\n");
        }
        return sb.toString();
    }

}
