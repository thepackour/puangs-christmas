package com.puang.domain.user.service;

import com.puang.domain.user.dto.CreateGuildRequest;
import com.puang.domain.user.dto.GuildResponse;
import com.puang.domain.user.dto.UpdateGuildRequest;
import com.puang.domain.user.entity.Guild;
import com.puang.domain.user.entity.User;
import com.puang.domain.user.repository.GuildRepository;
import com.puang.domain.user.repository.UserRepository;
import com.puang.domain.user.exception.GuildException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Always runs on in-memory H2, even if .env has DB_URL.
// Each test is rolled back by @Transactional.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:guild-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@Transactional
class GuildServiceTest {

    private static final Long NOT_EXISTING_ID = 999_999L;

    @Autowired
    private GuildService guildService;

    @Autowired
    private GuildRepository guildRepository;

    @Autowired
    private UserRepository userRepository;

    private int userSeq = 0;

    private User saveUser() {
        userSeq++;
        return userRepository.save(User.create("user" + userSeq + "@test.com", "encoded-password", "user" + userSeq));
    }

    // Creates a guild directly in the DB (not through the service) and makes the leader a member.
    private Guild saveGuild(String name, User leader) {
        Guild guild = guildRepository.save(Guild.builder()
                .name(name)
                .description(name + " description")
                .leader(leader)
                .build());
        leader.joinGuild(guild);
        return guild;
    }

    @Nested
    @DisplayName("searchGuilds")
    class SearchGuilds {

        @Test
        @DisplayName("Returns guilds whose name contains the keyword, ignoring case")
        void containsIgnoringCase() {
            User leader = saveUser();
            saveGuild("Puang Club", leader);
            saveGuild("super PUANG", saveUser());
            saveGuild("other", saveUser());

            List<GuildResponse> result = guildService.searchGuilds("puang");

            assertThat(result).extracting(GuildResponse::name)
                    .containsExactlyInAnyOrder("Puang Club", "super PUANG");
        }

        @Test
        @DisplayName("Orders by exact match, then prefix match, then contains, then shorter name")
        void orderedBySimilarity() {
            saveGuild("super puang", saveUser());
            saveGuild("puangpuang", saveUser());
            saveGuild("puang clubs", saveUser());
            saveGuild("puang", saveUser());

            List<GuildResponse> result = guildService.searchGuilds("puang");

            assertThat(result).extracting(GuildResponse::name)
                    .containsExactly("puang", "puangpuang", "puang clubs", "super puang");
        }

        @Test
        @DisplayName("Trims the keyword")
        void trimsKeyword() {
            saveGuild("puang", saveUser());

            List<GuildResponse> result = guildService.searchGuilds("  puang  ");

            assertThat(result).extracting(GuildResponse::name).containsExactly("puang");
        }

        @Test
        @DisplayName("Treats % and _ in the keyword as plain characters")
        void wildcardsAreLiteral() {
            saveGuild("pu_ang", saveUser());
            saveGuild("puxang", saveUser());
            saveGuild("100% win", saveUser());

            assertThat(guildService.searchGuilds("_")).extracting(GuildResponse::name)
                    .containsExactly("pu_ang");
            assertThat(guildService.searchGuilds("%")).extracting(GuildResponse::name)
                    .containsExactly("100% win");
        }

        @Test
        @DisplayName("Returns at most 20 guilds")
        void limitedTo20() {
            for (int i = 0; i < 25; i++) {
                saveGuild("guild" + i, saveUser());
            }

            assertThat(guildService.searchGuilds("guild")).hasSize(20);
        }

        @Test
        @DisplayName("Returns an empty list when nothing matches")
        void noMatch() {
            saveGuild("puang", saveUser());

            assertThat(guildService.searchGuilds("nothing")).isEmpty();
        }
    }

    @Nested
    @DisplayName("createGuild")
    class CreateGuild {

        @Test
        @DisplayName("The creator becomes the leader and joins the guild")
        void creatorIsLeaderAndMember() {
            User user = saveUser();

            GuildResponse response = guildService.createGuild(
                    user.getId(), new CreateGuildRequest("puang", "hello"));

            assertThat(response.guildId()).isNotNull();
            assertThat(response.name()).isEqualTo("puang");
            assertThat(response.description()).isEqualTo("hello");
            assertThat(response.leaderId()).isEqualTo(user.getId());

            Guild saved = guildRepository.findById(response.guildId()).orElseThrow();
            assertThat(saved.isLeader(user.getId())).isTrue();
            assertThat(userRepository.findById(user.getId()).orElseThrow().getGuild().getId())
                    .isEqualTo(response.guildId());
        }

        @Test
        @DisplayName("Fails when the name is already used")
        void duplicateName() {
            saveGuild("puang", saveUser());
            User user = saveUser();

            assertThatThrownBy(() -> guildService.createGuild(
                    user.getId(), new CreateGuildRequest("puang", null)))
                    .isInstanceOf(GuildException.class);
        }

        @Test
        @DisplayName("Fails when the user is already in a guild")
        void alreadyInGuild() {
            User user = saveUser();
            saveGuild("first", user);

            assertThatThrownBy(() -> guildService.createGuild(
                    user.getId(), new CreateGuildRequest("second", null)))
                    .isInstanceOf(GuildException.class);
        }

