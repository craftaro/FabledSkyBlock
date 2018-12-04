package me.goodandevil.skyblock.island;

public class Setting {

	private String name;
	private boolean status;
	
	public Setting(String name, boolean status) {
		this.name = name;
		this.status = status;
	}
	
	public String getName() {
		return name;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public enum Role {
		
		Coop,
		Visitor,
		Member,
		Operator,
		Owner;
		
	}
}
