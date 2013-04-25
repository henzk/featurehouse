package composer;

import java.util.HashMap;
import java.util.Map;

import composer.rules.CompositionRule;

public class CompositionRuleset {

	private Map<String, CompositionRule> rules = new HashMap<String, CompositionRule>();
	
	protected CompositionRuleset setRule(CompositionRule rule) {
		rules.put(rule.getRuleName(), rule);
		return this;
	}
	
	public CompositionRule getRule(String name) {
		return rules.get(name);
	}
}
