import java.sql.*;
import java.util.*;

public class WorkoutPlannerDaoImpl implements WorkoutPlannerDao {
    /*
        Please adjust the fields below according to your configuration before starting the program.
     */
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/workout_planner";
    private static final String USERNAME = "root";
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
                int id = rs.getInt("id");
                exercises.add(getExercise(id));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return exercises;
    }

    @Override
    public Exercise getExercise(int id) {
        Exercise exercise = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM exercises WHERE id=?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                int repetitions = rs.getInt("repetitions");
                int sets = rs.getInt("sets");
                exercise = new Exercise(id, name, duration, repetitions, sets);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return exercise;
    }

    @Override
    public void addExercise(Exercise exercise) {
        String name = exercise.getName();
        int duration = exercise.getDuration();
        int repetitions = exercise.getRepetitions();
        int sets = exercise.getSets();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO exercises (name, duration, repetitions, sets) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setInt(2, duration);
            pstmt.setInt(3, repetitions);
            pstmt.setInt(4, sets);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            while (rs.next())
                exercise.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void updateExercise(Exercise exercise) {
        int id = exercise.getId();
        String name = exercise.getName();
        int duration = exercise.getDuration();
        int repetitions = exercise.getRepetitions();
        int sets = exercise.getSets();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE exercises SET name=?, duration=?, repetitions=?, sets=? WHERE id=?");
            pstmt.setString(1, name);
            pstmt.setInt(2, duration);
            pstmt.setInt(3, repetitions);
            pstmt.setInt(4, sets);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeExercise(Exercise exercise) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM exercises WHERE id=?");
            pstmt.setInt(1, exercise.getId());
            pstmt.executeUpdate();
            pstmt = connection.prepareStatement(
                    "DELETE FROM routine_exercises WHERE exercise_id=?");
            pstmt.setInt(1, exercise.getId());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private List<Exercise> getRoutineExercises(int routineId) {
        List<Exercise> exercises = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT e.id, e.name, e.duration, e.repetitions, e.sets " +
                            "FROM routine_exercises AS re " +
                            "INNER JOIN exercises e " +
                            "ON re.exercise_id = e.id " +
                            "WHERE routine_id=?");
            pstmt.setInt(1, routineId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int exerciseId = rs.getInt("id");
                exercises.add(getExercise(exerciseId));
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
                int id = rs.getInt("id");
                routines.add(getRoutine(id));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return routines;
    }

    @Override
    public Routine getRoutine(int id) {
        Routine routine = null;
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM routines WHERE id=?");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                routine = new Routine(id, name, getRoutineExercises(id));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return routine;
    }

    @Override
    public void addRoutine(Routine routine) {
        String routineName = routine.getName();
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO routines (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, routineName);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            while (rs.next())
                routine.setId(rs.getInt(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void updateRoutine(Routine routine) {
        int id = routine.getId();
        String name = routine.getName();
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE routines SET name=? WHERE id=?");
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRoutine(Routine routine) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM routines WHERE id=?");
            pstmt.setInt(1, routine.getId());
            pstmt.executeUpdate();
            pstmt = connection.prepareStatement("DELETE FROM routine_exercises WHERE routine_id=?");
            pstmt.setInt(1, routine.getId());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void addRoutineExercise(Routine routine, Exercise exercise) {
        // check if relation already exists
        if (getRoutineExercises(routine.getId()).contains(exercise)) return;
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO routine_exercises (routine_id, exercise_id) VALUES (?, ?)");
            pstmt.setInt(1, routine.getId());
            pstmt.setInt(2, exercise.getId());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeRoutineExercise(Routine routine, Exercise exercise) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "DELETE FROM routine_exercises WHERE routine_id=? && exercise_id=?");
            pstmt.setInt(1, routine.getId());
            pstmt.setInt(2, exercise.getId());
            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
