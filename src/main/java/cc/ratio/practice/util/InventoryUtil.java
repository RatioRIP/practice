package cc.ratio.practice.util;

import com.google.common.base.Joiner;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InventoryUtil {

    public static final List<Material> HELMET_MATERIALS = Arrays.asList(Material.DIAMOND_HELMET, Material.IRON_HELMET, Material.CHAINMAIL_HELMET, Material.GOLD_HELMET, Material.LEATHER_HELMET);
    public static final List<Material> CHESTPLATE_MATERIALS = Arrays.asList(Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.LEATHER_CHESTPLATE);
    public static final List<Material> LEGGINS_MATERIALS = Arrays.asList(Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLD_LEGGINGS, Material.LEATHER_LEGGINGS);
    public static final List<Material> BOOTS_MATERIALS = Arrays.asList(Material.DIAMOND_BOOTS, Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLD_BOOTS, Material.LEATHER_BOOTS);

    public static ItemStack[] fixInventoryOrder(final ItemStack[] source) {
        final ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static String serializeInventory(final ItemStack[] source) {
        final StringBuilder builder = new StringBuilder();

        for (final ItemStack itemStack : source) {
            builder.append(serializeItemStack(itemStack));
            builder.append(";");
        }

        return builder.toString();
    }

    public static ItemStack[] deserializeInventory(final String source) {
        final List<ItemStack> items = new ArrayList<>();
        final String[] split = source.split(";");

        for (final String piece : split) {
            items.add(deserializeItemStack(piece));
        }

        return items.toArray(new ItemStack[0]);
    }

    public static String serializeItemStack(final ItemStack item) {
        final StringBuilder builder = new StringBuilder();

        if (item == null) {
            return "null";
        }

        final String isType = String.valueOf(item.getType().getId());
        builder.append("t@").append(isType);

        if (item.getDurability() != 0) {
            final String isDurability = String.valueOf(item.getDurability());
            builder.append(":d@").append(isDurability);
        }

        if (item.getAmount() != 1) {
            final String isAmount = String.valueOf(item.getAmount());
            builder.append(":a@").append(isAmount);
        }

        if (item.hasItemMeta()) {
            final ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                builder.append(":dn@").append(itemMeta.getDisplayName());
            }

            if (itemMeta.hasLore()) {
                builder.append(":l@").append("[").append(Joiner.on(",,").join(itemMeta.getLore())).append("]");
            }

            if (itemMeta.hasEnchants()) {
                final Map<Enchantment, Integer> enchantments = item.getItemMeta().getEnchants();

                for (final Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
                    builder.append(":e@").append(enchantment.getKey().getId()).append("@").append(enchantment.getValue());
                }
            }
        }

        if (item.getType() == Material.POTION) {
            final Potion potion = Potion.fromItemStack(item);

            builder.append(":pd@")
                    .append(potion.getType().getDamageValue())
                    .append("-")
                    .append(potion.getLevel());
            for (final PotionEffect effect : potion.getEffects()) {
                builder.append("=")
                        .append(effect.getType().getId())
                        .append("-")
                        .append(effect.getDuration())
                        .append("-")
                        .append(effect.getAmplifier());
            }
        }

        if ((item.getType() == Material.LEATHER_HELMET) ||
                (item.getType() == Material.LEATHER_CHESTPLATE) ||
                (item.getType() == Material.LEATHER_LEGGINGS) ||
                (item.getType() == Material.LEATHER_BOOTS) &&
                        (item.hasItemMeta())) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            builder.append(":lc@").append(meta.getColor().asRGB());
        }

        if ((item.getType() == Material.BOOK_AND_QUILL) || (item.getType() == Material.WRITTEN_BOOK) && (item.hasItemMeta())) {
            final BookMeta meta = (BookMeta) item.getItemMeta();
            if (meta.hasAuthor()) {
                builder.append(":ba@").append(meta.getAuthor());
            }
            if (meta.hasTitle()) {
                builder.append(":bt@").append(meta.getTitle());
            }
            if (meta.hasPages()) {
                builder.append(":bp@").append("[").append(Joiner.on(";;").join(meta.getPages())).append("]");
            }
        }

        if ((item.getType() == Material.ENCHANTED_BOOK) && (item.hasItemMeta())) {
            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta.hasStoredEnchants()) {
                for (final Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
                    builder.append(":esm@").append(entry.getKey().getId()).append("@").append(entry.getValue());
                }
            }
        }

        return builder.toString();
    }

    public static ItemStack deserializeItemStack(final String in) {
        ItemStack item = null;
        ItemMeta meta = null;

        if (in.equals("null")) {
            return new ItemStack(Material.AIR);
        }

        final String[] split = in.split(":");

        for (final String itemInfo : split) {
            final String[] itemAttribute = itemInfo.split("@");
            final String attributeId = itemAttribute[0];

            switch (attributeId) {
                case "t": {
                    item = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    meta = item.getItemMeta();
                    break;
                }
                case "d": {
                    if (item != null) {
                        item.setDurability(Short.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "a": {
                    if (item != null) {
                        item.setAmount(Integer.valueOf(itemAttribute[1]));
                        break;
                    }
                    break;
                }
                case "e": {
                    if (meta != null) {
                        meta.addEnchant(
                                Enchantment.getById(Integer.parseInt(itemAttribute[1])),
                                Integer.parseInt(itemAttribute[2]),
                                true
                        );
                        break;
                    }
                    break;
                }
                case "dn": {
                    if (meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        break;
                    }
                    break;
                }
                case "l": {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    final List<String> lore = Arrays.asList(itemAttribute[1].split(",,"));

                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);
                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }

                                lore.set(x, s);
                            }
                        }
                    }

                    if (meta != null) {
                        meta.setLore(lore);
                        break;
                    }

                    break;
                }
                case "pd": {
                    if (item != null && item.getType() == Material.POTION) {
                        final String[] effectsList = itemAttribute[1].split("=");
                        final String[] potionData = effectsList[0].split("-");

                        final Potion potion = new Potion(PotionType.getByDamageValue(Integer.parseInt(potionData[0])),
                                Integer.parseInt(potionData[1]));
                        potion.setSplash(item.getDurability() >= 16000);

                        final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

                        for (int i = 1; i < effectsList.length; i++) {
                            final String[] effectData = effectsList[1].split("-");

                            final PotionEffect potionEffect = new PotionEffect(PotionEffectType.getById(
                                    Integer.valueOf(effectData[0])), Double.valueOf(effectData[1]).intValue(),
                                    Integer.valueOf(effectData[2]), false
                            );

                            potionMeta.addCustomEffect(potionEffect, true);
                        }

                        item = potion.toItemStack(item.getAmount());
                        item.setItemMeta(potionMeta);
                    }

                    break;
                }
                case "lc": {
                    if (meta != null) {
                        final LeatherArmorMeta armorMeta = (LeatherArmorMeta) item.getItemMeta();
                        armorMeta.setColor(Color.fromRGB(Integer.valueOf(itemAttribute[1])));
                        item.setItemMeta(armorMeta);
                    }
                    break;
                }
                case "ba": {
                    if (meta != null) {
                        final BookMeta bookMeta = (BookMeta) item.getItemMeta();
                        bookMeta.setAuthor(itemAttribute[1]);
                        item.setItemMeta(bookMeta);
                    }
                    break;
                }
                case "bt": {
                    if (meta != null) {
                        final BookMeta bookMeta = (BookMeta) item.getItemMeta();
                        bookMeta.setTitle(itemAttribute[1]);
                        item.setItemMeta(bookMeta);
                    }
                    break;
                }
                case "bp": {
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    final List<String> pages = Arrays.asList(itemAttribute[1].split(",,"));

                    for (int x = 0; x < pages.size(); ++x) {
                        String s = pages.get(x);

                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }

                                pages.set(x, s);
                            }
                        }
                    }

                    if (meta != null) {
                        final BookMeta bookMeta = (BookMeta) item.getItemMeta();
                        bookMeta.setPages(pages);
                        item.setItemMeta(bookMeta);
                        break;
                    }
                    break;
                }
                case "esm": {
                    if (meta != null) {
                        final EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) item.getItemMeta();
                        storageMeta.addStoredEnchant(
                                Enchantment.getById(Integer.valueOf(itemAttribute[1])),
                                Integer.valueOf(itemAttribute[2]),
                                true
                        );
                        item.setItemMeta(storageMeta);
                        break;
                    }
                    break;
                }
            }
        }

        if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
            item.setItemMeta(meta);
        }

        return item;
    }

    public static void removeCrafting(final Material material) {
        //Iterator<Recipe> iterator = ILib.getInstance().getServer().recipeIterator();

        /*while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe != null && recipe.getResult().getType() == material) {
                iterator.remove();
            }
        }*/
    }

    public static boolean isHelmet(final Material material) {
        return HELMET_MATERIALS.contains(material);
    }

    public static boolean isHelmet(final ItemStack itemStack) {
        return isHelmet(itemStack.getType());
    }

    public static boolean isChestplate(final Material material) {
        return CHESTPLATE_MATERIALS.contains(material);
    }

    public static boolean isChestplate(final ItemStack itemStack) {
        return isChestplate(itemStack.getType());
    }

    public static boolean isLeggings(final Material material) {
        return LEGGINS_MATERIALS.contains(material);
    }

    public static boolean isLeggings(final ItemStack itemStack) {
        return isLeggings(itemStack.getType());
    }

    public static boolean isBoots(final Material material) {
        return BOOTS_MATERIALS.contains(material);
    }

    public static boolean isBoots(final ItemStack itemStack) {
        return isBoots(itemStack.getType());
    }

}