package me.goodandevil.skyblock.api.structure;

import java.util.ArrayList;
import java.util.List;

public class StructureManager {

	private final me.goodandevil.skyblock.structure.StructureManager structureManager;

	public StructureManager(me.goodandevil.skyblock.structure.StructureManager structureManager) {
		this.structureManager = structureManager;
	}

	/**
	 * @return The Structure for an Island
	 */
	public Structure getStructure(String structure) {
		return this.structureManager.getStructure(structure);
	}

	/**
	 * @return true of conditions met, false otherwise
	 */
	public boolean hasStructure(String structure) {
		return structureManager.containsStructure(structure);
	}

	/**
	 * @return A List of Structures for an Island
	 */
	public List<Structure> getStructures() {
		List<Structure> structures = new ArrayList<>();

		for (Structure structureList : structureManager.getStructures()) {
			structures.add(structureList);
		}

		return structures;
	}
}
