package taskmanager.console;

import java.util.Scanner;
import taskmanager.exceptions.InvalidCommandException;
import taskmanager.exceptions.InvalidDataException;
import taskmanager.exceptions.NotFoundException;
import taskmanager.exceptions.RepositoryException;
import taskmanager.data.PersonRepository;
import taskmanager.data.TaskRepository;
import taskmanager.services.TaskService;

/**
 * ConsoleUI Class (Part C3)
 * 
 * Responsible for:
 * - Main menu loop
 * - Reading user input
 * - Displaying output
 * - Handling errors gracefully
 */
public class ConsoleUI {
    private final CommandParser commandParser;
    private final Scanner scanner;
    private static final String SEPARATOR = "================================";

    public ConsoleUI() {
        PersonRepository personRepo = new PersonRepository();
        TaskRepository taskRepo = new TaskRepository(personRepo);
        TaskService taskService = new TaskService(personRepo, taskRepo);
        
        this.commandParser = new CommandParser(taskService);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main run loop for the console UI.
     * Displays welcome message and continuously reads commands until exit.
     * Gracefully handles all exceptions without crashing.
     */
    public void run() {
        displayWelcomeMessage();

        while (true) {
            try {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String input = scanner.nextLine().trim();

                // Skip empty input
                if (input.isEmpty()) {
                    continue;
                }

                // Handle exit command
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("q")) {
                    displayGoodbyeMessage();
                    break;
                }

                // Execute command through parser
                commandParser.execute(input, scanner);

            } catch (InvalidCommandException e) {
                displayError("Invalid Command", e.getMessage());
            } catch (InvalidDataException e) {
                displayError("Invalid Data", e.getMessage());
            } catch (NotFoundException e) {
                displayError("Not Found", e.getMessage());
            } catch (RepositoryException e) {
                displayError("Repository Error", e.getMessage());
            } catch (Exception e) {
                displayError("Unexpected Error", e.getMessage());
                // Uncomment for debugging:
                // e.printStackTrace();
            }
        }

        scanner.close();
    }

    /**
     * Display the welcome banner and initial instructions.
     */
    private void displayWelcomeMessage() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("   WELCOME TO TASK MANAGER");
        System.out.println(SEPARATOR);
        System.out.println("Manage and track programming tasks for your team.");
        System.out.println("Type 'help' to see available commands.");
        System.out.println("Type 'exit' or 'quit' to close the application.");
        System.out.println(SEPARATOR + "\n");
    }

    /**
     * Display the goodbye message when exiting.
     */
    private void displayGoodbyeMessage() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("Thank you for using Task Manager!");
        System.out.println("Goodbye!");
        System.out.println(SEPARATOR + "\n");
    }

    /**
     * Display an error message with formatted output.
     * 
     * @param errorType The type of error (e.g., "Invalid Command")
     * @param message The error message
     */
    private void displayError(String errorType, String message) {
        System.out.println("\n❌ [" + errorType + "]: " + message);
        System.out.println("   Type 'help' for command options.\n");
    }

    /**
     * Display a success message.
     * 
     * @param message The success message
     */
    public void displaySuccess(String message) {
        System.out.println("\n✓ [Success]: " + message + "\n");
    }

    /**
     * Display an info message.
     * 
     * @param message The info message
     */
    public void displayInfo(String message) {
        System.out.println("\nℹ [Info]: " + message + "\n");
    }

    /**
     * Display a separator for better readability.
     */
    public void displaySeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Get the scanner for reading input.
     * Used by CommandParser for multi-line input.
     * 
     * @return The Scanner instance
     */
    public Scanner getScanner() {
        return scanner;
    }
}
