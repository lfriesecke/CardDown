import java.util.List;

public abstract class ListElement extends ContentElement {

    protected List<String> points;

    public ListElement(List<String> points) {
        this.points = points;
    }

    @Override
    public void parseInlineFormatting() {
        points = parseInlineFormatting(points);
    }

    @Override
    public void parseLinks() {
        points = parseLinks(points);
    }

    @Override
    public String toString() {
        if (points.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String bPoint : points) {
            builder.append(bPoint);
            builder.append('\n');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
