package eu.trentorise.game.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import eu.trentorise.game.model.Game;
import eu.trentorise.game.model.PlayerState;
import eu.trentorise.game.repo.PlayerRepo;
import eu.trentorise.game.repo.StatePersistence;
import eu.trentorise.game.services.GameService;
import eu.trentorise.game.services.PlayerService;

@Component
public class DBPlayerManager implements PlayerService {

	private final Logger logger = LoggerFactory
			.getLogger(DBPlayerManager.class);

	@Autowired
	private PlayerRepo repo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GameService gameSrv;

	public PlayerState loadState(String userId, String gameId) {
		eu.trentorise.game.repo.StatePersistence state = repo
				.findByGameIdAndPlayerId(gameId, userId);

		return state == null ? init(userId, gameId) : state.toPlayerState();
	}

	public boolean saveState(PlayerState state) {
		StatePersistence toSave = new StatePersistence(state);

		if (StringUtils.isBlank(state.getGameId())
				|| StringUtils.isBlank(state.getPlayerId())) {
			throw new IllegalArgumentException(
					"field gameId and playerId of PlayerState MUST be set");
		}

		Criteria criteria = new Criteria();
		criteria = criteria.and("gameId").is(state.getGameId()).and("playerId")
				.is(state.getPlayerId());
		Query query = new Query(criteria);
		Update update = new Update();
		update.set("concepts", toSave.getConcepts());
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.upsert(true);
		mongoTemplate.findAndModify(query, update, options,
				StatePersistence.class);
		return true;
	}

	public List<String> readPlayers(String gameId) {
		List<StatePersistence> states = repo.findByGameId(gameId);
		List<String> result = new ArrayList<String>();
		for (StatePersistence state : states) {
			result.add(state.getPlayerId());
		}

		return result;
	}

	public List<PlayerState> loadStates(String gameId) {
		List<StatePersistence> states = repo.findByGameId(gameId);
		List<PlayerState> result = new ArrayList<PlayerState>();
		for (StatePersistence state : states) {
			result.add(state.toPlayerState());
		}

		return result;
	}

	private PlayerState init(String playerId, String gameId) {
		Game g = gameSrv.loadGameDefinitionById(gameId);
		PlayerState p = new PlayerState(playerId, gameId);
		if (g != null) {
			p.setState(g.getConcepts());
		}

		return p;
	}
}