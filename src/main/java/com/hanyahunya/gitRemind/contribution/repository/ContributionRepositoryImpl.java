package com.hanyahunya.gitRemind.contribution.repository;

import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContributionRepositoryImpl implements ContributionRepository {
    private final JdbcTemplate jdbcTemplate;
    public ContributionRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Contribution> getContributionByMemberId(String memberId) {
        final String sql = "SELECT email, git_username, alarm_hour_bit, is_today_committed FROM member WHERE member_id = ?";
        List<Contribution> rows = jdbcTemplate.query(sql, contributionRowMapper(sql), memberId);
        return rows.stream().findAny();
    }

    @Override
    public Optional<Contribution> getContributionByGitUsername(String gitUsername) {
        final String sql = "SELECT alarm_hour_bit, is_today_committed FROM member WHERE git_username = ?";
        List<Contribution> rows = jdbcTemplate.query(sql, contributionRowMapper(sql), gitUsername);
        return rows.stream().findAny();
    }

    @Override
    public boolean updateContribution(Contribution contribution) {
        StringBuilder sqlSb = new StringBuilder("UPDATE member SET ");
        List<Object> parameters = new ArrayList<>();

        if (contribution.getGitUsername() != null) {
            sqlSb.append("git_username = ?, ");
            parameters.add(contribution.getGitUsername());
        }
        if (contribution.getAlarmBit() != -1) {
            sqlSb.append("alarm_hour_bit = ?, ");
            parameters.add(contribution.getAlarmBit());
        }
        if (contribution.getCommitted() != null) {
            sqlSb.append("is_today_committed = ?, ");
            parameters.add(contribution.getCommitted());
        }
        int sbLength = sqlSb.length();
        final String sql = sqlSb.delete(sbLength - 2, sbLength).toString() + " WHERE member_id = ?";
        parameters.add(contribution.getMemberId());

        Object[] parameterArray = parameters.toArray();
        return (jdbcTemplate.update(sql, parameterArray) > 0);
    }

    @Override
    public List<Contribution> findAllContributions() {
        final String sql = "SELECT member_id, email, git_username, alarm_hour_bit, is_today_committed FROM member WHERE is_today_committed = false AND git_username is not null";
        return jdbcTemplate.query(sql, contributionRowMapper(sql));
    }

    @Override
    public void resetIsTodayCommitted() {
        final String sql = "UPDATE member SET is_today_committed = 0";
        jdbcTemplate.update(sql);
    }

    private RowMapper<Contribution> contributionRowMapper(String sql) {
        String sqlColumns = sql.substring(0, sql.toLowerCase().indexOf("from"));
        return (rs, rowNum) -> {
            Contribution contribution = Contribution.builder().build();
            if (sqlColumns.contains("member_id")) {
                contribution.setMemberId(rs.getString("member_id"));
            }
            if (sqlColumns.contains("git_username")) {
                contribution.setGitUsername(rs.getString("git_username"));
            }
            if(sqlColumns.contains("alarm_hour_bit")) {
                contribution.setAlarmBit(rs.getInt("alarm_hour_bit"));
            }
            if (sqlColumns.contains("is_today_committed")) {
                contribution.setCommitted(rs.getBoolean("is_today_committed"));
            }

            // for Scheduler only
            if (sqlColumns.contains("email")) {
                contribution.setEmail(rs.getString("email"));
            }
            return contribution;
        };
    }
}
