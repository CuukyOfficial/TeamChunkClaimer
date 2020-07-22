package de.cuuky.teamchunkclaimer;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private ChunkClaimer tcc;

	@Override
	public void onEnable() {
		System.out.println(getConsolePrefix() + "Enabling " + this.getName() + " v" + this.getDescription().getVersion() + " by " + this.getDescription().getAuthors().get(0));

		tcc = new ChunkClaimer(this);

		System.out.println(getConsolePrefix() + "Enabled!");
		super.onEnable();
	}

	@Override
	public void onDisable() {
		System.out.println(getConsolePrefix() + "Disabling...");

		tcc.disable();

		System.out.println(getConsolePrefix() + "Disabled!");
		super.onDisable();
	}

	public ChunkClaimer getTcc() {
		return tcc;
	}

	public String getConsolePrefix() {
		return "[" + this.getName() + "] ";
	}
}