/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.game.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.trentorise.game.model.Game;
import eu.trentorise.game.model.core.ClasspathRule;
import eu.trentorise.game.model.core.DBRule;
import eu.trentorise.game.model.core.FSRule;
import eu.trentorise.game.model.core.GameConcept;
import eu.trentorise.game.model.core.GameTask;
import eu.trentorise.game.model.core.Rule;
import eu.trentorise.game.repo.GamePersistence;
import eu.trentorise.game.repo.GameRepo;
import eu.trentorise.game.repo.GenericObjectPersistence;
import eu.trentorise.game.repo.RuleRepo;
import eu.trentorise.game.services.GameService;
import eu.trentorise.game.services.TaskService;

@Component
public class GameManager implements GameService {

	private final Logger logger = LoggerFactory.getLogger(GameManager.class);

	public static final String INTERNAL_ACTION_PREFIX = "scogei_";

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private GameRepo gameRepo;

	@Autowired
	private RuleRepo ruleRepo;

	@PostConstruct
	@SuppressWarnings("unused")
	private void startup() {

		for (Game game : loadGames(true)) {
			startupTasks(game.getId());
		}

	}

	public String getGameIdByAction(String actionId) {
		GamePersistence game = gameRepo.findByActions(actionId);
		return game != null ? game.getId() : null;
	}

	public void startupTasks(String gameId) {
		Game game = loadGameDefinitionById(gameId);
		if (game != null) {
			for (GameTask task : game.getTasks()) {
				taskSrv.createTask(task, gameId);
			}
		}
	}

	public Game saveGameDefinition(Game game) {
		GamePersistence pers = null;
		if (game.getId() != null) {
			pers = gameRepo.findOne(game.getId());
			if (pers != null) {
				pers.setName(game.getName());
				pers.setActions(new HashSet<String>());

				// add all external actions
				if (game.getActions() != null) {
					for (String a : game.getActions()) {
						if (!a.startsWith(INTERNAL_ACTION_PREFIX)) {
							pers.getActions().add(a);
						}

					}
				}
				pers.setExpiration(game.getExpiration());
				pers.setTerminated(game.isTerminated());
				pers.setRules(game.getRules());

				if (game.getConcepts() != null) {
					Set<GenericObjectPersistence> concepts = new HashSet<GenericObjectPersistence>();
					for (GameConcept c : game.getConcepts()) {
						concepts.add(new GenericObjectPersistence(c));
					}
					pers.setConcepts(concepts);
				} else {
					pers.setConcepts(null);
				}

				if (game.getTasks() != null) {
					Set<GenericObjectPersistence> tasks = new HashSet<GenericObjectPersistence>();
					for (GameTask t : game.getTasks()) {
						tasks.add(new GenericObjectPersistence(t));
						// set internal actions
						pers.getActions().addAll(t.retrieveActions());
					}
					pers.setTasks(tasks);
				} else {
					pers.setTasks(null);
				}
			} else {
				pers = new GamePersistence(game);
			}
		} else {
			pers = new GamePersistence(game);
		}

		pers = gameRepo.save(pers);
		return pers.toGame();
	}

	public Game loadGameDefinitionById(String gameId) {
		GamePersistence gp = gameRepo.findOne(gameId);
		return gp == null ? null : gp.toGame();
	}

	public List<Game> loadGames(boolean onlyActive) {
		List<Game> result = new ArrayList<Game>();
		for (GamePersistence gp : gameRepo.findByTerminated(!onlyActive)) {
			result.add(gp.toGame());
		}
		return result;
	}

	public List<Game> loadAllGames() {
		List<Game> result = new ArrayList<Game>();
		for (GamePersistence gp : gameRepo.findAll()) {
			result.add(gp.toGame());
		}
		return result;
	}

