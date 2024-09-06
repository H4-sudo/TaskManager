package FrontEnd.UI;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;
import BackEnd.Model.TaskTableModel;
import BackEnd.Util.FileManagement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TaskEntryPanel extends JPanel {
    private final TaskDAO taskDAO;
    private final DefaultListModel<Task<TaskCategory>> taskListModel;
    private TaskManagerUI taskManagerUI; // Add this field

    private JTextField taskNameField;
    private JTextArea taskDescriptionField;
    private JComboBox<TaskCategory> taskCategoryComboBox;

    public TaskEntryPanel(TaskDAO taskDAO, DefaultListModel<Task<TaskCategory>> taskListModel) {
        this.taskDAO = taskDAO;
        this.taskListModel = taskListModel;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.insets = new Insets(5, 5, 5, 5);

        gridConstraints.gridx = 0; gridConstraints.gridy = 0;
        add(new JLabel("Task Name:"), gridConstraints);
        gridConstraints.gridx = 1;
        gridConstraints.gridwidth = 2;
        add(taskNameField = new JTextField(15), gridConstraints);

        gridConstraints.gridx = 0; gridConstraints.gridy = 1;
        add(new JLabel("Task Description:"), gridConstraints);
        gridConstraints.gridx = 1;
        gridConstraints.gridwidth = 2;
        taskDescriptionField = new JTextArea(5, 15);
        taskDescriptionField.setLineWrap(true);
        taskDescriptionField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(taskDescriptionField);
        add(scrollPane, gridConstraints);
        gridConstraints.gridwidth = 1;

        gridConstraints.gridx = 0; gridConstraints.gridy = 2;
        add(new JLabel("Task Category:"), gridConstraints);
        gridConstraints.gridx = 1;
        add(taskCategoryComboBox = new JComboBox<>(TaskCategory.values()), gridConstraints);

        gridConstraints.gridx = 1; gridConstraints.gridy = 3;
        gridConstraints.anchor = GridBagConstraints.WEST;
        JButton addButton = getAddButton();
        JButton addFromFileButton = getAddFromFileButton();
        add(addButton, gridConstraints);
        gridConstraints.gridx = 2; gridConstraints.gridy = 3;
        add(addFromFileButton, gridConstraints);
    }

    private JButton getAddButton() {
        JButton addButton = new JButton("Add Task");
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setMnemonic('A');
        addButton.addActionListener(_ -> {
            String taskName = taskNameField.getText();
            String taskDescription = taskDescriptionField.getText();
            boolean isCompleted = false;
            TaskCategory taskCategory = (TaskCategory) taskCategoryComboBox.getSelectedItem();

            Task<TaskCategory> task = new Task<>(taskName, taskDescription, isCompleted, taskCategory);

            try {
                taskDAO.insertTask(task);
                taskListModel.addElement(task);
                TaskTableModel taskTableModel = new TaskTableModel(taskDAO.getAllTasks());
                taskTableModel.addTask(task);
                taskNameField.setText("");
                taskDescriptionField.setText("");
                taskCategoryComboBox.setSelectedIndex(0);
                TaskManagerUI.loadTasks();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TaskEntryPanel.this, "Failed to add this task: " +
                        ex.getMessage());
            }
        });
        return addButton;
    }

    private JButton getAddFromFileButton() {
        JButton addButton = new JButton("Add Tasks From File");
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setMnemonic('F');
        addButton.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    FileManagement.importFromFile(selectedFile.getAbsolutePath());
                } catch (IOException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to add tasks from file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return addButton;
    }
}
