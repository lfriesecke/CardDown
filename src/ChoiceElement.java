import java.util.List;


/** ContentElement representing ONE multiple choice answer and if it is correct or not. */
public class ChoiceElement extends ContentElement {

    private static int numIDs = 0;

    private final boolean isCorrect;

    private String text;

    public ChoiceElement(boolean isCorrect, String text) {
        this.isCorrect = isCorrect;
        this.text = text;
    }

    @Override
    public void parseInlineFormatting() {
        text = parseInlineFormatting(text);
    }

    @Override
    public void parseLinks() {
        text = parseLinks(text);
    }

    @Override
    public List<String> toHTML() {
        numIDs++;
        return List.of(
                String.format("<input type=\"checkbox\" id=\"cElem%d\">", numIDs),
                String.format("<label for=\"cElem%d\"> %s</label><br>", numIDs, text)
        );
    }

    @Override
    public String toString() {
        return (isCorrect ? "[x] " : "[ ] ") + text;
    }

    @Override
    public ElementTag getElementTag() {
        return isCorrect ? ElementTag.RIGHT_ANSWER : ElementTag.WRONG_ANSWER;
    }
}
