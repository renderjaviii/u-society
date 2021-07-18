package usociety.manager.domain.service.otp.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.OtpApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Otp;
import usociety.manager.domain.repository.OtpRepository;
import usociety.manager.domain.service.otp.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpServiceImpl.class);

    private static final String INVALID_OTP_MESSAGE = "INVALID_OTP";
    private static final int ZERO = 0;

    @Value("${config.otp-length:5}")
    private int otpLength;

    @Value("${config.otp-expiry-time}")
    private int otpExpiryTime;

    private final OtpRepository otpRepository;
    private final Clock clock;

    @Autowired
    public OtpServiceImpl(OtpRepository otpRepository, Clock clock) {
        this.otpRepository = otpRepository;
        this.clock = clock;
    }

    @Override
    public OtpApi create(String email) {
        Otp otp = otpRepository.save(Otp.newBuilder()
                .expiresAt(LocalDateTime.now(clock).plusDays(otpExpiryTime))
                .createdAt(LocalDateTime.now(clock))
                .otpCode(generateOtpCode())
                .emailOwner(email)
                .active(TRUE)
                .build());

        LOGGER.debug("Created OTP {} for email = {}.", otp.getOtpCode(), email);
        return Converter.otp(otp);
    }

    private String generateOtpCode() {
        String otpCode;
        do {
            otpCode = RandomStringUtils.random(otpLength, FALSE, TRUE);
        } while (otpRepository.countByOtpCodeAndActive(otpCode, TRUE) != ZERO);
        return otpCode;
    }

    @Override
    public void validate(String email, String otpCode) throws GenericException {
        Optional<Otp> optionalOTP = otpRepository.findByEmailOwnerAndOtpCode(email, otpCode);
        if (optionalOTP.isPresent()) {
            Otp otp = optionalOTP.get();

            validateOTP(otpCode, otp);

            otp.setActive(FALSE);
            otpRepository.save(otp);
        } else {
            throw new GenericException(getErrorMessage("OTP does not exist: %s.", otpCode), INVALID_OTP_MESSAGE);
        }
    }

    private void validateOTP(String otpCode, Otp otp) throws GenericException {
        if (!Boolean.TRUE.equals(otp.isActive())) {
            throw new GenericException(getErrorMessage("Invalid OTP: %s.", otpCode), INVALID_OTP_MESSAGE);
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
            throw new GenericException(getErrorMessage("Expired OTP: %s.", otpCode), INVALID_OTP_MESSAGE);
        }
    }

    private String getErrorMessage(String format, String otpCode) {
        return String.format(format, otpCode);
    }

}
