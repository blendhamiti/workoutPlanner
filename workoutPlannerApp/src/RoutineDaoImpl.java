import java.sql.*;
import java.util.Collection;
import java.util.LinkedHashSet;

public class RoutineDaoImpl implements RoutineDao {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/workout_planner";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String SCHEMA = "workout_planner";
    private static final String TABLE_ROUTINES = "routines";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_RELATIONS = "routine_exercises";
    private final Connection connection;

    public RoutineDaoImpl() {
        this.connection = getConnection();
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private int getRoutineId(Routine routine) {
        String STMT = String.format("SELECT * FROM `%s`.`%s` WHERE name='%s';", SCHEMA, TABLE_ROUTINES, routine.getName());
        int id = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(STMT);
            if (rs.next())
                id = rs.getInt("id");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    private Collection<Exercise> getRoutineExercises(int id) {
        String STMT = String.format("SELECT * FROM %s.%s as rel INNER JOIN %s.%s AS exe ON rel.exercise_id=exe.id WHERE routine_id = %d;",
                SCHEMA, TABLE_RELATIONS, SCHEMA, TABLE_EXERCISES, id);
        Collection<Exercise> exercises = new LinkedHashSet<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(STMT);
            while (rs.next()) {
                String exerciseName = rs.getString("name");
                int duration = rs.getInt("duration");
                int repetitions = rs.getInt("repetitions");
                int sets = rs.getInt("sets");
                exercises.add(new Exercise(exerciseName, duration, repetitions, sets));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return exercises;
    }

    @Override
    public Collection<Routine> getRoutines() {
        String STMT = String.format("SELECT * FROM `%s`.`%s`;", SCHEMA, TABLE_ROUTINES);
        Collection<Routine> routines = new LinkedHashSet<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(STMT);
            while (rs.next()) {
                int id = rs.getInt("id");
                String routineName = rs.getString("name");
                routines.add(new Routine(routineName, getRoutineExercises(id)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return routines;
    }

    @Override
    public Routine getRoutine(String name) {
        String STMT = String.format("SELECT * FROM `%s`.`%s` WHERE name='%s';", SCHEMA, TABLE_ROUTINES, name);
        Routine routine = null;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(STMT);
            while (rs.next()) {
                int id = rs.getInt("id");
                String routineName = rs.getString("name");
                routine = new Routine(routineName, getRoutineExercises(id));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return routine;
    }

    @Override
    public void addRoutine(Routine routine) {
        String routineName = routine.getName();
        String STMT = String.format("INSERT INTO `%s`.`%s` (`name`) VALUES ('%s');",
                SCHEMA, TABLE_ROUTINES, routineName);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void updateRoutine(Routine routine) {
        String routineName = routine.getName();
        int id = getRoutineId(routine);
        String STMT = String.format("UPDATE `%s`.`%s` SET `name` = '%s' WHERE (`id` = '%d');",
                SCHEMA, TABLE_ROUTINES, routineName, id);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRoutine(Routine routine) {
        int id = getRoutineId(routine);
        String STMT = String.format("DELETE FROM `%s`.`%s` WHERE (`id` = '%d');", SCHEMA, TABLE_ROUTINES, id);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        STMT = String.format("DELETE FROM `%s`.`%s` WHERE (`routine_id` = '%d');", SCHEMA, TABLE_RELATIONS, id);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeQuery(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void addRoutineExercise(Routine routine, Exercise exercise) {
        int routineId = getRoutineId(routine);

        // check if relation already exists
        Collection<Exercise> exercises = getRoutineExercises(routineId);
        if (exercises.contains(exercise)) return;

        // get exercise id
        ExerciseDao exerciseDao = new ExerciseDaoImpl();
        int exerciseId = exerciseDao.getExerciseId(exercise);

        String STMT = String.format("INSERT INTO `%s`.`%s` (`routine_id`, `exercise_id`) VALUES ('%d', '%d');",
                SCHEMA, TABLE_RELATIONS, routineId, exerciseId);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRoutineExercise(Routine routine, Exercise exercise) {
        int routineId = getRoutineId(routine);

        // check if relation already exists
        Collection<Exercise> exercises = getRoutineExercises(routineId);
        if (exercises.contains(exercise)) return;

        // get exercise id
        ExerciseDao exerciseDao = new ExerciseDaoImpl();
        int exerciseId = exerciseDao.getExerciseId(exercise);

        String STMT = String.format("DELETE FROM `%s`.`%s` WHERE (`routine_id` = '%d' && `exercise_id` = '%d');",
                SCHEMA, TABLE_RELATIONS, routineId, exerciseId);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
