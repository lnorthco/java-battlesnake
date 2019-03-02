package io.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

public class GetGameInfo {
	JsonNode moveRequest;
	
	public GetGameInfo(JsonNode moveRequest)
	{
		this.moveRequest = moveRequest;
	}

	public int GetBoardSize()
	{
		return moveRequest.get("board").get("height").intValue() - 1; // Height or width is irrelevant; it's a square arena
	}
	
	public BoardSnake GetSelf()
	{
		// Make a snake here, using info from moveRequest.
		return new BoardSnake(moveRequest.get("you"));
	}
}
