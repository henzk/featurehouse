package composer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import printer.FeaturePrintVisitor;
import printer.PrintVisitorException;

import builder.ArtifactBuilderInterface;

import composer.rules.CompositionRule;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * provides a customizable Composition process
 * 
 * subclasses may implement specific behaviour e.g. variability encoding, contract composition, feature annotations,...
 */
public abstract class CompositionRuleset {

	private Map<String, CompositionRule> rules = new HashMap<String, CompositionRule>();
	protected FeaturePrintVisitor printVisitor = new FeaturePrintVisitor();
	private boolean generateOutput = true;
	
	public CompositionRuleset enableOutput() {
		generateOutput = true;
		return this;
	}

	public CompositionRuleset disableOutput() {
		generateOutput = false;
		return this;
	}
	
	public boolean outputEnabled() {
		return generateOutput;
	}
	
	protected CompositionRuleset setRule(CompositionRule rule) {
		rules.put(rule.getRuleName(), rule);
		return this;
	}
	
	public CompositionRule getRule(String name) {
		return rules.get(name);
	}
	
	public void configure(Configuration conf) {}
	public void initializeComposition() {}
	public void finalizeComposition() {}
	public void preCompose(ArtifactBuilderInterface builder, List<FSTNonTerminal> features) {}

	public void postCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features, FSTNode composition) {
		if (outputEnabled()) {
			try {
				printVisitor.visit((FSTNonTerminal) composition);
			} catch (PrintVisitorException e) {
				e.printStackTrace();
			}
		}
	}
}
