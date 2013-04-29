package composer.rulesets;

import java.io.File;
import java.util.List;

import builder.ArtifactBuilderInterface;
import builder.capprox.CApproxBuilder;
import builder.java.JavaBuilder;
import composer.CompositionRuleset;
import counter.Counter;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public class CounterRuleset extends CompositionRulesetWrapper {

	public CounterRuleset(CompositionRuleset wrappee) {
		super(wrappee);
	}

	@Override
	public void preCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features) {
		super.preCompose(builder, features);
		if (builder instanceof JavaBuilder || builder instanceof CApproxBuilder) {
			Counter counter = new Counter();
			for (FSTNonTerminal feature : features) {
				counter.collect(feature);
			}
			if(features.size() > 0)
				counter.writeFile(new File(conf.equationFileName + ".rsf"));
		}
	}
}
