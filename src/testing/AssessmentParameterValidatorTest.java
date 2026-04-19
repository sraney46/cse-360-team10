package testing;

import CRUDAssessment.AssessmentParameter;
import org.junit.jupiter.api.Test;
import validation.AssessmentParameterValidator;

import static org.junit.jupiter.api.Assertions.*;

public class AssessmentParameterValidatorTest {

    @Test
    public void testValidParameter() {
        AssessmentParameter parameter = new AssessmentParameter(
                "Minimum Replies",
                "Minimum number of required replies",
                "Participation",
                3,
                10.0,
                true,
                true,
                "staffUser"
        );

        AssessmentParameterValidator validator = new AssessmentParameterValidator();
        assertTrue(validator.validateParameter(parameter));
    }

    @Test
    public void testBlankNameFails() {
        AssessmentParameter parameter = new AssessmentParameter(
                "",
                "Description",
                "Participation",
                3,
                10.0,
                true,
                true,
                "staffUser"
        );

        AssessmentParameterValidator validator = new AssessmentParameterValidator();
        assertFalse(validator.validateParameter(parameter));
    }

    @Test
    public void testNegativeThresholdFails() {
        AssessmentParameter parameter = new AssessmentParameter(
                "Minimum Replies",
                "Description",
                "Participation",
                -1,
                10.0,
                true,
                true,
                "staffUser"
        );

        AssessmentParameterValidator validator = new AssessmentParameterValidator();
        assertFalse(validator.validateParameter(parameter));
    }

    @Test
    public void testNegativePointValueFails() {
        AssessmentParameter parameter = new AssessmentParameter(
                "Minimum Replies",
                "Description",
                "Participation",
                3,
                -5.0,
                true,
                true,
                "staffUser"
        );

        AssessmentParameterValidator validator = new AssessmentParameterValidator();
        assertFalse(validator.validateParameter(parameter));
    }
}