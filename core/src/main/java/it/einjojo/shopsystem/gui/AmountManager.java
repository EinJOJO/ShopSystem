package it.einjojo.shopsystem.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.function.Consumer;

public class AmountManager {
    private final Gui gui;
    private Material iconMaterial = Material.CHEST_MINECART;
    private int[] amountStages = new int[]{1, 8, 16, 32, 64};
    private int baseSlot = 9 * 5 + 2;
    private Consumer<Integer> onAmountChange;
    private int amount = amountStages[0];

    public AmountManager(Gui gui) {
        this.gui = gui;
    }

    public void update() {
        for (int i = 0; i < amountStages.length; i++) {
            int amountStage = amountStages[i];
            Icon icon = new Icon(iconMaterial).setAmount(amountStage);
            if (amountStage == amount) {
                icon.setName("§d" + amountStage);
                icon.enchant(Enchantment.ARROW_DAMAGE);
                icon.hideFlags(ItemFlag.HIDE_ENCHANTS);
                icon.setLore("§7(ausgewählt)");
            } else {
                icon.setName("§7" + amountStage);
                icon.setLore("§7Klicke um die Menge zu ändern");
                icon.onClick(clickEvent -> {
                    setAmount(amountStage);
                    update();
                });
            }

            gui.addItem(baseSlot + i, icon);
        }
    }

    public void setIconMaterial(Material iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public int getAmount() {
        return amount;
    }


    public int[] getAmountStages() {
        return amountStages;
    }

    public void setAmountStages(int[] amountStages) {
        this.amountStages = amountStages;
    }

    public int getBaseSlot() {
        return baseSlot;
    }

    public void setBaseSlot(int baseSlot) {
        this.baseSlot = baseSlot;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        if (onAmountChange != null) {
            onAmountChange.accept(amount);
        }
    }

    public void setOnAmountChange(Consumer<Integer> onAmountChange) {
        this.onAmountChange = onAmountChange;
    }
}
