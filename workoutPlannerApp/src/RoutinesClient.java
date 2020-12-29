import javax.swing.*;
import java.awt.*;

public class RoutinesClient {
    private WorkoutPlannerDao workoutPlanner;
    private JList routinesList;

    public RoutinesClient() {
        workoutPlanner = new WorkoutPlannerDaoImpl();
    }

    private void createUIComponents() {
        routinesList = new JList(workoutPlanner.getRoutines().toArray(new Routine[0]));
    }
}
