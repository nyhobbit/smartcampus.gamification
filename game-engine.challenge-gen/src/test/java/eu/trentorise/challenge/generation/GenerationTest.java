package eu.trentorise.challenge.generation;

import static eu.trentorise.challenge.PropertiesUtil.CONTEXT;
import static eu.trentorise.challenge.PropertiesUtil.GAMEID;
import static eu.trentorise.challenge.PropertiesUtil.HOST;
import static eu.trentorise.challenge.PropertiesUtil.INSERT_CONTEXT;
import static eu.trentorise.challenge.PropertiesUtil.get;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.game.challenges.ChallengeFactory;
import eu.trentorise.game.challenges.ChallengesRulesGenerator;
import eu.trentorise.game.challenges.exception.UndefinedChallengeException;
import eu.trentorise.game.challenges.rest.Content;
import eu.trentorise.game.challenges.rest.GamificationEngineRestFacade;
import eu.trentorise.game.challenges.rest.RuleDto;
import eu.trentorise.game.challenges.util.ChallengeRuleRow;
import eu.trentorise.game.challenges.util.ChallengeRules;
import eu.trentorise.game.challenges.util.ChallengeRulesLoader;
import eu.trentorise.game.challenges.util.Matcher;

public class GenerationTest {

    private static final Logger logger = LogManager
	    .getLogger(GenerationTest.class);

    private GamificationEngineRestFacade facade;
    private GamificationEngineRestFacade insertFacade;

    @Before
    public void setup() {
	facade = new GamificationEngineRestFacade(get(HOST) + get(CONTEXT));
	insertFacade = new GamificationEngineRestFacade(get(HOST)
		+ get(INSERT_CONTEXT));
    }

    @Test
    public void loadChallengeRuleGenerate() throws NullPointerException,
	    IllegalArgumentException, IOException {
	// load
	ChallengeRules result = ChallengeRulesLoader
		.load("BetaTestChallenges.csv");

	assertTrue(result != null && !result.getChallenges().isEmpty());

	// get users from gamification engine
	// TODO paginazione risultati da gamification engine
	List<Content> users = facade.readGameState(get(GAMEID));

	// generate challenges
	Matcher matcher = new Matcher(result.getChallenges().get(0));
	List<Content> r = matcher.match(users);

	assertTrue(!r.isEmpty());
    }

    @Test
    public void loadTestGeneration() throws NullPointerException,
	    IllegalArgumentException, IOException, UndefinedChallengeException {
	// load
	ChallengeRules result = ChallengeRulesLoader
		.load("BetaTestChallenges.csv");

	assertTrue(result != null && !result.getChallenges().isEmpty());

	// get users from gamification engine
	List<Content> users = facade.readGameState(get(GAMEID));

	ChallengesRulesGenerator crg = new ChallengesRulesGenerator(
		new ChallengeFactory());

	// generate challenges
	for (ChallengeRuleRow challengeSpec : result.getChallenges()) {
	    logger.debug("rules generation for challenge: "
		    + challengeSpec.getName());
	    Matcher matcher = new Matcher(challengeSpec);
	    List<Content> filteredUsers = matcher.match(users);
	    logger.debug("found users: " + filteredUsers.size());
	    String res = crg.generateRules(challengeSpec, filteredUsers,
		    "rules/templates");
	    logger.debug("generated rules \n" + res + "\n");

	    assertTrue(!res.isEmpty());

	}
    }

    @Test
    public void generateChallengeRulesAndInsertToGamificationEngine()
	    throws NullPointerException, IllegalArgumentException, IOException,
	    UndefinedChallengeException {
	// load
	ChallengeRules result = ChallengeRulesLoader
		.load("challengesRules.csv");

	assertTrue(result != null && !result.getChallenges().isEmpty());

	// get users from gamification engine
	List<Content> users = facade.readGameState(get(GAMEID));

	ChallengesRulesGenerator crg = new ChallengesRulesGenerator(
		new ChallengeFactory());

	Map<String, Map<String, Object>> playerIdCustomData = new HashMap<String, Map<String, Object>>();
	// generate challenges
	for (ChallengeRuleRow challengeSpec : result.getChallenges()) {
	    logger.debug("rules generation for challenge: "
		    + challengeSpec.getName());
	    Matcher matcher = new Matcher(challengeSpec);
	    List<Content> filteredUsers = matcher.match(users);
	    logger.debug("found users: " + filteredUsers.size());
	    String res = crg.generateRules(challengeSpec, filteredUsers,
		    "rules/templates");
	    logger.debug("generated rules \n" + res + "\n");

	    assertTrue(!res.isEmpty());

	    // update custom data for every user in challenge
	    playerIdCustomData = crg.getPlayerIdCustomData();
	    for (Content user : filteredUsers) {
		insertFacade.updateChallengeCustomData(get(GAMEID),
			user.getPlayerId(),
			playerIdCustomData.get(user.getPlayerId()));
	    }

	    // define rule
	    RuleDto rule = new RuleDto();
	    rule.setContent(res);
	    rule.setName(challengeSpec.getName());
	    // insert rule
	    RuleDto insertedRule = insertFacade.insertGameRule(get(GAMEID),
		    rule);
	    if (insertedRule != null) {
		logger.debug("Inserted rule ");
		assertTrue(!insertedRule.getId().isEmpty());
	    } else {
		logger.error("Error during insertion of rules");
	    }

	}
    }
}
