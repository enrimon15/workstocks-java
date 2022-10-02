package it.workstocks.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractType {
	FULLTIME("full_time"),PARTTIME("part_time"),CONSTRUCTIONBASE("construction_base"),INTERNSHIP("internship");
	
	private String snake;
	
	ContractType(String snake) {
		this.snake = snake;
	}
	
	@JsonValue
	public String getSnake() {
		return this.snake;
	}
}
