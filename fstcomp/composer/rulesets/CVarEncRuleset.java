package composer.rulesets;

import composer.rules.rtcomp.c.CRuntimeFunctionRefinement;
import composer.rules.rtcomp.c.CRuntimeReplacement;

public class CVarEncRuleset extends DefaultRuleset {

	public CVarEncRuleset() {
		setRule(new CRuntimeReplacement());
		setRule(new CRuntimeFunctionRefinement());
	}
}
