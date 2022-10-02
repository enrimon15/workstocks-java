package it.workstocks.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
	MALE("M"), FEMALE("F"), NOTSPECIFIED("NS");
	
	private String value;

    private Gender(String code) {
        this.value = code;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
