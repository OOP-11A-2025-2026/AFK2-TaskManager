package taskmanager.console;

import java.util.Scanner;
import taskmanager.exceptions.InvalidCommandException;
// import taskmanager.services.TaskService;
// import taskmanager.core.Category;
// import taskmanager.core.Status;
// import taskmanager.core.Person;
// import java.time.LocalDate;

public class CommandParser {

    // public void execute(String input, TaskService service, Scanner scanner) {
    public void execute(String input, Scanner scanner) {
        String[] parts = input.split(" ", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "create":
            case "c":
                handleCreate(args, scanner); // Pass service
                break;
            case "read":
            case "r":
                handleRead(args); // Pass service
                break;
            case "update":
            case "u":
                handleUpdate(args, scanner); // Pass service
                break;
            case "delete":
            case "d":
                handleDelete(args); // Pass service
                break;
            case "sort":
            case "s":
                handleSort(args); // Pass service
                break;
            case "help":
                showHelp();
                break;
            default:
                throw new InvalidCommandException("Unknown command: " + command);
        }
    }

    // private void handleCreate(String args, TaskService service, Scanner scanner) {
    private void handleCreate(String args, Scanner scanner) {
        if (args.startsWith("/bug ")) {
            String title = args.substring(5).trim();
            if (title.isEmpty()) {
                throw new InvalidCommandException("Title cannot be empty for bug shortcut.");
            }
            // service.createTaskShortcut(title);
            System.out.println("Task created (shortcut): " + title);
        } else if (args.isEmpty()) {
            System.out.println("--- Create New Task ---");
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) throw new InvalidCommandException("Title is required.");

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Category (BUG_FIX, FEATURE, REFACTOR, DOCUMENTATION, OTHER): ");
            String categoryStr = scanner.nextLine().trim().toUpperCase();
            // Category category = Category.valueOf(categoryStr);

            System.out.print("Assignee ID: ");
            String assigneeId = scanner.nextLine().trim();
            // Person assignee = service.findPersonById(assigneeId);

            System.out.print("Eisenhower (I, II, III, IV): ");
            String eisenhower = scanner.nextLine().trim().toUpperCase();
            if (!eisenhower.matches("I|II|III|IV")) {
                throw new InvalidCommandException("Invalid Eisenhower value. Must be I, II, III, or IV.");
            }

            // service.createTask(title, description, category, assignee, eisenhower);
            System.out.println("Task created: " + title);
        } else {
            throw new InvalidCommandException("Invalid create command format. Use 'create' or 'create /bug <title>'.");
        }
    }

    // private void handleRead(String args, TaskService service) {
    private void handleRead(String args) {
        if (args.isEmpty()) {
            // System.out.println(service.readAll());
            System.out.println("[MOCK] Reading all tasks...");
        } else {
            // System.out.println(service.readByAssignee(args));
            System.out.println("[MOCK] Reading tasks for assignee: " + args);
        }
    }

    // private void handleUpdate(String args, TaskService service, Scanner scanner) {
    private void handleUpdate(String args, Scanner scanner) {
        if (args.isEmpty()) {
            throw new InvalidCommandException("Task ID required for update.");
        }
        String taskId = args;
        System.out.println("--- Update Task " + taskId + " ---");
        System.out.println("1. Update Status");
        System.out.println("2. Update Due Date");
        System.out.println("3. Update Eisenhower");
        System.out.print("Choose option: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1":
                System.out.print("New Status (TO_DO, IN_PROCESS, DONE): ");
                String statusStr = scanner.nextLine().trim().toUpperCase();
                // Status status = Status.valueOf(statusStr);
                // service.updateTaskStatus(taskId, status);
                System.out.println("[MOCK] Updated status to " + statusStr);
                break;
            case "2":
                System.out.print("New Due Date (YYYY-MM-DD): ");
                String dateStr = scanner.nextLine().trim();
                // LocalDate date = LocalDate.parse(dateStr);
                // service.updateTaskDueDate(taskId, date);
                System.out.println("[MOCK] Updated due date to " + dateStr);
                break;
            case "3":
                System.out.print("New Eisenhower (I, II, III, IV): ");
                String eisenhower = scanner.nextLine().trim().toUpperCase();
                if (!eisenhower.matches("I|II|III|IV")) {
                    throw new InvalidCommandException("Invalid Eisenhower value.");
                }
                // service.updateTaskEisenhower(taskId, eisenhower);
                System.out.println("[MOCK] Updated Eisenhower to " + eisenhower);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // private void handleDelete(String args, TaskService service) {
    private void handleDelete(String args) {
        if (args.isEmpty()) {
            throw new InvalidCommandException("Task ID required for delete.");
        }
        // service.deleteTask(args);
        System.out.println("[MOCK] Deleted task: " + args);
    }

    // private void handleSort(String args, TaskService service) {
    private void handleSort(String args) {
        switch (args.toLowerCase()) {
            case "due":
                // System.out.println(service.sortByDueDate());
                System.out.println("[MOCK] Sorted by due date");
                break;
            case "cat":
                // System.out.println(service.sortByCategory());
                System.out.println("[MOCK] Sorted by category");
                break;
            case "eisenhower":
                // System.out.println(service.sortByEisenhower());
                System.out.println("[MOCK] Sorted by Eisenhower matrix");
                break;
            default:
                throw new InvalidCommandException("Invalid sort criteria. Use 'due', 'cat', or 'eisenhower'.");
        }
    }

    private void showHelp() {
        System.out.println("\n================================");
        System.out.println("   AVAILABLE COMMANDS");
        System.out.println("================================");
        System.out.println("\n📝 TASK OPERATIONS:");
        System.out.printf("  %-35s - %s%n", "create, c", "Create a new task (interactive)");
        System.out.printf("  %-35s - %s%n", "create /bug <title>", "Shortcut to create a bug");
        System.out.printf("  %-35s - %s%n", "read, r", "Show all tasks");
        System.out.printf("  %-35s - %s%n", "read <personID>, r <personID>", "Show tasks for a person");
        System.out.printf("  %-35s - %s%n", "update <taskID>, u <taskID>", "Update a task");
        System.out.printf("  %-35s - %s%n", "delete <taskID>, d <taskID>", "Delete a task");
        
        System.out.println("\n🔍 SORTING:");
        System.out.printf("  %-35s - %s%n", "sort due", "Sort by due date");
        System.out.printf("  %-35s - %s%n", "sort cat", "Sort by category");
        System.out.printf("  %-35s - %s%n", "sort eisenhower", "Sort by Eisenhower matrix");
        
        System.out.println("\n🛠️  UTILITIES:");
        System.out.printf("  %-35s - %s%n", "help", "Show this help menu");
        System.out.printf("  %-35s - %s%n", "exit, quit", "Exit the application");
        System.out.println("\n================================\n");
    }
}
