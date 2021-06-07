package de.cuuky.teamchunkclaimer.entity.team.chunks;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.version.types.Materials;

public enum ChunkFlag implements CFWSerializeable {

	@CFWSerializeField(enumValue = "BUILD")
	BUILD("Build", Materials.CHEST, "Ob Nichtteammitglieder auf deinem Grundstück bauen können"),

	@CFWSerializeField(enumValue = "PVP")
	PVP("PvP", Materials.IRON_SWORD, "Ob auf dem Grundstück PvP aktiviert sein soll"),

	@CFWSerializeField(enumValue = "USE")
	USE("Use", Materials.STONE_SHOVEL, "Ob Nichtteammitglieder Kisten, Öfen etc. nutzen dürfen");

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