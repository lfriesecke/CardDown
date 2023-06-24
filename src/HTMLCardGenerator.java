import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


/** Represents object to export LearningCards to HTML file. */
public class HTMLCardGenerator extends CardGenerator {

    /** Creates HTML file containing given list of LearningCards. */
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

        // generate HTML content:
        List<String> output = new ArrayList<>();
        output.add("<html lang=\"de\">");
        output.add("<head>");
        output.add("  <meta http-equiv=\"content-type\" content=\"text/html\" charset=\"utf-8\">");
        output.add("</head>");
        output.add("");

        output.add("<body>");
        for (LearningCard c : cards) {
            output.addAll(c.getContentAsHTML());
            output.add("<br>");
        }
        output.add("</body>");
        output.add("</html>");

        // write content to file:
        try {
            Files.write(filePath, output, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println("There was an error creating the file.");
        }
    }
}
