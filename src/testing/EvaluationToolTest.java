package testing;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import database.Database;
import entityClasses.EvaluationTool;
import entityClasses.EvaluationTool.EvaluationRow;

/**
 * <p>
 * Title: EvaluationToolTest
 * </p>
 *
 * <p>
 * Description: Test cases for Elval tool connecting to DB and assinging a grade.
 * </p>
 *
 * @author Jonathan Stark
 * @version 1.00 2026-04-05 
 */
public class EvaluationToolTest {

    private static final String TABLE = "evaluationTool";
    private static Database database = new Database();
    private final EvaluationTool tool = new EvaluationTool(database);
    private boolean connectedDB = false;

    /**
     * <p>
     * Method: setupTestCase()
     * </p>
     *
     * <p>
     * Description: Generates the environment needed to run test cases by connecting to DB.
     * </p>
     */
    public void setupTestCase() {
        if (!connectedDB) {
            try {
                database.connectToDatabase();
                connectedDB = true;
            } catch (SQLException e) {
                System.exit(0);
            }
        }
        try {
            database.getConnection().createStatement().executeUpdate("DELETE FROM " + TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Method: cleanTestCases()
     * </p>
     *
     * <p>
     * Description: Cleans DB.
     * </p>
     */
    public void cleanTestCases() {
        try {
            database.getConnection().createStatement().executeUpdate("DELETE FROM " + TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.closeConnection();
    }

 

    /**
     * <p>
     * Method: letterGradeMapping()
     * </p>
     * <p>
     * Description: Letter grade boundaries map correctly
     * </p>
     */
    @Test
    public void letterGradeMapping() {
        setupTestCase();
        assertEquals("A", EvaluationTool.computeLetterGrade(100));
        assertEquals("A", EvaluationTool.computeLetterGrade(90));
        assertEquals("B", EvaluationTool.computeLetterGrade(89));
        assertEquals("B", EvaluationTool.computeLetterGrade(80));
        assertEquals("C", EvaluationTool.computeLetterGrade(79));
        assertEquals("C", EvaluationTool.computeLetterGrade(70));
        assertEquals("D", EvaluationTool.computeLetterGrade(69));
        assertEquals("D", EvaluationTool.computeLetterGrade(60));
        assertEquals("F", EvaluationTool.computeLetterGrade(59));
        assertEquals("F", EvaluationTool.computeLetterGrade(0));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: saveAndFindByStudentId()
     * </p>
     * <p>
     * Description: A saved row can be retrieved by student id.
     * </p>
     */
    @Test
    public void saveAndFindByStudentId() throws SQLException {
        setupTestCase();
        EvaluationRow et = EvaluationTool.compute(3,
                new String[] { "Quality", "Participation" },
                new Double[] { 50.0, 50.0 },
                new double[] { 45.0, 40.0 });
        tool.save(et);

        EvaluationRow loaded = tool.findById(3);
        assertNotNull(loaded);
        assertEquals(3, loaded.studentID);
        assertEquals(85.0, loaded.percentage, 0.001);
        assertEquals(85, loaded.numberGrade);
        assertEquals("B", loaded.letterGrade);
        cleanTestCases();
    }

    /**
     * <p>
     * Method: multipleRowsFindAll()
     * </p>
     * <p>
     * Description: All saved rows are returned by findAll
     * </p>
     */
    @Test
    public void multipleRowsFindAll() throws SQLException {
        setupTestCase();
        for (int i = 0; i < 3; i++) {
            tool.save(EvaluationTool.compute(100 + i,
                    new String[] { "A" },
                    new Double[] { 100.0 },
                    new double[] { 80.0 + i }));
        }
        assertEquals(3, tool.findAll().size());
        cleanTestCases();
    }

    /**
     * <p>
     * Method: updateRowViaSql()
     * </p>
     * <p>
     * Description: A row's grade fields can be updated via SQL
     * </p>
     */
    @Test
    public void updateRowViaSql() throws SQLException {
        setupTestCase();
        tool.save(EvaluationTool.compute(42, new String[] { "One" }, new Double[] { 100.0 }, new double[] { 70.0 }));

        EvaluationRow updated = EvaluationTool.compute(42, new String[] { "One" }, new Double[] { 100.0 }, new double[] { 95.0 });
        String upd = "UPDATE " + TABLE + " SET percentage = ?, numberGrade = ?, letterGrade = ? WHERE studentID = ?";
        try (PreparedStatement ps = database.getConnection().prepareStatement(upd)) {
            ps.setDouble(1, updated.percentage);
            ps.setInt(2, updated.numberGrade);
            ps.setString(3, updated.letterGrade);
            ps.setInt(4, 42);
            ps.executeUpdate();
        }

        EvaluationRow loaded = tool.findById(42);
        assertNotNull(loaded);
        assertEquals(95.0, loaded.percentage, 0.001);
        assertEquals(95, loaded.numberGrade);
        assertEquals("A", loaded.letterGrade);
        cleanTestCases();
    }

    /**
     * <p>
     * Method: distinctStudentsDoNotCollide()
     * </p>
     * <p>
     * Description: Rows for different students are stored independently
     * </p>
     */
    @Test
    public void distinctStudentsDoNotCollide() throws SQLException {
        setupTestCase();
        tool.save(EvaluationTool.compute(501, new String[] { "X" }, new Double[] { 100.0 }, new double[] { 10.0 }));
        tool.save(EvaluationTool.compute(502, new String[] { "Y" }, new Double[] { 100.0 }, new double[] { 5.0 }));
        assertEquals(2, tool.findAll().size());
        assertEquals(501, tool.findById(501).studentID);
        assertEquals(502, tool.findById(502).studentID);
        cleanTestCases();
    }

    /**
     * <p>
     * Method: findByStudentIdMissingReturnsNull()
     * </p>
     * <p>
     * Description: Querying a non-existent student id returns null.
     * </p>
     */
    @Test
    public void findByStudentIdMissingReturnsNull() throws SQLException {
        setupTestCase();
        assertNull(tool.findById(999999));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: computeRejectsMismatchedLengths()
     * </p>
     * <p>
     * Description: compute() throws when array lengths do not match
     * </p>
     */
    @Test
    public void computeRejectsMismatchedLengths() {
        setupTestCase();
        assertThrows(IllegalArgumentException.class, () ->
                EvaluationTool.compute(1,
                        new String[] { "a", "b" },
                        new Double[] { 1.0 },
                        new double[] { 1.0, 2.0 }));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: computeRejectsNullArrays()
     * </p>
     * <p>
     * Description: compute() throws when a null array is passed
     * </p>
     */
    @Test
    public void computeRejectsNullArrays() {
        setupTestCase();
        assertThrows(IllegalArgumentException.class, () ->
                EvaluationTool.compute(1, null, new Double[] { 1.0 }, new double[] { 1.0 }));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: rubricAcceptsAccuracySpellingGrammarLengthWeights25Each()
     * </p>
     * <p>
     * Description: Standard rubric with four parameters at 25% each is accepted
     * </p>
     */
    @Test
    public void rubricAcceptsAccuracySpellingGrammarLengthWeights25Each() throws SQLException {
        setupTestCase();
        String[] params = { "Accuracy", "Spelling", "Grammar", "Length" };
        Double[] weights = { 25.0, 25.0, 25.0, 25.0 };
        tool.save(EvaluationTool.compute(700, params, weights, new double[] { 20, 20, 20, 20 }));

        EvaluationRow loaded = tool.findById(700);
        assertNotNull(loaded);
        assertEquals(4, loaded.definedParams.length);
        assertEquals(100.0,
                loaded.paramWeights[0] + loaded.paramWeights[1] + loaded.paramWeights[2] + loaded.paramWeights[3],
                0.001);
        cleanTestCases();
    }

    /**
     * <p>
     * Method: rubricRejectsWhenWeightsDoNotTotal100()
     * </p>
     * <p>
     * Description: Rubric validation throws when weights do not sum to 100
     * </p>
     */
    @Test
    public void rubricRejectsWhenWeightsDoNotTotal100() {
        setupTestCase();
        assertThrows(IllegalArgumentException.class, () ->
                EvaluationTool.validateEvaluationRubric(
                        new String[] { "Accuracy", "Spelling" },
                        new Double[] { 40.0, 50.0 }));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: rubricRejectsBlankParameterName()
     * </p>
     * <p>
     * Description: Rubric validation throws when a parameter name is blank
     * </p>
     */
    @Test
    public void rubricRejectsBlankParameterName() {
        setupTestCase();
        assertThrows(IllegalArgumentException.class, () ->
                EvaluationTool.validateEvaluationRubric(
                        new String[] { "Accuracy", "    ", "Grammar" },
                        new Double[] { 40.0, 30.0, 30.0 }));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: rubricRejectsNullWeight()
     * </p>
     * <p>
     * Description: Rubric validation throws when a weight is null
     * </p>
     */
    @Test
    public void rubricRejectsNullWeight() {
        setupTestCase();
        assertThrows(IllegalArgumentException.class, () ->
                EvaluationTool.validateEvaluationRubric(
                        new String[] { "Accuracy", "Spelling" },
                        new Double[] { 50.0, null }));
        cleanTestCases();
    }

    /**
     * <p>
     * Method: rubricRejectsNoParameters()
     * </p>
     * <p>
     * Description: Rubric validation throws when no parameters are provided
     * </p>
     */
    @Test
    public void rubricRejectsNoParameters() {
        setupTestCase();
        assertThrows(IllegalArgumentException.class, () ->
                EvaluationTool.validateEvaluationRubric(new String[0], new Double[0]));
        cleanTestCases();
    }
}
