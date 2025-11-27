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
                handleDelete(args, scanner);
                break;
            case "sort":
            case "s":
                handleSort(args);
                break;
            case "help":
            case "h":
                showHelp();
                break;
            case "q":
                // console UI handles quitting; still accept and treat as exit hint
                System.out.println("Type 'quit' or 'exit' to leave the app.");
                break;
            default:
                throw new InvalidCommandException("Unknown command: " + command);
        }
    }

    private void handleCreate(String args, Scanner scanner) {
        if (args.startsWith("/")) {
            // Shortcut creation (any /<token> <title> ) -> interactive remainder
            String[] parts = args.trim().split(" ", 2);
            if (parts.length < 2) throw new InvalidCommandException("Shortcut must contain a title after the category.");
            String catToken = parts[0].substring(1).toLowerCase();
            String title = parts[1].trim();

            // map common shortcut names to Category enum
            Category category;
            switch (catToken) {
                case "bug":
                case "bug_fix":
                case "bugfix":
                    category = Category.BUG_FIX;
                    break;
                case "feature":
                case "feat":
                    category = Category.FEATURE;
                    break;
                case "refactor":
                    category = Category.REFACTOR;
                    break;
                case "doc":
                case "documentation":
                    category = Category.DOCUMENTATION;
                    break;
                case "other":
                    category = Category.OTHER;
                    break;
                default:
                    try {
                        category = Category.valueOf(catToken.toUpperCase());
                    } catch (Exception e) {
                        throw new InvalidCommandException("Invalid category shortcut: " + catToken);
                    }
            }

            System.out.println("--- Create New Task (shortcut) ---");
            System.out.println("Title: " + title);
            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            // List people with IDs
            System.out.println("Available team members:");
            service.listPeople().forEach(p -> System.out.println("  " + p.toString()));
            System.out.print("Assignee ID: ");
            String assigneeId = scanner.nextLine().trim();
            if (!service.personExists(assigneeId)) {
                throw new InvalidCommandException("No person with ID: " + assigneeId);
            }

            System.out.print("Due Date (dd MM yyyy) [Optional]: ");
            String dateStr = scanner.nextLine().trim();
            java.time.LocalDate dueDate = null;
            if (!dateStr.isEmpty()) {
                try {
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd MM yyyy");
                    dueDate = java.time.LocalDate.parse(dateStr, fmt);
                } catch (java.time.format.DateTimeParseException e) {
                    throw new InvalidCommandException("Invalid date format. Use dd MM yyyy (e.g. 26 11 2025).");
                }
            }

            System.out.println("Eisenhower mapping:");
            System.out.println("  I   -> Important, Urgent");
            System.out.println("  II  -> Important, Not urgent");
            System.out.println("  III -> Not important, Urgent");
            System.out.println("  IV  -> Not important, Not urgent");
            System.out.print("Eisenhower (I, II, III, IV) [Optional]: ");
            String eisenhower = scanner.nextLine().trim().toUpperCase();
            if (eisenhower.isEmpty()) eisenhower = null;
            else if (!eisenhower.matches("I|II|III|IV")) {
                throw new InvalidCommandException("Invalid Eisenhower value. Must be I, II, III, or IV.");
            }

            service.createTask(title, description, category, assigneeId, dueDate, eisenhower);
            System.out.println("Task created via shortcut.");
            return;
        } else if (args.isEmpty()) {
            System.out.println("--- Create New Task ---");
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) throw new InvalidCommandException("Title is required.");

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            // Show categories as numbered list and accept number
            System.out.println("Select category:");
            Category[] cats = Category.values();
            for (int i = 0; i < cats.length; i++) {
                String pretty = cats[i].name().replace('_', ' ').toLowerCase();
                pretty = pretty.substring(0,1).toUpperCase() + pretty.substring(1);
                System.out.printf("  %d) %s\n", i+1, pretty);
            }
            System.out.print("Enter number: ");
            String catChoice = scanner.nextLine().trim();
            Category category;
            try {
                int choice = Integer.parseInt(catChoice);
                if (choice < 1 || choice > cats.length) throw new NumberFormatException();
                category = cats[choice-1];
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid category selection.");
            }

            // List people with IDs
            System.out.println("Available team members:");
            service.listPeople().forEach(p -> System.out.println("  " + p.toString()));
            System.out.print("Assignee ID: ");
            String assigneeId = scanner.nextLine().trim();
            if (!service.personExists(assigneeId)) {
                throw new InvalidCommandException("No person with ID: " + assigneeId);
            }
            System.out.print("Due Date (dd MM yyyy) [Optional]: ");
            String dateStr = scanner.nextLine().trim();
            LocalDate dueDate = null;
            if (!dateStr.isEmpty()) {
                try {
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd MM yyyy");
                    dueDate = LocalDate.parse(dateStr, fmt);
                } catch (DateTimeParseException e) {
                    throw new InvalidCommandException("Invalid date format. Use dd MM yyyy (e.g. 26 11 2025).");
                }
            }

            System.out.println("Eisenhower mapping:");
            System.out.println("  I   -> Important, Urgent");
            System.out.println("  II  -> Important, Not urgent");
            System.out.println("  III -> Not important, Urgent");
            System.out.println("  IV  -> Not important, Not urgent");
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
        String taskId = args;
        if (taskId.isEmpty()) {
            // prompt user to select
            System.out.println("Select a task to update:");
            java.util.List<taskmanager.core.Task> all = service.readAll();
            if (all.isEmpty()) { System.out.println("No tasks available."); return; }
            for (int i = 0; i < all.size(); i++) {
                System.out.printf("  %d) %s (ID: %s)\n", i+1, all.get(i).getTitle(), all.get(i).getId());
            }
            System.out.print("Enter number: ");
            String sel = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(sel);
                if (idx < 1 || idx > all.size()) throw new NumberFormatException();
                taskId = all.get(idx-1).getId();
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid selection.");
            }
        }
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
                    boolean deleted = service.updateStatus(taskId, status);
                    if (deleted) System.out.println("Task marked DONE and removed from list.");
                    else System.out.println("Status updated.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status.");
                }
                break;
            case "2":
                System.out.print("New Due Date (dd MM yyyy): ");
                String dateStr = scanner.nextLine().trim();
                try {
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd MM yyyy");
                    LocalDate date = LocalDate.parse(dateStr, fmt);
                    service.updateDueDate(taskId, date);
                    System.out.println("Due date updated.");
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Use dd MM yyyy.");
                }
                break;
            case "3":
                System.out.println("Eisenhower mapping:");
                System.out.println("  I   -> Important, Urgent");
                System.out.println("  II  -> Important, Not urgent");
                System.out.println("  III -> Not important, Urgent");
                System.out.println("  IV  -> Not important, Not urgent");
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

    private void handleDelete(String args, Scanner scanner) {
        String taskId = args;
        if (taskId.isEmpty()) {
            System.out.println("Select a task to delete:");
            java.util.List<taskmanager.core.Task> all = service.readAll();
            if (all.isEmpty()) { System.out.println("No tasks available."); return; }
            for (int i = 0; i < all.size(); i++) {
                System.out.printf("  %d) %s (ID: %s)\n", i+1, all.get(i).getTitle(), all.get(i).getId());
            }
            System.out.print("Enter number: ");
            String sel = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(sel);
                if (idx < 1 || idx > all.size()) throw new NumberFormatException();
                taskId = all.get(idx-1).getId();
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Invalid selection.");
            }
        }
        service.deleteTask(taskId);
        System.out.println("Deleted task: " + taskId);
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
                System.out.println("Eisenhower mapping:");
                System.out.println("  I   -> Important, Urgent");
                System.out.println("  II  -> Important, Not urgent");
                System.out.println("  III -> Not important, Urgent");
                System.out.println("  IV  -> Not important, Not urgent");
                System.out.println();
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

