package taskmanager.console;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import taskmanager.core.Category;
import taskmanager.core.Status;
import taskmanager.exceptions.InvalidCommandException;
import taskmanager.services.TaskService;

public class CommandParser {

    private final TaskService service;

    public CommandParser(TaskService service) {
        this.service = service;
    }

    public void execute(String input, Scanner scanner) {
        String[] parts = input.split(" ", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "create":
            case "c":
                handleCreate(args, scanner);
                break;
            case "read":
            case "r":
                handleRead(args);
                break;
            case "update":
            case "u":
                handleUpdate(args, scanner);
                break;
            case "delete":
            case "d":
                handleDelete(args);
                break;
            case "sort":
            case "s":
                handleSort(args);
                break;
            case "help":
                showHelp();
                break;
            default:
                throw new InvalidCommandException("Unknown command: " + command);
        }
    }

    private void handleCreate(String args, Scanner scanner) {
        if (args.startsWith("/bug ")) {
            // Shortcut creation
            service.createTaskShortcut(args);
            System.out.println("Task created via shortcut.");
        } else if (args.isEmpty()) {
            System.out.println("--- Create New Task ---");
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) throw new InvalidCommandException("Title is required.");

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Category (BUG_FIX, FEATURE, REFACTOR, DOCUMENTATION, OTHER): ");
            String categoryStr = scanner.nextLine().trim().toUpperCase();
            Category category;
            try {
                category = Category.valueOf(categoryStr);
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandException("Invalid category.");
            }

            System.out.print("Assignee ID: ");
            String assigneeId = scanner.nextLine().trim();

            System.out.print("Due Date (YYYY-MM-DD) [Optional]: ");
            String dateStr = scanner.nextLine().trim();
            LocalDate dueDate = null;
            if (!dateStr.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dateStr);
                } catch (DateTimeParseException e) {
                    throw new InvalidCommandException("Invalid date format. Use YYYY-MM-DD.");
                }
            }

            System.out.print("Eisenhower (I, II, III, IV) [Optional]: ");
            String eisenhower = scanner.nextLine().trim().toUpperCase();
            if (eisenhower.isEmpty()) eisenhower = null;
            else if (!eisenhower.matches("I|II|III|IV")) {
                throw new InvalidCommandException("Invalid Eisenhower value. Must be I, II, III, or IV.");
            }

            service.createTask(title, description, category, assigneeId, dueDate, eisenhower);
            System.out.println("Task created successfully.");
        } else {
            throw new InvalidCommandException("Invalid create command format. Use 'create' or 'create /bug <title>'.");
        }
    }

    private void handleRead(String args) {
        if (args.isEmpty()) {
            service.readAll().forEach(System.out::println);
        } else {
            service.readByAssignee(args).forEach(System.out::println);
        }
    }

    private void handleUpdate(String args, Scanner scanner) {
        if (args.isEmpty()) {
            throw new InvalidCommandException("Task ID required for update.");
        }
        String taskId = args;
        System.out.println("--- Update Task " + taskId + " ---");
        System.out.println("1. Update Status");
        System.out.println("2. Update Due Date");
        System.out.println("3. Update Eisenhower");
        System.out.println("4. Update Title");
        System.out.println("5. Update Description");
        System.out.println("6. Update Category");
        System.out.println("7. Update Assignee");
        System.out.print("Choose option: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1":
                System.out.print("New Status (TO_DO, IN_PROCESS, DONE): ");
                String statusStr = scanner.nextLine().trim().toUpperCase();
                try {
                    Status status = Status.valueOf(statusStr);
                    service.updateStatus(taskId, status); // I will add this to Service
                    System.out.println("Status updated.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status.");
                }
                break;
            case "2":
                System.out.print("New Due Date (YYYY-MM-DD): ");
                String dateStr = scanner.nextLine().trim();
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    service.updateDueDate(taskId, date); // I will add this to Service
                    System.out.println("Due date updated.");
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
                break;
            case "3":
                System.out.print("New Eisenhower (I, II, III, IV): ");
                String eisenhower = scanner.nextLine().trim().toUpperCase();
                if (!eisenhower.matches("I|II|III|IV")) {
                    throw new InvalidCommandException("Invalid Eisenhower value.");
                }
                service.updateEisenhower(taskId, eisenhower);
                System.out.println("Eisenhower updated.");
                break;
            case "4":
                System.out.print("New Title: ");
                String title = scanner.nextLine().trim();
                if (title.isEmpty()) throw new InvalidCommandException("Title cannot be empty.");
                service.updateTitle(taskId, title);
                System.out.println("Title updated.");
                break;
            case "5":
                System.out.print("New Description: ");
                String description = scanner.nextLine().trim();
                service.updateDescription(taskId, description);
                System.out.println("Description updated.");
                break;
            case "6":
                System.out.print("New Category (BUG_FIX, FEATURE, REFACTOR, DOCUMENTATION, OTHER): ");
                String catStr = scanner.nextLine().trim().toUpperCase();
                try {
                    Category cat = Category.valueOf(catStr);
                    service.updateCategory(taskId, cat);
                    System.out.println("Category updated.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid category.");
                }
                break;
            case "7":
                System.out.print("New Assignee ID: ");
                String assigneeId = scanner.nextLine().trim();
                if (assigneeId.isEmpty()) throw new InvalidCommandException("Assignee ID cannot be empty.");
                service.updateAssignee(taskId, assigneeId);
                System.out.println("Assignee updated.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void handleDelete(String args) {
        if (args.isEmpty()) {
            throw new InvalidCommandException("Task ID required for delete.");
        }
        service.deleteTask(args);
        System.out.println("Deleted task: " + args);
    }

    private void handleSort(String args) {
        switch (args.toLowerCase()) {
            case "due":
                service.sortByDueDate().forEach(System.out::println);
                break;
            case "cat":
                service.sortByCategory().forEach(System.out::println);
                break;
            case "eisenhower":
                service.sortByEisenhower().forEach(System.out::println);
                break;
            default:
                throw new InvalidCommandException("Invalid sort criteria. Use 'due', 'cat', or 'eisenhower'.");
        }
    }

    private void showHelp() {
        System.out.println("\n================================");
        System.out.println("   AVAILABLE COMMANDS");
        System.out.println("================================");
        System.out.println("\nTASK OPERATIONS:");
        System.out.printf("  %-35s - %s%n", "create, c", "Create a new task (interactive)");
        System.out.printf("  %-35s - %s%n", "create /bug <title>", "Shortcut to create a bug");
        System.out.printf("  %-35s - %s%n", "read, r", "Show all tasks");
        System.out.printf("  %-35s - %s%n", "read <personID>, r <personID>", "Show tasks for a person");
        System.out.printf("  %-35s - %s%n", "update <taskID>, u <taskID>", "Update a task");
        System.out.printf("  %-35s - %s%n", "delete <taskID>, d <taskID>", "Delete a task");
        
        System.out.println("\nSORTING:");
        System.out.printf("  %-35s - %s%n", "sort due", "Sort by due date");
        System.out.printf("  %-35s - %s%n", "sort cat", "Sort by category");
        System.out.printf("  %-35s - %s%n", "sort eisenhower", "Sort by Eisenhower matrix");
        
        System.out.println("\nUTILITIES:");
        System.out.printf("  %-35s - %s%n", "help", "Show this help menu");
        System.out.printf("  %-35s - %s%n", "exit, quit", "Exit the application");
        System.out.println("\n================================\n");
    }
}

