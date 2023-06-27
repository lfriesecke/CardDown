import java.util.ArrayList;
import java.util.List;


/** ContentElement representing unordered list of items. */
public class BulletListElement extends ListElement {

    public BulletListElement(List<String> bulletPoints) {
        super(bulletPoints);
    }

    @Override
    public List<String> toHTML() {
        List<String> output = new ArrayList<>();
        output.add("<ul>");

        for (String point : points) {
            output.add(String.format("<li>%s</li>", point));
        }

        output.add("</ul>");
        return output;
    }

    @Override
    public ElementTag getElementTag() {
        return ElementTag.BULLET_LIST;
    }
}
