import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/** Represents LearningCard composed of title and list of content elements containing ChoiceElements. */
public class ChoiceCard extends LearningCard {
    private final List<ContentElement> content = new ArrayList<>();

    public ChoiceCard(HeadingElement heading, List<ContentElement> content) {
        this.heading = heading;
        this.content.addAll(content);
    }

    public List<ContentElement> getChoiceElements() {
        return content.stream().filter(
                c -> c.getElementTag() == ElementTag.RIGHT_ANSWER || c.getElementTag() == ElementTag.WRONG_ANSWER
        ).collect(Collectors.toList());
    }

    @Override
    public List<String> getFrontContent() {
        List<String> res = new ArrayList<>();
        res.add(heading.toString());
        res.addAll(ContentElement.listToString(this.getChoiceElements()));
        return res;
    }

    @Override
    protected List<ContentElement> getFrontContentElements() {
        List<ContentElement> frontContent = new ArrayList<>();
        frontContent.add(heading);
        frontContent.addAll(this.getChoiceElements());
        return frontContent;
    }

    @Override
    public List<String> getBackContent() {
        return ContentElement.listToString(content);
    }

    @Override
    protected List<ContentElement> getBackContentElements() {
        return content;
    }

    @Override
    public void extendFrontContent(ContentElement content) {
        this.content.add(content);
    }

    @Override
    public void extendBackContent(ContentElement content) {
        this.content.add(content);
    }

    @Override
    protected String getName() {
        return "ChoiceCard";
    }
}
