package FrontEnd.UI;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;
import BackEnd.Model.TaskTableModel;
import BackEnd.Util.DatabaseHelper;
import BackEnd.Util.FileManagement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class TaskManagerUI extends JFrame {
    private static TaskDAO taskDAO = new TaskDAO();
    private static DefaultListModel<Task<TaskCategory>> taskListModel;
    private JTable taskTable;
    private static TaskTableModel taskTableModel;

    public TaskManagerUI() throws SQLException {
        taskDAO = new TaskDAO();
        taskListModel = new DefaultListModel<>();
        JList<Task<TaskCategory>> taskList = new JList<>(taskListModel);
        initUI();
        loadTasks();
    }

    private void initUI() throws SQLException {
        setTitle("Task Manager");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        taskTableModel = new TaskTableModel(taskDAO.getAllTasks());
        taskTable = new JTable(taskTableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = getButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        TaskEntryPanel taskEntryPanel = new TaskEntryPanel(taskDAO, taskListModel);
        mainPanel.add(taskEntryPanel, BorderLayout.NORTH);

        add(mainPanel);
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton deleteButton = getDeleteButton();
        JButton completeButton = getCompleteButton();
        JButton revertButton = getRevertButton();
        JButton openSaveDialog = new JButton("Open Save Dialog");
        openSaveDialog.addActionListener(_ -> getSaveDialog());
        JButton editButton = new JButton("Edit Task");
        editButton.addActionListener(_ -> {
            int index = taskTable.getSelectedRow();
            Task<TaskCategory> selectedTask = taskTableModel.getTaskAt(index);
            openTaskPopup(selectedTask);
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(revertButton);
        buttonPanel.add(openSaveDialog);
        return buttonPanel;
    }

    private JButton getDeleteButton() {
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(_ -> {
            int selectedIndex = taskTable.getSelectedRow();
            if (selectedIndex != -1) {
                Task<TaskCategory> selectedTask = taskTableModel.getTaskAt(selectedIndex);
                try {
                    taskDAO.deleteTask(selectedTask);
                    taskTableModel.removeTask(selectedIndex);
                    taskListModel.removeElement(selectedTask);
                    taskTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
                    JOptionPane.showMessageDialog(this, "Task deleted successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to delete task: "
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No task selected",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        return deleteButton;
    }

    private JButton getCompleteButton() {
        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(_ -> {
            int selectedIndex = taskTable.getSelectedRow();
            if (selectedIndex != -1) {
                Task<TaskCategory> selectedTask = taskTableModel.getTaskAt(selectedIndex);
                try {
                    selectedTask.setCompleted(true);
                    taskDAO.updateTask(selectedTask);
                    taskTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
                    JOptionPane.showMessageDialog(this, "Task marked as completed",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to complete task: "
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No task selected",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        return completeButton;
    }

    private JButton getRevertButton() {
        JButton revertButton = new JButton("Revert Completion");
        revertButton.addActionListener(_ -> {
            int selectedIndex = taskTable.getSelectedRow();
            if (selectedIndex != -1) {
                Task<TaskCategory> selectedTask = taskTableModel.getTaskAt(selectedIndex);
                try {
                    selectedTask.setCompleted(false);
                    taskDAO.updateTask(selectedTask);
                    taskTableModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
                    JOptionPane.showMessageDialog(this, "Task reverted",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to revert task: "
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No task selected",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        return revertButton;
    }

    private JButton getSaveToTextButton() {
        JButton saveToTextButton = new JButton("Save To Text");

        saveToTextButton.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save To Text");

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    FileManagement.exportTasksToFile(fileToSave.getAbsolutePath() + ".txt");
                    JOptionPane.showMessageDialog(this, "Tasks exported to text file.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting tasks: "
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return saveToTextButton;
    }

    private JButton getSaveToCSVButton() {
        JButton saveToCSVButton = new JButton("Save To CSV");
        saveToCSVButton.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save To CSV");

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    FileManagement.exportTasksToCSV(fileToSave.getAbsolutePath() + ".csv");
                    JOptionPane.showMessageDialog(this, "Tasks exported to CSV file.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting tasks: "
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return saveToCSVButton;
    }

    private void getSaveDialog() {
        JDialog dialog = new JDialog(this, "Save Dialog", true);
        dialog.setLayout(new GridLayout(2, 1));

        JButton saveToTxt = getSaveToTextButton();
        saveToTxt.addActionListener(_ -> dialog.dispose());
        JButton saveToCSV = getSaveToCSVButton();
        saveToCSV.addActionListener(_ -> dialog.dispose());

        dialog.add(saveToTxt);
        dialog.add(saveToCSV);

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    protected static void loadTasks() {
        try {
            List<Task<TaskCategory>> tasks = taskDAO.getAllTasks();
            System.out.println("Fetched " + tasks.size() + " tasks from the database.");

            taskTableModel.setTasks(tasks);
            System.out.println("Updated taskTableModel.");

            taskListModel.clear();
            tasks.forEach(taskListModel::addElement);
            System.out.println("Updated taskListModel.");

            // Notify the table of the changes (if necessary)
             taskTableModel.fireTableDataChanged();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load tasks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openTaskPopup(Task<TaskCategory> selectedTask) {
        JDialog dialog = new JDialog(this, "Edit Task", true);
        dialog.setLayout(new GridLayout(5, 2));

        JTextField nameField = new JTextField(selectedTask.getName());
        JTextArea descriptionField = new JTextArea(selectedTask.getDescription());
        JComboBox<TaskCategory> categoryComboBox = new JComboBox<>(TaskCategory.values());
        categoryComboBox.setSelectedItem(selectedTask.getCategory());

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(descriptionField));
        dialog.add(new JLabel("Category:"));
        dialog.add(categoryComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            selectedTask.setName(nameField.getText());
            selectedTask.setDescription(descriptionField.getText());
            selectedTask.setCategory((TaskCategory) categoryComboBox.getSelectedItem());

            try {
                taskDAO.updateTask(selectedTask);
                taskTableModel.fireTableRowsUpdated(taskTable.getSelectedRow(), taskTable.getSelectedRow());
                JOptionPane.showMessageDialog(dialog, "Task updated successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to update task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());

        JButton deleteButton = getDeleteButton();
        deleteButton.addActionListener(_ -> dialog.dispose());

        JButton completeButton = getCompleteButton();
        completeButton.addActionListener(_ -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.add(deleteButton);
        dialog.add(completeButton);

        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseHelper.createTableIfNotExist();
            try {
                new TaskManagerUI().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Failed to load tasks: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE );
            }
        });
    }
}
