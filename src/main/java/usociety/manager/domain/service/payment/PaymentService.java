package usociety.manager.domain.service.payment;

import usociety.manager.app.api.PaymentApi;

public interface PaymentService {

    void create(String username, PaymentApi payment);

}
