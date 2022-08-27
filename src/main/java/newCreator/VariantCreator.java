package newCreator;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class VariantCreator {
    public static File configFolder = new File("Config");
    public static File outputFolder = new File("Output");
    private static File config = new File(configFolder.getAbsolutePath() + "/Config.txt");
    private static File output = new File(outputFolder.getAbsolutePath() + "/Output.txt");

    private static void createFiles() {
        try {
            System.out.println("Config folder:" + (configFolder.mkdir() ? " created(" + configFolder.getAbsolutePath() + ")" : " exists (" + configFolder.getAbsolutePath() + ")"));
            System.out.println("Config file:  " + (config.createNewFile() ? " created(" + config.getAbsolutePath() + ")" : " exists (" + config.getAbsolutePath() + ")"));
            System.out.println("Output folder:" + (outputFolder.mkdir() ? " created(" + outputFolder.getAbsolutePath() + ")" : " exists (" + outputFolder.getAbsolutePath() + ")"));
            System.out.println("Output file:  " + (output.createNewFile() ? " created(" + output.getAbsolutePath() + ")" : " exists (" + output.getAbsolutePath() + ")"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    public static void clearFile(File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8));
            bw.write("");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendStrToFile(File file, String str) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
            bw.write(str);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        ArrayList<Question> questions;
        ArrayList<Variant> variants;

        int variantCount;
        int variantQuestionCount;
        String questionsText;
        String variantConfig;

        createFiles();

        questionsText = parse(readFile(config.toURI().toURL()), "questions");
        variantConfig = parse(readFile(config.toURI().toURL()), "variants");

        questions = fillQuestions(questionsText);

        variantCount = getConfigValue(variantConfig, "variantcount");
        variantQuestionCount = getConfigValue(variantConfig, "questioncount");

        variants = fillAndCheckVariants(variantCount, variantQuestionCount, questions);

        clearFile(output);
        for (int i = 0; i < variants.size(); i++) {
            appendStrToFile(output, variants.get(i).toString());
            System.out.println(variants.get(i));
        }
    }

    private static long factorial(int n) {
        long res = 1;

        for (int i = 1; i < n + 1; i++) {
            res *= i;
        }
        return res;
    }

    private static long CNK(int n, int k) {
        return factorial(n) / factorial(n - k) / factorial(k);
    }

    private static ArrayList<Variant> fillAndCheckVariants(int variantCount, int variantQuestionCount, ArrayList<Question> questions) {
        ArrayList<Variant> res = new ArrayList<>();

        if (variantCount > CNK(questions.size(), variantQuestionCount)) {
            throw new RuntimeException("Too many variants to generate");
        }

        int variantNumber = 1;

        //цикл, чтобы все варианты отличались хотя-бы на 1 вопрос
        while (res.size() < variantCount) {
            System.out.println("Generating variant №" + variantNumber);
            Variant var = generateVariant(variantQuestionCount, variantNumber, questions);
            if (!res.contains(var)) {
                System.out.println("Variant №" + variantNumber + " generated");
                res.add(var);
                variantNumber++;
            }
        }

        return res;
    }

    //выносит массив значений hashMap в отдельный ArrayList
    private static ArrayList<String> hashMapToArray(HashMap<Integer, String> hashMap) {
        ArrayList<String> res = new ArrayList<>();

        for (int i = 0; i < hashMap.keySet().size(); i++) {
            res.add(hashMap.get(i + 1));
        }

        return res;
    }

    private static Variant generateVariant(int variantQuestionCount, int variantNumber, ArrayList<Question> questions) {
        ArrayList<Question> questionsArrayList = new ArrayList<>();

        if (questions.size() <= variantQuestionCount) {
            throw new RuntimeException("Unable to generate variants: too many questions per variant");
        }

        //разворачивает список, чтобы Collections.shuffle() корректно его обработал
        for (int i = questions.size() - 1; i > -1; i--) {
            questionsArrayList.add(questions.get(i));
        }
        Collections.shuffle(questionsArrayList);

        for (Question question : questionsArrayList) {
            ArrayList<String> answers = hashMapToArray(question.getAnswers());
            Collections.shuffle(answers);
            question.setAnswers(answers);
        }

        return new Variant(variantNumber, questionsArrayList.subList(0, variantQuestionCount));
    }

    //конвертирует строку формата <Номер вопроса> Вопрос?& 1] Ответ 1 2] Ответ 2.... </Номер вопроса> ...
    //в список Questions
    private static ArrayList<Question> fillQuestions(String questionsText) {
        ArrayList<Question> res = new ArrayList<>();
        int questionCount = getQuestionNumber(questionsText);

        String[] questionsRaw = new String[questionCount];

        for (int i = 0; i < questionCount; i++) {
            HashMap<Integer, String> answers = new HashMap<>();

            questionsRaw[i] = parse(questionsText, String.valueOf(i + 1));
            res.add(new Question(questionsRaw[i].substring(0, questionsRaw[i].indexOf("?&") + 1)));

            String answersRaw = questionsRaw[i].substring(questionsRaw[i].indexOf("?&"));
            int answerNumber = countSymbol(answersRaw, ']');

            for (int j = 0; j < answerNumber - 1; j++) {
                answers.put(j + 1, answersRaw.substring(answersRaw.indexOf((j + 1) + "]") + 2, answersRaw.indexOf((j + 2) + "]")).trim().replaceAll("]", ""));
            }

            answers.put(answerNumber, answersRaw.substring(answersRaw.indexOf(answerNumber + "]") + 2).trim().replaceAll("]", ""));
            res.get(i).setAnswers(answers);
        }

        return res;
    }

    private static int countSymbol(String str, char c) {
        int res = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                res++;
            }
        }
        return res;
    }

    /**
     * @param config - строка, в которой содержаться конфиги
     * @param toGet  - конфиг, значение которого нужно получить
     **/
    private static int getConfigValue(String config, String toGet) {
        toGet += '=';

        String[] variantSplit = config.split(System.lineSeparator());
        for (int i = 0; i < variantSplit.length; i++) {
            if (variantSplit[i].contains(toGet)) {
                return Integer.parseInt(variantSplit[i].substring(variantSplit[i].indexOf(toGet) + toGet.length()));
            }
        }
        return -1;
    }

    private static int getQuestionNumber(String questions) {
        return Integer.parseInt(
                questions.substring(questions.lastIndexOf('<') + 2, questions.lastIndexOf('>'))
        );
    }

    private static String readFile(URL file) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader read = new BufferedReader(
                new InputStreamReader(file.openStream(), StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = read.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String trimAll(String str) {
        StringBuilder res = new StringBuilder();
        String[] split = str.split(System.lineSeparator());

        for (String s : split) {
            s = s.trim();
            res.append(s).append(System.lineSeparator());
        }

        return res.toString();
    }

    //получение подстроки между <name> и </name>
    private static String parse(String file, String name) {
        String res = "";
        name = name.trim();

        if (!file.contains("<" + name + ">") ||
                !file.contains("</" + name + ">") ||
                file.indexOf("<" + name + ">") > file.indexOf("</" + name + ">")
        ) {
            throw new RuntimeException("Incorrect <> in parsed file");
        } else {
            res += (file.substring(
                    file.indexOf("<" + name + ">") + ("<" + name + ">").length(),
                    file.indexOf("</" + name + ">"))
            ).trim();
        }
        res = trimAll(res);
        return res;
    }
}

//обёртка для хранения данных
record Variant(int number, List<Question> questions) {
    //кастомный equals, чтобы сравнивать списки по составу элементов(порядок не важен)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variant variant = (Variant) o;
        //страшная штука для сравнения не по порядку
        //https://ru.stackoverflow.com/questions/588363/java-arraylist-Сравнить-содержимое-двух-листов-без-учета-последовательности
        return questions.stream().collect(groupingBy(k -> k, counting()))
                .equals(variant.questions.stream().collect(groupingBy(k -> k, counting())));
    }

    @Override
    public int hashCode() {
        return Objects.hash(questions);
    }

    @Override
    public String toString() {
        return "Variant{" +
                "number=" + number +
                ", questions=" + questions +
                '}';
    }

}

