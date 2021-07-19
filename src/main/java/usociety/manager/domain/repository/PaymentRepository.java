package usociety.manager.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}
