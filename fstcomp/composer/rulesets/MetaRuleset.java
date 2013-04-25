package composer.rulesets;

import composer.rules.meta.ConstructorConcatenationMeta;
import composer.rules.meta.ContractCompositionMeta;
import composer.rules.meta.FieldOverridingMeta;
import composer.rules.meta.InvariantCompositionMeta;
import composer.rules.meta.JavaMethodOverridingMeta;

public class MetaRuleset extends JMLRuleset {
	public MetaRuleset(String contractStyle) {
		super(contractStyle);
		setRule(new JavaMethodOverridingMeta());
		setRule(new InvariantCompositionMeta());
		setRule(new ContractCompositionMeta(contractStyle));
		setRule(new ConstructorConcatenationMeta());
		setRule(new FieldOverridingMeta());

	}
}
