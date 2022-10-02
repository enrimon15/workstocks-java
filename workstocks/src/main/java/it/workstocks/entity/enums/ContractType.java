package it.workstocks.entity.enums;

public enum ContractType {
	FULLTIME("full_time"),PARTTIME("part_time"),CONSTRUCTIONBASE("construction_base"),INTERNSHIP("internship");
	
	private String snake;
	
	ContractType(String snake) {
		this.snake = snake;
	}
	
	public String getSnake() {
		return this.snake;
	}
}
