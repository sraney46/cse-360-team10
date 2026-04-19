package CRUDAssessment;

public class AssessmentParameter {
	private int parameterId;
	private String parameterName;
	private String description;
	private String category;
	private Integer thresholdValue;
	private Double pointValue;
	private boolean isRequired;
	private boolean isActive;
	private String createdBy;

	public AssessmentParameter() {
	}

	public AssessmentParameter(String parameterName, String description, String category, Integer thresholdValue,
			Double pointValue, boolean isRequired, boolean isActive, String createdBy) {
		this.parameterName = parameterName;
		this.description = description;
		this.category = category;
		this.thresholdValue = thresholdValue;
		this.pointValue = pointValue;
		this.isRequired = isRequired;
		this.isActive = isActive;
		this.createdBy = createdBy;
	}

	public int getParameterId() {
		return parameterId;
	}

	public void setParameterId(int parameterId) {
		this.parameterId = parameterId;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(Integer thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public Double getPointValue() {
		return pointValue;
	}

	public void setPointValue(Double pointValue) {
		this.pointValue = pointValue;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean required) {
		isRequired = required;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
