package pl.dev.news.devnewsservice;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class MaildevInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final int SMTP_PORT = 1025;
    public static final int WEB_PORT = 1080;

    public static final String MAILDEV_USERNAME = "devnews";
    public static final String MAILDEV_PASSWORD = "devnews";

    public static final GenericContainer MAILDEV =
            new GenericContainer("djfarrelly/maildev:latest")
                    .withCommand("bin/maildev"
                            + " --smtp " + SMTP_PORT
                            + " --web " + WEB_PORT
                            + " --incoming-user " + MAILDEV_USERNAME
                            + " --incoming-pass " + MAILDEV_PASSWORD)
                    .waitingFor(Wait.forListeningPort())
                    .withExposedPorts(WEB_PORT, SMTP_PORT);

    static {
        MAILDEV.start();
    }

    public static String getHost() {
        return MAILDEV.getContainerIpAddress();
    }

    public static int getSmtpPort() {
        return MAILDEV.getMappedPort(SMTP_PORT);
    }

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        applyProperties(applicationContext);
    }

    private void applyProperties(final ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.mail.host:" + getHost(),
                "spring.mail.password:" + MAILDEV_PASSWORD,
                "spring.mail.port:" + getSmtpPort(),
                "spring.mail.username:" + MAILDEV_USERNAME
        ).applyTo(applicationContext);
    }

}
