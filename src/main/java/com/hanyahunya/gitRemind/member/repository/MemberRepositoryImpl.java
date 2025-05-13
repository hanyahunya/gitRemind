
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
        final String sql = "INSERT INTO member(member_id, login_id, password, email, country) VALUES (?, ?, ?, ?, ?)";
        int updated = jdbcTemplate.update(
                conn -> {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, member.getMemberId());
                    ps.setString(2, member.getLoginId());
                    ps.setString(3, member.getPassword());
                    ps.setString(4, member.getEmail());
                    ps.setString(5, member.getCountry());
                    return ps;
                });
        return updated > 0;
    }

    @Override
    public Optional<Member> findMemberByMemberId(String memberId) {
        final String sql = "SELECT login_id, password, email, country FROM member WHERE member_id = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), memberId);
        return memberList.stream().findFirst();
    }

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        final String sql = "SELECT member_id, country FROM member WHERE email = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), email);
        return memberList.stream().findFirst();
    }

    @Override
    public Optional<Member> findMemberByLoginId(Member member) {
        final String sql = "SELECT member_id, password FROM member WHERE login_id = ?";
        List<Member> memberList = jdbcTemplate.query(sql, memberRowMapper(sql), member.getLoginId());
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
        if (member.getPassword() != null) {
            sqlSb.append("password = ?, ");
            parameters.add(member.getPassword());
        }
        if (member.getCountry() != null) {
            sqlSb.append("country = ?, ");
            parameters.add(member.getCountry());
        }

        int columnSbLength = sqlSb.length();

        final String sql = sqlSb.delete(columnSbLength - 2, columnSbLength).toString() + " WHERE member_id = ?";

        parameters.add(member.getMemberId());

        Object[] parameterArray = parameters.toArray();

        return jdbcTemplate.update(sql, parameterArray) > 0;
    }

    @Override
    public boolean deleteMember(Member member) {
        final String sql = "DELETE FROM member WHERE member_id = ?";
        int updated = jdbcTemplate.update(sql, member.getMemberId());
        return updated > 0;
    }


    private RowMapper<Member> memberRowMapper (String sql) {
        String sqlColumns = sql.substring(0, sql.toLowerCase().indexOf("from"));
        return (rs, rowNum) -> {
            Member member = Member.builder().build();
            if(sqlColumns.contains("member_id")) {
                member.setMemberId(rs.getString("member_id"));
            }
            if (sqlColumns.contains("login_id")) {
                member.setLoginId(rs.getString("login_id"));
            }
            if (sqlColumns.contains("password")) {
                member.setPassword(rs.getString("password"));
            }
            if(sqlColumns.contains("email")) {
                member.setEmail(rs.getString("email"));
            }
            if(sqlColumns.contains("country")) {
                member.setCountry(rs.getString("country"));
            }
            return member;
        };
    }

}
