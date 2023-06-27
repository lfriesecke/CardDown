import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/** Represents object to export LearningCards to HTML file suitable for importing into Anki. */
public class AnkiCardGenerator extends CardGenerator {

    /** Creates txt file containing given list of LearningCards suitable for anki imports. */
    @Override
    public void exportCards(List<LearningCard> cards, String output_file) {

        // create output file:
        Path filePath = Paths.get(output_file);
        try {
            Files.createFile(filePath);
        } catch (FileAlreadyExistsException ex) {
            System.err.println("A file with the given name already exists.");
            return;
        } catch (IOException ex) {
            System.err.println("The given file name is invalid.");
            return;
        }

        // generate header:
        List<String> output = new ArrayList<>();
        output.add("#seperator=;");
        output.add("#html=true");
        output.add("#columns=Front;Back");
        output.add("#notetype=Basic");
        output.add("");

        // generate learning cards:
        for (LearningCard c :cards) {
            output.add(c.getContentAsAnki());
        }

        // write content to file:
        try {
            Files.write(filePath, output, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println("There was an error creating the file.");
        }
    }
}
