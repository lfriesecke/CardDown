import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ------------------------------------------------------ //

/** Represents all possible valid card tags. */
enum Tag {
    QUESTION,
    CHOICE,
    NONE,
}


/** Represents all possible ContentElements as tags. */
enum ElementTag {
    HEADING,
    TEXT_BLOCK,
    BULLET_LIST,
    WRONG_ANSWER,
    RIGHT_ANSWER,
    EMPTY_LINE,
}

// ------------------------------------------------------ //

/** Contains the specified card tag and the content of a card using a list of Strings. */
class RawCard {
    public List<String> content;
    public Tag tag;

    public RawCard(List<String> content, Tag tag) {
        this.content = content;
        this.tag = tag;
    }

    public void appendLine(String line) {
        this.content.add(line);
    }
}


/** Contains the specified card tag and the content of a card using a list of ContentElement objects. */
class ContentCard {
    public HeadingElement heading;
    public List<ContentElement> content;
    public Tag tag;

    public ContentCard(HeadingElement heading, Tag tag) {
        this.heading = heading;
        this.content = new ArrayList<>();
        this.tag = tag;
    }

    public void appendContent(ContentElement cElement) {
        this.content.add(cElement);
    }
}

// ------------------------------------------------------ //

public class MarkdownLoader {

    /** Loads content of given file into list of strings and returns it. */
    private static List<String> importFile(String file_path) throws IOException {
        return Files.readAllLines(Paths.get(file_path));
    }

    /** Splits the given list of Strings into cards (using RawCard objects with Tag 'NONE').
     * A new card starts with a level 1 heading. */
    private static List<RawCard> splitIntoCards(List<String> content) {

        List<RawCard> output = new ArrayList<>();
        boolean readingCard = false;
        RawCard currentCard = new RawCard(new ArrayList<>(), Tag.NONE);

        // create separate list of Strings for each card:
        for (String curLine : content) {

            // special case: reach beginning of new card
            if (curLine.startsWith("# ")) {
                if (readingCard) output.add(currentCard);
                currentCard = new RawCard(new ArrayList<>(), Tag.NONE);
                currentCard.appendLine(curLine);
                readingCard = true;
            }

            // add other lines to current card until end of card is reached:
            else {
                currentCard.appendLine(curLine);
            }
        }
        if (readingCard) output.add(currentCard);

        return output;
    }

    /** Reads and matches tag of heading of each card. */
    private static List<RawCard> readCardType(List<RawCard> cards) {

        final String[] mainTagStrings = {" {QUESTION}", " {CHOICE}"};
        final Tag[] mainTags =          {Tag.QUESTION , Tag.CHOICE };

        // find corresponding tag for each given card:
        for (RawCard c : cards) {
            String heading = c.content.get(0).replaceAll("\\s+$", "");

            // match main tags:
            for (int i = 0; i < mainTags.length; i++) {
                if (heading.endsWith(mainTagStrings[i])) {
                    c.tag = mainTags[i];
                    heading = heading.substring(0, heading.length() - mainTagStrings[i].length());
                    break;
                }
            }

            c.content.set(0, heading);
        }

        return cards;
    }

    /** Matches tag of corresponding ContentElement of given String. */
    private static ElementTag getElement (String line) {
        if (line.matches("^#+ .*")) return ElementTag.HEADING;
        if (line.matches("^[-\\*+] .*")) return ElementTag.BULLET_LIST;
        if (line.startsWith("[ ] ")) return ElementTag.WRONG_ANSWER;
        if (line.startsWith("[x] ")) return ElementTag.RIGHT_ANSWER;
        if (line.equals("")) return ElementTag.EMPTY_LINE;
        return ElementTag.TEXT_BLOCK;
    }

    /** Parses given heading string into Heading ContentElement. */
    private static final String tagRegex = "\\{.*\\}$";
    private static final Pattern tagPattern = Pattern.compile(tagRegex);
    private static HeadingElement parseHeading(String line) {

        // get level of given heading:
        int level = line.indexOf(' ');

        // read and remove tag of given heading:
        line = line.substring(level + 1).trim();
        Matcher tagMatcher = tagPattern.matcher(line);
        String tag = "";
        if (tagMatcher.find()) {
            tag = line.substring(tagMatcher.start() + 1, tagMatcher.end() - 1);
            line = line.replaceAll(tagRegex, "");
        }

        return new HeadingElement(level, tag, line);
    }

