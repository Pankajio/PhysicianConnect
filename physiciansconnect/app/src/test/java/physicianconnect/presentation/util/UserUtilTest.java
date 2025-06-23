package physicianconnect.presentation.util;

import org.junit.jupiter.api.Test;
import physicianconnect.objects.Physician;
import physicianconnect.objects.Receptionist;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserUtilTest {

    @Test
    void testGetUserId() {
        Physician p = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Receptionist r = new Receptionist("rec1", "Bob", "bob@email.com", "pw");
        assertEquals("doc1", UserUtil.getUserId(p));
        assertEquals("rec1", UserUtil.getUserId(r));
        assertEquals("", UserUtil.getUserId("not a user"));
    }

    @Test
    void testGetUserName() {
        Physician p = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Receptionist r = new Receptionist("rec1", "Bob", "bob@email.com", "pw");
        assertEquals("Dr. Alice", UserUtil.getUserName(p));
        assertEquals("Bob", UserUtil.getUserName(r));
        assertEquals("", UserUtil.getUserName("not a user"));
    }

    @Test
    void testGetUserEmail() {
        Physician p = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Receptionist r = new Receptionist("rec1", "Bob", "bob@email.com", "pw");
        assertEquals("alice@email.com", UserUtil.getUserEmail(p));
        assertEquals("bob@email.com", UserUtil.getUserEmail(r));
        assertEquals("", UserUtil.getUserEmail("not a user"));
    }

    @Test
    void testGetUserType() {
        Physician p = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Receptionist r = new Receptionist("rec1", "Bob", "bob@email.com", "pw");
        assertEquals("physician", UserUtil.getUserType(p));
        assertEquals("receptionist", UserUtil.getUserType(r));
        assertEquals("", UserUtil.getUserType("not a user"));
    }

    @Test
    void testGetUserNameByIdAndType() {
        Physician p = new Physician("doc1", "Dr. Alice", "alice@email.com", "pw");
        Receptionist r = new Receptionist("rec1", "Bob", "bob@email.com", "pw");
        List<Object> users = List.of(p, r);
        assertEquals("Dr. Alice", UserUtil.getUserName("doc1", "physician", users));
        assertEquals("Bob", UserUtil.getUserName("rec1", "receptionist", users));
        assertEquals("unknown", UserUtil.getUserName("unknown", "physician", users));
    }
}