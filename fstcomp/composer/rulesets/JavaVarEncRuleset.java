package composer.rulesets;

import composer.Configuration;
import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;
import composer.rules.rtcomp.java.JavaRuntimeReplacement;

public class JavaVarEncRuleset extends DefaultRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new JavaRuntimeReplacement());
		setRule(new JavaRuntimeFunctionRefinement());
	}
}
