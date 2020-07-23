package de.cuuky.teamchunkclaimer.entity.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.teamchunkclaimer.entity.ChunkEntityHandler;
import de.cuuky.teamchunkclaimer.entity.player.ChunkPlayer;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ChunkFlag;
import de.cuuky.teamchunkclaimer.entity.team.chunks.ClaimChunk;
import de.cuuky.teamchunkclaimer.menu.ChunkMapMenu;
import de.cuuky.teamchunkclaimer.menu.TeamMainMenu;
import de.cuuky.teamchunkclaimer.menu.team.ChunkListMenu;
import de.cuuky.teamchunkclaimer.menu.team.TeamMemberMenu;
import de.cuuky.teamchunkclaimer.menu.team.options.FlagOptionsMenu;
import de.cuuky.teamchunkclaimer.menu.team.options.GeneralOptionsMenu;
import de.cuuky.teamchunkclaimer.utils.ChunkUtils;

public class ChunkTeam implements CFWSerializeable {

	private ChunkEntityHandler handler;

	@CFWSerializeField(path = "teamId")
	private int teamId;

	@CFWSerializeField(path = "name")
	private String name;

	@CFWSerializeField(path = "title")
	private String title;

	@CFWSerializeField(path = "tag")
	private String tag;

	@CFWSerializeField(path = "color")
	private String color;

	@CFWSerializeField(path = "claimedChunks", keyClass = ClaimChunk.class)
	private List<ClaimChunk> claimedChunks;

	@CFWSerializeField(path = "member", valueClass = TeamMemberType.class)
	private Map<String, TeamMemberType> memberIds;

	@CFWSerializeField(path = "flags", keyClass = ChunkFlag.class)
	private Map<ChunkFlag, Boolean> flags;

	private Map<ChunkPlayer, TeamMemberType> members;

	public ChunkTeam(ChunkEntityHandler handler) {
		this.handler = handler;

		this.claimedChunks = new ArrayList<ClaimChunk>();
		this.memberIds = new HashMap<String, TeamMemberType>();
		this.members = new HashMap<ChunkPlayer, TeamMemberType>();
		this.flags = new HashMap<ChunkFlag, Boolean>();
	}

	public ChunkTeam(ChunkEntityHandler handler, String name) {
		this(handler);

		this.teamId = generateTeamId();
		this.name = name;
	}

	private int generateTeamId() {
		int id = handler.getTeams().size();
		while (handler.getTeam(id) != null)
			id++;

		return id;
	}

	private ArrayList<ClaimChunk> getAttached(ArrayList<ClaimChunk> found, ClaimChunk chunk) {
		ArrayList<ClaimChunk> attached = new ArrayList<ClaimChunk>();

		for (ClaimChunk chunks : chunk.getClaimedChunksAround()) {
			if (found.contains(chunks))
				continue;

			found.add(chunks);
			attached.addAll(getAttached(found, chunks));
		}

		return attached;
	}

	private void checkChunks(ChunkPlayer removed) {
		int allowed = getAllowedChunkAmount();
		if (allowed <= this.claimedChunks.size())
			return;

		for (int i = this.claimedChunks.size() - allowed; i >= 0; i--) {
			ArrayList<ClaimChunk> chunks = new ArrayList<ClaimChunk>(this.claimedChunks);
			// GET CHUNKS OF PLAYER WHO CLAIMED
			for (ClaimChunk chunk : chunks)
				if (chunk.getClaimedBy().equals(removed.getUuid())) {
					removeChunk(chunk);
					continue;
				}

			removeChunk(this.claimedChunks.get(this.claimedChunks.size() - 1));
		}
	}

	public void remove() {
		for (ChunkPlayer member : this.members.keySet()) {
			member.setTeam(null);
			if (member.isOnline())
				member.getPlayer().sendMessage(this.handler.getClaimer().getPrefix() + "Dein Team wurde aufgelöst!");
		}

		this.members.clear();
	}

