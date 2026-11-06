package com.puang.domain.user.service;

import com.puang.domain.user.dto.CreateGuildRequest;
import com.puang.domain.user.dto.GuildResponse;
import com.puang.domain.user.dto.UpdateGuildRequest;
import com.puang.domain.user.entity.Guild;
import com.puang.domain.user.entity.User;
import com.puang.domain.user.repository.GuildRepository;
import com.puang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implement the body of each method to match the given method signatures.
// If you need additional classes, add them in the matching directory.
// When an exceptional case occurs, throw GuildException.
// Test code is under src/test/.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuildService {

    // Max number of results for GET /api/guilds?keyword=
    private static final int SEARCH_LIMIT = 20;

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;

    /**
     * GET /api/guilds?keyword=
     * Similarity search on guild name (exact -> prefix -> contains).
     */
    public List<GuildResponse> searchGuilds(String keyword) {
        // TODO
        return null;
    }

    /**
     * POST /api/guilds
     * Creates a guild. The creator joins it automatically and becomes the leader.
     */
    @Transactional
    public GuildResponse createGuild(Long userId, CreateGuildRequest request) {
        // TODO
        return null;
    }

    /**
     * PATCH /api/guilds/{guildId}
     * Updates name / description. Leader only.
     */
    @Transactional
    public GuildResponse updateGuild(Long userId, Long guildId, UpdateGuildRequest request) {
        // TODO
        return null;
    }

    /**
     * POST /api/guilds/{guildId}/members
     * Joins the guild right away (no approval step).
     */
    @Transactional
    public GuildResponse joinGuild(Long userId, Long guildId) {
        // TODO
        return null;
    }

    /**
     * DELETE /api/guilds/{guildId}/members/me
     * Leaves the guild. What happens when the leader leaves is not decided yet.
     */
    @Transactional
    public void leaveGuild(Long userId, Long guildId) {
        // TODO
    }

    private User getUser(Long userId) {
        // TODO
        return null;
    }

    private Guild getGuild(Long guildId) {
        // TODO
        return null;
    }

    // Throws if the user is not the leader of the guild
    private void validateLeader(Guild guild, Long userId) {
        // TODO
    }

    // Throws if another guild already uses this name
    private void validateDuplicateName(String name) {
        // TODO
    }
}
