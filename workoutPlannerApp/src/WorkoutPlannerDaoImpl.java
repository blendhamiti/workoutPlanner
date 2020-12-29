import java.sql.*;
import java.util.*;

public class WorkoutPlannerDaoImpl implements WorkoutPlannerDao {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/workout_planner";
    private static final String USERNAME = "root";
    // private static final String PASSWORD = "root";
    private static final String PASSWORD = "";
    private Connection connection;

    public WorkoutPlannerDaoImpl() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public List<Exercise> getExercises() {
        List<Exercise> exercises = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM exercises");
            ResultSet rs = pstmt.executeQuery();
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
    public Exercise getExercise(String exerciseName) {
        Exercise exercise = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM exercises WHERE name=?");
            pstmt.setString(1, exerciseName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int duration = rs.getInt("duration");
                int repetitions = rs.getInt("repetitions");
                int sets = rs.getInt("sets");
                exercise = new Exercise(exerciseName, duration, repetitions, sets);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return exercise;
    }

    @Override
    public void addExercise(Exercise exercise) {
        String exerciseName = exercise.getName();
        int duration = exercise.getDuration();
        int repetitions = exercise.getRepetitions();
        int sets = exercise.getSets();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO exercises (name, duration, repetitions, sets) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, exerciseName);
            pstmt.setInt(2, duration);
            pstmt.setInt(3, repetitions);
            pstmt.setInt(4, sets);
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void updateExercise(Exercise exercise) {
        String exerciseName = exercise.getName();
        int duration = exercise.getDuration();
        int repetitions = exercise.getRepetitions();
        int sets = exercise.getSets();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE exercises SET duration=?, repetitions=?, sets=? WHERE name=?");
            pstmt.setInt(1, duration);
            pstmt.setInt(2, repetitions);
            pstmt.setInt(3, sets);
            pstmt.setString(4, exerciseName);
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeExercise(Exercise exercise) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM exercises WHERE name=?");
            pstmt.setString(1, exercise.getName());
            pstmt.executeUpdate();
            pstmt = connection.prepareStatement(
                    "DELETE FROM routine_exercises WHERE exercise_id=" +
                            "(SELECT id FROM exercises WHERE name=?)");
            pstmt.setString(1, exercise.getName());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private List<Exercise> getRoutineExercises(String routineName) {
        List<Exercise> exercises = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT e.name, e.duration, e.repetitions, e.sets " +
                            "FROM routine_exercises AS re " +
                            "INNER JOIN exercises e " +
                            "ON re.exercise_id = e.id " +
                            "WHERE routine_id=" +
                            "(SELECT id FROM routines WHERE name=?)");
            pstmt.setString(1, routineName);
            ResultSet rs = pstmt.executeQuery();
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
    public List<Routine> getRoutines() {
        List<Routine> routines = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM routines");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String routineName = rs.getString("name");
                routines.add(new Routine(routineName, getRoutineExercises(routineName)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return routines;
    }

    @Override
    public Routine getRoutine(String routineName) {
        Routine routine = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM routines WHERE name=?");
            pstmt.setString(1, routineName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                routine = new Routine(routineName, getRoutineExercises(routineName));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return routine;
    }

    @Override
    public void addRoutine(Routine routine) {
        String routineName = routine.getName();
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO routines (name) VALUES (?)");
            pstmt.setString(1, routineName);
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRoutine(Routine routine) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM routines WHERE name=?");
            pstmt.setString(1, routine.getName());
            pstmt.executeUpdate();
            pstmt = connection.prepareStatement(
                    "DELETE FROM routine_exercises WHERE routine_id=" +
                            "(SELECT id FROM routines WHERE name=?)");
            pstmt.setString(1, routine.getName());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void addRoutineExercise(Routine routine, Exercise exercise) {
        // check if relation already exists
        if (getRoutineExercises(routine.getName()).contains(exercise)) return;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO routine_exercises (routine_id, exercise_id) " +
                            "VALUES ((SELECT id FROM routines WHERE name=?), (SELECT id FROM exercises WHERE name=?))");
            pstmt.setString(1, routine.getName());
            pstmt.setString(2, exercise.getName());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRoutineExercise(Routine routine, Exercise exercise) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "DELETE FROM routine_exercises " +
                            "WHERE routine_id = (SELECT id FROM routines WHERE name=?) " +
                            "&& exercise_id = (SELECT id FROM exercises WHERE name=?)");
            pstmt.setString(1, routine.getName());
            pstmt.setString(2, exercise.getName());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
