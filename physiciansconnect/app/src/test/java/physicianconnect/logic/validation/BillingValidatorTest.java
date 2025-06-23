package physicianconnect.logic.validation;

import org.junit.jupiter.api.Test;
import physicianconnect.logic.exceptions.InvalidBillingException;
import physicianconnect.objects.ServiceItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BillingValidatorTest {

    @Test
    void testValidatePaymentAmountValid() {
        assertDoesNotThrow(() -> BillingValidator.validatePaymentAmount(50, 100));
    }

    @Test
    void testValidatePaymentAmountZeroThrows() {
        assertThrows(InvalidBillingException.class, () -> BillingValidator.validatePaymentAmount(0, 100));
    }

    @Test
    void testValidatePaymentAmountNegativeThrows() {
        assertThrows(InvalidBillingException.class, () -> BillingValidator.validatePaymentAmount(-10, 100));
    }

    @Test
    void testValidatePaymentAmountExceedsBalanceThrows() {
        assertThrows(InvalidBillingException.class, () -> BillingValidator.validatePaymentAmount(200, 100));
    }

    @Test
    void testValidateInvoiceServicesValid() {
        assertDoesNotThrow(() -> BillingValidator.validateInvoiceServices(List.of(new ServiceItem("Consult", 100))));
    }

    @Test
    void testValidateInvoiceServicesNullThrows() {
        assertThrows(InvalidBillingException.class, () -> BillingValidator.validateInvoiceServices(null));
    }

    @Test
    void testValidateInvoiceServicesEmptyThrows() {
        assertThrows(InvalidBillingException.class, () -> BillingValidator.validateInvoiceServices(List.of()));
    }
}