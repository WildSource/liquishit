import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final DateTimeFormatter SECONDS_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private static DateTimeFormatter formatter = STANDARD_FORMATTER;
    private static boolean hasRollback = false;

    public static void help() {
        System.out.println("Small cli tool to generate liquibase changelogs");
        System.out.println();
        System.out.println("Warning: Only works for projects using includeAll and pointed towards a /migration dir");
        System.out.println("    Just use this path: resources/db/changelog/migrations");
        System.out.println();
        System.out.println("java -jar liquishit.jar [author] [changelog_name] [flags...]");
        System.out.println("    Example: java -jar liquishit.jar wildsource create_user_table");
        System.out.println();
        System.out.println("The example above generate an sql changelog");
        System.out.println("yyyyMMddHHmm_nom_de_la_migration.sql");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("    -s --seconds        add seconds to timestamp of changelog");
        System.out.println("    -r --rollback       add rollback in the changelog");
    }

    public static void main(String[] args) {
        // simple execution
        if (args.length == 0) {
            help();
            System.exit(1);
        }

        // Get arguments
        String author = args[0];
        String migrationName = args[1];

        Set<String> mainArguments = new HashSet<>(Arrays.asList(author, migrationName));
        Set<String> arguments = new HashSet<>(List.of(args));
        arguments.removeAll(mainArguments);

        List<String> flags = new ArrayList<>(arguments);

        if (!flags.isEmpty()) {
            for (String flag : flags) {
                switch (flag) {
                    case "-s", "--seconds" -> formatter = SECONDS_FORMATTER;
                    case "-r", "--rollback" -> hasRollback = true;
                    default -> {
                        System.out.println("Error: flag \"" + flag + "\" not recognized");
                        help();
                        System.exit(1);
                    }
                }
            }
        }

        // Get timestamp
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(formatter);
        String migrationFilepath = "src/main/resources/db/changelog/migrations/" + timestamp + "_" + migrationName + ".sql";

        try {
            File migrationFile = new File(migrationFilepath);

            // Create file
            if (migrationFile.createNewFile()) {
                System.out.println("The changelog " + migrationFilepath + " was generated successfully !");
            } else {
                System.out.println("Error: Changelog " + migrationFilepath + " already exists");
                System.exit(1);
            }

            // Write sql changelog to file
            try (FileWriter myWriter = new FileWriter(migrationFilepath, true)) {
                myWriter.write("--liquibase formatted sql\n");
                myWriter.write("\n");
                myWriter.write("--changeset " + author + ":" + timestamp + "_" + migrationName +"\n");
                myWriter.write("------> Sql here <------\n");
                myWriter.write("\n");

                if (hasRollback) {
                    myWriter.write("--rollback /* --> sql here <-- */\n");
                }

                System.out.println("Writing template to changelog successful !");
            } catch (IOException e) {
                System.out.println("Error: Writing to changelog failed");
                System.out.println("Here's the stack trace:");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error: Something went wrong with IO");
            System.out.println("Here's the stack trace:");
            e.printStackTrace();
        }
    }
}