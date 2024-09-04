package FrontEnd.UI;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaskEntryPanel extends JPanel {
    private TaskDAO taskDAO;
    private DefaultListModel<Task<TaskCategory>> taskListModel;

    private JTextField taskNameField;
    private JTextArea taskDescriptionField;
    private JComboBox<TaskCategory> taskCategoryComboBox;

    public TaskEntryPanel(TaskDAO taskDAO, DefaultListModel<Task<TaskCategory>> taskListModel) {
        this.taskDAO = taskDAO;
        this.taskListModel = taskListModel;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Task Name:"));
        add(taskNameField = new JTextField(2));

        add(new JLabel("Task Description:"));
        add(taskDescriptionField = new JTextArea(6, 2));

        add(new JLabel("Task Category:"));
        add(taskCategoryComboBox = new JComboBox<>(TaskCategory.values()));

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new AddTaskListener());
        add(addButton);
    }

    private class AddTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String taskName = taskNameField.getText();
            String taskDescription = taskDescriptionField.getText();
            TaskCategory taskCategory = (TaskCategory) taskCategoryComboBox.getSelectedItem();

            Task<TaskCategory> task = new Task<>(taskName, taskDescription, taskCategory);

            try {
                taskDAO.insertTask(task);
                taskListModel.addElement(task);
                taskNameField.setText("");
                taskDescriptionField.setText("");
                taskCategoryComboBox.setSelectedIndex(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(TaskEntryPanel.this, "Failed to add this task: " +
                        ex.getMessage());
            }
        }
    }
}
