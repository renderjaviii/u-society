package usociety.manager.domain.service.payment.impl;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.PaymentApi;
import usociety.manager.app.api.PaymentApi.CardPaymentApi;
import usociety.manager.app.api.PaymentApi.PSEPaymentApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.CardPayment;
import usociety.manager.domain.model.PSEPayment;
import usociety.manager.domain.repository.PaymentRepository;
import usociety.manager.domain.service.common.impl.AbstractServiceImpl;
import usociety.manager.domain.service.payment.PaymentService;

@Service
public class PaymentServiceImpl extends AbstractServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public void create(String username, PaymentApi payment) {
        if (payment instanceof CardPaymentApi) {
            saveCardPayment(payment);
        } else if (payment instanceof PSEPaymentApi) {
            savePSEPayment(payment);
        } else {
            throw new UnsupportedOperationException("Unsupported payment type");
        }
    }

    private void saveCardPayment(PaymentApi payment) {
        CardPaymentApi cardPaymentApi = (CardPaymentApi) payment;
        paymentRepository.save(CardPayment.newBuilder()
                .amount(cardPaymentApi.getAmount())
                .documentType(cardPaymentApi.getDocumentType().getValue())
                .documentNumber(cardPaymentApi.getDocumentNumber())
                .cardType(cardPaymentApi.getCardType().getValue())
                .nameOnTheCard(cardPaymentApi.getNameOnTheCard())
                .cardNumber(cardPaymentApi.getCardNumber())
                .createdAt(cardPaymentApi.getCreatedAt())
                .createdAt(LocalDateTime.now(clock))
                .quotes(cardPaymentApi.getQuotes())
                .cvv(cardPaymentApi.getCvv())
                .build());
    }

    private void savePSEPayment(PaymentApi payment) {
        PSEPaymentApi psePaymentApi = (PSEPaymentApi) payment;
        paymentRepository.save(PSEPayment.newBuilder()
                .documentType(psePaymentApi.getDocumentType().getValue())
                .documentNumber(psePaymentApi.getDocumentNumber())
                .pseBankCode(psePaymentApi.getBankCode())
                .createdAt(psePaymentApi.getCreatedAt())
                .createdAt(LocalDateTime.now(clock))
                .pseEmail(psePaymentApi.getEmail())
                .amount(psePaymentApi.getAmount())
                .build());
    }

}
