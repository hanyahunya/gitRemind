package com.hanyahunya.gitRemind.member.repository;

import com.hanyahunya.gitRemind.member.entity.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl implements  MemberRepository{
    private final JdbcTemplate jdbcTemplate;
    public MemberRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean saveMember(Member member) {
        final String sql = "INSERT INTO member(mid, id, pw, email) VALUES (?, ?, ?, ?)";
        int updated = jdbcTemplate.update(
                conn -> {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, member.getMid());
                    ps.setString(2, member.getId());
                    ps.setString(3, member.getPw());
                    ps.setString(4, member.getEmail());
                    return ps;
                });
        return updated > 0;
    }

    @Override
    public Optional<Member> findMemberByMid(String mid) {
        final String sql = "SELECT pw, email, token_version FROM member WHERE mid = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), mid);
        return memberList.stream().findFirst();
    }

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        final String sql = "SELECT mid, token_version FROM member WHERE email = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), email);
        return memberList.stream().findFirst();
    }

    @Override
    public Optional<Member> validateMember(Member member) {
        final String sql = "SELECT pw, mid FROM member WHERE id = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), member.getId());
        return memberList.stream().findFirst();
    }

    @Override
    public boolean updateMember(Member member) {
        StringBuilder sqlSb = new StringBuilder("UPDATE member SET ");
        List<Object> parameters = new ArrayList<>();

        if (member.getEmail() != null) {
            sqlSb.append("email = ?, ");
            parameters.add(member.getEmail());
        }
        if (member.getPw() != null) {
            sqlSb.append("pw = ?, ");
            parameters.add(member.getPw());
        }
        if (member.getToken_version() != -1) {
            sqlSb.append("token_version = ?, ");
            parameters.add(member.getToken_version());
        }

        int columnSbLength = sqlSb.length();

        final String sql = sqlSb.delete(columnSbLength - 2, columnSbLength).toString() + " WHERE mid = ?";

        parameters.add(member.getMid());

        Object[] parameterArray = parameters.toArray();

        return jdbcTemplate.update(sql, parameterArray) > 0;
    }


    private RowMapper<Member> memberRowMapper (String sql) {
        String sqlColumns = sql.substring(0, sql.toLowerCase().indexOf("from"));
        return (rs, rowNum) -> {
            Member member = Member.builder().build();
            if(sqlColumns.contains("mid")) {
                member.setMid(rs.getString("mid"));
            }
            if (sqlColumns.contains("pw")) {
                member.setPw(rs.getString("pw"));
            }
            if(sqlColumns.contains("email")) {
                member.setEmail(rs.getString("email"));
            }
            if (sqlColumns.contains("token_version")) {
                member.setToken_version(rs.getInt("token_version"));
            }
            return member;
        };
    }

}
