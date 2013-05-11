package composer;

import java.util.List;

import composer.rules.CompositionRule;
import composer.rulesets.MetaRuleset;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class FSTGenComposerExtension extends FSTGenComposer {
	
	public static boolean key = false;
	public static boolean metaproduct = false;
	
	
	public FSTGenComposerExtension() {
		super();
	}
	
	/**
	 * Builds the full FST of the project without composition.
	 * @param args Default build parameters
	 * @param featuresArg An array containing all features of the project
	 */
	public void buildFullFST(String[] args, String[] featuresArg) {
		metaproduct = false;
		build(args, featuresArg, false);
	}
	
	public void buildMetaProduct(String[] args, String[] featuresArg) {
		metaproduct = true;
		build(args, featuresArg, true);
	}

	private void build(String[] args, String[] featuresArg, boolean compose) {
		Configuration conf = CmdLineInterpreter.parseCmdLineArguments(args);
		conf.compose = compose;
		run(conf, featuresArg);
	}

	@Override
	protected void setupCompositionRuleset(Configuration conf) {
		compositionRules = new MetaRuleset();
	}

	@Override
	protected FSTNode compose(List<FSTNonTerminal> tl) {
		FSTNode composed = null;
		for (FSTNode current : tl) {
			if (metaproduct) {
				preProcessSubtree(current);
			}
			if (composed != null) {
				composed = compose(current, composed);
			} else
				composed = current;
		}
		if (metaproduct) {
			postProcess(composed);
		}
		return composed;
	}
	
	private void preProcessSubtree(FSTNode child) {
		if (child instanceof FSTNonTerminal) {
			if (child.getType().equals("MethodSpecification") && ((FSTNonTerminal) child).getChildren().isEmpty()) {
				FSTNonTerminal spec = new FSTNonTerminal("Specification", "-");
				((FSTNonTerminal) child).addChild(spec);
				(spec).addChild(new FSTTerminal("SpecCaseSeq", "-", "\\req FeatureModel." + getFeatureName(spec) + "\\or_original;", "", "ContractComposition"));
			} else {
				for (FSTNode node : ((FSTNonTerminal) child).getChildren()) {
					preProcessSubtree(node);
				}
			}
		} else if (child instanceof FSTTerminal) {
			CompositionRule rule = compositionRules.getRule(((FSTTerminal) child).getCompositionMechanism());
			if (rule != null) {
				rule.preCompose((FSTTerminal) child);
			}
		}
	}
	
	private void postProcess(FSTNode child) {
		if (child instanceof FSTNonTerminal) {
			for (FSTNode node : ((FSTNonTerminal) child).getChildren()) {
				postProcess(node);
			}
		} else if (child instanceof FSTTerminal) {
			CompositionRule rule = compositionRules.getRule(((FSTTerminal) child).getCompositionMechanism());
			if (rule != null) {
				rule.postCompose((FSTTerminal) child);
			}
		}
	}

	private static String getFeatureName(FSTNode node) {
		if (node.getType().equals("Feature"))
			return node.getName().toLowerCase() + (key ? "" : "()");
		else
			return getFeatureName(node.getParent());
	}
}
