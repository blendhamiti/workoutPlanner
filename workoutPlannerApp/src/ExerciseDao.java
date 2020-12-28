import java.util.Collection;

public interface ExerciseDao {

    Collection<Exercise> getExercises();

    Exercise getExercise(String name);

    int getExerciseId(Exercise exercise);

    void addExercise(Exercise exercise);

    void updateExercise(Exercise exercise);

    void removeExercise(Exercise exercise);
}
