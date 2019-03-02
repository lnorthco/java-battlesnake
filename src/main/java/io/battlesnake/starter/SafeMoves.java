package io.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

public class SafeMoves {
	JsonNode moveRequest;
	
	public SafeMoves(JsonNode moveRequest)
	{
		this.moveRequest = moveRequest;
	}
}
