package entityClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import database.Database;

/**
 * <p>
 * Title: EvaluationTool
 * </p>
 *
 * <p>
 * Description: Core logic for computing student grades and managing database 
 * operations for the Evaluation tool.
 * </p>
 *
 * @author Jonathan Stark
 * @version 1.00 2026-04-05 
 */
public class EvaluationTool {

    private static final String TABLE = "evaluationTool";
    private Database database;
    public static final double EVALUATION_RUBRIC_WEIGHT_TOTAL = 100.0;
    private static final double EVALUATION_RUBRIC_WEIGHT_EPSILON = 0.001;



    public EvaluationTool(Database database) {
        this.database = database;
    }

    /**
     * Snapshot of one row for evaluations.
     */
    public static final class EvaluationRow {
        public final int studentID;
        public final String[] definedParams;
        public final Double[] paramWeights;
        public final double percentage;
        public final int numberGrade;
        public final String letterGrade;

        public EvaluationRow(int studentID, String[] definedParams, Double[] paramWeights,
                             double percentage, int numberGrade, String letterGrade) {
            this.studentID = studentID;
            this.definedParams = definedParams;
            this.paramWeights = paramWeights;
            this.percentage = percentage;
            this.numberGrade = numberGrade;
            this.letterGrade = letterGrade;
        }
    }

    /**
     * <p>
     * Method: computeLetterGrade()
     * </p>
     *
     * <p>
     * Description: Converts a numeric percent score into a letter grade
     * </p>
     *
     * @param percentScore the numeric score as a percentage 
     * @return the letter grade as a string.
     */
    public static String computeLetterGrade(int percentScore) {
        if (percentScore >= 90) return "A";
        if (percentScore >= 80) return "B";
        if (percentScore >= 70) return "C";
        if (percentScore >= 60) return "D";
        return "F";
    }

    /**
     * <p>
     * Method: compute()
     * </p>
     *
     * <p>
     * Description: Computes a student's evaluation result by summing raw scores
     * and weights, calculating a percentage, rounding to a number grade, and
     * mapping to a letter grade
     * </p>
     *
     * @param studentName   the name of the student being evaluated
     * @param definedParams the rubric parameter names 
     * @param paramWeights  the maximum weight of 100
     * @param scores        the student's actual scores for each param
     * @return a new EvaluationRow containing the student's name, params, weights,
     * computed percentage, rounded number grade, and letter grade
     * @throws IllegalArgumentException if any array is null or if the arrays do
     * not all have the same length
     */
    public static EvaluationRow compute(int studentID, String[] definedParams,
                                        Double[] paramWeights, double[] scores) {
        if (definedParams == null || paramWeights == null || scores == null) {
            throw new IllegalArgumentException("Arrays must not be null.");
        }
        if (definedParams.length != paramWeights.length || paramWeights.length != scores.length) {
            throw new IllegalArgumentException("Rubric arrays must have the same length.");
        }
        double sumMax = 0, sumScore = 0;
        for (int i = 0; i < paramWeights.length; i++) {
            sumMax += paramWeights[i];
            sumScore += scores[i];
        }
        double pct = sumMax > 0 ? (sumScore / sumMax) * 100.0 : 0;
        int num = (int) Math.round(pct);
        return new EvaluationRow(studentID, definedParams, paramWeights, pct, num, computeLetterGrade(num));
    }

    /**
     * <p>
     * Method: save()
     * </p>
     *
     * <p>
     * Description: saves an EvaluationRow to the database.
     * </p>
     *
     * @param row the EvaluationRow to save
     */
    public void save(EvaluationRow row) {
        database.insertEvaluation(row.studentID, row.definedParams, row.paramWeights,
                row.percentage, row.numberGrade, row.letterGrade);
    }