	public void addMember(ChunkPlayer player, TeamMemberType memberType) {
		this.sendMessage(this.handler.getClaimer().getPrefix() + "§5" + player.getName() + " §7ist deinem Team beigetreten!");

		this.members.put(player, memberType);
		player.setTeam(this);

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMemberMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMainMenu.class);
	}

	public void removeMember(ChunkPlayer player) {
		this.members.remove(player);
		player.setTeam(null);

		this.sendMessage(this.handler.getClaimer().getPrefix() + "§5" + player.getName() + " §7hat dein Team verlassen!");

		checkChunks(player);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMemberMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMainMenu.class);
	}

	public void setMemberType(ChunkPlayer player, TeamMemberType memberType) {
		if (!isMember(player)) {
			addMember(player, memberType);
			return;
		}

		this.members.put(player, memberType);
		this.sendMessage(this.handler.getClaimer().getPrefix() + "§5" + player.getName() + " §7ist nun ein §5" + memberType.toString() + "§7!");

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMemberMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMainMenu.class);
	}

	public TeamMemberType getMemberType(ChunkPlayer player) {
		return this.members.get(player);
	}

	public boolean isMember(ChunkPlayer player) {
		return getMemberType(player) != null;
	}

	public ChunkPlayer getOwner() {
		return getMember(TeamMemberType.OWNER).get(0);
	}

	public ArrayList<ChunkPlayer> getMember(TeamMemberType type) {
		ArrayList<ChunkPlayer> found = new ArrayList<ChunkPlayer>();
		for (ChunkPlayer player : this.members.keySet())
			if (this.members.get(player) == type)
				found.add(player);

		return found;
	}

	public void addChunk(Chunk chunk, ChunkPlayer claimedBy) {
		ClaimChunk newChunk = null;
		this.claimedChunks.add(newChunk = new ClaimChunk(this, chunk, claimedBy));
		this.sendMessage(this.handler.getClaimer().getPrefix() + "Ein Chunk bei " + "X: " + newChunk.getLocationX() + ", Z: " + newChunk.getLocationZ() + " in " + newChunk.getWorld() + " wurde für dein Team von " + claimedBy.getName() + " §7geclaimt!");

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(ChunkMapMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(ChunkListMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMainMenu.class);
	}

	public void removeChunk(ClaimChunk chunk) {
		this.claimedChunks.remove(chunk);
		this.sendMessage(this.handler.getClaimer().getPrefix() + "Dein Team-Chunk bei " + "X: " + chunk.getLocationX() + ", Z: " + chunk.getLocationZ() + " in " + chunk.getWorld() + " wurde entclaimt!");

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(ChunkMapMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(ChunkListMenu.class);
		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(TeamMainMenu.class);
	}

	public boolean hasMaximumChunksReached() {
		return getAllowedChunkAmount() <= this.claimedChunks.size();
	}

	public int getChunkRegions() {
		int regions = 0;
		ArrayList<ClaimChunk> found = new ArrayList<ClaimChunk>();
		for (ClaimChunk chunk : this.claimedChunks) {
			if (found.contains(chunk))
				continue;

			found.addAll(getAttached(found, chunk));
			regions++;
		}

		return regions;
	}

	public boolean canAddChunk(Chunk toAdd) {
		boolean extraRegion = true;

		for (Chunk found : ChunkUtils.getChunksAround(toAdd)) {
			ClaimChunk c = this.handler.getChunk(found);
			if (c == null || !c.getTeam().equals(this))
				continue;

			extraRegion = false;
			break;
		}

		return extraRegion ? getChunkRegions() + 1 <= this.handler.getClaimer().getConfiguration().getMaxChunkGroups() : true;
	}

	@Override
	public void onSerializeStart() {
		this.memberIds.clear();

		for (ChunkPlayer player : members.keySet()) {
			this.memberIds.put(player.getUuid(), this.members.get(player));
			player.setTeam(this);
		}
	}

	@Override
	public void onDeserializeEnd() {
		this.members.clear();

		for (String uuid : this.memberIds.keySet()) {
			ChunkPlayer player = handler.getPlayer(uuid);
			if (player == null) {
				System.err.println(handler.getClaimer().getPrefix() + "COULD NOT FIND PLAYER '" + uuid + "'! Did you delete it manually?");
				continue;
			}

			this.members.put(player, this.memberIds.get(uuid));
			player.setTeam(this);
		}

		if (!this.claimedChunks.isEmpty())
			for (int i = this.claimedChunks.size() - 1; i != 0; i--) {
				ClaimChunk chunk = this.claimedChunks.get(i);

				if (chunk.getChunk() == null)
					this.claimedChunks.remove(chunk);
			}
	}

	public void setFlag(ChunkFlag flag, boolean enabled) {
		this.flags.put(flag, enabled);

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(FlagOptionsMenu.class);
	}

	public boolean getFlag(ChunkFlag flag) {
		if (!this.flags.containsKey(flag))
			return false;

		return this.flags.get(flag);
	}

	public int getAllowedChunkAmount() {
		int allowed = 0;
		for (ChunkPlayer player : this.members.keySet())
			allowed += player.getAllowedChunks();

		return allowed;
	}

	public void sendMessage(String message) {
		for (ChunkPlayer pl : this.members.keySet())
			if (pl.isOnline())
				pl.getPlayer().sendMessage(message);
	}

	public String getDisplayname() {
		return getColor() + this.name;
	}

	public ChunkEntityHandler getHandler() {
		return handler;
	}

	public long getTeamId() {
		return teamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(GeneralOptionsMenu.class);
	}

	public String getTitle() {
		return title == null ? null : ChatColor.translateAlternateColorCodes('&', title);
	}

	public void setTitle(String title) {
		this.title = title;

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(GeneralOptionsMenu.class);
	}

	public String getTag() {
		return tag == null ? null : ChatColor.translateAlternateColorCodes('&', tag);
	}

	public void setTag(String tag) {
		this.tag = tag;

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(GeneralOptionsMenu.class);
	}

	public void setColor(String color) {
		this.color = color;

		this.handler.getClaimer().getCuukyFrameWork().getInventoryManager().updateInventories(GeneralOptionsMenu.class);
	}

	public String getColor() {
		return color == null ? "§f" : ChatColor.translateAlternateColorCodes('&', color);
	}

	public List<ClaimChunk> getClaimedChunks() {
		return claimedChunks;
	}

	public Map<ChunkFlag, Boolean> getFlags() {
		return flags;
	}

	public Map<ChunkPlayer, TeamMemberType> getMembers() {
		return members;
	}
}