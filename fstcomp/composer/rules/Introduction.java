package composer.rules;

import metadata.CompositionMetadataStore;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

/**
 * default introduction
 */
public class Introduction implements IntroductionRule {

	private CompositionMetadataStore metadataStore;

	public Introduction(CompositionMetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}

	/**
	 * `Func` introductions are discovered and stored in metadataStore.
	 * No transformations are applied to the introduced FST.
	 */
	@Override
	public FSTNode introduce(FSTNode a, FSTNode b, FSTNonTerminal compParent) {
		FSTNode result = (a != null) ? a : b;
		metadataStore.discoverFuncIntroductions(result);
		return result;
	}
}