package de.maibornwolff.ste.tesla.inputFileParsing.jiraXRAY;

public enum JiraXrayParsingState {
    START,
    IN_ITEM_TAG,
    IN_CUSTOMFIELDS_TAG,
    IN_CUSTOMFIELD_TAG,
    FIELD_NAME_EXTRACTION,
    FIELD_VALUE_EXTRACTION
}