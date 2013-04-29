package composer;

import java.util.List;
import builder.ArtifactBuilderInterface;
import composer.rules.CompositionRule;
import composer.rules.IntroductionRule;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * provides a customizable Composition process
 * 
 * implementers provide specific behavior e.g. variability encoding, contract composition, feature annotations,...
 */
public interface CompositionRuleset {

	public CompositionRule getRule(String name);

	public IntroductionRule getIntroductionRule();

	public void configure(Configuration conf);

	public void initializeComposition();
	public void finalizeComposition();
	public void preCompose(ArtifactBuilderInterface builder, List<FSTNonTerminal> features);
	public void postCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features, FSTNode composition);
}
