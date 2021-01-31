package usociety.manager.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    int countByOtpCodeAndActive(String otpCode, boolean active);

    Optional<Otp> findByEmailOwnerAndOtpCode(String email, String otpCode);

}
