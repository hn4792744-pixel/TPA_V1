package me.uc_hussein.ultrastpa.util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public final class ItemUtil {
 private ItemUtil() {}
 public static ItemStack item(Material material, String name){
  ItemStack item=new ItemStack(material); ItemMeta meta=item.getItemMeta(); meta.displayName(ColorUtil.component(name)); item.setItemMeta(meta); return item;
 }
}
