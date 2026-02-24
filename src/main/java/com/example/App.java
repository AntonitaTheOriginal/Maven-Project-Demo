package com.example;
import com.example.DatabaseService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App 
{
    private DatabaseService dbService;
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private Scanner scanner;
    private List<String> taskList;
    
    public App() {
        
        scanner = new Scanner(System.in);
        taskList = new ArrayList<>();
        dbService = new DatabaseService();
        logger.info("Application initialized");
    }
    
    public static void main(String[] args)
    {
        App app = new App();
        
        if (args.length > 0) {
            // Command line mode
            app.processCommandLine(args);
        } else {
            // Interactive mode
            app.startInteractiveMode();
        }
    }
    
    private void processCommandLine(String[] args) {
        logger.info("Running in command line mode");
        
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "hello":
                if (args.length > 1) {
                    logger.info("Hello, {}!", args[1]);
                } else {
                    logger.info("Hello World!");
                }
                break;
                
            case "add":
                if (args.length > 1) {
                    String task = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    addTask(task);
                } else {
                    logger.warn("Please provide a task to add");
                }
                break;
                
            case "list":
                listTasks();
                break;
                
            case "calc":
                if (args.length == 4) {
                    calculate(args[1], args[2], args[3]);
                } else {
                    logger.warn("Usage: calc <num1> <operator> <num2>");
                }
                break;
                
            case "time":
                showCurrentTime();
                break;
                
            default:
                logger.warn("Unknown command: {}", command);
                showHelp();
        }
    }
    
    private void startInteractiveMode() {
        logger.info("Starting interactive mode. Type 'help' for commands.");
        System.out.println("\n=== Welcome to Enhanced Java App ===");
        
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                logger.info("Exiting application");
                dbService.close();  
                break;
            }
            
            processInteractiveCommand(input);
        }
        
        scanner.close();
    }
    
    private void processInteractiveCommand(String input) {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();
        
        switch (command) {

            case "dblist":
    showDatabaseTasks();
    break;
    
case "dbadd":
    if (parts.length > 1) {
        String task = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        dbService.addTask(task);
    } else {
        logger.warn("Please provide a task to add");
    }
    break;
    
case "dbdone":
    if (parts.length > 1) {
        try {
            int id = Integer.parseInt(parts[1]);
            dbService.completeTask(id);
        } catch (NumberFormatException e) {
            logger.error("Please provide a valid task ID");
        }
    }
    break;
    
case "dbdelete":
    if (parts.length > 1) {
        try {
            int id = Integer.parseInt(parts[1]);
            dbService.deleteTask(id);
        } catch (NumberFormatException e) {
            logger.error("Please provide a valid task ID");
        }
    }
    break;
            case "help":
                showHelp();
                break;
                
            case "hello":
                if (parts.length > 1) {
                    logger.info("Hello, {}!", parts[1]);
                } else {
                    logger.info("Hello World!");
                }
                break;
                
            case "add":
                if (parts.length > 1) {
                    String task = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                    addTask(task);
                } else {
                    logger.warn("Please provide a task to add");
                }
                break;
                
            case "list":
                listTasks();
                break;
                
            case "remove":
                if (parts.length > 1) {
                    try {
                        int index = Integer.parseInt(parts[1]) - 1;
                        removeTask(index);
                    } catch (NumberFormatException e) {
                        logger.error("Please provide a valid task number");
                    }
                }
                break;
                
            case "calc":
                if (parts.length == 4) {
                    calculate(parts[1], parts[2], parts[3]);
                } else {
                    logger.warn("Usage: calc <num1> <operator> <num2>");
                }
                break;
                
            case "time":
                showCurrentTime();
                break;
                
            case "date":
                showCurrentDate();
                break;
                
            case "clear":
                clearTasks();
                break;
                
            default:
                logger.warn("Unknown command: {}", command);
                showHelp();
        }
    }

    private void showDatabaseTasks() {
    List<String> tasks = dbService.getAllTasks();
    if (tasks.isEmpty()) {
        logger.info("No tasks in database");
    } else {
        logger.info("=== Database Tasks ===");
        for (String task : tasks) {
            System.out.println(task);
        }
    }
}
    
    // Task Management Features
    private void addTask(String task) {
        taskList.add(task);
        logger.info("Task added: '{}' (Total tasks: {})", task, taskList.size());
    }
    
    private void listTasks() {
        if (taskList.isEmpty()) {
            logger.info("No tasks in the list");
        } else {
            logger.info("=== Your Tasks ({}) ===", taskList.size());
            for (int i = 0; i < taskList.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, taskList.get(i));
            }
        }
    }
    
    private void removeTask(int index) {
        if (index >= 0 && index < taskList.size()) {
            String removed = taskList.remove(index);
            logger.info("Task removed: '{}'", removed);
        } else {
            logger.error("Invalid task number");
        }
    }
    
    private void clearTasks() {
        taskList.clear();
        logger.info("All tasks cleared");
    }
    
    // Calculator Feature
    private void calculate(String num1Str, String operator, String num2Str) {
        try {
            double num1 = Double.parseDouble(num1Str);
            double num2 = Double.parseDouble(num2Str);
            double result = 0;
            
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                case "x":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        logger.error("Division by zero is not allowed");
                        return;
                    }
                    break;
                case "%":
                    result = num1 % num2;
                    break;
                default:
                    logger.error("Unknown operator: {}", operator);
                    return;
            }
            
            logger.info("Result: {} {} {} = {}", num1, operator, num2, result);
        } catch (NumberFormatException e) {
            logger.error("Invalid numbers provided");
        }
    }
    
    // Time and Date Features
    private void showCurrentTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        logger.info("Current time: {}", time.format(formatter));
    }
    
    private void showCurrentDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        logger.info("Today's date: {}", date.format(formatter));
    }
    
    // Help Feature
    private void showHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("hello [name]          - Say hello");
        System.out.println("add <task>            - Add a task");
        System.out.println("list                  - List all tasks");
        System.out.println("remove <number>       - Remove a task by number");
        System.out.println("clear                 - Clear all tasks");
        System.out.println("calc <n1> <op> <n2>   - Calculate (op: + - * / %)");
        System.out.println("time                  - Show current time");
        System.out.println("date                  - Show current date");
        System.out.println("help                  - Show this help");
        System.out.println("exit/quit             - Exit application");
        System.out.println("========================\n");
        System.out.println("dblist                - List all tasks from database");
System.out.println("dbadd <task>           - Add task to database");
System.out.println("dbdone <id>            - Mark task as complete");
System.out.println("dbdelete <id>          - Delete task from database");
    }
}