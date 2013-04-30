package composer.rules;

import composer.rules.Introduction;
import composer.rules.JavaMethodOverriding;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class FeatureAnnotatingIntroduction extends Introduction {

	public FeatureAnnotatingIntroduction(Introduction introduction) {
		super(null);
		metadataStore = introduction.metadataStore;
	}

	@Override
	public FSTNode introduce(FSTNode childA, FSTNode childB, FSTNonTerminal compParent) {
		FSTNode result = super.introduce(childA, childB, compParent);
		if (childA != null && childB == null) {
			FSTNode newChildA = result;
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

	public void addAnnotationToChildrenMethods(FSTNode current, String featureName) {
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