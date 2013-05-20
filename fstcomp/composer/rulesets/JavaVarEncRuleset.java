package composer.rulesets;

import java.io.File;
import java.io.IOException;

import composer.Configuration;
import composer.rules.rtcomp.java.JavaRuntimeFeatureSelection;
import composer.rules.rtcomp.java.JavaRuntimeFunctionRefinement;

public class JavaVarEncRuleset extends VarEncRuleset {

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new JavaRuntimeFunctionRefinement());
	}
	
	@Override
	protected void generateSimulator() {
		try {
			new JavaRuntimeFeatureSelection(metadataStore, cnfFile).saveTo(new File(conf.outputDirectoryName));
		} catch (IOException e) {
			System.err.println("Error generating program simulator:");
			e.printStackTrace();
		}		
	}
}
