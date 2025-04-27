package com.hanyahunya.gitRemind.token.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

public class MemberTokenRepositoryImpl implements MemberTokenRepository {
    private final JdbcTemplate jdbcTemplate;

    public MemberTokenRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean saveMemberToken(String memberId, String tokenId) {
        final String sql = "INSERT INTO member_token (member_id, token_id) VALUES (?, ?)";
        int updated = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, memberId);
            ps.setString(2, tokenId);
            return ps;
        });
        return updated > 0;
    }
}
