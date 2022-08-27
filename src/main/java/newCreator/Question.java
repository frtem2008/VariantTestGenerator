package newCreator;

import java.util.ArrayList;
import java.util.HashMap;

public class Question {
    private String question;
    private HashMap<Integer, String> answers = null;
    //for question-answer shuffling
    private ArrayList<String> answerList = null;

    public Question(String question) {
        this.question = question;
    }

    @Override
    public String toString() {
        if (answerList == null) {
            return "Question{" +
                    "question='" + question + '\'' +
                    ", answers=" + answers +
                    '}';
        }
        return "Question{" +
                "question='" + question + '\'' +
                ", answerList=" + answerList +
                '}';

    }

    public String getQuestion() {
        return question;
    }

    public HashMap<Integer, String> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<Integer, String> answers) {
        this.answers = answers;
    }

    public void setAnswers(ArrayList<String> answerList) {
        this.answerList = answerList;
    }
}