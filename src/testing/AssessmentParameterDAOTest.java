package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.AssessmentParameterDAO;
import database.Database;
import CRUDAssessment.AssessmentParameter;

public class AssessmentParameterDAOTest {

    private Database database;
    private AssessmentParameterDAO dao;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        database = new Database();
        database.connectToDatabase();
        connection = database.getConnection();
        dao = new AssessmentParameterDAO(connection);

        clearAssessmentParametersTable();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        clearAssessmentParametersTable();
        database.closeConnection();
    }

    private void clearAssessmentParametersTable() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM assessment_parameters")) {
            stmt.executeUpdate();
        }
    }

    private int getInsertedParameterIdByName(String parameterName) throws SQLException {
        String sql = "SELECT parameter_id FROM assessment_parameters WHERE parameter_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, parameterName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("parameter_id");
                }
            }
        }
        return -1;
    }

    @Test
    public void testCreateParameter() throws SQLException {
        AssessmentParameter parameter = new AssessmentParameter(
                "Minimum Replies",
                "Minimum number of replies required",
                "Participation",
                3,
                10.0,
                true,
                true,
                "staff1"
        );

        boolean created = dao.createParameter(parameter);

        assertTrue(created);

        List<AssessmentParameter> allParameters = dao.getAllParameters();
        assertEquals(1, allParameters.size());
        assertEquals("Minimum Replies", allParameters.get(0).getParameterName());
    }

    @Test
    public void testGetParameterById() throws SQLException {
        AssessmentParameter parameter = new AssessmentParameter(
                "Unique Students Replied To",
                "Minimum unique students replied to",
                "Reply Requirement",
                3,
                15.0,
                true,
                true,
                "staff1"
        );

        dao.createParameter(parameter);
        int id = getInsertedParameterIdByName("Unique Students Replied To");

        AssessmentParameter retrieved = dao.getParameterById(id);

        assertNotNull(retrieved);
        assertEquals("Unique Students Replied To", retrieved.getParameterName());
        assertEquals("Reply Requirement", retrieved.getCategory());
        assertEquals(3, retrieved.getThresholdValue());
    }

    @Test
    public void testUpdateParameter() throws SQLException {
        AssessmentParameter parameter = new AssessmentParameter(
                "Initial Post Required",
                "Student must create an initial post",
                "Participation",
                1,
                5.0,
                true,
                true,
                "staff1"
        );

        dao.createParameter(parameter);
        int id = getInsertedParameterIdByName("Initial Post Required");

        AssessmentParameter existing = dao.getParameterById(id);
        assertNotNull(existing);

        existing.setDescription("Updated description");
        existing.setPointValue(8.0);

        boolean updated = dao.updateParameter(existing);
        assertTrue(updated);

        AssessmentParameter updatedParameter = dao.getParameterById(id);
        assertEquals("Updated description", updatedParameter.getDescription());
        assertEquals(8.0, updatedParameter.getPointValue());
    }

    @Test
    public void testDeactivateParameter() throws SQLException {
        AssessmentParameter parameter = new AssessmentParameter(
                "Manual Quality Review",
                "Requires staff review of answer quality",
                "Quality",
                null,
                20.0,
                true,
                true,
                "staff1"
        );

        dao.createParameter(parameter);
        int id = getInsertedParameterIdByName("Manual Quality Review");

        boolean deactivated = dao.deactivateParameter(id);
        assertTrue(deactivated);

        AssessmentParameter retrieved = dao.getParameterById(id);
        assertNotNull(retrieved);
        assertFalse(retrieved.isActive());
    }
}