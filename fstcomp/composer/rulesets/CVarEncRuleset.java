package composer.rulesets;

import composer.Configuration;
import composer.rules.rtcomp.c.CRuntimeFunctionRefinement;
import composer.rules.rtcomp.c.CRuntimeReplacement;

public class CVarEncRuleset extends DefaultRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new CRuntimeReplacement());
		setRule(new CRuntimeFunctionRefinement());
	}
}
