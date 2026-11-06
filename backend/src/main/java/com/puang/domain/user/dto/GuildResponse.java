package com.puang.domain.user.dto;

import com.puang.domain.user.entity.Guild;

public record GuildResponse(
        Long guildId,
        String name,
        String description,
        Long leaderId
) {
    public static GuildResponse from(Guild guild) {
        return new GuildResponse(
                guild.getId(),
                guild.getName(),
                guild.getDescription(),
                guild.getLeader().getId()
        );
    }
}
