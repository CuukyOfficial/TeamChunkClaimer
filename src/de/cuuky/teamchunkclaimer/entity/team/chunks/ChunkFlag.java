package de.cuuky.teamchunkclaimer.entity.team.chunks;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.version.types.Materials;

public enum ChunkFlag implements CFWSerializeable {

	@CFWSerializeField(enumValue = "BUILD")
	BUILD("Build", Materials.CHEST, "Blöcke abbauen/platzieren"),

	@CFWSerializeField(enumValue = "PVP")
	PVP("PvP", Materials.IRON_SWORD, "PvP zwischen Spielern"),

	@CFWSerializeField(enumValue = "USE")
	USE("Use", Materials.STONE_SHOVEL, "Kisten/Öfen/... nutzen");

	private String name, description;
	private Materials material;

	private ChunkFlag(String name, Materials material, String description) {
		this.name = name;
		this.material = material;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Materials getMaterial() {
		return material;
	}
}