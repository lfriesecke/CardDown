import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** Represents block of content (e.g. heading, text block, bullet list, ...). */
public abstract class ContentElement {

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
