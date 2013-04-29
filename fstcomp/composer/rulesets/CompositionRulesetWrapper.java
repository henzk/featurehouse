package composer.rulesets;

import java.util.List;

import builder.ArtifactBuilderInterface;
import composer.CompositionRuleset;
import composer.Configuration;
import composer.rules.CompositionRule;
import composer.rules.IntroductionRule;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public abstract class CompositionRulesetWrapper implements CompositionRuleset {

	protected CompositionRuleset wrappee;
	protected Configuration conf;

	public CompositionRulesetWrapper(CompositionRuleset wrappee) {
		this.wrappee = wrappee;
	}

	@Override
	public CompositionRule getRule(String name) {
		return wrappee.getRule(name);
	}

	@Override
	public IntroductionRule getIntroductionRule() {
		return wrappee.getIntroductionRule();
	}

	@Override
	public void configure(Configuration conf) {
		wrappee.configure(conf);
		this.conf = conf;
	}

	@Override
	public void initializeComposition() {
		wrappee.initializeComposition();
	}

	@Override
	public void preCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features) {
		wrappee.preCompose(builder, features);
	}

	@Override
	public void postCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features, FSTNode composition) {
		wrappee.postCompose(builder, features, composition);
	}

	@Override
	public void finalizeComposition() {
		wrappee.finalizeComposition();
	}
}