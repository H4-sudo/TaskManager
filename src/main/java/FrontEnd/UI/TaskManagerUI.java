package FrontEnd.UI;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;
import BackEnd.Util.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TaskManagerUI extends JFrame {
    private TaskDAO taskDAO;
    private JList<Task<TaskCategory>> taskList;
    private DefaultListModel<Task<TaskCategory>> taskListModel;

    public TaskManagerUI() {
        taskDAO = new TaskDAO();
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        initUI();
        loadTasks();
    }

    private void initUI() {
        setTitle("Task Manager");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(taskList, BorderLayout.CENTER);

        TaskEntryPanel taskEntryPanel = new TaskEntryPanel(taskDAO, taskListModel);
        mainPanel.add(taskEntryPanel, BorderLayout.NORTH);

        add(mainPanel);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseHelper.createTableIfNotExist();
            new TaskManagerUI().setVisible(true);
        });
    }
}
