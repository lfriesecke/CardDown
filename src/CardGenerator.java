import java.util.List;


/** Represents class of objects used for exporting LearningCards into different file formats. */
public abstract class CardGenerator {
    public abstract void exportCards(List<LearningCard> cards, String output_file);
}
