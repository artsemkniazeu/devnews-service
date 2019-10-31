package pl.dev.news.devnewsservice.service;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

public interface TwilioService {

    Verification sendVerificationSms(String phone);

    VerificationCheck checkVerificationCode(String phone, String code);

}
