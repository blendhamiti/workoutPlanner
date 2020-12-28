public class User {

    public static void main(String[] args) {
        RoutineDao routineDao = new RoutineDaoImpl();
        ExerciseDao exerciseDao = new ExerciseDaoImpl();

        for (Routine r : routineDao.getRoutines()) {
            System.out.println(r);
        }
    }
}
