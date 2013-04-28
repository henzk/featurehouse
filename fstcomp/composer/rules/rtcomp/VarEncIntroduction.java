package composer.rules.rtcomp;

import metadata.CompositionMetadataStore;
import composer.rules.Introduction;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public class VarEncIntroduction extends Introduction {

	private CompositionMetadataStore metadataStore;

	public VarEncIntroduction(CompositionMetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}

	@Override
	public FSTNode introduce(FSTNode a, FSTNode b, FSTNonTerminal compParent) {
		FSTNode result = super.introduce(a, b, compParent);
		metadataStore.discoverFuncIntroductions(result);
		return result;
	}
}