        @Test
        @DisplayName("Fails when the user does not exist")
        void userNotFound() {
            assertThatThrownBy(() -> guildService.createGuild(
                    NOT_EXISTING_ID, new CreateGuildRequest("puang", null)))
                    .isInstanceOf(GuildException.class);
        }
    }

    @Nested
    @DisplayName("updateGuild")
    class UpdateGuild {

        @Test
        @DisplayName("The leader can change the name and description")
        void leaderUpdates() {
            User leader = saveUser();
            Guild guild = saveGuild("puang", leader);

            GuildResponse response = guildService.updateGuild(
                    leader.getId(), guild.getId(), new UpdateGuildRequest("new name", "new description"));

            assertThat(response.name()).isEqualTo("new name");
            assertThat(response.description()).isEqualTo("new description");
            Guild saved = guildRepository.findById(guild.getId()).orElseThrow();
            assertThat(saved.getName()).isEqualTo("new name");
        }

        @Test
        @DisplayName("Keeping the same name is not a duplicate")
        void sameNameIsAllowed() {
            User leader = saveUser();
            Guild guild = saveGuild("puang", leader);

            GuildResponse response = guildService.updateGuild(
                    leader.getId(), guild.getId(), new UpdateGuildRequest("puang", "only description changed"));

            assertThat(response.description()).isEqualTo("only description changed");
        }

        @Test
        @DisplayName("A member who is not the leader cannot update")
        void memberCannotUpdate() {
            Guild guild = saveGuild("puang", saveUser());
            User member = saveUser();
            member.joinGuild(guild);

            assertThatThrownBy(() -> guildService.updateGuild(
                    member.getId(), guild.getId(), new UpdateGuildRequest("hacked", null)))
                    .isInstanceOf(GuildException.class);
        }

        @Test
        @DisplayName("Fails when the new name is used by another guild")
        void duplicateName() {
            saveGuild("taken", saveUser());
            User leader = saveUser();
            Guild guild = saveGuild("puang", leader);

            assertThatThrownBy(() -> guildService.updateGuild(
                    leader.getId(), guild.getId(), new UpdateGuildRequest("taken", null)))
                    .isInstanceOf(GuildException.class);
        }

        @Test
        @DisplayName("Fails when the guild does not exist")
        void guildNotFound() {
            User user = saveUser();

            assertThatThrownBy(() -> guildService.updateGuild(
                    user.getId(), NOT_EXISTING_ID, new UpdateGuildRequest("puang", null)))
                    .isInstanceOf(GuildException.class);
        }
    }

    @Nested
    @DisplayName("joinGuild")
    class JoinGuild {

        @Test
        @DisplayName("Joins the guild right away")
        void joins() {
            Guild guild = saveGuild("puang", saveUser());
            User user = saveUser();

            GuildResponse response = guildService.joinGuild(user.getId(), guild.getId());

            assertThat(response.guildId()).isEqualTo(guild.getId());
            assertThat(userRepository.findById(user.getId()).orElseThrow().getGuild().getId())
                    .isEqualTo(guild.getId());
        }

        @Test
        @DisplayName("Fails when the user is already in a guild")
        void alreadyInGuild() {
            Guild first = saveGuild("first", saveUser());
            Guild second = saveGuild("second", saveUser());
            User user = saveUser();
            user.joinGuild(first);

            assertThatThrownBy(() -> guildService.joinGuild(user.getId(), second.getId()))
                    .isInstanceOf(GuildException.class);
        }

        @Test
        @DisplayName("Fails when the guild does not exist")
        void guildNotFound() {
            User user = saveUser();

            assertThatThrownBy(() -> guildService.joinGuild(user.getId(), NOT_EXISTING_ID))
                    .isInstanceOf(GuildException.class);
        }
    }

    @Nested
    @DisplayName("leaveGuild")
    class LeaveGuild {

        @Test
        @DisplayName("A member leaves the guild")
        void memberLeaves() {
            Guild guild = saveGuild("puang", saveUser());
            User member = saveUser();
            member.joinGuild(guild);

            guildService.leaveGuild(member.getId(), guild.getId());

            assertThat(userRepository.findById(member.getId()).orElseThrow().getGuild()).isNull();
        }

        @Test
        @DisplayName("Fails when the user is not in that guild")
        void notMember() {
            Guild guild = saveGuild("puang", saveUser());
            Guild other = saveGuild("other", saveUser());
            User user = saveUser();
            user.joinGuild(other);

            assertThatThrownBy(() -> guildService.leaveGuild(user.getId(), guild.getId()))
                    .isInstanceOf(GuildException.class);
        }

        @Test
        @DisplayName("Fails when the guild does not exist")
        void guildNotFound() {
            User user = saveUser();

            assertThatThrownBy(() -> guildService.leaveGuild(user.getId(), NOT_EXISTING_ID))
                    .isInstanceOf(GuildException.class);
        }

        // TODO: add a test once it is decided what happens when the leader leaves
        //       (hand over to another member, or delete the guild).
    }
}
