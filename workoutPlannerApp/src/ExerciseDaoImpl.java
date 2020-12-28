import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class ExerciseDaoImpl implements ExerciseDao {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/workout_planner";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String SCHEMA = "workout_planner";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_RELATIONS = "routine_exercises";
    private final Connection connection;

    public ExerciseDaoImpl() {
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

    @Override
    public Collection<Exercise> getExercises() {
        String STMT = String.format("SELECT * FROM `%s`.`%s`;", SCHEMA, TABLE_EXERCISES);
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
    public Exercise getExercise(String name) {
        String STMT = String.format("SELECT * FROM `%s`.`%s` WHERE name='%s';", SCHEMA, TABLE_EXERCISES, name);
        Exercise exercise = null;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(STMT);
            if (rs.next()) {
                String exerciseName = rs.getString("name");
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
    public int getExerciseId(Exercise exercise) {
        String STMT = String.format("SELECT * FROM `%s`.`%s` WHERE name='%s';", SCHEMA, TABLE_EXERCISES, exercise.getName());
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

    @Override
    public void addExercise(Exercise exercise) {
        String exerciseName = exercise.getName();
        int duration = exercise.getDuration();
        int repetitions = exercise.getRepetitions();
        int sets = exercise.getSets();
        String STMT = String.format("INSERT INTO `%s`.`%s` (`name`, `duration`, `repetitions`, `sets`) VALUES ('%s', '%d', '%d', '%d');",
                SCHEMA, TABLE_EXERCISES, exerciseName, duration, repetitions, sets);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
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
        int id = getExerciseId(exercise);
        String STMT = String.format("UPDATE `%s`.`%s` SET `name` = '%s', `duration` = '%d', `duration` = '%d', `sets` = '%d' WHERE (`id` = '%d');",
                SCHEMA, TABLE_EXERCISES, exerciseName, duration, repetitions, sets, id);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void removeExercise(Exercise exercise) {
        int id = getExerciseId(exercise);
        String STMT = String.format("DELETE FROM `%s`.`%s` WHERE (`id` = '%d');", SCHEMA, TABLE_EXERCISES, id);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        STMT = String.format("DELETE FROM `%s`.`%s` WHERE (`exercise_id` = '%d');", SCHEMA, TABLE_RELATIONS, id);
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(STMT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
