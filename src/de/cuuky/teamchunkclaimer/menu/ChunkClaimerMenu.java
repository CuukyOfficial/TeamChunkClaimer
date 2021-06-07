package de.cuuky.teamchunkclaimer.menu;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemInserter;
import de.cuuky.cfw.inventory.inserter.AnimatedClosingInserter;
import org.bukkit.entity.Player;

public abstract class ChunkClaimerMenu extends AdvancedInventory {

    public ChunkClaimerMenu(AdvancedInventoryManager manager, Player player) {
        super(manager, player);
    }

    @Override
    protected ItemInserter getInserter() {
        return new AnimatedClosingInserter();
    }
}