    /** Parses given list of RawCards into ContentCards by splitting its list of Strings into a list of
     * ContentElements. */
    private static List<ContentCard> parseContentElements(List<RawCard> cards) {

        List<ContentCard> output = new ArrayList<>();
        ContentCard curContentCard;

        // parse RawCards into ContentCards:
        for (RawCard c : cards) {
            int lineNo = 0;

            // add first heading:
            HeadingElement heading = parseHeading(c.content.get(lineNo));
            curContentCard = new ContentCard(heading, c.tag);
            lineNo++;

            // parse remaining content elements:
            while (lineNo < c.content.size()) {

                String curLine = c.content.get(lineNo);
                ElementTag elemCurLine = getElement(curLine);

                // match element of current line:
                switch(elemCurLine) {
                    case HEADING -> {
                        curContentCard.appendContent(parseHeading(c.content.get(lineNo)));
                        lineNo++;
                    }

                    case BULLET_LIST -> {
                        List<String> bulletPoints = new ArrayList<>();
                        while (lineNo < c.content.size() && getElement(c.content.get(lineNo)) == ElementTag.BULLET_LIST) {
                            bulletPoints.add(c.content.get(lineNo).substring(2));
                            lineNo++;
                        }
                        BulletListElement bulletList = new BulletListElement(bulletPoints);
                        curContentCard.appendContent(bulletList);
                    }

                    case WRONG_ANSWER -> {
                        curContentCard.appendContent(new ChoiceElement(false, curLine.substring(4)));
                        lineNo++;
                    }

                    case RIGHT_ANSWER -> {
                        curContentCard.appendContent(new ChoiceElement(true, curLine.substring(4)));
                        lineNo++;
                    }

                    case EMPTY_LINE -> {
                        lineNo++;
                    }

                    case TEXT_BLOCK -> {
                        List<String> strList = new ArrayList<>();
                        while (lineNo < c.content.size() && getElement(c.content.get(lineNo)) == ElementTag.TEXT_BLOCK) {
                            strList.add(c.content.get(lineNo));
                            lineNo++;
                        }
                        TextBlockElement textBlock = new TextBlockElement(strList);
                        curContentCard.appendContent(textBlock);
                    }
                }
            }

            // add created card to list of cards:
            output.add(curContentCard);
        }

        return output;
    }

    /** Parses inline formatting (bold, italics, strikethrough effect, inline code) from Markdown formatting to HTML
     * using corresponding method of ContentElement objects. */
    private static void parseInlineFormatting(List<ContentCard> cards) {
        for (ContentCard cCard : cards) {
            cCard.heading.parseInlineFormatting();
            for (ContentElement cElem : cCard.content) {
                cElem.parseInlineFormatting();
            }
        }
    }

    /** Parses inline links from Markdown formatting to HTML using corresponding method of ContentElement objects. */
    private static void parseLinks(List<ContentCard> cards) {
        for (ContentCard cCard : cards) {
            cCard.heading.parseLinks();
            for (ContentElement cElem : cCard.content) {
                cElem.parseLinks();
            }
        }
    }

    /** Parses ContentCards into LearningCards using Constructor of corresponding LearningCard classes. */
    private static List<LearningCard> generateLearningCards(List<ContentCard> cards) {

        List<LearningCard> output = new ArrayList<>();

        // parse ContentCard into LearningCard:
        for (ContentCard cCard : cards) {
            LearningCard lCard = switch(cCard.tag) {
                case NONE -> new SimpleCard(cCard.heading, cCard.content);
                case QUESTION -> new QuestionCard(cCard.heading, cCard.content);
                case CHOICE -> new ChoiceCard(cCard.heading, cCard.content);
            };
            output.add(lCard);
        }

        return output;
    }

    /** Parses specified Markdown File into List of LearningCard objects. */
    public List<LearningCard> loadCardFile(String file_path) throws IOException {

        // load file:
        List<String> fileContent = importFile(file_path);

        // create RawCards:
        List<RawCard> cards = splitIntoCards(fileContent);
        readCardType(cards);

        // create ContentCards and parse inline formatting:
        List<ContentCard> contentCards = parseContentElements(cards);
        parseInlineFormatting(contentCards);
        parseLinks(contentCards);

        // create LearningCards:
        return generateLearningCards(contentCards);
    }

    public static void main(String[] args) {

        // initialize MarkdownLoader and HTMLCardGenerator:
        MarkdownLoader loader = new MarkdownLoader();
        HTMLCardGenerator generator = new HTMLCardGenerator();
        List<LearningCard> cards = new ArrayList<>();

        // load Markdown File:
        try {
            cards = loader.loadCardFile("cards.md");
        } catch (IOException ex) {
            System.err.println("Invalid file path");
            System.exit(1);
        }

        // export to HTML:
        generator.createHTMLCards(cards, "example.html");
    }
}
