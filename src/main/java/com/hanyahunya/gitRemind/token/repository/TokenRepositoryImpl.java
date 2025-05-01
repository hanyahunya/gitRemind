package com.hanyahunya.gitRemind.token.repository;

import com.hanyahunya.gitRemind.token.entity.Token;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TokenRepositoryImpl implements TokenRepository {
    private final JdbcTemplate jdbcTemplate;

    public TokenRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean saveToken(Token token) {
        final String sql = "INSERT INTO token (token_id, access_token, refresh_token, access_token_expiry, refresh_token_expiry) VALUES (?, ?, ?, ?, ?)";
        int updated = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token.getTokenId());
            ps.setString(2, token.getAccessToken());
            ps.setString(3, token.getRefreshToken());
            ps.setTimestamp(4, new Timestamp(token.getAccessTokenExpiry().getTime()));
            ps.setTimestamp(5, new Timestamp(token.getRefreshTokenExpiry().getTime()));
            return ps;
        });
        return updated > 0;
    }

    @Override
    public boolean updateToken(Token token) {
        StringBuilder sqlSb = new StringBuilder("UPDATE token SET ");
        List<Object> params = new ArrayList<>();

        if (token.getAccessToken() != null) {
            sqlSb.append("access_token = ?, ");
            params.add(token.getAccessToken());
        }
        if (token.getRefreshToken() != null) {
            sqlSb.append("refresh_token = ?, ");
            params.add(token.getRefreshToken());
        }
        if (token.getAccessTokenExpiry() != null) {
            sqlSb.append("access_token_expiry = ?, ");
            params.add(token.getAccessTokenExpiry());
        }
        if (token.getRefreshTokenExpiry() != null) {
            sqlSb.append("refresh_token_expiry = ?, ");
            params.add(token.getRefreshTokenExpiry());
        }
        int sqlSbLength = sqlSb.length();
        final String sql =  sqlSb.delete(sqlSbLength - 2, sqlSbLength).toString() + " WHERE token_id = ?";
        params.add(token.getTokenId());
        
        return jdbcTemplate.update(sql, params.toArray()) > 0;
    }

    @Override
    public Optional<Token> findByTokenId(String tokenId) {
        final String sql = "SELECT token_id, access_token, refresh_token, access_token_expiry FROM token WHERE token_id = ?";
        List<Token> rows = jdbcTemplate.query(sql, tokenRowMapper(sql), tokenId);
        return rows.stream().findFirst();
    }

    @Override
    public boolean deleteByTokenId(String tokenId) {
        final String sql = "DELETE FROM token WHERE token_id = ?";
        return jdbcTemplate.update(sql, tokenId) > 0;
    }

    private RowMapper<Token> tokenRowMapper(String sql) {
        String sqlColumns = sql.substring(0, sql.toLowerCase().indexOf("from"));
        return (rs, rowNum) -> {
            Token token = Token.builder().build();
            if(sqlColumns.contains("token_id")) {
                token.setTokenId(rs.getString("token_id"));
            }
            if (sqlColumns.contains("access_token")) {
                token.setAccessToken(rs.getString("access_token"));
            }
            if(sqlColumns.contains("refresh_token")) {
                token.setRefreshToken(rs.getString("refresh_token"));
            }
            if (sqlColumns.contains("access_token_expiry")) {
                token.setAccessTokenExpiry(rs.getTimestamp("access_token_expiry"));
            }
            if (sqlColumns.contains("refresh_token_expiry")) {
                token.setRefreshTokenExpiry(rs.getTimestamp("refresh_token_expiry"));
            }
            return token;
        };
    }
}
