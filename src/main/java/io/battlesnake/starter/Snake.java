package io.battlesnake.starter;

import java.lang.Math;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

/**
 * Snake server that deals with requests from the snake engine.
 * Just boiler plate code.  See the readme to get started.
 * It follows the spec here: https://github.com/battlesnakeio/docs/tree/master/apis/snake
 */
public class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);

    /**
     * Main entry point.
     *
     * @param args are ignored.
     */

    // Mersid says hello!
    public static void main(String[] args) {
        String port = System.getProperty("PORT");
        if (port != null) {
            LOG.info("Found system provided port: {}", port);
        } else {
            LOG.info("Using default port: {}", port);
            port = "8080";
        }
        port(Integer.parseInt(port));
        get("/", (req, res) -> "Battlesnake documentation can be found at " +
            "<a href=\"https://docs.battlesnake.io\">https://docs.battlesnake.io</a>.");
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
    }

    /**
     * Handler class for dealing with the routes set up in the main method.
     */
    public static class Handler {

        /**
         * For the ping request
         */
        private static final Map<String, String> EMPTY = new HashMap<>();

        /**
         * Generic processor that prints out the request and response from the methods.
         *
         * @param req
         * @param res
         * @return
         */
        public Map<String, String> process(Request req, Response res) {
            try {
                JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                String uri = req.uri();
                LOG.info("{} called with: {}", uri, req.body());
                Map<String, String> snakeResponse;
                if (uri.equals("/start")) {
                    snakeResponse = start(parsedRequest);
                } else if (uri.equals("/ping")) {
                    snakeResponse = ping();
                } else if (uri.equals("/move")) {
                    snakeResponse = move(parsedRequest);
                } else if (uri.equals("/end")) {
                    snakeResponse = end(parsedRequest);
                } else {
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }
                LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
                return snakeResponse;
            } catch (Exception e) {
                LOG.warn("Something went wrong!", e);
                return null;
            }
        }

        /**
         * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
         * snake is still alive.
         *
         * @param pingRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return an empty response.
         */
        public Map<String, String> ping() {
            return EMPTY;
        }

        /**
         * /start is called by the engine when a game is first run.
         *
         * @param startRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return a response back to the engine containing the snake setup values.
         */
        public Map<String, String> start(JsonNode startRequest) {
            Map<String, String> response = new HashMap<>();
            response.put("color", "#C690E5");
            response.put("headType", "beluga");
            response.put("tailType", "freckled");
            return response;
        }

        /**
         * /move is called by the engine for each turn the snake has.
         *
         * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return a response back to the engine containing snake movement values.
         */
        public Map<String, String> move(JsonNode moveRequest) {
            Map<String, String> response = new HashMap<>();
            // Instantiate a GetGameInfo, containing relevant information. This cleans up the function
			// and allows easy access to information throughout the move function.
			GetGameInfo GetGameInfo = new GetGameInfo(moveRequest);
			SafeMoves SafeMoves = new SafeMoves(moveRequest);

			// Get height and size
			int height = GetGameInfo.GetBoardHeight();
			int width = GetGameInfo.GetBoardHeight();

			BoardSnake mySnake = GetGameInfo.GetSelf();

			System.out.println("Health " + mySnake.health);
			System.out.println("HeadPos " + mySnake.head);

			for (BoardSnake bs : GetGameInfo.GetSnakes())
			{
				System.out.println("Health itersnake " + bs.health);
				System.out.println("HeadPos itersnake " + bs.head);
			}
			System.out.println("Is up safe?" + SafeMoves.up);
			System.out.println("Is down safe?" + SafeMoves.down);
			System.out.println("Is left safe?" + SafeMoves.left);
			System.out.println("Is right safe?" + SafeMoves.right);
			System.out.println("Turn: " + GetGameInfo.GetRound());

			//We now have possible safe moves, now we just need to determine better moves and hunt for food.
			// Determine which directions are safe, then make a move. 
			int Xfood;
			int Yfood;
			int distance = 0;
			JsonNode closestFood = moveRequest.get("board").get("food").get(0);
			int numOfFood = moveRequest.get("board").get("food").size();
			for(int k=0;k<numOfFood;k++){
				Xfood=moveRequest.get("board").get("food").get(k).get("x").intValue();
				Yfood=moveRequest.get("board").get("food").get(k).get("y").intValue();

				if(k==0){
					distance = Math.abs(mySnake.head.x - Xfood) + Math.abs(mySnake.head.y - Yfood);
					}
				else{
					if(distance > Math.abs(mySnake.head.x - Xfood) + Math.abs(mySnake.head.y - Yfood)){
						distance = Math.abs(mySnake.head.x - Xfood) + Math.abs(mySnake.head.y - Yfood);
						closestFood = moveRequest.get("board").get("food").get(k);
					}

				}

			}

			System.out.println("closestFood="+closestFood);
			System.out.println("Shortest Distance: " + distance);
			System.out.println("downsafe="+SafeMoves.downsafe);
			System.out.println("upsafe="+SafeMoves.upsafe);
			System.out.println("leftsafe="+SafeMoves.leftsafe);
			System.out.println("rightsafe="+SafeMoves.rightsafe);
			boolean goUp = false;
			boolean goDown = false;
			boolean goLeft = false;
			boolean goRight = false;
			if(SafeMoves.upsafe >= SafeMoves.downsafe && SafeMoves.upsafe >= SafeMoves.leftsafe && SafeMoves.upsafe >= SafeMoves.rightsafe && SafeMoves.up){
				goUp = true;
			}
			if(SafeMoves.downsafe >= SafeMoves.upsafe && SafeMoves.downsafe >= SafeMoves.leftsafe && SafeMoves.downsafe >= SafeMoves.rightsafe && SafeMoves.down){
				goDown = true;
			}
			if(SafeMoves.leftsafe >= SafeMoves.downsafe && SafeMoves.leftsafe >= SafeMoves.upsafe && SafeMoves.leftsafe >= SafeMoves.rightsafe && SafeMoves.left){
				goLeft = true;
			}
			if(SafeMoves.rightsafe >= SafeMoves.downsafe && SafeMoves.rightsafe >= SafeMoves.leftsafe && SafeMoves.rightsafe >= SafeMoves.upsafe && SafeMoves.right){
				goRight = true;
			}


      if (mySnake.health < 50){
        if (mySnake.head.x < closestFood.get("x").intValue() && SafeMoves.right){
          //check if safe/best move
          response.put("move", "right");
          System.out.println("Opt A");
          }
        else if (mySnake.head.x > closestFood.get("x").intValue() && SafeMoves.left){
          //check if safe/best move
          response.put("move", "left");
          System.out.println("Opt B");
          }
        else if (mySnake.head.y < closestFood.get("y").intValue() && SafeMoves.down){
          //check if safe/best move
          response.put("move", "down");
          System.out.println("Opt C");
          }
        else if (mySnake.head.y > closestFood.get("y").intValue() && SafeMoves.up){
          //check if safe/best move
          response.put("move", "up");
          System.out.println("Opt D");
        }
      }


			else if (mySnake.health >= 50 && goUp)
			{
				response.put("move", "up");
				System.out.println("Opt E");
			}
			else if (mySnake.health >= 50 && goDown)
			{
				response.put("move", "down");
				System.out.println("Opt F");
			}
			else if (mySnake.health >= 50 && goLeft)
			{
				response.put("move", "left");
				System.out.println("Opt G");
			}
			else if (mySnake.health >= 50 && goRight)
			{
				response.put("move", "right");
				System.out.println("Opt H");
			}
			else
			{
				if(SafeMoves.up){
					response.put("move", "up");
				}
				else if (SafeMoves.down){
					response.put("move", "down");
				}
				else if (SafeMoves.left){
					response.put("move", "left");
				}
				else{
					response.put("move", "right");
				}
					
				System.out.println("Opt I - Nothing looks safe!");
			}
			// What happens if all the above fail? (we die, ofc, but will it crash?)

            //response.put("move", "left");
      		System.out.println("Response: " + response.toString() + ", Length: " + (response.toString().length() - 2)); // Discount braces
            return response;
        }

        /**
         * /end is called by the engine when a game is complete.
         *
         * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {
        	System.out.println("Game end!");
            Map<String, String> response = new HashMap<>();
            return response;
        }
    }

}
