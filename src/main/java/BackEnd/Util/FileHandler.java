package BackEnd.Util;

import BackEnd.DAO.TaskDAO;
import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FileHandler {
    TaskDAO taskDAO = new TaskDAO();
    public void saveTasksToFile(List<Task<TaskCategory>> tasks, String filename) throws IOException {
        try (OutputStream os = new FileOutputStream(filename)) {
            for (Task<TaskCategory> task : tasks) {
                String taskData = task.toString() + "\n";
                os.write(taskData.getBytes());
            }
        }
    }


    public void saveToCSVFile(String fileName, String text) {


    }
}
