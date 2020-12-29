public class Tester {

    public static void main(String[] args) {
        WorkoutPlannerDao workoutPlanner = new WorkoutPlannerDaoImpl();
        for (Routine r : workoutPlanner.getRoutines()) {
            System.out.println(r);
        }
        Exercise exercise = workoutPlanner.getExercise("lunges");
        Routine routine = workoutPlanner.getRoutine("late night");
        workoutPlanner.removeRoutineExercise(routine, exercise);
        for (Routine r : workoutPlanner.getRoutines()) {
            System.out.println(r);
        }
    }
}
