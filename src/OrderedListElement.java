import java.util.ArrayList;
import java.util.List;

/** ContentElement representing ordered (i.e. numbered) list of items. */
public class OrderedListElement extends ListElement {

    public OrderedListElement(List<String> items) {
        super(items);
    }

    @Override
    public List<String> toHTML() {
        List<String> output = new ArrayList<>();
        output.add("<ol>");

        for (String point : points) {
            output.add(String.format("<li>%s</li>", point));
        }

        output.add("</ol>");
        return output;
    }

    @Override
    public ElementTag getElementTag() {
        return ElementTag.ORDERED_LIST;
    }
}
