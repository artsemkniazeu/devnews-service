package pl.dev.news.devnewsservice.service.impl;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dev.news.devnewsservice.config.AppConfiguration;
import pl.dev.news.devnewsservice.service.TwilioService;

@Service
@RequiredArgsConstructor
public class TwilioServiceImpl implements TwilioService {

    private final AppConfiguration appConfiguration;

    @Override
    public Verification sendVerificationSms(final String phone) {
        return Verification.creator(
                appConfiguration.getTwilio().getServiceSid(),
                phone,
                "sms"
        ).create();
    }

    @Override
    public VerificationCheck checkVerificationCode(final String phone, final String code) {
        return  VerificationCheck.creator(
                appConfiguration.getTwilio().getServiceSid(),
                code
        ).setTo(phone).create();
    }
}
