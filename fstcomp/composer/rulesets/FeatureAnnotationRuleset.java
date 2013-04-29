package composer.rulesets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import metadata.CompositionMetadataStore;

import composer.CompositionRuleset;
import composer.rules.Introduction;
import composer.rules.JavaMethodOverriding;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class FeatureAnnotationRuleset extends CompositionRulesetWrapper {

	private class FeatureAnnotatingIntroduction extends Introduction {

		public FeatureAnnotatingIntroduction(
				CompositionMetadataStore metadataStore) {
			super(metadataStore);
		}

		@Override
		public FSTNode introduce(FSTNode childA, FSTNode childB, FSTNonTerminal compParent) {
			FSTNode result = super.introduce(childA, childB, compParent);
			if (childA != null && childB == null) {
				FSTNode newChildA = childA.getDeepClone();
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
			return result;
		}
	}
	
	public FeatureAnnotationRuleset(CompositionRuleset wrappee) {
		super(wrappee);
	}

	@Override
	public void finalizeComposition() {
		super.finalizeComposition();
		String equationName = new File(conf.equationFileName).getName();
		equationName = equationName.substring(0, equationName.length() - 4);

		File srcDir = new File(conf.getOutputDir(), equationName);
		saveFeatureAnnotationFile(srcDir);
		if (conf.lifting && "java".equals(conf.lifting_language.toLowerCase())) {
			saveSwitchIDAnnotationFile(srcDir);
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
}