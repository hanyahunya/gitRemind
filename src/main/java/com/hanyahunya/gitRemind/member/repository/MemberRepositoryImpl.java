package com.hanyahunya.gitRemind.member.repository;

import com.hanyahunya.gitRemind.entity.Member;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

public class MemberRepositoryImpl implements  MemberRepository{
    private final JdbcTemplate jdbcTemplate;
    public MemberRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean saveMember(Member member) {
        final String sql = "INSERT INTO member(mid, id, pw, email, git_addr) VALUES (?, ?, ?, ?, ?)";
        int updated = jdbcTemplate.update(
                conn -> {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, member.getMid());
                    ps.setString(2, member.getId());
                    ps.setString(3, member.getPw());
                    ps.setString(4, member.getEmail());
                    ps.setString(5, member.getGit_addr());
                    return ps;
                });
        return updated > 0;
    }
}
