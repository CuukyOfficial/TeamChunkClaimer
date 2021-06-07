package de.cuuky.teamchunkclaimer.entity.team.chunks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.utils.DirectionFace;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.ChunkTeam;
import de.cuuky.teamchunkclaimer.utils.ChunkUtils;

public class ClaimChunk implements CFWSerializeable {

	@CFWSerializeField(path = "world")
	private String world;

	@CFWSerializeField(path = "chunkX")
	private int chunkX;

	@CFWSerializeField(path = "chunkZ")
	private int chunkZ;

	@CFWSerializeField(path = "claimedBy")
	private String claimedBy;

	@CFWSerializeField(path = "claimedAt")
	private Date claimedAt;

	private ChunkTeam team;
	private Chunk chunk;

	public ClaimChunk(ChunkTeam team) {
		this.team = team;
	}

	public ClaimChunk(ChunkTeam team, Chunk chunk, ChunkPlayer claimedBy) {
		this(team);

		this.claimedAt = new Date();
		this.chunk = chunk;
		this.claimedBy = claimedBy.getUuid();

		setInfo();
	}

	private void setInfo() {
		this.world = chunk.getWorld().getName();
		this.chunkX = this.chunk.getX();
		this.chunkZ = this.chunk.getZ();
	}

	public List<ClaimChunk> getClaimedChunksAround() {
		List<ClaimChunk> chunks = new ArrayList<ClaimChunk>();
		for (Chunk chunk : ChunkUtils.getChunksAround(this.chunk)) {
			ClaimChunk cc = team.getHandler().getChunk(chunk);

			if (cc != null && cc.getTeam().equals(this.team))
				chunks.add(cc);
		}

		return chunks;
	}

	public List<DirectionFace> getBorders(boolean outline) {
		List<DirectionFace> walls = new ArrayList<DirectionFace>();
		for (DirectionFace face : DirectionFace.values()) {
			if (outline) {
				Chunk neighbour = null;
				World world = getBukkitWorld();
				switch (face) {
				case NORTH:
					neighbour = world.getChunkAt(chunkX, chunkZ - 1);
					break;
				case WEST:
					neighbour = world.getChunkAt(chunkX + 1, chunkZ);
					break;
				case SOUTH:
					neighbour = world.getChunkAt(chunkX, chunkZ + 1);
					break;
				case EAST:
					neighbour = world.getChunkAt(chunkX - 1, chunkZ);
					break;
				default:
					break;
				}

				ClaimChunk claimed = team.getHandler().getChunk(neighbour);
				if (claimed != null && claimed.getTeam().equals(team))
					continue;
			}

			walls.add(face);
		}

		return walls;
	}

	@Override
	public void onSerializeStart() {
		setInfo();
	}

	@Override
	public void onDeserializeEnd() {
		World world = this.team.getHandler().getClaimer().getPlugin().getServer().getWorld(this.world);
		if (world == null)
			return;

		this.chunk = world.getChunkAt(this.chunkX, this.chunkZ);
	}

	public int getLocationX() {
		return this.chunkX * 16 + 8;
	}

	public int getLocationZ() {
		return this.chunkZ * 16 + 8;
	}

	public String getWorld() {
		return world;
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public Date getClaimedAt() {
		return claimedAt;
	}

	public ChunkTeam getTeam() {
		return team;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public String getClaimedBy() {
		return claimedBy;
	}

	public World getBukkitWorld() {
		return chunk.getWorld();
	}

	public ChunkPlayer getClaimedByPlayer() {
		return this.team.getHandler().getPlayer(claimedBy);
	}
}