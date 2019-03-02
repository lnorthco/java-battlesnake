package io.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

public class BoardSnake {
	public String id;
	public String name;
	public int health;
	public List<BoardSnakeSegment> body = new ArrayList<BoardSnakeSegment>();
	
	public BoardSnake(JsonNode entity){
		id = entity.get("id").asText();
		name = entity.get("name").asText();
		health = entity.get("health").intValue();
		int snakeSize = entity.get("body").size();
		
		
		for(int i=0; i<snakeSize ; i++){
			int snakeBodyX = entity.get("body").get(i).get("x").intValue();
			int snakeBodyY = entity.get("body").get(i).get("y").intValue();
			body.add(new BoardSnakeSegment(snakeBodyX, snakeBodyY));
			
		}

}
