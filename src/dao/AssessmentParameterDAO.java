package dao;

import CRUDAssessment.AssessmentParameter;
import database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentParameterDAO {

//    private final Connection connection;
//
//    public AssessmentParameterDAO(Connection connection) {
//        this.connection = connection;
//    }

    public boolean createParameter(AssessmentParameter parameter) throws SQLException {
        String sql = "INSERT INTO assessment_parameters " +
                "(parameter_name, description, category, threshold_value, point_value, is_required, is_active, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql)) {
            stmt.setString(1, parameter.getParameterName());
            stmt.setString(2, parameter.getDescription());
            stmt.setString(3, parameter.getCategory());

            if (parameter.getThresholdValue() != null) {
                stmt.setInt(4, parameter.getThresholdValue());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (parameter.getPointValue() != null) {
                stmt.setDouble(5, parameter.getPointValue());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            stmt.setBoolean(6, parameter.isRequired());
            stmt.setBoolean(7, parameter.isActive());
            stmt.setString(8, parameter.getCreatedBy());

            return stmt.executeUpdate() > 0;
        }
    }

    public AssessmentParameter getParameterById(int id) throws SQLException {
        String sql = "SELECT * FROM assessment_parameters WHERE parameter_id = ?";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToParameter(rs);
                }
            }
        }

        return null;
    }

    public List<AssessmentParameter> getAllParameters() throws SQLException {
        List<AssessmentParameter> parameters = new ArrayList<>();
        String sql = "SELECT * FROM assessment_parameters";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                parameters.add(mapRowToParameter(rs));
            }
        }

        return parameters;
    }

    public boolean updateParameter(AssessmentParameter parameter) throws SQLException {
        String sql = "UPDATE assessment_parameters SET parameter_name = ?, description = ?, category = ?, " +
                "threshold_value = ?, point_value = ?, is_required = ?, is_active = ? WHERE parameter_id = ?";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql)) {
            stmt.setString(1, parameter.getParameterName());
            stmt.setString(2, parameter.getDescription());
            stmt.setString(3, parameter.getCategory());

            if (parameter.getThresholdValue() != null) {
                stmt.setInt(4, parameter.getThresholdValue());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (parameter.getPointValue() != null) {
                stmt.setDouble(5, parameter.getPointValue());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            stmt.setBoolean(6, parameter.isRequired());
            stmt.setBoolean(7, parameter.isActive());
            stmt.setInt(8, parameter.getParameterId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deactivateParameter(int id) throws SQLException {
        String sql = "UPDATE assessment_parameters SET is_active = FALSE WHERE parameter_id = ?";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private AssessmentParameter mapRowToParameter(ResultSet rs) throws SQLException {
        AssessmentParameter parameter = new AssessmentParameter();
        parameter.setParameterId(rs.getInt("parameter_id"));
        parameter.setParameterName(rs.getString("parameter_name"));
        parameter.setDescription(rs.getString("description"));
        parameter.setCategory(rs.getString("category"));

        int threshold = rs.getInt("threshold_value");
        if (rs.wasNull()) {
            parameter.setThresholdValue(null);
        } else {
            parameter.setThresholdValue(threshold);
        }

        double pointValue = rs.getDouble("point_value");
        if (rs.wasNull()) {
            parameter.setPointValue(null);
        } else {
            parameter.setPointValue(pointValue);
        }

        parameter.setRequired(rs.getBoolean("is_required"));
        parameter.setActive(rs.getBoolean("is_active"));
        parameter.setCreatedBy(rs.getString("created_by"));

        return parameter;
    }
    
    public List<AssessmentParameter> getActiveParameters() throws SQLException {
        List<AssessmentParameter> parameters = new ArrayList<>();
        String sql = "SELECT * FROM assessment_parameters WHERE is_active = TRUE ORDER BY parameter_id";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                parameters.add(mapRowToParameter(rs));
            }
        }

        return parameters;
    }
    public boolean deleteParameter(int id) throws SQLException {
        String sql = "DELETE FROM assessment_parameters WHERE parameter_id = ?";

        try (PreparedStatement stmt = Database.getStaticConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}