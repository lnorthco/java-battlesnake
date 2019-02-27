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
			
			//Get game variables
			int height = moveRequest.get("board").get("height").intValue() - 1;
			int width = moveRequest.get("board").get("height").intValue() - 1;
			JsonNode myHead = moveRequest.get("you").get("body").get(0);
			JsonNode snakes = moveRequest.get("board").get("snakes");
			int numOfSnakes = snakes.size();
			//System.out.println(moveRequest.get("board").get("height"));
			System.out.println("numOfSnakes=" + numOfSnakes);
			
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
			for(int j=0; j<numOfSnakes; j++) {
				snakeBody = snakes.get(j).get("body");
				System.out.println("snakeBody="+snakeBody);
				System.out.println("snakeSize="+snakeBody.size());
				for(int i=0; i<snakeBody.size(); i++){
					System.out.println("test");
					System.out.println("x:" + xHead + "=" + snakeBody.get("x").intValue());
					System.out.println("y:" + yHead + "=" + snakeBody.get("y").intValue());
					if(snakeBody.get("x").intValue() == xHead && snakeBody.get("y").intValue() == yHead-1)up=false;
					if(snakeBody.get("x").intValue() == xHead && snakeBody.get("y").intValue() == yHead+1)down=false;
					if(snakeBody.get("x").intValue() == xHead-1 && snakeBody.get("y").intValue() == yHead)left=false;
					if(snakeBody.get("x").intValue() == xHead+1 && snakeBody.get("y").intValue() == yHead)right=false;
				}
			}
			System.out.println("up="+up);
			System.out.println("down="+down);
			System.out.println("left="+left);
			System.out.println("right="+right);
			
			//ObjectNode upNode = JSON_MAPPER.createObjectNode();
			//ArrayNode arrayNode = JSON_MAPPER.createArrayNode();
			//upNode.put("x", myHead.get("x").intValue());
			//upNode.put("y",(myHead.get("y").intValue()-1));
			//arrayNode.add(upNode);
			//JsonNode upNewHead = arrayNode;
			//JsonNode upNewHead = {x:myHead.get("x").intValue(),y:myHead.get("y").intValue()-1};
			//JsonNode downNewHead = [{x:myHead.get("x").intValue(),y:myHead.get("y").intValue()+1}];
			//JsonNode leftNewHead = [{x:myHead.get("x").intValue()-1,y:myHead.get("y").intValue()}];
			//JsonNode rightNewHead = [{x:myHead.get("x").intValue()+1,y:myHead.get("y").intValue()}];
			//System.out.println("arrayNode=" + arrayNode);
			//System.out.println("upNewHead=" + upNewHead);
			//System.out.println("myHead=" + myHead);
/*
			//Test if new head location will hit any other snakes or its self. Snakes contains your body.	
			for(int j=0;j<snakes.length;j++){
				snakeBody = snakes[j].body;
				for(int i=0;i<snakeBody.length;i++){
					if(upNewHead.x==snakeBody[i].x && upNewHead.y==snakeBody[i].y){up=false};
					if(downNewHead.x==snakeBody[i].x && downNewHead.y==snakeBody[i].y){down=false};
					if(leftNewHead.x==snakeBody[i].x && leftNewHead.y==snakeBody[i].y){left=false};
					if(rightNewHead.x==snakeBody[i].x && rightNewHead.y==snakeBody[i].y){right=false};
				}
			}
*/			
			
			if(up)response.put("move", "up");
			else if(down)response.put("move", "down");
			else if(left)response.put("move", "left");
			else if(right)response.put("move", "right");
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
