package it.einjojo.shopsystem.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public class Messages {
    private final MiniMessage miniMessage;
    public static Messages instance;

    public Messages(@NotNull String prefixMiniMessage) {
        this.miniMessage = MiniMessage.builder()
                .postProcessor((c) -> c.decoration(TextDecoration.ITALIC, false))
                .tags(TagResolver.builder()
                        .resolver(TagResolver.standard())
                        .tag("prefix", Tag.selfClosingInserting(MiniMessage.miniMessage().deserialize(prefixMiniMessage)))
                        .build())
                .build();
    }

    public void init() {
        if (instance != null) {
            throw new IllegalStateException("Messages have already been initialized.");
        }
        instance = this;
    }

    public static Messages get() {
        if (instance == null) {
            throw new IllegalStateException("Messages have not been initialized yet.");
        }
        return instance;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public @NotNull Component deserialize(@NotNull String input, @NotNull TagResolver... tagResolvers) {
        return miniMessage.deserialize(input, tagResolvers);
    }

    public @NotNull Component deserialize(@NotNull String input, @NotNull TagResolver tagResolver) {
        return miniMessage.deserialize(input, tagResolver);
    }
}
