package newCreator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class VariantGui {
    private static final HashMap<Integer, Question> questions = new HashMap<>();

    private static final ArrayList<JTextField> answerFields = new ArrayList<>();
    private static final ArrayList<JLabel> answerNumbers = new ArrayList<>();

    private static final JFrame frame = new JFrame("Variant builder");

    private static GuiManager gm;
    private static int currentQuestion, answerCount;

    private static void configureFrame() {
        Mouse m = new Mouse();
        frame.addMouseListener(m);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
    }

    //Test function for gui
    private static void fillTestQuestions() {
        questions.put(1, null);
        questions.put(2, new Question("test"));
        questions.put(3, new Question("Field test"));
        HashMap<Integer, String> a = new HashMap<>();
        a.put(0, "1");
        a.put(1, "2");
        a.put(2, "3");
        a.put(3, "4");
        a.put(4, "5");
        a.put(5, "6");
        a.put(6, "7");
        a.put(7, "8");
        a.put(8, "9");
        questions.get(3).setAnswers(a);
    }

    public static void main(String[] args) {
        configureFrame();
        fillTestQuestions();

        gm = new GuiManager(frame, """
                StateInfo:
                1) Кнопка "Начать"
                2) От 2 до 4 вопросов и кнопка "+"
                3) 5 вопросов и кнопки "+" нет
                4) от 6 до 9 вопросов на 2 странице, кнопка "+" есть
                5) 10 вопросов на 2 странице, кнопки "+" нет
                """
        );

        currentQuestion = 1;
        answerCount = 0;
        addAnswer();
        addAnswer();

        JButton beginButton = gm.createJButton("Начать",
                new Rectangle(200, 225, 200, 100),
                e -> gm.setState(2),
                1
        );

        JLabel enterQuestions = gm.createJLabel("Введите вопрос: ",
                new Rectangle(85, 30, 3000, 100),
                new Font("Calibri", Font.BOLD, 64),
                2, 3, 4, 5
        );

        JLabel questionNumberLabel = gm.createJLabel("1)",
                new Rectangle(30, 115, 100, 100),
                new Font("Calibri", Font.BOLD, 48),
                2, 3, 4, 5
        );

        JTextField questionField = gm.createJTextField(
                new Rectangle(110, 135, 400, 50),
                new Font("Times New Roman", Font.BOLD, 20),
                2, 3, 4, 5
        );

        JButton addAnswer = gm.createJButton("+",
                new Rectangle(55, 325, 50, 50),
                null,
                2, 4
        );


        JButton changePage = gm.createJButton(">",
                new Rectangle(520, 240, 45, 120),
                null,
                3, 4, 5
        );

        JLabel helpText = gm.createJLabel("Введите предидущий ответ, чтобы добавить новый",
                new Rectangle(70, 100, 600, 50),
                new Font("Calibri", Font.ITALIC, 20)
        );

        changePage.addActionListener(e -> {
            if (gm.getState() == 3) {
                if (answerCount != 10)
                    gm.setState(4);
                else
                    gm.setState(5);

                if (answerCount == 5)
                    addAnswer();

                addAnswer.setBounds(55, 205 + 60 * ((answerCount % 5)), 60, 60);

                changePage.setBounds(5, 240, 45, 120);
                changePage.setText("<");
            } else if (gm.getState() == 4 || gm.getState() == 5) {
                gm.setState(3);
                changePage.setBounds(520, 240, 45, 120);
                changePage.setText(">");
            }
        });

        addAnswer.addActionListener(e -> {
            System.out.println("_________________________");
            for (int i = 0; i < answerFields.size(); i++) {
                System.out.println("Answer " + i + ")" + answerFields.get(i).getText());
            }

            if (!answerFields.get(answerCount - 1).getText().trim().equals("")) {
                helpText.setVisible(false);
                addAnswer.setBounds(55, 325 + 60 * ((answerCount % 5) - 1), 60, 60);
                addAnswer();

                if (answerCount % 5 == 0) {
                    if (gm.getState() == 2)
                        gm.setState(3);
                    else if (gm.getState() == 4)
                        gm.setState(5);
                }
            } else {
                helpText.setVisible(true);
            }
        });

        JButton nextQuestion = gm.createJButton("Следующий вопрос",
                new Rectangle(340, 520, 160, 50),

                e -> {
                    if (!questionField.getText().isEmpty() &&
                            !answerFields.get(0).getText().isEmpty() &&
                            !answerFields.get(1).getText().isEmpty()
                    ) {
                        saveQuestion(currentQuestion, questionField);
                        currentQuestion++;

                        if (questions.size() > currentQuestion - 1) {
                            loadQuestion(currentQuestion, questionNumberLabel, questionField, addAnswer, changePage);
                        } else {
                            clearTextFields(questionField);
                            addAnswer.setBounds(55, 325, 60, 60);
                            answerCount = 2;

                            questionNumberLabel.setText(currentQuestion + ")");
                            changePage.setBounds(520, 240, 45, 120);
                            changePage.setText(">");

                            gm.setState(2);
                        }
                    }
                },
                2, 3, 4, 5
        );

        JButton prevQuestion = gm.createJButton("Предыдущий вопрос",
                new Rectangle(70, 520, 160, 50),
                e -> {
                    if (currentQuestion > 1) {
                        if (!questionField.getText().isEmpty() &&
                                !answerFields.get(0).getText().isEmpty() &&
                                !answerFields.get(1).getText().isEmpty()
                        ) {
                            saveQuestion(currentQuestion, questionField);
                        }
                        System.out.println(questions);
                        currentQuestion--;

                        loadQuestion(currentQuestion, questionNumberLabel, questionField, addAnswer, changePage);
                    }
                },
                2, 3, 4, 5
        );


        frame.pack();
        frame.setBounds(400, 100, 600, 650);
        frame.setVisible(true);

        gm.setState(2);
    }

    private static void clearTextFields(JTextField questionField) {
        questionField.setText("");

        for (int i = 0; i < answerFields.size(); i++) {
            gm.removeFromStates(answerNumbers.get(i), new int[]{2, 3, 4, 5});
            gm.removeFromStates(answerFields.get(i), new int[]{2, 3, 4, 5});
        }

        answerNumbers.clear();
        answerFields.clear();
        answerCount = 0;
        addAnswer();
        addAnswer();
    }

    private static void saveQuestion(int number, JTextField questionField) {
        Question question = new Question(questionField.getText());

        HashMap<Integer, String> answers = new HashMap<>();
        for (int i = 0; i < answerFields.size(); i++) {
            if (!answerFields.get(i).getText().trim().equals(""))
                answers.put(i, answerFields.get(i).getText());
        }

        question.setAnswers(answers);
        questions.put(number, question);
    }

    private static void loadQuestion(int number, JLabel questionNumberLabel, JTextField questionField, JButton addAnswer, JButton changePage) {
        clearTextFields(questionField);

        Question question = questions.get(number);
        questionField.setText(question.getQuestion());
        questionNumberLabel.setText(number + ")");

        if (question.getAnswers() != null) {
            setAnswers(question, addAnswer, changePage);
        }

        if (answerCount < 5)
            gm.setState(2);
        else if (answerCount == 5)
            gm.setState(3);
        else if (answerCount < 10)
            gm.setState(4);
        else if (answerCount == 10)
            gm.setState(5);
    }

    private static void setAnswers(Question question, JButton addAnswer, JButton changePage) {
        while (answerFields.size() < question.getAnswers().size()) {
            addAnswer();
        }

        for (int i = 0; i < question.getAnswers().size(); i++) {
            answerFields.get(i).setText(question.getAnswers().get(i));
        }

        System.out.println("Answer count: " + answerCount);

        if (answerCount % 5 == 0) {
            if (gm.getState() == 2)
                gm.setState(3);
            else if (gm.getState() == 4)
                gm.setState(5);
        }

        addAnswer.setBounds(55, 205 + 60 * ((answerCount % 5)), 60, 60);
        if (gm.getState() == 3 || gm.getState() == 2) {
            addAnswer.setBounds(55, 205 + 60 * ((answerCount % 5)), 60, 60);
            changePage.setBounds(5, 240, 45, 120);
            changePage.setText("<");
        } else if (gm.getState() == 4 || gm.getState() == 5) {
            changePage.setBounds(520, 240, 45, 120);
            changePage.setText(">");
        }
    }

    private static void addAnswer() {
        int index = answerCount++;
        answerNumbers.add(addAnswerNumber(index));
        answerFields.add(addAnswerText(index));
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
                new Font("Times New Roman", Font.BOLD, 20), index < 5 ? 2 : 5, index < 5 ? 3 : 4
        );
    }
}
