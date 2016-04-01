package eu.trentorise.game.challenges;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.trentorise.game.challenges.api.Constants;
import eu.trentorise.game.challenges.rest.GamificationEngineRestFacade;
import eu.trentorise.game.challenges.rest.RuleDto;

/**
 * Uploader tool get a json file created from {@link ChallengeGeneratorTool} and
 * upload into Gamification engine using {@link GamificationEngineRestFacade}.
 * Options: </br></br> - input json input file </br> - host host for
 * gamification engine</br> - gameId unique indentifier for game into
 * gamification engine
 */
public class UploaderTool {

    private static Options options;
    private static HelpFormatter helpFormatter;

    public static void main(String[] args) throws ParseException {
	// parse options
	init();
	CommandLineParser parser = new DefaultParser();
	CommandLine cmd = null;
	try {
	    cmd = parser.parse(options, args);
	} catch (MissingOptionException e) {
	    printHelp();
	    return;
	}
	if (cmd.getOptions() == null || cmd.getOptions().length == 0) {
	    printHelp();
	    return;
	}
	if (cmd.hasOption("help")) {
	    helpFormatter.printHelp("challengeGeneratorTool", options);
	    return;
	}
	String host = "";
	String gameId = "";
	String input = "";
	if (cmd.hasOption("host")) {
	    host = cmd.getArgList().get(0);
	} else {
	    printHelp();
	    return;
	}
	if (cmd.hasOption("gameId")) {
	    gameId = cmd.getArgList().get(1);
	} else {
	    printHelp();
	    return;
	}
	if (cmd.hasOption("input")) {
	    input = cmd.getArgList().get(2);
	} else {
	    printHelp();
	    return;
	}
	// call generation
	upload(host, gameId, input);
    }

    private static void printHelp() {
	helpFormatter.printHelp("challengeUploader",
		"-host <host> -gameId <gameId> -input <input json file> ",
		options, "");
    }

    private static void upload(String host, String gameId, String input) {
	if (input == null) {
	    System.err.println("Input file cannot be null");
	    return;
	}
	GamificationEngineRestFacade insertFacade = new GamificationEngineRestFacade(
		host + "console/");
	// read input file
	ObjectMapper mapper = new ObjectMapper();
	List<RuleDto> rules = null;
	try {
	    String jsonString = IOUtils.toString(new FileInputStream(input));
	    rules = mapper.readValue(jsonString,
		    new TypeReference<List<RuleDto>>() {
		    });
	} catch (IOException e) {
	    System.err.println("Error in reading input file " + input + " "
		    + e.getMessage());
	    return;
	}
	int tot = 0;
	StringBuffer buffer = new StringBuffer();
	buffer.append("CHALLENGE_NAME;CHALLENGE_UUID;RULE_TEXT\n");
	System.out.println("Read rules " + rules.size());
	for (RuleDto rule : rules) {
	    RuleDto insertedRule = insertFacade.insertGameRule(gameId, rule);
	    if (insertedRule != null) {
		String ruleId = StringUtils.removeStart(insertedRule.getId(),
			Constants.RULE_PREFIX);
		System.out.println("Uploaded rule " + insertedRule.getName()
			+ " with id=" + ruleId);
		buffer.append(insertedRule.getName() + ";");
		buffer.append(ruleId + ";");
		buffer.append("test;\n");
		tot++;
	    } else {
		System.err.println("Error in uploaded rule " + rule.getName());
	    }
	}
	try {
	    IOUtils.write(buffer, new FileOutputStream("report.csv"));
	} catch (IOException e) {
	    System.err.println("Error in writing report file");
	    return;
	}
	System.out.println("Inserted rules " + tot);
	System.out.println("Rule upload completed");

    }

    private static void init() {
	options = new Options();
	options.addOption(Option.builder("help").desc("display this help")
		.build());
	options.addOption(Option.builder("host")
		.desc("gamification engine host").required().build());
	options.addOption(Option.builder("gameId")
		.desc("uuid for gamification engine").required().build());
	options.addOption(Option.builder("input")
		.desc("rules to upload in json format").required().build());
	helpFormatter = new HelpFormatter();
    }

}
