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
            ps.setString(1, token.getToken_id());
            ps.setString(2, token.getAccess_token());
            ps.setString(3, token.getRefresh_token());
            ps.setTimestamp(4, new Timestamp(token.getAccess_token_expiry().getTime()));
            ps.setTimestamp(5, new Timestamp(token.getRefresh_token_expiry().getTime()));
            return ps;
        });
        return updated > 0;
    }

    @Override
    public boolean updateToken(Token token) {
        StringBuilder sqlSb = new StringBuilder("UPDATE token SET ");
        List<Object> params = new ArrayList<>();

        if (token.getAccess_token() != null) {
            sqlSb.append("access_token = ?, ");
            params.add(token.getAccess_token());
        }
        if (token.getRefresh_token() != null) {
            sqlSb.append("refresh_token = ?, ");
            params.add(token.getRefresh_token());
        }
        if (token.getAccess_token_expiry() != null) {
            sqlSb.append("access_token_expiry = ?, ");
            params.add(token.getAccess_token_expiry());
        }
        if (token.getRefresh_token_expiry() != null) {
            sqlSb.append("refresh_token_expiry = ?, ");
            params.add(token.getRefresh_token_expiry());
        }
        int sqlSbLength = sqlSb.length();
        final String sql =  sqlSb.delete(sqlSbLength - 2, sqlSbLength).toString() + " WHERE token_id = ?";
        params.add(token.getToken_id());

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
                token.setToken_id(rs.getString("token_id"));
            }
            if (sqlColumns.contains("access_token")) {
                token.setAccess_token(rs.getString("access_token"));
            }
            if(sqlColumns.contains("refresh_token")) {
                token.setRefresh_token(rs.getString("refresh_token"));
            }
            if (sqlColumns.contains("access_token_expiry")) {
                token.setAccess_token_expiry(rs.getTimestamp("access_token_expiry"));
            }
            if (sqlColumns.contains("refresh_token_expiry")) {
                token.setRefresh_token_expiry(rs.getTimestamp("refresh_token_expiry"));
            }
            return token;
        };
    }
}
