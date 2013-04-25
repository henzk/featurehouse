package composer.rulesets;

import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;
import composer.rules.rtcomp.java.JavaRuntimeReplacement;

public class JavaVarEncRuleset extends DefaultRuleset {

	public JavaVarEncRuleset() {
		super();
		setRule(new JavaRuntimeReplacement());
		setRule(new JavaRuntimeFunctionRefinement());
	}
}
