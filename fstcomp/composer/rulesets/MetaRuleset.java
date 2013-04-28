package composer.rulesets;

import composer.Configuration;
import composer.rules.meta.ConstructorConcatenationMeta;
import composer.rules.meta.ContractCompositionMeta;
import composer.rules.meta.FieldOverridingMeta;
import composer.rules.meta.InvariantCompositionMeta;
import composer.rules.meta.JavaMethodOverridingMeta;

public class MetaRuleset extends JMLRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new JavaMethodOverridingMeta(metadataStore));
		setRule(new InvariantCompositionMeta());
		setRule(new ContractCompositionMeta(conf.contract_style));
		setRule(new ConstructorConcatenationMeta());
		setRule(new FieldOverridingMeta());
	}
}