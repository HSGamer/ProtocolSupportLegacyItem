package me.hsgamer.protocolsupportlegacyitem;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.TranslationAPI;
import protocolsupport.api.chat.components.BaseComponent;
import protocolsupport.api.events.ItemStackWriteEvent;
import protocolsupport.api.remapper.ItemRemapperControl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ProtocolSupportLegacyItem extends JavaPlugin implements Listener {
    private final MainConfig mainConfig = new MainConfig(this);
    private final Map<ProtocolVersion, ItemRemapperControl> itemRemappers = new EnumMap<>(ProtocolVersion.class);

    @Override
    public void onLoad() {
        mainConfig.setup();
        for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
            itemRemappers.put(version, new ItemRemapperControl(version));
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) this);
        itemRemappers.clear();
    }

    @EventHandler
    public void onItemWrite(ItemStackWriteEvent event) {
        ProtocolVersion version = event.getVersion();
        if (!itemRemappers.containsKey(version)) {
            return;
        }
        ItemStack itemStack = event.getOriginal();

        Material material = itemStack.getType();
        if (material == itemRemappers.get(version).getRemap(material)) {
            return;
        }

        String translationKey = getTranslationKey(material);
        String translated = TranslationAPI.translate(event.getLocale(), translationKey);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (MainConfig.TRANSLATE_ITEM_NAME.getValue() && itemMeta != null && !itemMeta.hasDisplayName()) {
            event.setForcedDisplayName(BaseComponent.fromMessage(translated));
        }

        if (MainConfig.ADD_ITEM_LORE_ENABLED.getValue()) {
            NamespacedKey namespacedKey = material.getKey();
            List<String> lore = MainConfig.ADD_ITEM_LORE_LINE.getValue();
            lore.replaceAll(MessageUtils::colorize);
            lore.replaceAll(s -> s.replace("{name}", translated)
                    .replace("{trans_key}", translationKey)
                    .replace("{id}", namespacedKey.getNamespace() + ":" + namespacedKey.getKey())
                    .replace("{namespace}", namespacedKey.getNamespace())
                    .replace("{key}", namespacedKey.getKey()));
            event.getAdditionalLore().addAll(lore);
        }
    }

    private String getTranslationKey(Material material) {
        NamespacedKey namespacedKey = material.getKey();
        String root = material.isBlock() ? "block" : "item";
        return root + "." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
    }
}
