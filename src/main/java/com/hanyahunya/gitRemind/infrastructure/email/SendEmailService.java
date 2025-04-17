package com.hanyahunya.gitRemind.infrastructure.email;

import java.util.Map;

public interface SendEmailService {

    boolean sendEmail(String toEmail, String subject, String templateName, Map<String, Object> variables);
}
