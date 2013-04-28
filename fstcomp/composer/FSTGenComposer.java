package composer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import builder.ArtifactBuilderInterface;
import builder.capprox.CApproxBuilder;
import builder.java.JavaBuilder;

import composer.rules.CompositionRule;
import composer.rules.JavaMethodOverriding;
import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;
import composer.rulesets.CVarEncRuleset;
import composer.rulesets.DefaultRuleset;
import composer.rulesets.JavaVarEncRuleset;

import counter.Counter;
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
	}
	
	public void run() {
		JavaMethodOverriding.setFeatureAnnotation(conf.featureAnnotation);
		JavaRuntimeFunctionRefinement.setFeatureAnnotation(conf.featureAnnotation);

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

			featureVisitor.setWorkingDir(conf.getOutputDir());
			featureVisitor.setExpressionName(conf.equationFileName);
			
			for (ArtifactBuilderInterface builder : getArtifactBuilders()) {
				LinkedList<FSTNonTerminal> features = builder.getFeatures();

				if(conf.isCount && (builder instanceof JavaBuilder || builder instanceof CApproxBuilder)) {
					Counter counter = new Counter();
					for (FSTNonTerminal feature : features) {
						counter.collect(feature);
					}
					if(features.size() > 0)
						counter.writeFile(new File(conf.equationFileName + ".rsf"));
				}

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

			String equationName = new File(conf.equationFileName).getName();
			equationName = equationName.substring(0, equationName.length() - 4);

			if (conf.featureAnnotation) {
				File srcDir = new File(conf.getOutputDir(), equationName);
				saveFeatureAnnotationFile(srcDir);
				if (conf.lifting && "java".equals(conf.lifting_language.toLowerCase())) {
					saveSwitchIDAnnotationFile(srcDir);
				}
			}

		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
		}
	}

	private void saveFeatureAnnotationFile(File srcDir) {
		File f = new File(srcDir+File.separator+"featureHouse"+File.separator, "FeatureAnnotation.java");
		f.getParentFile().mkdirs();
		System.out.println("writing FeatureAnnotation to file " +  f.getAbsolutePath());
		try (FileWriter fw = new FileWriter(f)) {
			String contents =
				"package featureHouse;\n"+
				"import java.lang.annotation.ElementType;\n" +
				"import java.lang.annotation.Retention;\n" +
				"import java.lang.annotation.RetentionPolicy;\n" +
				"import java.lang.annotation.Target;\n" +
	
				"@Retention(RetentionPolicy.RUNTIME)\n" +
				"@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})\n" +
				"public @interface FeatureAnnotation {\n" +
				"	String name();\n" +
				"}";
			fw.write(contents);
		} catch (IOException e) {
			System.err.println("Could not write FeatureAnnotation.java " + e.getMessage());
		}
	}

	private void saveSwitchIDAnnotationFile(File srcDir) {
		File f = new File(srcDir+File.separator+"featureHouse"+File.separator, "FeatureSwitchID.java");
		f.getParentFile().mkdirs();
		System.out.println("writing FeatureSwitchID to file " +  f.getAbsolutePath());
		try (FileWriter fw = new FileWriter(f)) {
			String contents =
				"package featureHouse;\n"+
				"import java.lang.annotation.ElementType;\n" +
				"import java.lang.annotation.Retention;\n" +
				"import java.lang.annotation.RetentionPolicy;\n" +
				"import java.lang.annotation.Target;\n" +
	
				"@Retention(RetentionPolicy.RUNTIME)\n" +
				"@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})\n" +
				"public @interface FeatureSwitchID {\n" +
				"	int id();\n" +
				"	String thenFeature();\n" +
				"	String elseFeature();\n" +
				"}";
			fw.write(contents);
		} catch (IOException e) {
			System.err.println("Could not write FeatureSwitchID.java " + e.getMessage());
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
				if (conf.featureAnnotation) {
					addAnnotationToChildrenMethods(current, JavaMethodOverriding.getFeatureName(current));
				}
				composed = current;
			}
		}
		return composed;
	}
	private void addAnnotationToChildrenMethods(FSTNode current,
			String featureName) {
		if (current instanceof FSTNonTerminal) {
			for (FSTNode child : ((FSTNonTerminal)current).getChildren())
				addAnnotationToChildrenMethods(child, featureName);
		} else if (current instanceof FSTTerminal) {
			if ("MethodDecl".equals(current.getType()) || 
					"ConstructorDecl".equals(current.getType())) {
				String body = ((FSTTerminal)current).getBody();
				((FSTTerminal)current).setBody(JavaMethodOverriding.featureAnnotationPrefix + featureName +"\")\n" + body);
			}
		} else {
			throw new RuntimeException("Somebody has introduced a subclass of FSTNode \"" + 
				current.getClass().getName() 
				+ "\" that is not considered by the annotation option.");
		}
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
								.introduce(null, childB.getDeepClone(), nonterminalComp);

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
								.introduce(childA.getDeepClone(), null, nonterminalComp);

						if (conf.featureAnnotation) {
							if (newChildA instanceof FSTNonTerminal) {
								addAnnotationToChildrenMethods(newChildA, JavaMethodOverriding.getFeatureName(childA));
							} else if (newChildA instanceof FSTTerminal) {
								if ("MethodDecl".equals(newChildA.getType()) ||
										"ConstructorDecl".equals(newChildA.getType())) {
									FSTTerminal termNewChildA = (FSTTerminal) newChildA;
									String body = termNewChildA.getBody();
									String feature = JavaMethodOverriding.getFeatureName(childA);
									termNewChildA.setBody(JavaMethodOverriding.featureAnnotationPrefix + feature +"\")\n" + body);
								}
							}
						}
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