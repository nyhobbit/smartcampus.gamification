package eu.trentorise.game.challenges.util;

public class ChallengeRuleRow {

    private String type;
    private String goalType;
    private Object target;
    private String pointType;
    private Integer bonus;
    private String name;
    private String baselineVar;
    private String selectionCriteriaCustomData;
    private String selectionCriteriaPoints;
    private String selectionCriteriaBadges;

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getGoalType() {
	return goalType;
    }

    public void setGoalType(String goalType) {
	this.goalType = goalType;
    }

    public Object getTarget() {
	return target;
    }

    public void setTarget(Object target) {
	this.target = target;
    }

    public String getPointType() {
	return pointType;
    }

    public void setPointType(String pointType) {
	this.pointType = pointType;
    }

    public void setSelectionCriteriaCustomData(
	    String selectionCriteriaCustomData) {
	this.selectionCriteriaCustomData = selectionCriteriaCustomData;
    }

    public void setBonus(Integer bonus) {
	this.bonus = bonus;
    }

    public Integer getBonus() {
	return bonus;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public String getBaselineVar() {
	return baselineVar;
    }

    public void setBaselineVar(String baselineVar) {
	this.baselineVar = baselineVar;
    }

    public void setSelectionCriteriaPoints(String selectionCriteriaPoints) {
	this.selectionCriteriaPoints = selectionCriteriaPoints;
    }

    public String getSelectionCriteriaPoints() {
	return selectionCriteriaPoints;
    }

    public String getSelectionCriteriaCustomData() {
	return selectionCriteriaCustomData;
    }

    public void setSelectionCriteriaBadges(String selectionCriteriaBadges) {
	this.selectionCriteriaBadges = selectionCriteriaBadges;
    }

    public String getSelectionCriteriaBadges() {
	return selectionCriteriaBadges;
    }

}
