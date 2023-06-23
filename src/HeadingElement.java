import java.util.List;


/** ContentElement representing heading. */
public class HeadingElement extends ContentElement {

    private final int level;

    private final String tag;

    private String text = "";

    public HeadingElement(int level, String tag, String text) {
        this.level = level;
        this.tag = tag;
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
        return List.of(String.format("<h%d>%s</h%d>", level, text, level));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("#".repeat(level));
        builder.append(' ');
        builder.append(text);
        return builder.toString();
    }

    @Override
    public ElementTag getElementTag() {
        return ElementTag.HEADING;
    }
}
