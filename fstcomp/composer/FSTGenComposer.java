package composer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import printer.PrintVisitorException;

import builder.ArtifactBuilderInterface;

import composer.rules.CompositionRule;
import composer.rulesets.CVarEncRuleset;
import composer.rulesets.CounterRuleset;
import composer.rulesets.DefaultRuleset;
import composer.rulesets.FeatureAnnotationRuleset;
import composer.rulesets.JMLRuleset;
import composer.rulesets.JavaVarEncRuleset;

import de.ovgu.cide.fstgen.ast.AbstractFSTParser;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class FSTGenComposer extends FSTGenProcessor {

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

	protected void setupCompositionRuleset(Configuration conf) {

		//select relevant composition ruleset
		if (conf.lifting) {
			if (conf.lifting_language.equals("c")) { 
				compositionRules = new CVarEncRuleset();
			} else if (conf.lifting_language.equals("java")) {
				compositionRules = new JavaVarEncRuleset();
			} else {
				throw new InternalError("lifting language \"" + conf.lifting_language + "\" is not implemented.");
			}
		} else if (!conf.contract_style.equals("none")){
			compositionRules = new JMLRuleset();
		} else {
			compositionRules = new DefaultRuleset();
		}

		//wrap ruleset if necessary

		if (conf.isCount) {
			compositionRules = new CounterRuleset(compositionRules);
		}
		if (conf.featureAnnotation) {
			compositionRules = new FeatureAnnotationRuleset(compositionRules);
		}
	}

	protected void loadFiles(Configuration conf, String[] featureNames) throws IOException {
		if (featureNames == null) {
			featureNames = fileLoader.getFeaturesFromEquationFile(conf.equationFileName);
		}
		System.out.println("Found the following features:");
		for (String s : featureNames) {
			System.out.println(s);
		}
		try {
			fileLoader.parseFeatures(conf.equationBaseDirectoryName, !conf.isAheadEquationFile, featureNames);
		} catch (cide.gparser.ParseException e1) {
			System.out.println("error");
			fireParseErrorOccured(e1);
			e1.printStackTrace();
		}
	}

	/**
	 * run the composer
	 * 
	 * @param args command line arguments
	 */
	public void run(String[] args) {
		Configuration conf = CmdLineInterpreter.parseCmdLineArguments(args);
		run(conf);
	}

	/**
	 * run the composer
	 *
	 * @param conf specifies how and what to compose(usually set by command line switches)
	 */
	public void run(Configuration conf) {
		run(conf, null);
	}

	/**
	 * run the composer
	 *
	 * @param conf specifies how and what to compose(usually set by command line switches)
	 * @param featureNames if featureNames is null, the list of features to compose is read from the equation file specified in conf.
	 *    If non-null, the list of features given in featureNames overrides the ones from the equation file.
	 */
	public void run(Configuration conf, String[] featureNames) {

		//select relevant composition ruleset and configure it
		setupCompositionRuleset(conf);
		compositionRules.configure(conf);

		//invoke initializeComposition hook
		compositionRules.initializeComposition();

		//prepare parser, read feature equation
		try {
			loadFiles(conf, featureNames);
		} catch (IOException e) {
			System.err.println("IOException occured while loading files:");
			e.printStackTrace();
		}

		//configure output dir
		featureVisitor.setWorkingDir(conf.getOutputDir().getAbsolutePath());
		featureVisitor.setExpressionName(conf.equationFileName);

		//iterate over builders
		for (ArtifactBuilderInterface builder : getArtifactBuilders()) {

			//load FSTs
			LinkedList<FSTNonTerminal> features = builder.getFeatures();

			//invoke preCompose hook
			compositionRules.preCompose(builder, features);

			FSTNode composition = null;

			if (conf.compose) {
				//compose FSTs
				composition = compose(features);
			}

			//invoke postCompose hook (composition may be null)
			compositionRules.postCompose(builder, features, composition);

			//TODO Is this still needed?
//			modify(composition);
			/* 
			 * hook for general purpose visitors
			 */
			/*	if (null != composition)
				for (FSTVisitor visitor: getFSTVisitors()) {
				    composition.accept(visitor);
				}
			*/

			if (conf.compose) {
				//write out composed FST
				try {
					featureVisitor.visit((FSTNonTerminal) composition);
				} catch (PrintVisitorException e) {
					e.printStackTrace();
				}
			}
		}

		//remember result
		setFstnodes(AbstractFSTParser.fstnodes);
		//invoke finalizeComposition hook
		compositionRules.finalizeComposition();
		
	}

	public static void main(String[] args) {
		new FSTGenComposer().run(args);
	}

	protected FSTNode compose(List<FSTNonTerminal> tl) {
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