    /**
     * <p>
     * Method: findAll()
     * </p>
     *
     * <p>
     * Description: finds all calculated evaluation in the database.
     * </p>
     *
     * @return a List<EvaluationRow> of all the evaluations in an list.
     */
    public List<EvaluationRow> findAll() throws SQLException {
        List<EvaluationRow> list = new ArrayList<>();
        try (Statement st = database.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + TABLE)) {
            while (rs.next()) list.add(readRow(rs));
        }
        return list;
    }

    /**
     * <p>
     * Method: findByStudentName()
     * </p>
     *
     * <p>
     * Description: Searches the DB table for a single row matching
     * the given student name.
     * </p>
     *
     * @param studentName the name of the student to search for
     * @return the matching EvaluationRow if found, or null if no row exists for
     * the given student name
     * @throws SQLException if a database access error occurs
     */
    public EvaluationRow findByStudentName(String studentName) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE studentName = ?";
        try (PreparedStatement ps = database.getConnection().prepareStatement(sql)) {
            ps.setString(1, studentName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readRow(rs) : null;
            }
        }
    }

    /**
     * <p>
     * Method: readRow()
     * </p>
     *
     * <p>
     * Description: sets all the values found in the DB to an EvaluationRow.
     * </p>
     *
     * @param rs the value needing to be set.
     * @return a new EvaluationRow populated with the data from the current values.
     * @throws SQLException if a database access error occurs or a column name
     * cannot be found
     */
    private static EvaluationRow readRow(ResultSet rs) throws SQLException {
        return new EvaluationRow(
                rs.getInt("studentID"),
                toStringArray(rs.getArray("definedParams")),
                toDoubleArray(rs.getArray("paramWeights")),
                rs.getDouble("percentage"),
                rs.getInt("numberGrade"),
                rs.getString("letterGrade"));
    }

    /**
     * <p>
     * Method: toStringArray()
     * </p>
     *
     * <p>
     * Description: Converts a DB Array into a Java String array
     * </p>
     *
     * @param sqlArray the DB Array to convert, or null
     * @return a String[] from the DB Array, or null if sqlArray is null
     * @throws SQLException if a database access error occurs
     */
    private static String[] toStringArray(Array sqlArray) throws SQLException {
        if (sqlArray == null) return null;
        try {
            Object data = sqlArray.getArray();
            if (data instanceof String[]) return (String[]) data;
            Object[] boxed = (Object[]) data;
            String[] out = new String[boxed.length];
            for (int i = 0; i < boxed.length; i++) 
                out[i] = boxed[i] != null ? boxed[i].toString() : null;
            return out;
        } finally { sqlArray.free(); }
    }

    /**
     * <p>
     * Method: toDoubleArray()
     * </p>
     *
     * <p>
     * Description: Converts a DB Array into a Double array
     * </p>
     *
     * @param sqlArray the DB Array to convert, or null
     * @return a Double[] from the DB Array, or null if sqlArray is null
     * @throws SQLException if a database access error occurs
     */
    private static Double[] toDoubleArray(Array sqlArray) throws SQLException {
        if (sqlArray == null) return null;
        try {
            Object data = sqlArray.getArray();
            if (data instanceof Double[]) return (Double[]) data;
            if (data instanceof double[]) {
                double[] p = (double[]) data;
                Double[] out = new Double[p.length];
                for (int i = 0; i < p.length; i++) out[i] = p[i];
                return out;
            }
            Object[] boxed = (Object[]) data;
            Double[] out = new Double[boxed.length];
            for (int i = 0; i < boxed.length; i++)
                out[i] = boxed[i] == null ? null : ((Number) boxed[i]).doubleValue();
            return out;
        } finally { sqlArray.free(); }
    }
   /*******
   * <p>
   * Method: validateEvaluationRubric
   * </p>
   *
   * <p>
   * Description: Ensures assessment parameters are all
   * defined with non-blank names, that each row has a corresponding weight, and that weights sum to
   * rubric total so the rubric matches a standard percentage breakdown.
   * </p>
   *
   * @param definedParams parallel criterion labels
   * @param paramWeights  parallel weights as percent points; must total 100
   *
   * @throws IllegalArgumentException if any name is missing, any weight is null/invalid, lengths
   *                                  differ, or the total is not 100%
   */
  public static void validateEvaluationRubric(String[] definedParams, Double[] paramWeights) {
    if (definedParams == null || paramWeights == null) {
      throw new IllegalArgumentException("definedParams and paramWeights must not be null.");
    }
    if (definedParams.length != paramWeights.length) {
      throw new IllegalArgumentException(
          "Each assessment parameter must have exactly one weight (array length mismatch).");
    }
    if (definedParams.length == 0) {
      throw new IllegalArgumentException(
          "At least one assessment parameter is required (e.g. accuracy, spelling, grammar, length).");
    }
    for (int i = 0; i < definedParams.length; i++) {
      String name = definedParams[i];
      if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "Assessment parameter at index " + i + " is not defined (null or blank).");
      }
      Double w = paramWeights[i];
      if (w == null) {
        throw new IllegalArgumentException(
            "Weight for parameter \"" + name + "\" is not defined (null).");
      }
      if (w < 0) {
        throw new IllegalArgumentException("Weights must be non-negative (parameter \"" + name + "\").");
      }
    }
    double sum = 0.0;
    for (Double w : paramWeights) {
      sum += w;
    }
    if (Math.abs(sum - EVALUATION_RUBRIC_WEIGHT_TOTAL) > EVALUATION_RUBRIC_WEIGHT_EPSILON) {
      throw new IllegalArgumentException(
          "Parameter weights must total 100% (currently " + sum + ").");
    }
  }

}
