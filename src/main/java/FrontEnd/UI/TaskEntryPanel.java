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
        setLayout(new GridBagLayout());
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.insets = new Insets(5, 5, 5, 5);  // Padding

        gridConstraints.gridx = 0; gridConstraints.gridy = 0;
        add(new JLabel("Task Name:"), gridConstraints);
        gridConstraints.gridx = 1;
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
        JButton addButton = new JButton("Add Task");
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setMnemonic('A');
        addButton.addActionListener(new AddTaskListener());
        add(addButton, gridConstraints);
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
