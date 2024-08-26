package parser;

import enums.CommandName;
import exceptions.JarException;
import tasks.*;
import ui.Ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The Parser class is responsible for interpreting user commands and
 * converting them into actionable tasks or operations within the Jar Bot application.
 */
public class Parser {

    /**
     * Extracts the task number from a given command string.
     *
     * @param command The command string containing the task number.
     * @return The task number as an integer.
     * @throws JarException If the task number is invalid or incorrectly formatted.
     */
    public int getTaskNumber(String command) throws JarException {
        String[] parts = command.split(" ");
        if (parts.length == 2) {
            try {
                int taskNumber = Integer.parseInt(parts[1]) - 1;
                if (taskNumber < 0) {
                    throw new JarException("tasks.Task number must be greater than 0.");
                }
                return taskNumber;
            } catch (NumberFormatException e) {
                throw new JarException("Invalid task number format.");
            }
        } else {
            throw new JarException("Invalid task command format.");
        }
    }

    /**
     * Parses a command string and converts it into a Task object.
     *
     * @param command The command string to be parsed.
     * @return The corresponding Task object.
     * @throws JarException If the command is unknown or incorrectly formatted.
     */
    public Task parseTask(String command) throws JarException {
        CommandName commandType = parseCommandType(command);

        switch (commandType) {
            case TODO:
                String todoDescription = command.substring(4).trim();
                if (todoDescription.isEmpty()) {
                    throw new JarException("The description of a todo cannot be empty.");
                }
                return new ToDo(todoDescription);

            case DEADLINE:
                String[] deadlineParts = command.substring(8).split("/by", 2);
                if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty() || deadlineParts[1].trim().isEmpty()) {
                    throw new JarException("Invalid deadline format. Use: deadline <description> /by <date>");
                }
                String deadlineDescription = deadlineParts[0].trim();
                LocalDateTime deadlineDateTime = parseDateTime(deadlineParts[1].trim());
                return new DeadLine(deadlineDescription, deadlineDateTime);

            case EVENT:
                String[] eventParts = command.substring(5).split("/from", 2);
                if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
                    throw new JarException("Invalid event format. Use: event <description> /from <start time> /to <end time>");
                }
                String eventDescription = eventParts[0].trim();
                String[] timeParts = eventParts[1].split("/to", 2);
                if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()) {
                    throw new JarException("Invalid event time format. Use: event <description> /from <start time> /to <end time>");
                }
                String from = timeParts[0].trim();
                String to = timeParts[1].trim();
                return new Event(eventDescription, from, to);

            default:
                throw new JarException("Unknown command: " + command + ". Please enter a valid command.");
        }
    }

    /**
     * Parses a date and time string into a LocalDateTime object.
     *
     * @param dateTimeStr The date and time string to be parsed.
     * @return A LocalDateTime object representing the parsed date and time.
     * @throws JarException If the date and time format is invalid.
     */
    private LocalDateTime parseDateTime(String dateTimeStr) throws JarException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            throw new JarException("Invalid date-time format. Use: d/M/yyyy HHmm.");
        }
    }

    /**
     * Handles a user command by interpreting it and performing the corresponding action.
     *
     * @param command  The user command string.
     * @param taskList The list of tasks in the Jar Bot application.
     * @param ui       The user interface used to interact with the user.
     * @return A boolean indicating whether the application should continue running.
     * @throws JarException If the command is unknown or if an error occurs during command handling.
     */
    public boolean handleCommand(String command, TaskList taskList, Ui ui) throws JarException {
        CommandName commandType = parseCommandType(command);

        switch (commandType) {
            case EXIT:
                ui.showGoodbye();
                return false;

            case LIST:
                ui.showTaskList(taskList.listTasks());
                break;

            case MARK:
                int markNumber = getTaskNumber(command);
                Task markTask = taskList.getTask(markNumber);
                if (markTask != null) {
                    taskList.markTaskAsDone(markNumber);
                    ui.showTaskMarked(markTask);
                } else {
                    throw new JarException("Invalid task number.");
                }
                break;

            case UNMARK:
                int unmarkNumber = getTaskNumber(command);
                Task unmarkTask = taskList.getTask(unmarkNumber);
                if (unmarkTask != null) {
                    taskList.markTaskAsUndone(unmarkNumber);
                    ui.showTaskUnmarked(unmarkTask);
                } else {
                    throw new JarException("Invalid task number.");
                }
                break;

            case DELETE:
                int deleteNumber = getTaskNumber(command);
                Task deleteTask = taskList.getTask(deleteNumber);
                taskList.deleteTask(deleteNumber);
                ui.showDeleteTask(deleteTask);
                ui.showTaskCount(taskList.getTaskCount());
                break;

            case TODO:
            case DEADLINE:
            case EVENT:
                Task task = parseTask(command);
                taskList.addTask(task);
                ui.showTaskAdded(task.toString());
                ui.showTaskCount(taskList.getTaskCount());
                break;

            default:
                throw new JarException("Unknown command: " + command + ". Please enter a valid command.");
        }

        return true;
    }

    /**
     * Determines the type of command based on the command string.
     *
     * @param command The command string to be interpreted.
     * @return The corresponding CommandName enum value.
     */
    public CommandName parseCommandType(String command) {
        if (command.startsWith("todo")) {
            return CommandName.TODO;
        } else if (command.startsWith("deadline")) {
            return CommandName.DEADLINE;
        } else if (command.startsWith("event")) {
            return CommandName.EVENT;
        } else if (command.startsWith("mark")) {
            return CommandName.MARK;
        } else if (command.startsWith("unmark")) {
            return CommandName.UNMARK;
        } else if (command.startsWith("delete")) {
            return CommandName.DELETE;
        } else if (command.equalsIgnoreCase("list")) {
            return CommandName.LIST;
        } else if (command.equalsIgnoreCase("bye")) {
            return CommandName.EXIT;
        } else {
            return CommandName.UNKNOWN;
        }
    }

}
