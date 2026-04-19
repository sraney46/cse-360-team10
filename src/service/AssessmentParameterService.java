package service;

import dao.AssessmentParameterDAO;
import CRUDAssessment.AssessmentParameter;
import validation.AssessmentParameterValidator;

import java.sql.SQLException;
import java.util.List;

public class AssessmentParameterService {

    private final AssessmentParameterDAO dao;
    private final AssessmentParameterValidator validator;

    public AssessmentParameterService(AssessmentParameterDAO dao) {
        this.dao = dao;
        this.validator = new AssessmentParameterValidator();
    }

    public boolean addParameter(AssessmentParameter parameter) throws SQLException {
        if (!validator.validateParameter(parameter)) {
            return false;
        }
        return dao.createParameter(parameter);
    }

    public AssessmentParameter viewParameter(int id) throws SQLException {
        return dao.getParameterById(id);
    }

    public List<AssessmentParameter> listParameters() throws SQLException {
        return dao.getAllParameters();
    }

    public boolean editParameter(AssessmentParameter parameter) throws SQLException {
        if (!validator.validateParameter(parameter)) {
            return false;
        }
        return dao.updateParameter(parameter);
    }

    public boolean deactivateParameter(int id) throws SQLException {
        return dao.deactivateParameter(id);
    }
}