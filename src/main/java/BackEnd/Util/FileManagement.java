package BackEnd.Util;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;
import FrontEnd.UI.TaskManagerUI;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;

public class FileManagement extends TaskManagerUI {
    private static final TaskDAO taskDAO = new TaskDAO();
    public static void exportTasksToFile(String filePath) throws SQLException {
        List<Task<TaskCategory>> tasks = taskDAO.getAllTasks();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(filePath), StandardCharsets.UTF_8)) {
            for (Task<TaskCategory> task : tasks) {
                bufferedWriter.write(task.getName() + "," + task.getDescription() + "," + task.isCompleted() + ","
                        + task.getCategory());
                bufferedWriter.newLine();
            }
            JOptionPane.showMessageDialog(null, "Tasks exported to file", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting to text file", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportTasksToCSV(String filePath) throws SQLException {
        List<Task<TaskCategory>> tasks = taskDAO.getAllTasks();
        Path path = Path.of(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {
            for (Task<TaskCategory> task : tasks) {
                writer.write(task.getName() + ","
                + task.getDescription() + ","
                + task.isCompleted() + ","
                + task.getCategory());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "CSV File created successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to create csv file.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void importFromFile(String filePath) throws IOException, SQLException {
        Path path = Path.of(filePath);
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] taskInfo = line.split(",");
                    String name = taskInfo[0];
                    String description = taskInfo[1];
                    boolean completed = Boolean.parseBoolean(taskInfo[2]);
                    TaskCategory category = TaskCategory.valueOf(taskInfo[3]);
                    Task<TaskCategory> task = new Task<>(name, description, completed, category);
                    taskDAO.insertTask(task);
                }
                JOptionPane.showMessageDialog(null, "Tasks added to database successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to add tasks to the database",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
