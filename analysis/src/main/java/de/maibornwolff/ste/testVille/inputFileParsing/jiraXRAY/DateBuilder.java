package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class DateBuilder {

    private static  String dd   = "\\s*\\d{1,2}";
    private static  String mm   = "\\s*[a-zA-Z]{3,9}";
    private static  String yyyy = "\\s*\\d{4}";
    public String buildFrom;

    public DateBuilder(String buildFrom) {
        this.buildFrom = buildFrom;
    }

    public LocalDate buildLocalDate() {
        if(this.buildFrom == null) return null;
        LocalDate result;
        return   (result = toLocaleDateWithFormatDDMMYYYY()) != null ? result
                :(result = toLocateDateWithFormatMMDDYYYY()) != null ? result
                :(result = toLocateDateWithFormatYYYYMMDD()) != null ? result
                :(result = toLocateDateWithFormatYYYYDDMM()) != null ? result
                : null
        ;
    }

    public double computeMetric() {
        LocalDate localDate = this.buildLocalDate();
        return transformDateToMetric(localDate);
    }

    public boolean containsValidDate() {
        return this.buildLocalDate() != null;
    }
    
    private static double transformDateToMetric(LocalDate localDate){
        if(localDate == null) return 0;
        Period period = computePeriodFromNowTo(localDate);
        return transformPeriodToMetric(period);
    }
    
    private static Period computePeriodFromNowTo(LocalDate localDate) {
        return localDate.until(LocalDate.now());
    }

    private static double transformPeriodToMetric(Period period) {
        int years  =  period.getYears();
        int days   =  period.getDays();
        int month =   period.getMonths();
        return (days + month*30 + years*365) / 49.0;
    }
    
    private LocalDate toLocaleDateWithFormatDDMMYYYY() {
        String [] ddmmyy = buildPatternAndMatch(dd, mm, yyyy);
        return ddmmyy == null ? null : buildLocalDateFrom(ddmmyy[0], ddmmyy[1], ddmmyy [2]);
    }

    private LocalDate toLocateDateWithFormatMMDDYYYY() {
        String [] mmddyy = buildPatternAndMatch(mm, dd, yyyy);
        return mmddyy == null ? null : buildLocalDateFrom(mmddyy[1], mmddyy[0] ,mmddyy [2]);
    }

    private LocalDate toLocateDateWithFormatYYYYMMDD() {
        String [] yymmdd = buildPatternAndMatch(yyyy, mm, dd);
        return yymmdd == null ? null : buildLocalDateFrom(yymmdd[2], yymmdd[1], yymmdd [0]);
    }

    private LocalDate toLocateDateWithFormatYYYYDDMM() {
        String [] yyddmm = buildPatternAndMatch(yyyy, dd, mm);
        return yyddmm == null ? null : buildLocalDateFrom(yyddmm[1], yyddmm[2] ,yyddmm[0]);
    }

    private String [] buildPatternAndMatch(String ... xs) {
        Pattern datePattern = buildPatternFrom(xs);
        String matchedResult = matchPattern(datePattern);
        return splitDDMMJJJJ(matchedResult);
    }

    private static LocalDate buildLocalDateFrom(String dd, String mm, String yy) {
        try {
            int dayOfMonth = Integer.valueOf(dd);
            int month      = monthTranslation(mm);
            int year       = Integer.valueOf(yy);
            return LocalDate.of(year, month, dayOfMonth);
        } catch (NumberFormatException | DateTimeException ex) {
            return null;
        }
    }

    private String matchPattern(Pattern patternToMatch) {
        Matcher matcher  = patternToMatch.matcher(this.buildFrom);
        Stream<MatchResult> matchedDates           = matcher.results();
        Iterator<MatchResult> matchedDatesIterator = matchedDates.iterator();
        return matchedDatesIterator.hasNext() ? matchedDatesIterator.next().group().trim() : null;
    }


    private static Pattern buildPatternFrom(String ... xs) {
        String result = "";
        for (String str: xs) {
            result = result.concat(str);
        }
        return Pattern.compile(result);
    }

    private static String [] splitDDMMJJJJ(String dateAsString) {
        if(dateAsString == null) return null;

        if(dateAsString.contains("."))      return splitDDMMJJJJByDot(dateAsString);
        else if(dateAsString.contains(" ")) return splitDDMMJJJJBySpace(dateAsString);
        else if(dateAsString.contains("/")) return splitDDMMJJJJBySlash(dateAsString);
        else if(dateAsString.contains("-")) return splitDDMMJJJJByHyphen(dateAsString);
        return null;
    }


    private static String[] splitDDMMJJJJByDot(String str) {
        String [] result = splitDDMMJJJJBySeparator(str, ".");
        return result.length == 3 ? result : null;
    }

    private static String[] splitDDMMJJJJByHyphen(String str) {
        String [] result = splitDDMMJJJJBySeparator(str, "-");
        return result.length == 3 ? result : null;
    }

    private static String[] splitDDMMJJJJBySpace(String str) {
        String [] result = splitDDMMJJJJBySeparator(str, "\\s+");
        return result.length == 3 ? result : null;
    }

    private static String[] splitDDMMJJJJBySlash(String str) {
        String [] result = splitDDMMJJJJBySeparator(str, "/");
        return result.length == 3 ? result : null;
    }

    private static String[] splitDDMMJJJJBySeparator(String str, String separator) {
        return str.split(separator);
    }

    private static int monthTranslation(String monthAsString) {
        List<String> allMonth = List.of("january", "february", "march", "april", "may", "june",
                "july", "august", "september", "october", "november", "december")
        ;

        for (String str: allMonth) {
            if(str.startsWith(monthAsString.toLowerCase()) || monthAsString.toLowerCase().equals(str)){
                return allMonth.indexOf(str)+1;
            }
        }
        return -1;
    }

}