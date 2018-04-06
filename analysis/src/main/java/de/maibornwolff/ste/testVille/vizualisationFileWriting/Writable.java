package de.maibornwolff.ste.testVille.vizualisationFileWriting;
import java.util.List;

/**
 * This interface is a general visualization file(in json-Format) writer designed with the pattern <b>composite</b>.
 * For each management tool this interface can be used to write the visualization file. For example this interface
 * is used to generate the visualization file for Xray and hpAlm exports.
 *
 * This mean that for any extension of the project for another management tool this interface
 * can be easily use instead of re-implementing the generation of the visualization file.
 *
 * Children of the seem node are grouped by priority before writing. The interface also takes care of the
 * generation of unique ID's for each node.
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, TestVille, 2018
 */
public interface Writable {

    List<Writable> getWritableChildren();

    String getWritablePriority();

    String getWritableID();

    private String getCorrectedWritableName() {
        return correctString(this.getWritableName());
    }

    String getWritableName();

    String getWritableType();

    StringBuilder getWritableMetricsAsString();

    String getWritableUntranslatableFieldsAsString();

    private String produceWritableHeader() {
        String writableHeader = "";
        writableHeader = writableHeader.concat(this.produceWritablePreHeader());
        writableHeader = writableHeader.concat(this.produceNameEntry());
        writableHeader = writableHeader.concat(this.produceIDEntry());
        writableHeader = writableHeader.concat(this.produceTypeEntry());
        writableHeader = writableHeader.concat(this.producePriorityEntry());
        writableHeader = writableHeader.concat(this.getWritableUntranslatableFieldsAsString());
        return writableHeader;
    }

    private String produceWritablePreHeader() { return "{ "; }

    private String produceNameEntry() {
        return produceEntryString("name", this.getCorrectedWritableName(), true);
    }

    private String produceIDEntry() {
        return produceEntryString("id", this.getWritableID(), true);
    }

    private String producePriorityEntry() {
        return produceEntryString("priority", this.getWritablePriority(), true);
    }

    private String produceTypeEntry() {
        return produceEntryString("type", this.getWritableType(), true);
    }

    static String produceEntryString(String entryName, String entryValue, boolean needCommaAtEnd) {
        String enquotedEntryName  = placeInQuotes(correctString(entryName));
        String enquotedEntryValue = placeInQuotes(correctString(entryValue));
        return concatEntryNameAndValue(enquotedEntryName, enquotedEntryValue, needCommaAtEnd);
    }

    private static String concatEntryNameAndValue(String entryName, String entryValue, boolean needCommaAtEnd) {
        String result = entryName + ": " + entryValue;
        return needCommaAtEnd ? result + ", " : result;
    }

    private  static <A> String placeInQuotes(A str) {
        String result = (str == null) ? "" : str.toString();
        return "\"" + result + "\"";
    }


    private String produceWritableFooter() {
        return "}";
    }

    default StringBuilder produceWritableStringRepresentation() {
        StringBuilder writableAsString = new StringBuilder();

        writableAsString.append(this.produceWritableHeader());
        writableAsString.append(this.produceWritableBody());
        writableAsString.append(this.produceWritableFooter());

        return writableAsString;
    }

    private StringBuilder produceWritableBody() {
        StringBuilder writableBody = new StringBuilder();
        writableBody.append(this.getWritableMetricsAsString());
        writableBody.append(this.produceWritablePreBody());
        writableBody.append(this.produceWritableChildrenStringRepresentation());
        writableBody.append(this.produceWritablePostBody());
        return writableBody;
    }

    private StringBuilder produceWritableChildrenStringRepresentation() {
        return this.getWritableChildren()
                .stream()
                .map(Writable:: produceWritableStringRepresentation)
                .reduce(new StringBuilder(), (x, y) -> x.toString().isEmpty() ? y : x.append(", ").append(y));
    }

    private String produceWritablePreBody() {
        return ", \"children\": [";
    }

    private String produceWritablePostBody() {
        return "]";
    }

    private static String correctString(String str) {
        return str.replaceAll("[\"']", "");
    }
}