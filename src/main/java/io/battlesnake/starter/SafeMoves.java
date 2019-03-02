package io.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

public class SafeMoves {
	JsonNode moveRequest;
	GetGameInfo GetGameInfo;
	
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	
	public SafeMoves(JsonNode moveRequest)
	{
		this.moveRequest = moveRequest;
		this.GetGameInfo = new GetGameInfo(moveRequest);
		
		// All directions are safe unless declared not so
		up = true;
		down = true;
		left = true;
		right = true;
		
		// Iterate over snakes and get safe values. This happens each move when a new SafeMoves object is initialized.
		for (BoardSnake snake : GetGameInfo.GetSnakes())
		{
			BoardSnake mySnake = GetGameInfo.GetSelf();
			
			// Edge detection x
			if (mySnake.head.x == 0)
			{
				left = false;
			}
			if (mySnake.head.x == GetGameInfo.GetBoardSize())
			{
				right = false;
			}
			
			// Edge detection y
			if (mySnake.head.y == 0)
			{
				up = false;
			}
			if (mySnake.head.y == GetGameInfo.GetBoardSize())
			{
				down = false;
			}
		}
	}
}