	public String addRule(Rule rule) {
		String ruleUrl = null;
		if (rule != null) {
			StopWatch stopWatch = new Log4JStopWatch();
			stopWatch.start("insert rule");
			Game game = loadGameDefinitionById(rule.getGameId());
			if (game != null) {
				if (rule instanceof ClasspathRule) {
					ClasspathRule r = (ClasspathRule) rule;
					if (!(r.getUrl().startsWith(ClasspathRule.URL_PROTOCOL))) {
						ruleUrl = ClasspathRule.URL_PROTOCOL + r.getUrl();
					}
				}

				if (rule instanceof FSRule) {
					FSRule r = (FSRule) rule;
					if (!(r.getUrl().startsWith(FSRule.URL_PROTOCOL))) {
						ruleUrl = FSRule.URL_PROTOCOL + r.getUrl();
					}
				}

				if (rule instanceof DBRule) {
					boolean alreadyExist = false;
					DBRule r = (DBRule) rule;
					if (r.getId() != null) {
						r.setId(r.getId().replace(DBRule.URL_PROTOCOL, ""));
					} else {
						alreadyExist = ruleRepo.findByGameIdAndName(
								rule.getGameId(), r.getName()) != null;
					}

					if (!alreadyExist) {
						rule = ruleRepo.save(r);
						ruleUrl = DBRule.URL_PROTOCOL + r.getId();
					}
				}

				if (ruleUrl != null && !game.getRules().contains(ruleUrl)) {
					game.getRules().add(ruleUrl);
					saveGameDefinition(game);
				} else {
					throw new IllegalArgumentException(
							"the rule already exist for game "
									+ rule.getGameId());
				}

				stopWatch.stop("insert rule",
						"inserted rule for game " + rule.getGameId());
			} else {
				logger.error("Game {} not found", rule.getGameId());
			}
		}
		return ruleUrl;
	}

	public Rule loadRule(String gameId, String url) {
		Rule rule = null;
		if (url != null) {
			if (url.startsWith(DBRule.URL_PROTOCOL)) {
				url = url.substring(DBRule.URL_PROTOCOL.length());
				return ruleRepo.findOne(url);
			} else if (url.startsWith(ClasspathRule.URL_PROTOCOL)) {
				url = url.substring(ClasspathRule.URL_PROTOCOL.length());
				if (Thread.currentThread().getContextClassLoader()
						.getResource(url) != null) {
					return new ClasspathRule(gameId, url);
				}

			} else if (url.startsWith(FSRule.URL_PROTOCOL)) {
				url = url.substring(FSRule.URL_PROTOCOL.length());
				if (new File(url).exists()) {
					return new FSRule(gameId, url);
				}
			}
		}
		return rule;
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void taskDestroyer() {
		logger.info("task destroyer invocation");
		long deadline = System.currentTimeMillis();

		List<Game> games = loadGames(true);
		for (Game game : games) {
			if (game.getExpiration() > 0 && game.getExpiration() < deadline) {
				for (GameTask task : game.getTasks()) {
					if (taskSrv.destroyTask(task, game.getId())) {
						logger.info("Destroy task - {} - of game {}",
								task.getName(), game.getId());
					}
				}
				game.setTerminated(true);
				saveGameDefinition(game);
			}
		}

	}

	public Game loadGameDefinitionByAction(String actionId) {
		GamePersistence gp = gameRepo.findByActions(actionId);
		return gp != null ? gp.toGame() : null;
	}

	@Override
	public void addConceptInstance(String gameId, GameConcept gc) {
		Game g = loadGameDefinitionById(gameId);
		if (g != null) {
			if (g.getConcepts() == null) {
				g.setConcepts(new HashSet<GameConcept>());
			}
			g.getConcepts().add(gc);
		}

		saveGameDefinition(g);
	}

	@Override
	public Set<GameConcept> readConceptInstances(String gameId) {
		Game g = loadGameDefinitionById(gameId);
		if (g != null) {
			return g.getConcepts() != null ? g.getConcepts() : Collections
					.<GameConcept> emptySet();
		} else {
			return Collections.<GameConcept> emptySet();
		}
	}

	@Override
	public boolean deleteRule(String gameId, String url) {
		Game g = loadGameDefinitionById(gameId);
		boolean res = false;
		if (g != null && url != null && url.indexOf(DBRule.URL_PROTOCOL) != -1) {
			String id = url.substring(5);
			ruleRepo.delete(id);
			res = g.getRules().remove(url);
			saveGameDefinition(g);
		}

		return res;
	}

	@Override
	public boolean deleteGame(String gameId) {
		boolean res = false;
		if (gameId != null) {
			gameRepo.delete(gameId);
			res = true;
		}
		return res;
	}

	@Override
	public List<Game> loadGameByOwner(String user) {
		List<Game> result = new ArrayList<Game>();
		if (user != null) {
			for (GamePersistence gp : gameRepo.findByOwner(user)) {
				result.add(gp.toGame());
			}
		}
		return result;

	}
}
