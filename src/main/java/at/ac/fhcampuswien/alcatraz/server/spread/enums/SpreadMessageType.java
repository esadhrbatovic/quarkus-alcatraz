package at.ac.fhcampuswien.alcatraz.server.spread.enums;

public enum SpreadMessageType {
    SYNC((short) 1);

    private final short numVal;

    SpreadMessageType(short numVal) {
        this.numVal = numVal;
    }

    public short getNumVal() {
        return numVal;
    }

    public static SpreadMessageType valueOfLabel(short label) {
        for (SpreadMessageType e : values()) {
            if (e.numVal == label) {
                return e;
            }
        }
        return null;
    }
}
