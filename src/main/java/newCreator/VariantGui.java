package newCreator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VariantGui {
    private static JFrame frame = new JFrame("Variant builder");

    private static GuiManager gm;
    private static ArrayList<Question> questions = new ArrayList<>();
    private static int currentQuestion, answerNumber;


    private static void configureFrame() {
        Mouse m = new Mouse();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addMouseListener(m);
        frame.setResizable(false);
        frame.setLayout(null);
    }

    public static void main(String[] args) {
        configureFrame();

        gm = new GuiManager(frame);

        currentQuestion = 1;
        answerNumber = 0;
        addAnswer();
        addAnswer();

        JLabel enterQuestions = gm.createJLabel("Введите вопрос: ",
                new Rectangle(85, 30, 3000, 100),
                new Font("Calibri", Font.BOLD, 64),
                2, 3, 4, 5
        );

        JLabel questionLabel = gm.createJLabel("1)",
                new Rectangle(30, 115, 100, 100),
                new Font("Calibri", Font.BOLD, 48),
                2, 3, 4, 5
        );

        JTextField questionField = gm.createJTextField(
                new Rectangle(110, 135, 400, 50),
                new Font("Times New Roman", Font.BOLD, 20),
                2, 3, 4, 5
        );

        JButton beginButton = gm.createJButton("Начать",
                new Rectangle(200, 225, 200, 100),
                e -> {
                    gm.setState(2);
                },
                1
        );

        JButton addAnswer = gm.createJButton("+",
                new Rectangle(55, 325, 50, 50),
                null,
                2, 4
        );

        JButton nextState = gm.createJButton(">",
                new Rectangle(520, 240, 45, 120),
                null,
                3, 4, 5
        );

        nextState.addActionListener(e -> {
            if (gm.getState() == 3) {
                if (answerNumber != 10)
                    gm.setState(4);
                else
                    gm.setState(5);

                if (answerNumber == 5)
                    addAnswer();

                addAnswer.setBounds(55, 205 + 60 * ((answerNumber % 5)), 60, 60);


                nextState.setBounds(5, 240, 45, 120);
                nextState.setText("<");
            } else if (gm.getState() == 4 || gm.getState() == 5) {
                gm.setState(3);
                nextState.setBounds(520, 240, 45, 120);
                nextState.setText(">");
            }
        });

        addAnswer.addActionListener(e -> {
            addAnswer.setBounds(55, 325 + 60 * ((answerNumber % 5) - 1), 60, 60);
            addAnswer();

            if (answerNumber % 5 == 0) {
                if (gm.getState() == 2)
                    gm.setState(3);
                else if (gm.getState() == 4)
                    gm.setState(5);
            }
        });


        frame.pack();
        frame.setBounds(400, 100, 600, 650);
        frame.setVisible(true);

        gm.setState(1);
    }

    private static void addAnswer() {
        int index = answerNumber++;
        addAnswerNumber(index);
        addAnswerText(index);
    }

    private static JLabel addAnswerNumber(int index) {
        return gm.createJLabel((index < 9 ? " " : "") + (index + 1) + ".",
                new Rectangle(55, 90 + 60 * (index % 5), 100, 300),
                new Font("Calibri", Font.BOLD, 42), index < 5 ? 2 : 5, index < 5 ? 3 : 4
        );
    }

    private static JTextField addAnswerText(int index) {
        return gm.createJTextField(
                new Rectangle(110, 210 + 60 * (index % 5), 400, 50),
                new Font("Times New Roman", Font.BOLD, 20), index < 5 ? 2 : 5, index < 5 ? 3 : 4, 5
        );
    }
}
