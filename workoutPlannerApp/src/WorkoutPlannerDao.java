import java.util.List;

public interface WorkoutPlannerDao {

    List<Exercise> getExercises();

    Exercise getExercise(int id);

    void addExercise(Exercise exercise);

    void updateExercise(Exercise exercise);

    void removeExercise(Exercise exercise);

    List<Routine> getRoutines();

    Routine getRoutine(int id);

    void addRoutine(Routine routine);

    void updateRoutine(Routine routine);

    void removeRoutine(Routine routine);

    void addRoutineExercise(Routine routine, Exercise exercise);

    void removeRoutineExercise(Routine routine, Exercise exercise);
}
