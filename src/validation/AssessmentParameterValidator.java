package validation;

import CRUDAssessment.AssessmentParameter;

public class AssessmentParameterValidator {

    public boolean validateParameter(AssessmentParameter parameter) {
        if (parameter == null) {
            return false;
        }

        if (parameter.getParameterName() == null || parameter.getParameterName().trim().isEmpty()) {
            return false;
        }

        if (parameter.getParameterName().length() > 100) {
            return false;
        }

        if (parameter.getCategory() == null || parameter.getCategory().trim().isEmpty()) {
            return false;
        }

        if (parameter.getDescription() != null && parameter.getDescription().length() > 255) {
            return false;
        }

        if (parameter.getThresholdValue() != null && parameter.getThresholdValue() < 0) {
            return false;
        }

        if (parameter.getPointValue() != null && parameter.getPointValue() < 0) {
            return false;
        }

        if (parameter.getCreatedBy() == null || parameter.getCreatedBy().trim().isEmpty()) {
            return false;
        }

        return true;
    }
}