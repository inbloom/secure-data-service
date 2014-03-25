package org.slc.sli.api.count;

public class TeacherAssociationCount {

	public enum TeacherAssociationType {
		ASSOCIATION, TEACHER
	}

	private TeacherAssociationType type;
	private int total;
	private int current;

	public TeacherAssociationType getType() {
		return type;
	}

	public void setType(TeacherAssociationType type) {
		this.type = type;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}
}
