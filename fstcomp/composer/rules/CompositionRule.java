package composer.rules;

import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * controls how FSTTerminal nodes are merged
 *
 */
public interface CompositionRule {

	/**
	 * 
	 * @return name of the composition rule e.g. "JavaMethodOverriding"
	 */
	public String getRuleName();
	
	
	/**
	 * composes terminal nodes.
	 * 
	 * @param terminalA
	 * @param terminalB
	 * @param terminalComp
	 * @param nonterminalParent
	 */
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent);
	
	/**
	 * This method is called on the given terminal node before composition. 
	 * @param terminal
	 */
	public void preCompose(FSTTerminal terminal);

	/**
	 * This method is called on the given terminal node after composition. 
	 * @param terminal
	 */
	public void postCompose(FSTTerminal terminal);
	
}
