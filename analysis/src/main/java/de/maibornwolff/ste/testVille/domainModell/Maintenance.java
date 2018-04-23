package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.time.LocalDate;
import java.util.Objects;

public class Maintenance {

    private String    reporter;
    private String    assignee;
    private LocalDate createdAt;
    private LocalDate lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return Objects.equals(reporter, that.reporter) &&
                Objects.equals(assignee, that.assignee) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reporter, assignee, createdAt, lastUpdate);
    }

    public Maintenance(String reporter, String assignee, LocalDate createdAt, LocalDate lastUpdate) {
        this.reporter   = correctName(reporter);
        this.assignee   = correctName(assignee);
        this.createdAt  = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public static Maintenance getDefaultMaintenance() {
        return new Maintenance("UNKNOWN", "UNKNOWN", null, null);
    }

    @Override
    public String toString() {
        return  Writable.produceEntryString("reporter", this.reporter, true) +
                Writable.produceEntryString("assignee", this.assignee, true) +
                Writable.produceEntryString("created", dateToString(this.createdAt), true) +
                Writable.produceEntryString("updated", dateToString(this.lastUpdate), false);
    }

    private static String correctName(String name) {
        return (name == null) || (name.trim().isEmpty()) ? "UNKNOWN" : name.trim();
    }

    private static String dateToString(LocalDate date) {
        return (date == null) ? "UNKNOWN" : date.toString();
    }
}