import java.util.Collection;

public interface RoutineDao {

    Collection<Routine> getRoutines();

    Routine getRoutine(String name);

    void addRoutine(Routine routine);

    void updateRoutine(Routine routine);

    void removeRoutine(Routine routine);

    void addRoutineExercise(Routine routine, Exercise exercise);

    void removeRoutineExercise(Routine routine, Exercise exercise);
}
