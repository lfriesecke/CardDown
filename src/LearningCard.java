import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------------------------------ //

/** Represents block of content (e.g. heading, text block, bullet list, ...). */
abstract class ContentElement {

    /** Replaces inline Markdown formatting with HTML tags. */
    public abstract void parseInlineFormatting();

    /** Replaces Markdown links with HTML tags. */
    public abstract void parseLinks();

    /** Returns HTML representation of content element. */
    public abstract List<String> toHTML();

    /** Returns simple String representation of given ContentElement. */
    @Override
    public abstract String toString();

    /** Returns corresponding ElementTag of ContentElement. */
    public abstract ElementTag getElementTag();

    /** Helper method to convert given list of ContentElements to List of Strings using toString() method implemented
     * by ContentElement objects. */
    protected static List<String> listToString(List<ContentElement> cElements) {
        List<String> res = new ArrayList<>();
        for (ContentElement cElem : cElements) {
            res.add(cElem.toString());
        }
        return res;
    }

    /** Helper methods to parse inline Markdown formatting to HTML. */
    private static String replaceInlineFormatting(String str, String mdTag, String regex, String htmlTag) {

        // add characters to account for formatting at beginning / end of line:
        String strMod = "[" + str + "]";

        // split line by using given regex pattern:
        String[] splits = strMod.split(regex);

        // special case: no reformatting needed:
        if (splits.length <= 2) return str;

        // merge splits using given htmlTags:
        StringBuilder strB = new StringBuilder();
        String htmlOpen = "<" + htmlTag + ">";
        String htmlClose = "</" + htmlTag + ">";
        for (int i = 0; i + 2 < splits.length; i+=2) {
            strB.append(splits[i]);
            strB.append(htmlOpen);
            strB.append(splits[i+1]);
            strB.append(htmlClose);
        }

        // append last string sequences:
        if (splits.length % 2 == 0) {
            strB.append(splits[splits.length - 2]);
            strB.append(mdTag);
        }
        strB.append(splits[splits.length - 1]);

        return strB.substring(1, strB.length() - 1);
    }
    private static final String[][] FORMATS = {
            {"**", "\\*\\*", "b"},
            {"*", "\\*", "em"},
            {"~~", "~~", "s"},
            {"``", "``", "code"},
            {"`", "`", "code"},
    };
    protected static String parseInlineFormatting(String line) {

        // replace inline formatting using all specified formats:
        for (String[] curFormat : FORMATS) {
            line = replaceInlineFormatting(line, curFormat[0], curFormat[1], curFormat[2]);
        }

        return line;
    }
    protected static List<String> parseInlineFormatting(List<String> lines) {
        List<String> output = new ArrayList<>();
        for (String curLine : lines) {
            output.add(parseInlineFormatting(curLine));
        }
        return output;
    }

    /** Helper method to parse inline Markdown links to HTML. */
    private static final Pattern[] linkPatterns = {
            Pattern.compile("\\[\\]\\(.*\\)"),
            Pattern.compile("\\[.]\\]\\(.*\\)"),
            Pattern.compile("\\[..*.\\]\\(.*\\)"),
    };
    protected static String parseLinks(String line) {
        for (Pattern pat : linkPatterns) {
            Matcher matcher = pat.matcher(line);
            while (matcher.find()) {
                String linkString = line.substring(matcher.start(), matcher.end());
                String linkText = linkString.substring(1, linkString.indexOf(']'));
                String link = linkString.substring(linkString.indexOf(']') + 2, linkString.length() - 1);
                line = line.substring(0, matcher.start())
                        + "<a href=\"" + link + "\">" + linkText + "</a>"
                        + line.substring(matcher.end());
                matcher = pat.matcher(line);
            }
        }
        return line;
    }
    protected static List<String> parseLinks(List<String> lines) {
        List<String> output = new ArrayList<>();
        for (String curLine : lines) {
            output.add(parseLinks(curLine));
        }
        return output;
    }

    /** Helper method to split list of ContentElement objects into front and back elements. */
    public static List<List<ContentElement>> splitFrontBack(List<ContentElement> elements) {

        List<ContentElement> front = new ArrayList<>();
        List<ContentElement> back = new ArrayList<>();
        boolean isFront = true;

        // split ContentElements:
        for (ContentElement curElem : elements) {

            // check if sides should be switched:
            String strRep = curElem.toString().replaceAll("\\s+$", "");
            if (curElem.getElementTag() == ElementTag.HEADING) {
                if (strRep.endsWith(" {FRONT}")) {
                    isFront = true;
                }
                else if (strRep.endsWith(" {BACK}")) {
                    isFront = false;
                }
            }

            // add current element to current side:
            if (isFront) front.add(curElem);
            else back.add(curElem);
        }

        // return result:
        List<List<ContentElement>> output = new ArrayList<>();
        output.add(front);
        output.add(back);
        return output;
    }
}

/** ContentElement representing heading. */
class HeadingElement extends ContentElement {

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


/** ContentElement representing text block. */
class TextBlockElement extends ContentElement {

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


/** ContentElement representing unordered list of items. */
class BulletListElement extends ContentElement {

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


/** ContentElement representing ONE multiple choice answer and if it is correct or not. */
class ChoiceElement extends ContentElement {

    private static int numIDs = 0;

    private final boolean isCorrect;

    private String text;

    public ChoiceElement(boolean isCorrect, String text) {
        this.isCorrect = isCorrect;
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
        numIDs++;
        return List.of(
                String.format("<input type=\"checkbox\" id=\"cElem%d\">", numIDs),
                String.format("<label for=\"cElem%d\"> %s</label><br>", numIDs, text)
        );
    }

    @Override
    public String toString() {
        return (isCorrect ? "[x] " : "[ ] ") + text;
    }

    @Override
    public ElementTag getElementTag() {
        return isCorrect ? ElementTag.RIGHT_ANSWER : ElementTag.WRONG_ANSWER;
    }
}

// ------------------------------------------------------ //

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

    /** Returns the card with HTML formatting. */
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

    /** Writes the content to the console. */
    public void printToConsole() {
        for (String curLine : this.getContent()) {
            System.out.println(curLine);
        }
    }

    /** Returns name of corresponding LearningCard. */
    protected abstract String getName();
}


/** Represents simple LearningCard composed of title and back side. */
class SimpleCard extends LearningCard {
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


/** Represents LearningCard composed of title, front side and back side. */
class QuestionCard extends LearningCard {
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


/** Represents LearningCard composed of title and list of content elements containing ChoiceElements. */
class ChoiceCard extends LearningCard {
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
