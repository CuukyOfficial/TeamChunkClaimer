package de.cuuky.teamchunkclaimer.entity.team.chunks;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;

public enum ChunkFlag implements CFWSerializeable {

	@CFWSerializeField(enumValue = "PVP")
	PVP(),

	@CFWSerializeField(enumValue = "USE")
	USE(),

	@CFWSerializeField(enumValue = "build")
	BUILD();

}