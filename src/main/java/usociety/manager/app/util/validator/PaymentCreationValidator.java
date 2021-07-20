package usociety.manager.app.util.validator;

import static usociety.manager.domain.enums.CardTypeEnum.CREDIT;

import java.util.Objects;
import java.util.StringJoiner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import usociety.manager.app.api.PaymentApi;

public class PaymentCreationValidator implements ConstraintValidator<PaymentCreationConstraint, PaymentApi> {

    @Override
    public boolean isValid(PaymentApi payment, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        StringJoiner joiner = new StringJoiner(", ");
        if (payment instanceof PaymentApi.CardPaymentApi) {
            PaymentApi.CardPaymentApi cardPaymentApi = (PaymentApi.CardPaymentApi) payment;

            if (CREDIT.equals(cardPaymentApi.getCardType())
                    && (StringUtils.isEmpty(cardPaymentApi.getCvv()) || Objects.isNull(cardPaymentApi.getQuotes()))) {
                joiner.add(String.format("If card is %s, you must send CVV and Quotes", CREDIT));
            }
            context.buildConstraintViolationWithTemplate(joiner.toString()).addConstraintViolation();
        }

        return joiner.length() == 0;
    }

}
