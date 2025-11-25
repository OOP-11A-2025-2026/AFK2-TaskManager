package taskmanager.core;

/**
 * Enum representing the category of a task.
 * 
 * Possible values:
 * - BUG_FIX: Task is to fix a bug
 * - FEATURE: Task is to implement a new feature
 * - REFACTOR: Task is to refactor existing code
 * - DOCUMENTATION: Task is to create or update documentation
 * - OTHER: Any other type of task
 */
public enum Category {
    BUG_FIX,
    FEATURE,
    REFACTOR,
    DOCUMENTATION,
    OTHER
}
