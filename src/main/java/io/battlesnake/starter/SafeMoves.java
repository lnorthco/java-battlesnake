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
		
		BoardSnake mySnake = GetGameInfo.GetSelf();
		
		// Edge detection x
		if (mySnake.head.x == 0)
		{
			left = false;
		}
		if (mySnake.head.x == GetGameInfo.GetBoardWidth())
		{
			right = false;
		}
		
		// Edge detection y
		if (mySnake.head.y == 0)
		{
			up = false;
		}
		if (mySnake.head.y == GetGameInfo.GetBoardWidth())
		{
			down = false;
		}
		
		// Iterate over snakes and get safe values. This happens each move when a new SafeMoves object is initialized.
		for (BoardSnake snake : GetGameInfo.GetSnakes())
		{
			// Iterate over all segments
			for (BoardSnakeSegment snakeSegment : snake.body)
			{
				// Something to the right
				if (mySnake.head.x == snakeSegment.x - 1)
				{
					right = false;
				}
				// To the left
				if (mySnake.head.x == snakeSegment.x + 1)
				{
					left = false;
				}
				
				// Something above
				if (mySnake.head.y == snakeSegment.y + 1)
				{
					up = false;
				}
				if (mySnake.head.y == snakeSegment.y - 1)
				{
					down = false;
				}
			}
		}
	}
}
