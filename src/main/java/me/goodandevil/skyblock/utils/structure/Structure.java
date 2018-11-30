package me.goodandevil.skyblock.utils.structure;

public class Structure {
	
	private Storage storage;
    private String file;
    
    public Structure(Storage storage, String file) {
        this.storage = storage;
    	this.file = file;
    }
    
    public Storage getStructureStorage() {
    	return storage;
    }

    public String getStructureFile(){
        return this.file;
    }
}
