package io.battlesnake.starter;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

public class GetGameInfo {
	JsonNode moveRequest;
	// moveRequest is a json with all info as on the docs.
	public GetGameInfo(JsonNode moveRequest)
	{
		this.moveRequest = moveRequest;
	}

	public int GetBoardHeight()
	{
		return moveRequest.get("board").get("height").intValue() - 1;
	}
	
	public int GetBoardWidth()
	{
		return moveRequest.get("board").get("width").intValue() - 1;
	}
	
	public BoardSnake GetSelf()
	{
		// Make a snake here, using info from moveRequest.
		return new BoardSnake(moveRequest.get("you"));
	}
	
	public int GetRound()
	{
		return moveRequest.get("turn").intValue();
	}
	
	public ArrayList<BoardSnake> GetSnakes()
	{
		ArrayList<BoardSnake> SnakeList = new ArrayList<BoardSnake>();
		
		JsonNode jsnAllSnakes = moveRequest.get("board").get("snakes");
		
		for (int i = 0; i < jsnAllSnakes.size(); i++)
		{
			SnakeList.add(new BoardSnake(jsnAllSnakes.get(i)));
		}
		
		return SnakeList;
	}
}
