package composer.rulesets;

import java.io.IOException;

import composer.Configuration;
import composer.rules.rtcomp.c.CRuntimeFeatureSelection;
import composer.rules.rtcomp.c.CRuntimeFunctionRefinement;
import composer.rules.rtcomp.c.CRuntimeReplacement;

public class CVarEncRuleset extends VarEncRuleset {
	
	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		setRule(new CRuntimeReplacement());
		setRule(new CRuntimeFunctionRefinement());
	}

	@Override
	protected void generateSimulator() {
		try {
			new CRuntimeFeatureSelection(metadataStore, cnfFile).saveTo(conf.getOutputDir());
		} catch (IOException e) {
			System.err.println("Error while generating program simulator:");
			e.printStackTrace();
		}
	}
}
