package io.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;

public class SafeMoves {
	JsonNode moveRequest;
	GetGameInfo GetGameInfo;
	
	boolean up;
    boolean up2;
    boolean upleft;
    boolean upright;
    boolean down;
    boolean down2;
    boolean downleft;
    boolean downright;
    boolean left;
    boolean left2;
    boolean right;
	boolean right2;

	//Safety counters
	int upsafe = 3;
	int downsafe = 3;
	int leftsafe = 3;
	int rightsafe = 3;


	public SafeMoves(JsonNode moveRequest)
	{
		this.moveRequest = moveRequest;
		this.GetGameInfo = new GetGameInfo(moveRequest);
		
		// All directions are safe unless declared not so
		up = true;
		up2 = true;
		upleft = true;
		upright = true;
		down = true;
		down2 = true;
		downleft = true;
		downright = true;
		left = true;
		left2 = true;
		right = true;
		right2 = true;
		
		BoardSnake mySnake = GetGameInfo.GetSelf();
		
		// Edge detection x
		if (mySnake.head.x == 0)
		{
			left = false;
			leftsafe--;
		}
		if (mySnake.head.x == 1){
			left2 = false;
			leftsafe--;
		}
		if (mySnake.head.x == GetGameInfo.GetBoardWidth())
		{
			right = false;
			rightsafe--;
		}
		if (mySnake.head.x == GetGameInfo.GetBoardWidth() - 1){
			right2 = false;
			rightsafe--;
		}


		// corner detect.
		if (mySnake.head.x == 0 && mySnake.head.y == 0){
			upleft = false;
			leftsafe--;
			upsafe--;
		}
		if(mySnake.head.x == GetGameInfo.GetBoardWidth() && mySnake.head.y == 0){
			upright = false;
			upsafe--;
			rightsafe--;

		}
		if(mySnake.head.x == 0 && mySnake.head.y == GetGameInfo.GetBoardWidth()){
			downleft = false;
			downsafe--;
			leftsafe--;
		}
		if(mySnake.head.x == GetGameInfo.GetBoardWidth() && mySnake.head.y == GetGameInfo.GetBoardWidth()){
			downright = false;
			downsafe--;
			rightsafe--;
		}
		
		
		// Edge detection y
		if (mySnake.head.y == 0)
		{
			up = false;
			upsafe--;
		}
		if (mySnake.head.y == 1){
			up2 = false;
			upsafe--;
		}
		if (mySnake.head.y == GetGameInfo.GetBoardWidth())
		{
			down = false;
			downsafe--;
		}
		if(mySnake.head.y == GetGameInfo.GetBoardWidth()-1){
			down2 = false;
			downsafe--;
		}
		
		// Iterate over snakes and get safe values. This happens each move when a new SafeMoves object is initialized.
		for (BoardSnake snake : GetGameInfo.GetSnakes())
		{
			// Iterate over all segments
			for (BoardSnakeSegment snakeSegment : snake.body)
			{
				
				if (snakeSegment.x == mySnake.head.x && snakeSegment.y == mySnake.head.y - 1)
				{
					up = false;
				}
				if (snakeSegment.x == mySnake.head.x && snakeSegment.y == mySnake.head.y + 1)
				{
					down = false;
				}
				
				if (snakeSegment.x == mySnake.head.x - 1 && snakeSegment.y == mySnake.head.y)
				{
					left = false;
				}
				if (snakeSegment.x == mySnake.head.x + 1 && snakeSegment.y == mySnake.head.y)
				{
					right = false;
				}
			}
		}
	}
}
