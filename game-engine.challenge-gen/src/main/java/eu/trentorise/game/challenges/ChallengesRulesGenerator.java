package eu.trentorise.game.challenges;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.trentorise.game.challenges.api.ChallengeFactoryInterface;
import eu.trentorise.game.challenges.exception.UndefinedChallengeException;
import eu.trentorise.game.challenges.model.Challenge;
import eu.trentorise.game.challenges.model.ChallengeType;
import eu.trentorise.game.challenges.rest.Content;
import eu.trentorise.game.challenges.util.ChallengeRuleRow;

/**
 * Generate rules for challenges
 */
public class ChallengesRulesGenerator {

    private StringBuffer buffer;
    private ChallengeFactoryInterface factory;
    private Map<String, Map<String, Object>> playerIdCustomData;

    public ChallengesRulesGenerator(ChallengeFactoryInterface factory) {
	this.buffer = new StringBuffer();
	this.playerIdCustomData = new HashMap<String, Map<String, Object>>();
	this.factory = factory;
    }

    /**
     * Generate rules starting from a challenge specification for a set of given
     * users
     * 
     * @param challengeSpec
     * @param users
     * @return
     * @throws UndefinedChallengeException
     */
    public String generateRules(ChallengeRuleRow challengeSpec,
	    List<Content> users, String templateDir)
	    throws UndefinedChallengeException {
	Map<String, Object> params = new HashMap<String, Object>();
	buffer = new StringBuffer();
	buffer.append("/** " + challengeSpec.getType() + " "
		+ challengeSpec.getTarget().toString() + " **/\n");

	// get right challenge
	for (Content user : users) {
	    Challenge c = factory
		    .createChallenge(
			    ChallengeType.valueOf(challengeSpec.getType()),
			    templateDir);
	    params = new HashMap<String, Object>();
	    // TODO: con peppo, dobbiamo capire come le varie tipologie vanno ad
	    // essere usate nei template
	    if (challengeSpec.getTarget() instanceof Double) {
		params.put("target", challengeSpec.getTarget());
	    }
	    params.put("mode", challengeSpec.getGoalType());
	    params.put("bonus", challengeSpec.getBonus());
	    params.put("point_type", challengeSpec.getPointType());
	    params.put(
		    "baseline",
		    user.getCustomData().getAdditionalProperties()
			    .get(challengeSpec.getBaselineVar()));
	    c.setTemplateParams(params);
	    c.compileChallenge(user.getPlayerId());
	    buffer.append(c.getGeneratedRules());

	    // save custom data for user for later use
	    playerIdCustomData.put(user.getPlayerId(), c.getCustomData());
	}
	// remove package declaration after first
	// TODO: we have to find a better way to fix this
	String temp = buffer.toString();
	buffer = new StringBuffer();
	boolean remove = false;
	try {
	    BufferedReader rdr = new BufferedReader(new StringReader(temp));
	    for (String line = rdr.readLine(); line != null; line = rdr
		    .readLine()) {
		if (line.startsWith("package") && !remove) {
		    remove = true;
		    buffer.append(line).append(
			    System.getProperty("line.separator"));

		} else if (line.startsWith("package") && remove) {
		    // do nothing
		} else {
		    buffer.append(line).append(
			    System.getProperty("line.separator"));

		}
	    }
	    rdr.close();
	} catch (IOException e) {
	    // TODO log
	}
	// lines now contains all the strings between line breaks
	return buffer.toString();
    }

    public Map<String, Map<String, Object>> getPlayerIdCustomData() {
	return playerIdCustomData;
    }

}
