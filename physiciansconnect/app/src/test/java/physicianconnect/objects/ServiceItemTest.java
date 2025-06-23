package physicianconnect.objects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceItemTest {

    @Test
    void testConstructorAndGettersSetters() {
        ServiceItem s = new ServiceItem("Consult", 100);
        assertEquals("Consult", s.getName());
        assertEquals(100, s.getCost());
        s.setCost(120);
        assertEquals(120, s.getCost());
    }

    @Test
    void testParseList() {
        List<ServiceItem> list = ServiceItem.parseList("Consult:100,Lab:50");
        assertEquals(2, list.size());
        assertEquals("Consult", list.get(0).getName());
        assertEquals(100, list.get(0).getCost());
        assertEquals("Lab", list.get(1).getName());
        assertEquals(50, list.get(1).getCost());
    }

    @Test
    void testParseListEmptyOrNull() {
        assertTrue(ServiceItem.parseList("").isEmpty());
        assertTrue(ServiceItem.parseList(null).isEmpty());
    }
}