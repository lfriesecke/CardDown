import java.util.ArrayList;
import java.util.List;

/** Represents simple LearningCard composed of title and back side. */
public class SimpleCard extends LearningCard {
    private final List<ContentElement> backContent;

    public SimpleCard(HeadingElement heading, List<ContentElement> backContent) {
        this.heading = heading;
        this.backContent = backContent;
    }

    @Override
    public List<String> getFrontContent() {
        List<String> res = new ArrayList<>();
        res.add(heading.toString());
        return res;
    }

    @Override
    protected List<ContentElement> getFrontContentElements() {
        List<ContentElement> frontContent = new ArrayList<>();
        frontContent.add(this.heading);
        return frontContent;
    }

    @Override
    public List<String> getBackContent() {
        return ContentElement.listToString(backContent);
    }

    @Override
    protected List<ContentElement> getBackContentElements() {
        return backContent;
    }

    @Override
    public void extendFrontContent(ContentElement content) {
        if (! (content instanceof HeadingElement))
            throw new IllegalArgumentException("Given ContentElement has type " + content.getClass().getName() +
                    ", but should be of type 'HeadingElement'.");
        this.heading = (HeadingElement)content;
    }

    @Override
    public void extendBackContent(ContentElement content) {
        this.backContent.add(content);
    }

    @Override
    protected String getName() {
        return "SimpleCard";
    }
}