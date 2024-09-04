package BackEnd.Util;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;

public class FileHandler {
    TaskDAO taskDAO = new TaskDAO();
    public class SaveTasksListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try (FileOutputStream fileOutput = new FileOutputStream("Tasks.txt");
                 ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {
                List<Task<TaskCategory>> tasks = taskDAO.getAllTasks();
                objectOutput.writeObject(tasks);
                JOptionPane.showMessageDialog(null, "Saved Tasks Successfully");
            } catch (IOException | SQLException ex) {
                JOptionPane.showMessageDialog(null, "Something went wrong");
            }

        }
    }

    public void saveToCSVFile(String fileName, String text) {


    }
}
