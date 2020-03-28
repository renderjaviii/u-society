package company.businessmanager.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import company.businessmanager.domain.model.Credential;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {

    Optional<Credential> findByUsername(String username);

    Optional<Credential> findByUsernameOrClientId(String username,
                                                  String clientId);

}
