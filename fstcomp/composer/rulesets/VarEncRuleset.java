package composer.rulesets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import composer.Configuration;
import composer.rules.rtcomp.VarEncIntroduction;

import builder.ArtifactBuilderInterface;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import metadata.CompositionMetadataStore;

/**
 *
 * implements language agnostic part of variability encoding:
 *
 * - records necessary metadata about features and roles
 * - saves role metadata to `roles.meta` in JSON format(e.g. for error path analysis with CPAChecker)
 *
 * To add language support for variability encoding:
 *
 * - subclass VarEncRuleset
 * - implement language specific CompostionRule that supports variability encoding.
 *   Implementations for C and Java both provide rules for Replacement and FuntionRefinement.
 *   Use these as a guide.
 * - implement generateSimulator: use implementations for C and JAVA as a guide.
 */
public abstract class VarEncRuleset extends DefaultRuleset {

	protected CompositionMetadataStore metadataStore;
	protected File roleFile;
	protected File cnfFile;

	/**
	 * -set path where role metadata is written
	 * -set path to feature model in CNF
	 *
	 * subclasses need to setup specific composition rules here!
	 */
	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		metadataStore = CompositionMetadataStore.getInstance();
		setIntroductionRule(new VarEncIntroduction(metadataStore));
		roleFile = new File(conf.outputDirectoryName + File.separator + "roles.meta");
		cnfFile = new File(conf.equationBaseDirectoryName, "model.cnf");
	}

	/**
	 * -clear stored metadata
	 */
	@Override
	public void initializeComposition() {
		super.initializeComposition();
		metadataStore.clearFeatures();
	}

	/**
	 * -save metadata about features
	 */
	@Override
	public void preCompose(ArtifactBuilderInterface builder,
			List<FSTNonTerminal> features) {
		super.preCompose(builder, features);
		for (FSTNonTerminal feature : features) {
			metadataStore.addFeature(feature.getName());
		}
	}

	/**
	 * -writes metadata about roles to disk
	 * -triggers generation of the program simulator
	 */
	@Override
	public void finalizeComposition() {
		super.finalizeComposition();

		try {
			metadataStore.saveToFile(roleFile);
		} catch (IOException e) {
			System.err.println("Error writing roles metadata to `" + roleFile + "` :");
			e.printStackTrace();
		}
		//trigger language specific generation of the program simulator
		generateSimulator();
	}

	/**
	 * to be implemented by subclass
	 *
	 * needs to generate the language specific program simulator to
	 * allow family based analysis.
	 */
	abstract protected void generateSimulator();
}