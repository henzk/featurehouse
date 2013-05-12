package composer.rulesets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import builder.ArtifactBuilderInterface;

import metadata.CompositionMetadataStore;
import composer.Configuration;
import composer.rules.CSharpMethodOverriding;
import composer.rules.CompositionError;
import composer.rules.ConstructorConcatenation;
import composer.rules.ExpansionOverriding;
import composer.rules.FieldOverriding;
import composer.rules.ImplementsListMerging;
import composer.rules.Introduction;
import composer.rules.JavaMethodOverriding;
import composer.rules.ModifierListSpecialization;
import composer.rules.Replacement;
import composer.rules.StringConcatenation;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public class DefaultRuleset extends AbstractCompositionRuleset {

	/**
	 * stores metadata about the composition process
	 * currently supports:
	 * -Java,C
	 * -Java,C with Variability encoding
	 */
	protected CompositionMetadataStore metadataStore;

	/**
	 * File where role metadata gets written to
	 */
	protected File roleFile;

	@Override
	public void configure(Configuration conf) {
		super.configure(conf);
		metadataStore = new CompositionMetadataStore();
		roleFile = new File(conf.outputDirectoryName + File.separator + "roles.meta");
		setIntroductionRule(new Introduction(metadataStore));
		setRule(new Replacement());
		setRule(new JavaMethodOverriding(metadataStore));
		setRule(new StringConcatenation());
		setRule(new ImplementsListMerging());
		setRule(new CSharpMethodOverriding());
		setRule(new ConstructorConcatenation());
		setRule(new ModifierListSpecialization());
		setRule(new FieldOverriding());
		setRule(new ExpansionOverriding());
		setRule(new CompositionError());
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
	 */
	@Override
	public void finalizeComposition() {
		super.finalizeComposition();
		if (conf.compose) {
			try {
				metadataStore.saveToFile(roleFile);
			} catch (IOException e) {
				System.err.println("Error writing roles metadata to `" + roleFile + "` :");
				e.printStackTrace();
			}
		}
	}
}