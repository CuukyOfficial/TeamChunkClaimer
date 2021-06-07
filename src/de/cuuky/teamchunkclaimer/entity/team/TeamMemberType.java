package de.cuuky.teamchunkclaimer.entity.team;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;

public enum TeamMemberType implements CFWSerializeable {

	@CFWSerializeField(enumValue = "OWNER")
	OWNER,

	@CFWSerializeField(enumValue = "MODERATOR")
	MODERATOR,

	@CFWSerializeField(enumValue = "MEMBER")
	MEMBER;

}