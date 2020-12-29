import java.util.List;

public interface WorkoutPlannerDao {

    List<Exercise> getExercises();

    Exercise getExercise(String name);

    void addExercise(Exercise exercise);

    void updateExercise(Exercise exercise);

    void removeExercise(Exercise exercise);

    List<Routine> getRoutines();

    Routine getRoutine(String name);

    void addRoutine(Routine routine);

    void removeRoutine(Routine routine);

    void addRoutineExercise(Routine routine, Exercise exercise);

    void removeRoutineExercise(Routine routine, Exercise exercise);
}
