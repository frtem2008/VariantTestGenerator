package newCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class GuiManager {
    private final JFrame frame;
    private final ArrayList<ComponentState> states;
    private final ArrayList<JComponent> active;
    private final String stateInfo;

    private int state;

    public GuiManager(JFrame frame) {
        this.frame = frame;
        states = new ArrayList<>();
        active = new ArrayList<>();
        stateInfo = "State info hasn't been set";
    }

    public GuiManager(JFrame frame, String stateInfo) {
        this.frame = frame;
        states = new ArrayList<>();
        active = new ArrayList<>();
        this.stateInfo = stateInfo;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    private void updateStates() {
        active.clear();

        for (int i = 0; i < states.size(); i++) {
            states.get(i).component().setVisible(false);
        }

        for (int i = 0; i < states.size(); i++) {
            if (contains(states.get(i).states(), state)) {
                active.add(states.get(i).component());
            }
        }

        for (int i = 0; i < active.size(); i++) {
            active.get(i).setVisible(true);
        }
    }

    private boolean contains(int[] a, int b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b)
                return true;
        }
        return false;
    }

    public void removeFromStates(JComponent component, int[] state) {
        for (int i = 0; i < state.length; i++) {
            removeFromState(component, state[i]);
        }
    }

    public void removeFromState(JComponent component, int state) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).component().equals(component)) {
                if (contains(states.get(i).states(), state)) {
                    int[] newStates = new int[states.get(i).states().length - 1];
                    for (int j = 0; j < newStates.length; j++) {
                        if (states.get(i).states()[j] != state) {
                            newStates[j] = states.get(i).states()[j];
                        }
                    }
                    states.remove(i);
                    states.add(new ComponentState(component, newStates));
                    break;
                }
            }
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        updateStates();
    }

    public JLabel createJLabel(String text, Rectangle bounds, Font font, int... visibleStates) {
        JLabel label = new JLabel(text);
        label.setName(text);
        label.setBounds(bounds);
        label.setFont(font);
        frame.add(label);

        states.add(new ComponentState(label, visibleStates));

        return label;
    }

    public JTextField createJTextField(Rectangle bounds, Font font, int... visibleStates) {
        JTextField field = new JTextField();
        field.setName(bounds.toString());
        field.setBounds(bounds);
        field.setFont(font);
        frame.add(field);

        states.add(new ComponentState(field, visibleStates));

        return field;
    }

    public JButton createJButton(String text, Rectangle bounds, ActionListener actionListener, int... visibleStates) {
        JButton button = new JButton(text);
        button.setName(text);
        button.setBounds(bounds);
        button.addActionListener(actionListener);
        frame.add(button);

        states.add(new ComponentState(button, visibleStates));

        return button;
    }
}

record ComponentState(JComponent component, int[] states) {
    @Override
    public String toString() {
        return "ComponentState{" +
                "component=" + component.getName() +
                ", states=" + Arrays.toString(states) +
                '}';
    }
}
