package composer.rulesets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import builder.ArtifactBuilderInterface;

import composer.CompositionRuleset;
import composer.Configuration;
import composer.rules.CompositionRule;
import composer.rules.IntroductionRule;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public class AbstractCompositionRuleset implements CompositionRuleset {

	protected Configuration conf;
	private IntroductionRule introductionRule;
	private Map<String, CompositionRule> rules = new HashMap<String, CompositionRule>();

	protected void setRule(CompositionRule rule) {
		rules.put(rule.getRuleName(), rule);
	}

	@Override
	public CompositionRule getRule(String name) {
		return rules.get(name);
	}

	protected void setIntroductionRule(IntroductionRule introductionRule) {
		this.introductionRule = introductionRule;
	}

	@Override
	public IntroductionRule getIntroductionRule() {
		return introductionRule;
	}

	@Override
	public void configure(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public void initializeComposition() {}

	@Override
	public void preCompose(ArtifactBuilderInterface builder, List<FSTNonTerminal> features) {}

	@Override
	public void postCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features, FSTNode composition) {}
	
	@Override
	public void finalizeComposition() {}
}