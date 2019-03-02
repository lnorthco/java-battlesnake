package io.battlesnake.starter;

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
            response.put("color", "#ff00ff");
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
			GetGameInfo GetGameInfo = new GetGameInfo(moveRequest); // Instantiate a GetGameInfo, containing relevant information. This cleans up the function
																	// and allows easy access to information throughout the move function.
			//Get game variables
			//int height = moveRequest.get("board").get("height").intValue() - 1; // Replaced by GetGameInfo.GetBoardSize()
			//int width2 = moveRequest.get("board").get("height").intValue() - 1;  // See above
			
			int height = GetGameInfo.GetBoardSize();
			int width = GetGameInfo.GetBoardSize();
			
			BoardSnake mySnake = GetGameInfo.GetSelf();
			
			System.out.println("Health " + mySnake.health);
			System.out.println("HeadPos " + mySnake.head);
			
			for (BoardSnake bs : GetGameInfo.GetSnakes())
			{
				System.out.println("Health itersnake " + bs.health);
				System.out.println("HeadPos itersnake " + bs.head);
			}
			
			JsonNode myHead = moveRequest.get("you").get("body").get(0);
			JsonNode snakes = moveRequest.get("board").get("snakes");
			int numOfSnakes = snakes.size();
			//System.out.println(moveRequest.get("board").get("height"));
			//System.out.println("numOfSnakes=" + numOfSnakes);
			
			//Setup some boolean's to see which directions we can go safely.			
			boolean up = true;
			boolean down = true;
			boolean left = true;
			boolean right = true;
			
			  //check edges
			if(myHead.get("y").intValue() == 0) up=false;
			if(myHead.get("y").intValue() == height) down=false;
			if(myHead.get("x").intValue() == 0) left=false;
			if(myHead.get("x").intValue() == width)right=false;

			
			//Get new possible head locations
			int yHead = myHead.get("y").intValue();
			int xHead = myHead.get("x").intValue();
			JsonNode snakeBody;
			
			//interating through our snakes
			for(int j=0; j<numOfSnakes; j++) {
				snakeBody = snakes.get(j).get("body");
				//System.out.println("snakeBody="+snakeBody);
				int snakeSize = snakeBody.size();
				//System.out.println("snakeSize="+snakeSize);
				
				//interating through snake bodies and checking if any points match our new head locations
				for(int i=0; i<snakeSize; i++){
					int snakeBodyX = snakeBody.get(i).get("x").intValue();
					int snakeBodyY = snakeBody.get(i).get("y").intValue();
					
					if(snakeBodyX == xHead && snakeBodyY == yHead-1)up=false;
					if(snakeBodyX == xHead && snakeBodyY == yHead+1)down=false;
					if(snakeBodyX == xHead-1 && snakeBodyY == yHead)left=false;
					if(snakeBodyX == xHead+1 && snakeBodyY == yHead)right=false;
				}
			}	
			
			//We now have possible safe moves, now we just need to determine better moves and hunt for food.
			// Determine which directions are safe, then make a move.
			if (up)
			{
				response.put("move", "up");
			}
			else if (down)
			{
				response.put("move", "down");
			}
			else if (left)
			{
				response.put("move", "left");	
			}
			else if (right)
			{
				response.put("move", "right");
			}
			// What happens if all the above fail? (we die, ofc, but will it crash?)
			
            //response.put("move", "left");
            return response;
        }

        /**
         * /end is called by the engine when a game is complete.
         *
         * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {
            Map<String, String> response = new HashMap<>();
            return response;
        }
    }

}
