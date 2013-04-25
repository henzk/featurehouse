package composer.rulesets;

import composer.rules.ContractComposition;

public class JMLRuleset extends DefaultRuleset {

	public JMLRuleset(String contractStyle) {
		super();
		setRule(new ContractComposition(contractStyle));
	}
}
