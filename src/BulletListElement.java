import java.util.ArrayList;
import java.util.List;


/** ContentElement representing unordered list of items. */
public class BulletListElement extends ContentElement {

    private List<String> bulletPoints;

    public BulletListElement(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    @Override
    public void parseInlineFormatting() {
        bulletPoints = parseInlineFormatting(bulletPoints);
    }

    @Override
    public void parseLinks() {
        bulletPoints = parseLinks(bulletPoints);
    }

    @Override
    public List<String> toHTML() {
        List<String> output = new ArrayList<>();
        output.add("<ul>");

        for (String point : bulletPoints) {
            output.add(String.format("  <li>%s</li>", point));
        }

        output.add("</ul>");
        return output;
    }

    @Override
    public String toString() {
        if (bulletPoints.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String bPoint : bulletPoints) {
            builder.append(bPoint);
            builder.append('\n');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public ElementTag getElementTag() {
        return ElementTag.BULLET_LIST;
    }
}
