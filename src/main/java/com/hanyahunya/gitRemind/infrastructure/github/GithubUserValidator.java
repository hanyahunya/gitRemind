package com.hanyahunya.gitRemind.infrastructure.github;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class GithubUserValidator {
        public static boolean isValid(String username) {
            String urlStr = "https://github.com/users/" + username + "/contributions";
            HttpURLConnection conn = null;

            try {
                URL url = new URL(urlStr);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "git-remind-app");
                conn.setConnectTimeout(10000);
                return conn.getResponseCode() == 200;

            } catch (IOException e) {
                log.warn("{}-{}", "GithubUserValidator", e.getClass().getSimpleName());
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }
