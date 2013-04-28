package composer.rulesets;

import java.io.IOException;

import composer.Configuration;
import composer.rules.rtcomp.java.JavaRuntimeFeatureSelection;
import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;
import composer.rules.rtcomp.java.JavaRuntimeReplacement;

public class JavaVarEncRuleset extends VarEncRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new JavaRuntimeReplacement());
		setRule(new JavaRuntimeFunctionRefinement());
	}
	
	@Override
	protected void generateSimulator() {
		try {
			new JavaRuntimeFeatureSelection(metadataStore, cnfFile).saveTo(conf.getOutputDir());
		} catch (IOException e) {
			System.err.println("Error generating program simulator:");
			e.printStackTrace();
		}		
	}
}
