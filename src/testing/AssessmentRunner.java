package testing;

import java.util.List;

import dao.AssessmentParameterDAO;
import database.Database;
import CRUDAssessment.AssessmentParameter;
import service.AssessmentParameterService;

public class AssessmentRunner {
	
	public static void main(String[] args) {
        try {
            Database db = new Database();
            db.connectToDatabase();

            db.getConnection().createStatement().executeUpdate("DELETE FROM assessment_parameters");
            AssessmentParameterDAO dao = new AssessmentParameterDAO();
            AssessmentParameterService service = new AssessmentParameterService(dao);

            System.out.println("=== CREATE VALID PARAMETER ===");
            AssessmentParameter validParameter = new AssessmentParameter(
                    "Minimum Replies",
                    "Minimum number of replies required",
                    "Participation",
                    3,
                    10.0,
                    true,
                    true,
                    "staff1"
            );

            boolean createResult = service.addParameter(validParameter);
            System.out.println("Create result: " + createResult);

            System.out.println("\n=== LIST ALL PARAMETERS AFTER CREATE ===");
            List<AssessmentParameter> parameters = service.listParameters();
            for (AssessmentParameter p : parameters) {
                System.out.println(
                        p.getParameterId() + " | "
                        + p.getParameterName() + " | "
                        + p.getDescription() + " | "
                        + p.getCategory() + " | "
                        + p.getThresholdValue() + " | "
                        + p.getPointValue() + " | "
                        + p.isRequired() + " | "
                        + p.isActive() + " | "
                        + p.getCreatedBy()
                );
            }

            System.out.println("\n=== CREATE INVALID PARAMETER ===");
            AssessmentParameter invalidParameter = new AssessmentParameter(
                    "",
                    "This should fail",
                    "Participation",
                    -1,
                    -5.0,
                    true,
                    true,
                    "staff1"
            );

            boolean invalidCreateResult = service.addParameter(invalidParameter);
            System.out.println("Invalid create result: " + invalidCreateResult);

            System.out.println("\n=== UPDATE FIRST PARAMETER ===");
            if (!parameters.isEmpty()) {
                AssessmentParameter first = parameters.get(0);
                first.setDescription("Updated description from manual runner");
                first.setPointValue(15.0);

                boolean updateResult = service.editParameter(first);
                System.out.println("Update result: " + updateResult);

                AssessmentParameter updated = service.viewParameter(first.getParameterId());
                System.out.println("Updated parameter: "
                        + updated.getParameterId() + " | "
                        + updated.getParameterName() + " | "
                        + updated.getDescription() + " | "
                        + updated.getPointValue());
            }

            System.out.println("\n=== DEACTIVATE FIRST PARAMETER ===");
            parameters = service.listParameters();
            if (!parameters.isEmpty()) {
                AssessmentParameter first = parameters.get(0);
                boolean deactivateResult = service.deactivateParameter(first.getParameterId());
                System.out.println("Deactivate result: " + deactivateResult);

                AssessmentParameter deactivated = service.viewParameter(first.getParameterId());
                System.out.println("Deactivated parameter active status: " + deactivated.isActive());
            }

            System.out.println("\n=== FINAL LIST OF PARAMETERS ===");
            parameters = service.listParameters();
            for (AssessmentParameter p : parameters) {
                System.out.println(
                        p.getParameterId() + " | "
                        + p.getParameterName() + " | "
                        + p.getDescription() + " | "
                        + p.getCategory() + " | "
                        + p.getThresholdValue() + " | "
                        + p.getPointValue() + " | "
                        + p.isRequired() + " | "
                        + p.isActive() + " | "
                        + p.getCreatedBy()
                );
            }
            
            Database.getStaticConnection().createStatement()
            .executeUpdate("DELETE FROM assessment_parameters");

            db.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}