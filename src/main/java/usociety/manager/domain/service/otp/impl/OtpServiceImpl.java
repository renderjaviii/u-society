package usociety.manager.domain.service.otp.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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
import usociety.manager.domain.service.common.CommonServiceImpl;
import usociety.manager.domain.service.otp.OtpService;

@Service
public class OtpServiceImpl extends CommonServiceImpl implements OtpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpServiceImpl.class);

    private static final String INVALID_OTP_MESSAGE = "INVALID_OTP";
    private static final int OTP_LENGTH = 5;

    @Value("${config.otp-expiry-time}")
    private int otpExpiryTime;

    private final OtpRepository otpRepository;

    @Autowired
    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public OtpApi create(String username, String email) {
        Otp otp = otpRepository.save(Otp.newBuilder()
                .active(TRUE)
                .createdAt(LocalDateTime.now(clock))
                .expiresAt(LocalDateTime.now(clock).plusDays(otpExpiryTime))
                .otpCode(generateOtpCode())
                .ownerUsername(username)
                .emailOwner(email)
                .build());

        LOGGER.debug("Created OTP {} for username = {}.", otp.getOtpCode(), username);
        return Converter.otp(otp);
    }

    private String generateOtpCode() {
        String otpCode;
        do {
            otpCode = RandomStringUtils.random(OTP_LENGTH, FALSE, TRUE);
        } while (otpRepository.countByOtpCodeAndActive(otpCode, TRUE) != 0);
        return otpCode;
    }

    @Override
    public void validate(String username, String otpCode) throws GenericException {
        Optional<Otp> optionalOTP = otpRepository.findByUsernameOwnerAndOtpCode(username, otpCode);
        if (optionalOTP.isPresent()) {
            Otp otp = optionalOTP.get();

            if (!otp.isActive()) {
                throw new GenericException(getErrorMessage("OTP already used: %s.", otpCode), INVALID_OTP_MESSAGE);
            }

            if (otp.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
                throw new GenericException(getErrorMessage("Expired OTP: %s.", otpCode), "EXPIRED_OTP");
            }

            otp.setActive(FALSE);
            otpRepository.save(otp);
        } else {
            throw new GenericException(getErrorMessage("Invalid OTP: %s.", otpCode), INVALID_OTP_MESSAGE);
        }
    }

    private String getErrorMessage(String format, String otpCode) {
        return String.format(format, otpCode);
    }

}
