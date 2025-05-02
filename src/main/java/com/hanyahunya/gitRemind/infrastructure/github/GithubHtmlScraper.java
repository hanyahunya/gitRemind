package com.hanyahunya.gitRemind.infrastructure.github;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDate;

@Slf4j
public class GithubHtmlScraper {
    public int getTodayContributionCount(String gitUsername) {
        String today = LocalDate.now().toString();
        String url = "https://github.com/users/" + gitUsername + "/contributions?to=" + today;
        try {
            Document doc = Jsoup.connect(url).timeout(10000).get();
            Element todayTd = doc.select("td[data-date=\"" + today + "\"]").first();

            if (todayTd != null) {
                String levelStr = todayTd.attr("data-level");
                return Integer.parseInt(levelStr);
            } else {
                throw new RuntimeException("Today Contribution not found");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }
}
