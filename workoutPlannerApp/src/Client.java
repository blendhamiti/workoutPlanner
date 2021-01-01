import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Client {
    WorkoutPlannerDao workoutPlannerDao;
    List<Routine> routines;
    List<Exercise> exercises;
    Routine selectedRoutine;
    int selectedRoutineIndex;
    List<Exercise> routineExercises;

    private JPanel rootPanel;
    private JList<String> routinesList;
    private JButton createNewRoutineButton;
    private JButton removeSelectedRoutineButton;
    private JLabel selectedRoutineLabel;
    private JTable routineExercisesTable;
    private JButton addExerciseToRoutineButton;
    private JButton deleteExerciseFromRoutineButton;
    private JTable exercisesTable;
    private JButton createNewExerciseButton;
    private JButton removeSelectedExerciseButton;

    public Client() {
        workoutPlannerDao = new WorkoutPlannerDaoImpl();
        routines = workoutPlannerDao.getRoutines();
        exercises = workoutPlannerDao.getExercises();
        routineExercises = new ArrayList<>();
        $$$setupUI$$$();

        // disable buttons that require selection
        removeSelectedRoutineButton.setEnabled(false);
        removeSelectedExerciseButton.setEnabled(false);
        addExerciseToRoutineButton.setEnabled(false);
        deleteExerciseFromRoutineButton.setEnabled(false);
        final boolean[] addExerciseToRoutineButtonState = {false};

        // get models
        DefaultListModel<String> routinesListModel = (DefaultListModel<String>) routinesList.getModel();
        AbstractTableModel exercisesTableModel = (AbstractTableModel) exercisesTable.getModel();
        AbstractTableModel routineExercisesTableModel = (AbstractTableModel) routineExercisesTable.getModel();

        // selection listeners
        routinesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routinesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int selectedIndex = routinesList.getSelectedIndex();
                selectedRoutineIndex = selectedIndex;
                if (selectedIndex < 0) {
                    selectedRoutine = null;
                    routineExercises = new ArrayList<>();
                    selectedRoutineLabel.setText("Selected routine: ");
                    removeSelectedRoutineButton.setEnabled(false);
                    addExerciseToRoutineButton.setEnabled(false);
                } else {
                    selectedRoutine = routines.get(selectedIndex);
                    routineExercises = selectedRoutine.getExercises();
                    selectedRoutineLabel.setText("Selected routine: " + selectedRoutine.getName());
                    TableModel tableModel = new AbstractTableModel() {
                        final String[] columnsNames = new String[]{"Name", "Duration", "Repetitions", "Sets"};

                        @Override
                        public String getColumnName(int column) {
                            return columnsNames[column];
                        }

                        @Override
                        public int getRowCount() {
                            return routineExercises.size();
                        }

                        @Override
                        public int getColumnCount() {
                            return columnsNames.length;
                        }

                        @Override
                        public Object getValueAt(int rowIndex, int columnIndex) {
                            switch (columnIndex) {
                                case 0:
                                    return routineExercises.get(rowIndex).getName();
                                case 1:
                                    return routineExercises.get(rowIndex).getDuration();
                                case 2:
                                    return routineExercises.get(rowIndex).getRepetitions();
                                case 3:
                                    return routineExercises.get(rowIndex).getSets();
                                default:
                                    return null;
                            }
                        }

                        @Override
                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return false;
                        }
                    };
                    routineExercisesTable.setModel(tableModel);
                    removeSelectedRoutineButton.setEnabled(true);
                    if (addExerciseToRoutineButtonState[0])
                        addExerciseToRoutineButton.setEnabled(true);
                }
            }
        });
        exercisesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        exercisesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int selectedRow = exercisesTable.getSelectedRow();
                if (selectedRow < 0) {
                    removeSelectedExerciseButton.setEnabled(false);
                    addExerciseToRoutineButton.setEnabled(false);
                } else {
                    removeSelectedExerciseButton.setEnabled(true);
                    if (selectedRoutine != null)
                        addExerciseToRoutineButton.setEnabled(true);
                    else
                        addExerciseToRoutineButtonState[0] = true;
                }
            }
        });
        routineExercisesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routineExercisesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int selectedRow = routineExercisesTable.getSelectedRow();
                deleteExerciseFromRoutineButton.setEnabled(selectedRow >= 0);
            }
        });

        // button action listeners
        createNewRoutineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String routineName = JOptionPane.showInputDialog(
                        null,
                        "Please type a name for the routine.",
                        "Create new routine",
                        JOptionPane.PLAIN_MESSAGE);
                if (routineName == null) return;
                Routine routine = new Routine(routineName);
                routines.add(routine);
                workoutPlannerDao.addRoutine(routine);
                routinesListModel.add(routines.size() - 1, routine.getName());
            }
        });
        removeSelectedRoutineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = routinesList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Routine routine = routines.get(selectedIndex);
                    for (Exercise exercise : routine.getExercises())
                        workoutPlannerDao.removeRoutineExercise(routine, exercise);
                    routine.getExercises().clear();
                    routines.remove(routine);
                    workoutPlannerDao.removeRoutine(routine);
                    routinesListModel.remove(selectedIndex);
                    routineExercisesTable.invalidate();
                }
            }
        });
        createNewExerciseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Exercise exercise = new Exercise("Name", 0, 0, 0);
                exercises.add(exercise);
                workoutPlannerDao.addExercise(exercise);
                exercisesTableModel.fireTableRowsInserted(exercises.size() - 1, exercises.size() - 1);
            }
        });
        removeSelectedExerciseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = exercisesTable.getSelectedRow();
                if (selectedRowIndex >= 0) {
                    Exercise exercise = exercises.get(selectedRowIndex);
                    exercises.remove(exercise);
                    for (Routine routine : routines) {
                        routine.getExercises().remove(exercise);
                        workoutPlannerDao.removeRoutineExercise(routine, exercise);
                    }
                    workoutPlannerDao.removeExercise(exercise);
                    exercisesTableModel.fireTableRowsDeleted(exercises.size() - 1, exercises.size() - 1);
                    routinesList.getListSelectionListeners()[0].valueChanged(
                            new ListSelectionEvent(
                                    new Object(),
                                    selectedRoutineIndex,
                                    selectedRoutineIndex,
                                    false));
                }
            }
        });
        addExerciseToRoutineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = exercisesTable.getSelectedRow();
                if (selectedRowIndex >= 0) {
                    Exercise exercise = exercises.get(selectedRowIndex);
                    if (!routineExercises.contains(exercise)) {
                        routineExercises.add(exercise);
                        workoutPlannerDao.addRoutineExercise(selectedRoutine, exercise);
                        routineExercisesTable.invalidate();
                        routinesList.getListSelectionListeners()[0].valueChanged(
                                new ListSelectionEvent(
                                        new Object(),
                                        selectedRoutineIndex,
                                        selectedRoutineIndex,
                                        false));
                    }
                }
            }
        });
        deleteExerciseFromRoutineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = routineExercisesTable.getSelectedRow();
                if (selectedRowIndex >= 0) {
                    Exercise exercise = routineExercises.get(selectedRowIndex);
                    routineExercises.remove(exercise);
                    workoutPlannerDao.removeRoutineExercise(selectedRoutine, exercise);
                    routineExercisesTableModel.fireTableRowsDeleted(routineExercises.size() - 1, routineExercises.size() - 1);
                    routinesList.getListSelectionListeners()[0].valueChanged(
                            new ListSelectionEvent(
                                    new Object(),
                                    selectedRoutineIndex,
                                    selectedRoutineIndex,
                                    false));
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Workout Planner");
        frame.setContentPane(new Client().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // create routine list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Routine routine : routines) {
            listModel.addElement(routine.getName());
        }
        routinesList = new JList<>(listModel);

        // create routine exercises table
        AbstractTableModel routineExercisesTableModel = new AbstractTableModel() {
            final String[] columnsNames = new String[]{"Name", "Duration", "Repetitions", "Sets"};
            final Class[] columnsClasses = new Class[]{String.class, Integer.class, Integer.class, Integer.class};

            @Override
            public String getColumnName(int column) {
                return columnsNames[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnsClasses[columnIndex];
            }

            @Override
            public int getRowCount() {
                return routineExercises.size();
            }

            @Override
            public int getColumnCount() {
                return columnsNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return routineExercises.get(rowIndex).getName();
                    case 1:
                        return routineExercises.get(rowIndex).getDuration();
                    case 2:
                        return routineExercises.get(rowIndex).getRepetitions();
                    case 3:
                        return routineExercises.get(rowIndex).getSets();
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        routineExercises.get(rowIndex).setName((String) aValue);
                        break;
                    case 1:
                        routineExercises.get(rowIndex).setDuration((Integer) aValue);
                        break;
                    case 2:
                        routineExercises.get(rowIndex).setRepetitions((Integer) aValue);
                        break;
                    case 3:
                        routineExercises.get(rowIndex).setSets((Integer) aValue);
                        break;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        routineExercisesTable = new JTable(routineExercisesTableModel);

        // create all exercises table
        AbstractTableModel exercisesTableModel = new AbstractTableModel() {
            final String[] columnsNames = new String[]{"Name", "Duration", "Repetitions", "Sets"};
            final Class[] columnsClasses = new Class[]{String.class, Integer.class, Integer.class, Integer.class};

            @Override
            public String getColumnName(int column) {
                return columnsNames[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnsClasses[columnIndex];
            }

            @Override
            public int getRowCount() {
                return exercises.size();
            }

            @Override
            public int getColumnCount() {
                return columnsNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return exercises.get(rowIndex).getName();
                    case 1:
                        return exercises.get(rowIndex).getDuration();
                    case 2:
                        return exercises.get(rowIndex).getRepetitions();
                    case 3:
                        return exercises.get(rowIndex).getSets();
                    default:
                        return null;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        exercises.get(rowIndex).setName((String) aValue);
                        break;
                    case 1:
                        exercises.get(rowIndex).setDuration((Integer) aValue);
                        break;
                    case 2:
                        exercises.get(rowIndex).setRepetitions((Integer) aValue);
                        break;
                    case 3:
                        exercises.get(rowIndex).setSets((Integer) aValue);
                        break;
                }
                workoutPlannerDao.updateExercise(exercises.get(rowIndex));
            }
        };
        exercisesTable = new JTable(exercisesTableModel);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), 1, 1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(2, 2, 2, 2), 1, 1));
        rootPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Exercises", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, panel1.getFont()), new Color(-16777216)));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(2, 2, 2, 2), 1, 1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane1.setViewportView(routineExercisesTable);
        selectedRoutineLabel = new JLabel();
        selectedRoutineLabel.setHorizontalAlignment(11);
        selectedRoutineLabel.setHorizontalTextPosition(11);
        selectedRoutineLabel.setText("Selected routine: ");
        panel2.add(selectedRoutineLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 1, new Insets(2, 2, 2, 2), 1, 1));
        panel1.add(panel3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane2.setViewportView(exercisesTable);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        createNewExerciseButton = new JButton();
        createNewExerciseButton.setText("Create new exercise");
        panel4.add(createNewExerciseButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeSelectedExerciseButton = new JButton();
        removeSelectedExerciseButton.setText("Remove selected exercise");
        panel4.add(removeSelectedExerciseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("All exercises");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteExerciseFromRoutineButton = new JButton();
        deleteExerciseFromRoutineButton.setText(">");
        panel5.add(deleteExerciseFromRoutineButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addExerciseToRoutineButton = new JButton();
        addExerciseToRoutineButton.setText("<");
        panel5.add(addExerciseToRoutineButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel5.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel5.add(spacer4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(2, 2, 2, 2), 1, 1));
        rootPanel.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Routines", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$(null, -1, -1, panel6.getFont()), new Color(-16777216)));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel7.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(200, 100), null, null, 0, false));
        scrollPane3.setViewportView(routinesList);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 1, 1));
        panel6.add(panel8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        removeSelectedRoutineButton = new JButton();
        removeSelectedRoutineButton.setText("Remove selected routine");
        panel8.add(removeSelectedRoutineButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createNewRoutineButton = new JButton();
        createNewRoutineButton.setText("Create new routine");
        panel8.add(createNewRoutineButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel8.add(spacer5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel6.add(spacer6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
