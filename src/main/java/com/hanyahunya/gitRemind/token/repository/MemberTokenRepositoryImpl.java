package com.hanyahunya.gitRemind.token.repository;

import com.hanyahunya.gitRemind.token.entity.MemberToken;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Optional;

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

    @Override
    public String findMemberIdByTokenId(String tokenId) {
        final String sql = "SELECT member_id FROM member_token WHERE token_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, tokenId);
    }

    @Override
    public boolean deleteAllByMemberId(String memberId) {
        final String sql = "DELETE FROM member_token WHERE member_id = ?";
        int updated = jdbcTemplate.update(sql, memberId);
        return updated > 0;
    }

    @Override
    public void deleteAllByMemberIdAndTokenIdNot(String memberId, String tokenId) {
        final String sql = "DELETE FROM member_token WHERE member_id = ? AND token_id != ?";
        jdbcTemplate.update(sql, memberId, tokenId);
    }
}
