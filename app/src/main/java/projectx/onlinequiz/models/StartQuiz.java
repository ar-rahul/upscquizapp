package projectx.onlinequiz.models;

public class StartQuiz {

        private String Name;

    public StartQuiz() {
    }

    public StartQuiz(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

