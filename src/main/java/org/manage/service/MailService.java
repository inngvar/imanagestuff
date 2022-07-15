package org.manage.service;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Location;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;
import org.manage.config.JHipsterProperties;
import org.manage.domain.User;
import io.quarkus.mailer.MailTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.manage.service.dto.DayReportDTO;
import org.manage.service.dto.TimeLogReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for sending emails.
 */
@ApplicationScoped
public class MailService {
    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    final JHipsterProperties jHipsterProperties;

//    final MailTemplate activationEmail;
//
//    final MailTemplate creationEmail;
//
//    final MailTemplate passwordResetEmail;
//    private final MailTemplate dayReport;
//    private final MailTemplate timeReport;

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        //@Location("mail/activationEmail")
        public static native MailTemplate.MailTemplateInstance activationEmail();

        ///@Location("mail/creationEmail")
        public static native MailTemplate.MailTemplateInstance  creationEmail();

        //@Location("mail/passwordResetEmail")
        public static native MailTemplate.MailTemplateInstance  passwordResetEmail();

        //@Location("mail/dayReport")
        public static native MailTemplate.MailTemplateInstance  dayReport();

        ///@Location("mail/timeReport")
        public static native MailTemplate.MailTemplateInstance  timeReport();
    }

    @Inject
    public MailService(
        JHipsterProperties jHipsterProperties) {
//        @ResourcePath("mail/activationEmail") MailTemplate activationEmail,
//        @ResourcePath("mail/creationEmail") MailTemplate creationEmail,
//        @ResourcePath("mail/passwordResetEmail") MailTemplate passwordResetEmail,
//        @ResourcePath("mail/dayReport") MailTemplate dayReport,
//        @ResourcePath("mail/timeReport") MailTemplate timeReport)

        this.jHipsterProperties = jHipsterProperties;
//        this.activationEmail = activationEmail;
//        this.creationEmail = creationEmail;
//        this.passwordResetEmail = passwordResetEmail;
//        this.dayReport = dayReport;
//        this.timeReport = timeReport;
    }

    public CompletionStage<Void> sendEmailFromTemplate(User user, MailTemplate.MailTemplateInstance template, String subject) {
        return template
            .to(user.email)
            .subject(subject)
            .data(BASE_URL, jHipsterProperties.mail.baseUrl)
            .data(USER, user)
            .send()
            .onItem().invoke( it -> log.debug("Sent email to User '{}'", user.email))
            .subscribeAsCompletionStage();

    }

    public CompletionStage<Void> sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.email);
        return sendEmailFromTemplate(user, Templates.activationEmail(), "jhipsterSampleApplication account activation is required");
    }

    public CompletionStage<Void> sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.email);
        return sendEmailFromTemplate(user,  Templates.creationEmail(), "jhipsterSampleApplication account activation is required");
    }

    public CompletionStage<Void> sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.email);
        return sendEmailFromTemplate(user, Templates.passwordResetEmail(), "jhipsterSampleApplication password reset");
    }

    public CompletionStage<Void> sendDayReport(DayReportDTO dayReportDTO) {
        return Templates.dayReport()
            .to(Arrays.stream(dayReportDTO.project.sendReports.split(",|;|:|\\s"))
                .filter(a -> !StringUtil.isBlank(a))
                .map(String::trim)
                .toArray(String[]::new))
            .subject(dayReportDTO.subject)
            .data("report", dayReportDTO)
            .send().subscribeAsCompletionStage();
    }

    public CompletionStage<Void> sendTimeReport(TimeLogReportDTO dto, Collection<String> addresses) {
        return Templates.timeReport().to(addresses
            .stream().filter(StringUtils::isNotBlank)
            .map(String::trim)
            .toArray(String[]::new))
            .subject(dto.subject)
            .data("report", dto)
            .send().subscribeAsCompletionStage();
    }
}
