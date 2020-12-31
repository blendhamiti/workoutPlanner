import java.util.Objects;

public class Exercise {
    private int id;
    private String name;
    private int duration;
    private int repetitions;
    private int sets;

    public Exercise(String name, int duration, int repetitions, int sets) {
        this.name = name;
        this.duration = duration;
        this.repetitions = repetitions;
        this.sets = sets;
    }

    public Exercise(int id, String name, int duration, int repetitions, int sets) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.repetitions = repetitions;
        this.sets = sets;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id == exercise.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
