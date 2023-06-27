import java.util.ArrayList;
import java.util.List;

/** ContentElement representing text block. */
public class TextBlockElement extends ContentElement {

    private List<String> text;

    public TextBlockElement(List<String> text) {
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
        if (text.size() == 0) {
            return List.of("<p></p>");
        }

        List<String> output = new ArrayList<>();
        output.add("<p>");
        for (int i = 0; i < text.size() - 1; i++) {
            output.add(text.get(i) + "<br>");
        }
        output.add(text.get(text.size() - 1));
        output.add("</p>");

        return output;
    }

    @Override
    public String toAnki() {
        StringBuilder output = new StringBuilder();
        for (String line : this.toHTML()) {
            output.append(line);
        }
        return output.toString();
    }

    @Override
    public String toString() {
        if (text.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String elem : text) {
            builder.append(elem);
            builder.append('\n');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public ElementTag getElementTag() {
        return ElementTag.TEXT_BLOCK;
    }
}

