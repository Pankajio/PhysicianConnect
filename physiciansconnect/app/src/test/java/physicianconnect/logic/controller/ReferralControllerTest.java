package physicianconnect.logic.controller;

import org.junit.jupiter.api.*;
import org.mockito.*;
import physicianconnect.logic.exceptions.InvalidReferralException;
import physicianconnect.logic.manager.ReferralManager;
import physicianconnect.objects.Referral;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReferralControllerTest {

    @Mock
    private ReferralManager referralManager;

    private ReferralController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ReferralController(referralManager);
    }

    @Test
    void testCreateReferralDelegates() throws Exception {
        doNothing().when(referralManager).addReferral(any());
        controller.createReferral("doc1", "Alice", "Specialist", "details");
        verify(referralManager).addReferral(any());
    }

    @Test
    void testCreateReferralInvalidThrows() {
        assertThrows(InvalidReferralException.class, () ->
                controller.createReferral("doc1", "", "Specialist", "details"));
    }
}