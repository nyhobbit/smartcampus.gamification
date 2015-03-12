package eu.trentorise.game.managers;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.game.model.PlayerState;
import eu.trentorise.game.services.GameEngine;
import eu.trentorise.game.services.GameService;
import eu.trentorise.game.services.PlayerService;
import eu.trentorise.game.services.Workflow;

@Component
public class GameWorkflow implements Workflow {

	private final Logger logger = org.slf4j.LoggerFactory
			.getLogger(GameWorkflow.class);

	@Autowired
	GameEngine gameEngine;

	@Autowired
	PlayerService playerSrv;

	@Autowired
	GameService gameSrv;

	public void apply(String actionId, String userId, Map<String, Object> data) {
		logger.info("actionId: {}, playerId: {}, data: {}", actionId, userId,
				data);

		String gameId = gameSrv.getGameIdByAction(actionId);

		PlayerState playerState = playerSrv.loadState(userId, gameId);

		PlayerState newState = gameEngine.execute(gameId, playerState,
				actionId, data);

		boolean result = playerSrv.saveState(newState);

		logger.info("Process terminated: {}", result);

	}
}