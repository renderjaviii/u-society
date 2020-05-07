package common.manager.domain.service.otp.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import common.manager.app.api.OtpApi;
import common.manager.domain.converter.Converter;
import common.manager.domain.exception.GenericException;
import common.manager.domain.model.Otp;
import common.manager.domain.repository.OtpRepository;
import common.manager.domain.service.otp.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    private static final String INVALID_OTP_MESSAGE = "INVALID_OTP";
    private static final int MIN_VALUE = 10000;
    private static final int MAX_VALUE = 99999;

    @Value("${config.otp-expiry-time}")
    private int otpExpiryTime;

    private final OtpRepository otpRepository;
    private final Random random;
    private final Clock clock;

    @Autowired
    public OtpServiceImpl(OtpRepository otpRepository, Clock clock) {
        this.otpRepository = otpRepository;
        random = new Random();
        this.clock = clock;
    }

    @Override
    public OtpApi create(String username) {
        Otp otp = otpRepository.save(Otp.newBuilder()
                .active(TRUE)
                .createdAt(LocalDateTime.now(clock))
                .expiresAt(LocalDateTime.now(clock).plusDays(otpExpiryTime))
                .ownerUsername(username)
                .otpCode(generateOtpCode())
                .build());

        return Converter.otp(otpRepository.getOne(otp.getId()));
    }

    private String generateOtpCode() {
        String otpCode;
        do {
            otpCode = String.valueOf(random.nextInt((MAX_VALUE - MIN_VALUE) + 1) + MIN_VALUE);
        } while (otpRepository.countByOtpCodeAndActive(otpCode, TRUE) != 0);
        return otpCode;
    }

    @Override
    public void validate(String username, String otpCode) throws GenericException {
        Optional<Otp> optionalOTP = otpRepository.findByOwnerUsernameAndOtpCode(username, otpCode);
        if (optionalOTP.isPresent()) {
            Otp otp = optionalOTP.get();

            if (!otp.isActive()) {
                throw new GenericException(getErrorMessage(otpCode, "OTP already used: %s."), INVALID_OTP_MESSAGE);
            }

            if (otp.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
                throw new GenericException(getErrorMessage(otpCode, "Expired OTP: %s."), "EXPIRED_OTP");
            }

            otp.setActive(FALSE);
            otpRepository.save(otp);
        } else {
            throw new GenericException(getErrorMessage(otpCode, "Invalid OTP: %s."), INVALID_OTP_MESSAGE);
        }
    }

    private String getErrorMessage(String otpCode, String s) {
        return String.format(s, otpCode);
    }

}
