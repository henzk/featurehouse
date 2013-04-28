package composer.rulesets;

import composer.CompositionRuleset;
import composer.Configuration;
import composer.rules.CSharpMethodOverriding;
import composer.rules.CompositionError;
import composer.rules.ConstructorConcatenation;
import composer.rules.ExpansionOverriding;
import composer.rules.FieldOverriding;
import composer.rules.ImplementsListMerging;
import composer.rules.Introduction;
import composer.rules.JavaMethodOverriding;
import composer.rules.ModifierListSpecialization;
import composer.rules.Replacement;
import composer.rules.StringConcatenation;

public class DefaultRuleset extends CompositionRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setIntroductionRule(new Introduction());
		setRule(new Replacement());
		setRule(new JavaMethodOverriding());
		setRule(new StringConcatenation());
		setRule(new ImplementsListMerging());
		setRule(new CSharpMethodOverriding());
		setRule(new ConstructorConcatenation());
		setRule(new ModifierListSpecialization());
		setRule(new FieldOverriding());
		setRule(new ExpansionOverriding());
		setRule(new CompositionError());
	}

}