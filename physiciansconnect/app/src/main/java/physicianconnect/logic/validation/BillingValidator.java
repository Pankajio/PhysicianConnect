package physicianconnect.logic.validation;

import physicianconnect.logic.exceptions.InvalidBillingException;
import java.util.List;
import physicianconnect.objects.ServiceItem;

public class BillingValidator {
    public static void validatePaymentAmount(double amount, double balance) throws InvalidBillingException {
        if (amount <= 0) throw new InvalidBillingException("Payment must be greater than zero.");
        if (amount > balance) throw new InvalidBillingException("Payment exceeds outstanding balance.");
    }
    public static void validateInvoiceServices(List<ServiceItem> services) throws InvalidBillingException {
        if (services == null || services.isEmpty()) throw new InvalidBillingException("At least one service is required.");
    }

}
