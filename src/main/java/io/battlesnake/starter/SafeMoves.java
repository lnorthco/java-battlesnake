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
	int upsafe;
	int downsafe;
	int leftsafe;
	int rightsafe;


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
		
		
		//Higher values mean safer moves
		upsafe = 4;
		downsafe = 4;
		leftsafe = 4;
		rightsafe = 4;
		
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
					upsafe--;
				}
				if (snakeSegment.x == mySnake.head.x && snakeSegment.y == mySnake.head.y - 2)
				{
					up2 = false;
					upsafe--;
				}		
				
				if (snakeSegment.x == mySnake.head.x - 1 && snakeSegment.y == mySnake.head.y - 1)
				{
					upleft = false;
					upsafe--;
					leftsafe--;
				}

				if (snakeSegment.x == mySnake.head.x + 1  && snakeSegment.y == mySnake.head.y - 1)
				{
					upright = false;
					upsafe--;
					rightsafe--;
				}

				if (snakeSegment.x == mySnake.head.x && snakeSegment.y == mySnake.head.y + 1)
				{
					down = false;
					downsafe--;
				}
				
				if (snakeSegment.x == mySnake.head.x && snakeSegment.y == mySnake.head.y + 2)
				{
					down2 = false;
					downsafe--;
				}

				if (snakeSegment.x == mySnake.head.x - 1 && snakeSegment.y == mySnake.head.y + 1)
				{
					downleft = false;
					downsafe--;
					leftsafe--;
				}

				if (snakeSegment.x == mySnake.head.x + 1 && snakeSegment.y == mySnake.head.y + 1)
				{
					downright = false;
					downsafe--;
					rightsafe--;
				}

				if (snakeSegment.x == mySnake.head.x - 1 && snakeSegment.y == mySnake.head.y)
				{
					left = false;
					leftsafe--;
				}

				if (snakeSegment.x == mySnake.head.x - 2 && snakeSegment.y == mySnake.head.y)
				{
					left2 = false;
					leftsafe--;
				}

				if (snakeSegment.x == mySnake.head.x + 1 && snakeSegment.y == mySnake.head.y)
				{
					right = false;
					rightsafe--;
				}

				if (snakeSegment.x == mySnake.head.x + 2 && snakeSegment.y == mySnake.head.y)
				{
					right2 = false;
					rightsafe--;
				}

			}
		}
	}
}
