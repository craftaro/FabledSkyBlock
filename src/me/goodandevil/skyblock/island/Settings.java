package me.goodandevil.skyblock.island;

public class Settings {
	
	private boolean status;
	
	public Settings(boolean status) {
		this.status = status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public enum Role {
		
		Visitor,
		Member,
		Operator,
		Owner;
		
	}
}
