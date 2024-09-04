package FrontEnd.UI;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;
import BackEnd.Util.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class TaskManagerUI extends JFrame {
    private final TaskDAO taskDAO;
    private final JList<Task<TaskCategory>> taskList;
    private final DefaultListModel<Task<TaskCategory>> taskListModel;

    public TaskManagerUI() {
        taskDAO = new TaskDAO();
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        initUI();
        loadTasks();
    }

    private void initUI() {
        setTitle("Task Manager");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(taskList, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(taskList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        taskList.setCellRenderer((list, value, index, isSelected, hasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(value.getName());
            JLabel categoryLabel = new JLabel(value.getCategory().toString());
            if (value.isCompleted()) {
                nameLabel.setForeground(Color.GREEN);
                nameLabel.setBackground(Color.DARK_GRAY);
                nameLabel.setText("<html><strike>" + nameLabel.getText() + "</strike></html>");
            }
            panel.add(nameLabel, BorderLayout.WEST);
            panel.add(categoryLabel, BorderLayout.EAST);
            panel.setBackground(isSelected ? Color.GRAY : Color.WHITE);
            return panel;
        });

        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = taskList.locationToIndex(e.getPoint());
                    Task<TaskCategory> selectedTask = taskListModel.getElementAt(index);
                    openTaskPopup(selectedTask);
                }
            }
        });

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

        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(revertButton);
        return buttonPanel;
    }

    private JButton getDeleteButton() {
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(_ -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task<TaskCategory> selectedTask = taskListModel.getElementAt(selectedIndex);
                try {
                    taskDAO.deleteTask(selectedTask);
                    taskListModel.removeElementAt(selectedIndex);
                    taskList.repaint();
                    JOptionPane.showMessageDialog(this, "Task deleted successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to delete task: " +
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return deleteButton;
    }

    private JButton getCompleteButton() {
        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(_ -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task<TaskCategory> selectedTask = taskListModel.getElementAt(selectedIndex);
                try {
                    selectedTask.setCompleted(true);
                    taskDAO.updateTask(selectedTask);
                    taskList.repaint();
                    JOptionPane.showMessageDialog(this, "Task marked as completed: ",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to complete task: " +
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return completeButton;
    }

    private JButton getRevertButton() {
        JButton changeCompletionButton = new JButton("Revert Completion");
        changeCompletionButton.addActionListener(_ -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task<TaskCategory> selectedTask = taskListModel.getElementAt(selectedIndex);
                try {
                    selectedTask.setCompleted(false);
                    taskDAO.updateTask(selectedTask);
                    taskList.repaint();
                    JOptionPane.showMessageDialog(this, "Task reverted: ",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to revert task: " +
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return changeCompletionButton;
    }

    private void loadTasks() {
        try {
            List<Task<TaskCategory>> tasks = taskDAO.getAllTasks();
            for (Task<TaskCategory> task : tasks) {
                taskListModel.addElement(task);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Failed to load tasks: " + e.getMessage());
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
                taskList.repaint();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to update this task: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());

        JButton deleteButton = getDeleteButton();
        JButton completeButton = getCompleteButton();

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.add(deleteButton);
        dialog.add(completeButton);

        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseHelper.createTableIfNotExist();
            new TaskManagerUI().setVisible(true);
        });
    }
}
