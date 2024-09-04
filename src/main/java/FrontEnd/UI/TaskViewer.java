package FrontEnd.UI;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;

public class TaskViewer extends JPanel {

    private final JList<String> taskList;
    private final DefaultListModel<String> taskListModel;

    public TaskViewer() {
        setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Tasks");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);
    }

    public void updateTaskList(List<Task<TaskCategory>> tasks) {
        taskListModel.clear();
        for (Task<TaskCategory> task : tasks) {
            taskListModel.addElement(task.getName());
        }
    }

    public String getSelectedTask() {
        return taskList.getSelectedValue();
    }
}
