package enums;

public enum TaskParameters {
    ID(0),
    TYPE(1),
    NAME(2),
    STATUS(3),
    DESCRIPTION(4),
    DURATION(5),
    STARTTIME(6),
    EPIC(7);

    private final int index;

    TaskParameters(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
