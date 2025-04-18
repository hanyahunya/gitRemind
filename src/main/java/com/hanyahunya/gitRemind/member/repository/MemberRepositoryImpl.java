package com.hanyahunya.gitRemind.member.repository;

import com.hanyahunya.gitRemind.member.entity.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl implements  MemberRepository{
    private final JdbcTemplate jdbcTemplate;
    public MemberRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean saveMember(Member member) {
        final String sql = "INSERT INTO member(mid, id, pw, email, git_username) VALUES (?, ?, ?, ?, ?)";
        int updated = jdbcTemplate.update(
                conn -> {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, member.getMid());
                    ps.setString(2, member.getId());
                    ps.setString(3, member.getPw());
                    ps.setString(4, member.getEmail());
                    ps.setString(5, member.getGit_username());
                    return ps;
                });
        return updated > 0;
    }

    @Override
    public Optional<Member> findMemberByMid(String mid) {
        final String sql = "SELECT email, git_username, token_version FROM member WHERE mid = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), mid);
        return memberList.stream().findFirst();
    }

    @Override
    public Optional<Member> validateMember(Member member) {
        final String sql = "SELECT mid FROM member WHERE id = ? AND pw = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), member.getId(), member.getPw());
        return memberList.stream().findFirst();
    }



    private RowMapper<Member> memberRowMapper (String sql) {
        String sqlColumns = sql.substring(0, sql.toLowerCase().indexOf("from"));
        return (rs, rowNum) -> {
            Member member = Member.builder().build();
            if(sqlColumns.contains("mid")) {
                member.setMid(rs.getString("mid"));
            }
            if(sqlColumns.contains("email")) {
                member.setEmail(rs.getString("email"));
            }
            if(sqlColumns.contains("git_username")) {
                member.setGit_username(rs.getString("git_username"));
            }
            if (sqlColumns.contains("token_version")) {
                member.setToken_version(rs.getInt("token_version"));
            }
            return member;
        };
    }

}
