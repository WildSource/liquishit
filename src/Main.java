import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void help() {
        System.out.println("Outils cli pour generer les changelogs (migrations) pour liquibase");
        System.out.println();
        System.out.println("Syntaxe: liquishit auteur nom_de_la_migration");
        System.out.println("Example: liquishit samuel create_user_table");
        System.out.println();
        System.out.println("L'exemple genere un fichier avec un template pret a recevoir du sql sous le format");
        System.out.println("yyyyMMddHHmmss_nom_de_la_migration.sql");
    }

    public static void main(String[] args) {
        // simple execution
        if (args.length == 0) {
            help();
            System.exit(1);
        }

        // Limit argument input amount
        if (args.length > 2) {
            System.out.println("Erreur: trop d'argument fournit, il faut juste l'auteur et le nom de la migration.");
            System.exit(1);
        }

        // Get arguments
        String author = args[0];
        String migrationName = args[1];

        // Get timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);

        String migrationFilepath = "src/main/resources/db/changelog/migrations/" + timestamp + "_" + migrationName + ".sql";

        try {
            File migrationFile = new File(migrationFilepath);

            // Create file
            if (migrationFile.createNewFile()) {
                System.out.println("Le fichier de migration " + migrationFilepath + " a ete creer avec succes !");
            } else {
                System.out.println("Erreur : le fichier de migration " + migrationFilepath + " existe deja.");
                System.exit(1);
            }

            // Write sql changelog to file
            try (FileWriter myWriter = new FileWriter(migrationFilepath, true)) {
                myWriter.write("--liquibase formatted sql\n");
                myWriter.write("\n");
                myWriter.write("--changeset " + author + ":" + timestamp + "_" + migrationName +"\n");
                myWriter.write("------> Sql ici <------\n");

                System.out.println("Ecriture du template sql effectuer avec succes !");
            } catch (IOException e) {
                System.out.println("Erreur: une erreur est survenue avec l'ecriture du template sql");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Erreur: erreur d'IO, demerde toi.");
            System.out.println("Tiens, la stack trace.");
            e.printStackTrace();
        }
    }
}