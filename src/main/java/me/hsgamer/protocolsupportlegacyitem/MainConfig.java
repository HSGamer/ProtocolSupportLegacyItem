package me.hsgamer.protocolsupportlegacyitem;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.BaseConfigPath;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class MainConfig extends PathableConfig {
    public static final BooleanConfigPath TRANSLATE_ITEM_NAME = new BooleanConfigPath("translate-item-name", true);
    public static final BooleanConfigPath ADD_ITEM_LORE_ENABLED = new BooleanConfigPath("add-item-lore.enabled", true);
    public static final BaseConfigPath<List<String>> ADD_ITEM_LORE_LINE = new BaseConfigPath<>(
            "add-item-lore.line",
            Collections.singletonList("&8This item is actually &7{name}&8 from newer versions of Minecraft"),
            o -> CollectionUtils.createStringListFromObject(o, false)
    );

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
