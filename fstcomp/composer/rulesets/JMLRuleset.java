package composer.rulesets;

import composer.Configuration;
import composer.rules.ContractComposition;

public class JMLRuleset extends DefaultRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new ContractComposition(conf.contract_style));
	}
}
