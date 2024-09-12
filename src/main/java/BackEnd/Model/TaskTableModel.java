package BackEnd.Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Task Name", "Task Description", "Completed", "Category"};
    private static List<Task<TaskCategory>> tasks;

    public TaskTableModel(List<Task<TaskCategory>> tasks) {
        TaskTableModel.tasks = new ArrayList<>(tasks); // Create a copy to avoid external modifications
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task<TaskCategory> task = tasks.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> task.getName();
            case 1 -> task.getDescription();
            case 2 -> task.isCompleted() ? "Yes" : "No";
            case 3 -> task.getCategory();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            Task<TaskCategory> task = tasks.get(rowIndex);
            if (aValue instanceof Boolean) {
                task.setCompleted((Boolean) aValue);
            } else if (aValue instanceof String) {
                task.setCompleted(aValue.equals("Yes"));
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public void addTask(Task<TaskCategory> task) {
        tasks.add(task);
        fireTableRowsInserted(tasks.size() - 1, tasks.size() - 1);
    }

    public void removeTask(int rowIndex) {
        tasks.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public Task<TaskCategory> getTaskAt(int rowIndex) {
        return tasks.get(rowIndex);
    }

    public void setTasks(List<Task<TaskCategory>> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        fireTableDataChanged();
    }
}
