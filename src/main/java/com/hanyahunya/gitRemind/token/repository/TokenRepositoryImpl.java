package com.hanyahunya.gitRemind.token.repository;

import com.hanyahunya.gitRemind.token.entity.Token;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

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
        return false;
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
