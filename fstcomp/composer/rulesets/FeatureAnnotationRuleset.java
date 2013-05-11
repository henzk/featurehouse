package composer.rulesets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import builder.ArtifactBuilderInterface;

import composer.CompositionRuleset;
import composer.Configuration;
import composer.rules.CompositionRule;
import composer.rules.FeatureAnnotatingIntroduction;
import composer.rules.Introduction;
import composer.rules.IntroductionRule;
import composer.rules.JavaMethodOverriding;
import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public class FeatureAnnotationRuleset extends CompositionRulesetWrapper {

	FeatureAnnotatingIntroduction introductionRule;

	public FeatureAnnotationRuleset(CompositionRuleset wrappee) {
		super(wrappee);
	}

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		IntroductionRule wrappedIntroductionRule = wrappee.getIntroductionRule();
		if (!(wrappedIntroductionRule instanceof Introduction)) {
			throw new RuntimeException("FeatureAnnotationRuleset needs to wrap a CompositionRuleset " +
					"with an IntroductionRule that inherits from composer.rules.Introduction");
		}
		introductionRule = new FeatureAnnotatingIntroduction((Introduction) wrappedIntroductionRule);

		//one of the following Rules must be used for "JavaMethodOverriding";otherwise feature annotations will not work
		JavaMethodOverriding.setFeatureAnnotation(conf.featureAnnotation);
		JavaRuntimeFunctionRefinement.setFeatureAnnotation(conf.featureAnnotation);

		//verify that valid rule is used for "JavaMethodOverriding"
		CompositionRule rule = getRule(JavaMethodOverriding.COMPOSITION_RULE_NAME);
		if (!(rule instanceof JavaMethodOverriding) ||
				!(rule instanceof JavaRuntimeFunctionRefinement)) {
			throw new RuntimeException("FeatureAnnotationRuleset needs to wrap a CompositionRuleset " +
					"that uses either JavaMethodOverriding or JavaRuntimeFunctionRefinement to function correctly!");
		}
	}

	@Override
	public void preCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features) {
		super.preCompose(builder, features);
		if (features.size() > 0) {
			FSTNonTerminal first = features.get(0);
			introductionRule.addAnnotationToChildrenMethods(first, JavaMethodOverriding.getFeatureName(first));
		}
	}

	@Override
	public void finalizeComposition() {
		super.finalizeComposition();
		if (conf.compose) {
			String equationName = new File(conf.equationFileName).getName();
			equationName = equationName.substring(0, equationName.length() - 4);

			File srcDir = new File(conf.getOutputDir(), equationName);
			saveFeatureAnnotationFile(srcDir);
			if (conf.lifting && "java".equals(conf.lifting_language.toLowerCase())) {
				saveSwitchIDAnnotationFile(srcDir);
			}
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
}