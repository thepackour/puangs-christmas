package com.puang.domain.user.repository;

import com.puang.domain.user.entity.Guild;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuildRepository extends JpaRepository<Guild, Long> {

    boolean existsByName(String name);

    /**
     * Similarity search on guild name.
     * Returns guilds whose name contains the keyword (case-insensitive), ordered by
     * exact match -> starts with keyword -> contains keyword, then shorter names first.
     * Use {@link #searchByName(String, Pageable)} so the keyword is escaped.
     */
    @Query("""
            SELECT g FROM Guild g
            WHERE LOWER(g.name) LIKE CONCAT('%', LOWER(:keyword), '%') ESCAPE '!'
            ORDER BY
                CASE
                    WHEN LOWER(g.name) = LOWER(:raw) THEN 0
                    WHEN LOWER(g.name) LIKE CONCAT(LOWER(:keyword), '%') ESCAPE '!' THEN 1
                    ELSE 2
                END,
                LENGTH(g.name),
                g.id
            """)
    List<Guild> searchByNameEscaped(@Param("keyword") String escapedKeyword,
                                    @Param("raw") String rawKeyword,
                                    Pageable pageable);

    // Escapes LIKE wildcards so "%" or "_" typed by the user are matched literally.
    // '!' is used as the escape char because '\' breaks string literals on MySQL.
    default List<Guild> searchByName(String keyword, Pageable pageable) {
        String trimmed = keyword.trim();
        String escaped = trimmed
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return searchByNameEscaped(escaped, trimmed, pageable);
    }
}
