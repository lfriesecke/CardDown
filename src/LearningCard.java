import java.util.ArrayList;
import java.util.List;


/** Represents learning card (e.g. Question Card with back and front side or Multiple Choice Card). */
public abstract class LearningCard {

    protected HeadingElement heading;

    /** Returns the front site of the card. */
    public abstract List<String> getFrontContent();

    /** Returns the front side of the card as ContentElement objects. */
    protected abstract List<ContentElement> getFrontContentElements();

    /** Returns the back site of the card. */
    public abstract List<String> getBackContent();

    /** Returns the back side of the card as ContentElement objects. */
    protected abstract List<ContentElement> getBackContentElements();

    /** Appends ContentElement to front side. */
    public abstract void extendFrontContent(ContentElement content);

    /** Appends multiple ContentElements to front site. */
    public void extendFrontContent(List<ContentElement> cElements) {
        for (ContentElement cElem : cElements) {
            this.extendFrontContent(cElem);
        }
    }

    /** Appends ContentElement to back site. */
    public abstract void extendBackContent(ContentElement content);

    /** Appends multiple ContentElements to back site. */
    public void extendBackContent(List<ContentElement> cElements) {
        for (ContentElement cElem : cElements) {
            this.extendBackContent(cElem);
        }
    }

    /** Returns combined content of front and back. */
    public List<String> getContent() {
        List<String> res = new ArrayList<>();

        res.add("Type: " + this.getName());
        res.add("");
        res.add("FrontContent:");
        res.addAll(this.getFrontContent());
        res.add("");
        res.add("BackContent:");
        res.addAll(this.getBackContent());

        return res;
    }

    /** Returns HTML representation of card. */
    public List<String> getContentAsHTML() {
        List<String> output = new ArrayList<>();
        for (ContentElement cElem : getFrontContentElements()) {
            output.addAll(cElem.toHTML());
        }
        for (ContentElement cElem : getBackContentElements()) {
            output.addAll(cElem.toHTML());
        }
        return output;
    }

    /** Returns anki representation of card. */
    public String getContentAsAnki() {
        StringBuilder output = new StringBuilder();
        output.append("\"");
        for (ContentElement cElem : getFrontContentElements()) {
            output.append(cElem.toAnki());
        }
        output.append("\";\"");
        for (ContentElement cElem : getBackContentElements()) {
            output.append(cElem.toAnki());
        }
        output.append("\"");
        return output.toString();
    }

    /** Writes the content to the console. */
    public void printToConsole() {
        for (String curLine : this.getContent()) {
            System.out.println(curLine);
        }
    }

    /** Returns name of corresponding LearningCard. */
    protected abstract String getName();
}
