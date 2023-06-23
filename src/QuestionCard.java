import java.util.ArrayList;
import java.util.List;

/** Represents LearningCard composed of title, front side and back side. */
public class QuestionCard extends LearningCard {
    private final List<ContentElement> frontContent = new ArrayList<>();
    private final List<ContentElement> backContent = new ArrayList<>();

    public QuestionCard(HeadingElement heading, List<ContentElement> content) {
        List<List<ContentElement>> frontBackContent = ContentElement.splitFrontBack(content);

        this.heading = heading;
        frontContent.addAll(frontBackContent.get(0));
        backContent.addAll(frontBackContent.get(1));
    }

    @Override
    public List<String> getFrontContent() {
        List<String> output = new ArrayList<>();
        output.add(heading.toString());
        output.addAll(ContentElement.listToString(frontContent));
        return output;
    }

    @Override
    protected List<ContentElement> getFrontContentElements() {
        List<ContentElement> output = new ArrayList<>();
        output.add(heading);
        output.addAll(frontContent);
        return output;
    }

    @Override
    public List<String> getBackContent() {
        return ContentElement.listToString(frontContent);
    }

    @Override
    public List<ContentElement> getBackContentElements() {
        return backContent;
    }

    @Override
    public void extendFrontContent(ContentElement content) {
        this.frontContent.add(content);
    }

    @Override
    public void extendBackContent(ContentElement content) {
        this.backContent.add(content);
    }

    @Override
    protected String getName() {
        return "QuestionCard";
    }
}

