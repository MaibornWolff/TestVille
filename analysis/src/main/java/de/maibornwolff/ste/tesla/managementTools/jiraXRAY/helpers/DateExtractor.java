package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.helpers;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class DateExtractor {

    private static final String dd   = "\\s*\\d{2}";
    private static final String mm   = "\\s*[a-zA-Z]{3}";
    private static final String yyyy = "\\s*\\d{4}";

    private static LocalDate translateStringToLocalDate(String dateAsString) {
        LocalDate result;
        return   (result = simplifyDateStringDDMMYYYYToLocateDate(dateAsString)) != null ? result
                :(result = simplifyDateStringMMDDYYYYToLocateDate(dateAsString)) != null ? result
                :(result = simplifyDateStringYYYYDDMMToLocateDate(dateAsString)) != null ? result
                :(result = simplifyDateStringYYYYMMDDToLocateDate(dateAsString)) != null ? result
                : null
        ;
    }

    private static LocalDate translateStringToLocalDate(String dd, String mm, String yy) {
        try {
            int dayOfMonth = Integer.valueOf(dd);
            int month      = monthTranslation(mm);
            int year       = Integer.valueOf(yy);
            return LocalDate.of(year, month, dayOfMonth);
        } catch (NumberFormatException | DateTimeException ex) {
            return null;
        }
    }

    private static LocalDate simplifyDateStringDDMMYYYYToLocateDate(String dateAsString) {
        String [] ddmmyy = buildPatternAndMatch(dateAsString, dd, mm, yyyy);
        return ddmmyy == null ? null : translateStringToLocalDate(ddmmyy[0], ddmmyy[1], ddmmyy [2]);
    }

    private static LocalDate simplifyDateStringMMDDYYYYToLocateDate(String dateAsString) {
        String [] mmddyy = buildPatternAndMatch(dateAsString, mm, dd, yyyy);
        return mmddyy == null ? null : translateStringToLocalDate(mmddyy[1], mmddyy[0] ,mmddyy [2]);
    }

    private static LocalDate simplifyDateStringYYYYMMDDToLocateDate(String dateAsString) {
        String [] yymmdd = buildPatternAndMatch(dateAsString, yyyy, mm, dd);
        return yymmdd == null ? null : translateStringToLocalDate(yymmdd[2], yymmdd[1], yymmdd [0]);
    }

    private static LocalDate simplifyDateStringYYYYDDMMToLocateDate(String dateAsString) {
        String [] yyddmm = buildPatternAndMatch(dateAsString, yyyy, dd, mm);
        return yyddmm == null ? null : translateStringToLocalDate(yyddmm[1], yyddmm[2] ,yyddmm[0]);
    }

    private static String matchesPattern(String containsMaybeDate, Pattern patternToBeMatch) {
        Matcher matcher  = patternToBeMatch.matcher(containsMaybeDate);
        Stream<MatchResult> matchedDates           = matcher.results();
        Iterator<MatchResult> matchedDatesIterator = matchedDates.iterator();
        return matchedDatesIterator.hasNext() ? matchedDatesIterator.next().group().trim() : null;
    }

    private static String [] buildPatternAndMatch(String containsMaybeDate, String ... xs) {
        Pattern datePattern = buildPatternFrom(xs);
        String matchedResult = matchesPattern(containsMaybeDate, datePattern);
        return splitDDMMJJJJ(matchedResult);
    }

    private static String [] splitDDMMJJJJ(String dateAsString) {
        if(dateAsString == null) return null;

        if(dateAsString.contains("."))      return splitDDMMJJJJByDot(dateAsString);
        else if(dateAsString.contains(" ")) return splitDDMMJJJJBySpace(dateAsString);
        else if(dateAsString.contains("/")) return splitDDMMJJJJBySlash(dateAsString);
        else if(dateAsString.contains("-")) return splitDDMMJJJJByHyphen(dateAsString);
        return null;
    }

    private static String[] splitDDMMJJJJBySeparator(String str, String separator) {
        return str.split(separator);
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

    private static Pattern buildPatternFrom(String ... xs) {
        String result = "";
        for (String str: xs) {
            result = result.concat(str);
        }
        return Pattern.compile(result);
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


    public static double translateDateStringToMetric(String dateAsString) {
        LocalDate localDate = translateStringToLocalDate(dateAsString);
        return translateDateToMetric(localDate);
    }

    private static double translateDateToMetric(LocalDate localDate){
        if(localDate == null) return 0;
        Period period = computePeriodToNow(localDate);
        return translatePeriodToMetric(period);
    }

    private static Period computePeriodToNow(LocalDate localDate) {
        return localDate.until(LocalDate.now());
    }

    private static double translatePeriodToMetric(Period period) {
        int years  =  period.getYears();
        int days   =  period.getDays();
        int month =   period.getMonths();
        return (days + month*30 + years*365) / 49.0;
    }
}