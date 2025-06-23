package physicianconnect.presentation.util;

import javax.swing.*;
import java.awt.*;

public class TestUtils {
    public static JTextField findTextField(Container container, int index) {
        return (JTextField) findComponentOfType(container, JTextField.class, index);
    }

    public static JTextArea findTextArea(Container container, int index) {
        return (JTextArea) findComponentOfType(container, JTextArea.class, index);
    }

    public static JSpinner findSpinner(Container container, int index) {
        return (JSpinner) findComponentOfType(container, JSpinner.class, index);
    }

    public static JButton getButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equalsIgnoreCase(text)) {
                return (JButton) c;
            }
            if (c instanceof Container) {
                JButton btn = getButton((Container) c, text);
                if (btn != null)
                    return btn;
            }
        }
        return null;
    }

    public static Object getField(Object obj, String fieldName) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Component findComponentOfType(Container container, Class<?> type, int index) {
        int[] count = { 0 };
        return findComponentOfTypeHelper(container, type, index, count);
    }

    private static Component findComponentOfTypeHelper(Container container, Class<?> type, int index, int[] count) {
        for (Component c : container.getComponents()) {
            if (type.isInstance(c)) {
                if (count[0] == index)
                    return c;
                count[0]++;
            }
            if (c instanceof Container) {
                Component found = findComponentOfTypeHelper((Container) c, type, index, count);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    // --- Additional Helper Methods ---

    /** Finds a visible JDialog by its title. */
    public static JDialog findDialogByTitle(String title) {
        for (Window w : Window.getWindows()) {
            if (w instanceof JDialog && w.isVisible() && title.equals(((JDialog) w).getTitle())) {
                return (JDialog) w;
            }
        }
        return null;
    }

    /** Finds the Nth component of the given type in the container tree. */
    public static <T extends JComponent> T findField(Container container, Class<T> clazz, int index) {
        int[] count = {0};
        return findFieldRecursive(container, clazz, index, count);
    }

    private static <T extends JComponent> T findFieldRecursive(Container container, Class<T> clazz, int index, int[] count) {
        for (Component c : container.getComponents()) {
            if (clazz.isInstance(c)) {
                if (count[0] == index) return clazz.cast(c);
                count[0]++;
            }
            if (c instanceof Container) {
                T found = findFieldRecursive((Container) c, clazz, index, count);
                if (found != null) return found;
            }
        }
        return null;
    }

    /** Finds a JButton by its exact text. */
    public static JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText())) return b;
            if (c instanceof Container) {
                JButton btn = findButton((Container) c, text);
                if (btn != null) return btn;
            }
        }
        return null;
    }

    /** Finds the first JComboBox in the container tree. */
    public static JComboBox<?> findComboBox(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComboBox) return (JComboBox<?>) c;
            if (c instanceof Container) {
                JComboBox<?> cb = findComboBox((Container) c);
                if (cb != null) return cb;
            }
        }
        return null;
    }

    /** Finds a component by its name property. */
    public static Component getComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name != null && name.equals(c.getName())) return c;
            if (c instanceof Container) {
                Component child = getComponentByName((Container) c, name);
                if (child != null) return child;
            }
        }
        return null;
    }

    /**
     * Presses the first visible "OK" button in any open dialog asynchronously.
     */
    public static void pressOkOnAnyDialogAsync() {
        javax.swing.Timer timer = new javax.swing.Timer(200, e -> {
            for (Window w : Window.getWindows()) {
                if (w.isShowing() && w instanceof JDialog) {
                    JButton okBtn = getButton((JDialog) w, "OK");
                    if (okBtn != null && okBtn.isShowing() && okBtn.isEnabled()) {
                        okBtn.doClick();
                        break;
                    }
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Presses the first visible "OK" button in any open dialog immediately.
     */
    public static void pressOkOnAnyDialog() {
        for (Window w : Window.getWindows()) {
            if (w.isShowing() && w instanceof JDialog) {
                JButton okBtn = getButton((JDialog) w, "OK");
                if (okBtn != null && okBtn.isShowing() && okBtn.isEnabled()) {
                    okBtn.doClick();
                    break;
                }
            }
        }
    }
}