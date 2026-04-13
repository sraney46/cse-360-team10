package entityClasses;

public class Constraint {
	private String text;
	private ConstraintType type;
	public enum ConstraintType {
	    AND,
	    OR
	  }
	
	public Constraint(String text, ConstraintType type)
	{
		this.text = text;
		this.type = type;
	}
	
	public String getText() { return text; }
	public ConstraintType getType() { return type; }
	
	public void setText(String text) { this.text = text; }
	public void setType(ConstraintType type) { this.type = type; }
}
