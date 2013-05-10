package composer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import builder.ArtifactBuilderInterface;

import composer.rules.CompositionRule;
import composer.rulesets.CVarEncRuleset;
import composer.rulesets.CounterRuleset;
import composer.rulesets.DefaultRuleset;
import composer.rulesets.FeatureAnnotationRuleset;
import composer.rulesets.JavaVarEncRuleset;

import de.ovgu.cide.fstgen.ast.AbstractFSTParser;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class FSTGenComposer extends FSTGenProcessor {

	protected Configuration conf;
	
	protected CompositionRuleset compositionRules;

	public FSTGenComposer() {
		super();
	}

	@SuppressWarnings("unchecked")
	public FSTGenComposer(boolean rememberFSTNodes) {
		super();
		if (!rememberFSTNodes) {
			setFstnodes((ArrayList<FSTNode>)AbstractFSTParser.fstnodes.clone());
			AbstractFSTParser.fstnodes.clear();
		}
	}

	public FSTGenComposer(Configuration conf) {
		this.conf = conf;
	}

	protected void setupCompositionRuleset() {
		//variability encoding uses special rules
		if (conf.lifting) {
			if (conf.lifting_language.equals("c")) { 
				compositionRules = new CVarEncRuleset();
								
			} else if (conf.lifting_language.equals("java")) {
				compositionRules = new JavaVarEncRuleset();
					
			} else {
				throw new InternalError("lifting language \"" + conf.lifting_language + "\" is not implemented.");
			}
		} else {
			//default rules
			compositionRules = new DefaultRuleset();
		}
		if (conf.isCount) {
			compositionRules = new CounterRuleset(compositionRules);
		}
		if (conf.featureAnnotation) {
			compositionRules = new FeatureAnnotationRuleset(compositionRules);
		}
	}
	
	public void run() {
		//select the composition rules
		setupCompositionRuleset();
		compositionRules.initializeComposition();
		try {
			try {
				fileLoader.loadFiles(conf.equationFileName, conf.equationBaseDirectoryName, conf.isAheadEquationFile);
			} catch (cide.gparser.ParseException e1) {
				System.out.println("error");
				fireParseErrorOccured(e1);
				e1.printStackTrace();
			}

			featureVisitor.setWorkingDir(conf.getOutputDir().getAbsolutePath());
			featureVisitor.setExpressionName(conf.equationFileName);
			
			for (ArtifactBuilderInterface builder : getArtifactBuilders()) {
				LinkedList<FSTNonTerminal> features = builder.getFeatures();

				compositionRules.preCompose(builder, features);

				FSTNode composition = compose(features);

				compositionRules.postCompose(builder, features, composition);
//				modify(composition);

				/* 
				 * hook for general purpose visitors
				 */
				 /*	if (null != composition)
        				for (FSTVisitor visitor: getFSTVisitors()) {
        				    composition.accept(visitor);
        				}
				*/

			}
			setFstnodes(AbstractFSTParser.fstnodes);
			compositionRules.finalizeComposition();

		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Configuration conf = CmdLineInterpreter.parseCmdLineArguments(args);
		FSTGenComposer composer = new FSTGenComposer(conf);
		composer.run();
	}

	private FSTNode compose(List<FSTNonTerminal> tl) {
		FSTNode composed = null;
		for (FSTNode current : tl) {
			if (composed != null) {
				composed = compose(current, composed);
			} else {
				composed = current;
			}
		}
		return composed;
	}


	public FSTNode compose(FSTNode nodeA, FSTNode nodeB) {
		return compose(nodeA, nodeB, null);
	}

	public FSTNode compose(FSTNode nodeA, FSTNode nodeB,
			FSTNode compParent) {

		if (nodeA.compatibleWith(nodeB)) {
			FSTNode compNode = nodeA.getShallowClone();
			compNode.setParent(compParent);

			// composed SubTree-stub is integrated in the new Tree, needs
			// children
			if (nodeA instanceof FSTNonTerminal
					&& nodeB instanceof FSTNonTerminal) {
				FSTNonTerminal nonterminalA = (FSTNonTerminal) nodeA;
				FSTNonTerminal nonterminalB = (FSTNonTerminal) nodeB;
				FSTNonTerminal nonterminalComp = (FSTNonTerminal) compNode;

				for (FSTNode childB : nonterminalB.getChildren()) {
					FSTNode childA = nonterminalA.getCompatibleChild(childB);
					// for each child of B get the first compatible child of A
					// (CompatibleChild means a Child which root equals B's
					// root)
					if (childA == null) {
						// no compatible child, FST-node only in B

						FSTNode newChildB = compositionRules.getIntroductionRule()
								.introduce(null, childB, nonterminalComp);

						nonterminalComp.addChild(newChildB);
					} else {
						nonterminalComp.addChild(compose(childA, childB,
								nonterminalComp));
					}
				}
				for (FSTNode childA : nonterminalA.getChildren()) {
					FSTNode childB = nonterminalB.getCompatibleChild(childA);
					if (childB == null) {
						// no compatible child, FST-node only in A
						FSTNode newChildA = compositionRules.getIntroductionRule()
								.introduce(childA, null, nonterminalComp);

						nonterminalComp.addChild(newChildA);
					}
				}
				return nonterminalComp;
			} else if (nodeA instanceof FSTTerminal
					&& nodeB instanceof FSTTerminal
					&& compParent instanceof FSTNonTerminal) {
				FSTTerminal terminalA = (FSTTerminal) nodeA;
				FSTTerminal terminalB = (FSTTerminal) nodeB;
				FSTTerminal terminalComp = (FSTTerminal) compNode;
				FSTNonTerminal nonterminalParent = (FSTNonTerminal) compParent;

				CompositionRule applicableRule = null;
				//get applicable rule from compositionRules
				applicableRule = compositionRules.getRule(terminalA.getCompositionMechanism());

				if (applicableRule != null) {
					//apply composition rule
					applicableRule.compose(terminalA, terminalB, terminalComp, nonterminalParent);
				} else {
					System.err
							.println("Error: don't know how to compose terminals: "
									+ terminalB.toString()
									+ " replaces "
									+ terminalA.toString());
				}
				return terminalComp;
			}
			return null;
		} else
			return null;
	}
}