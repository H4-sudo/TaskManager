package BackEnd.DAO;

import BackEnd.Model.Task;
import BackEnd.Model.TaskCategory;
import BackEnd.Util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private final Connection connection;

    public TaskDAO() {
        connection = DatabaseHelper.getConnection();
    }

    public void insertTask(Task<TaskCategory> task) throws SQLException {
        String query = "INSERT INTO Tasks (name, description, isCompleted, category) VALUES (?,?,?,?)";
        preparedStatement(task, query);
    }

    public List<Task<TaskCategory>> getAllTasks() throws SQLException {
        List<Task<TaskCategory>> tasks = new ArrayList<>();
        String query = "SELECT * FROM Tasks";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                boolean completed = resultSet.getBoolean("isCompleted");
                TaskCategory category = TaskCategory.valueOf(resultSet.getString("category"));
                Task<TaskCategory> task = new Task<>(name, description, category);
                task.setCompleted(task.isCompleted());
                tasks.add(task);
            }
        }
        return tasks;
    }

    public void updateTask(Task<TaskCategory> task) throws SQLException {
        String query = "UPDATE Tasks SET name = ?, description = ?, isCompleted = ? WHERE category = ?";
        preparedStatement(task, query);
    }

    private void preparedStatement(Task<TaskCategory> task, String query) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setBoolean(3, task.isCompleted());
            preparedStatement.setString(4, task.getCategory().name());
            preparedStatement.executeUpdate();
        }
    }
}
