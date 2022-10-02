package it.workstocks.entity.enums;

public enum Gender {
	MALE("M"), FEMALE("F"), NOTSPECIFIED("NS");
	
	private String value;

    private Gender(String code) {
        this.value = code;
    }

    public String getValue() {
        return value;
    }
}
