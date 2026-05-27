package pe.edu.vallegrande.sigei.civicDates.domain.model.enums;

public enum CalendarStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    CalendarStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CalendarStatus fromValue(String value) {
        for (CalendarStatus status : CalendarStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid calendar status: " + value);
    }
}
