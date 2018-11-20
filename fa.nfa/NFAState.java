package fa.nfa;

import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * The NFA state to be used in this project.
 *
 */
public class NFAState extends fa.State {

	//Variables
	private HashMap<Character, LinkedHashSet<NFAState>> delta;
	private boolean isFinal;

	/**
	 * The constructor with one parameter
	 * 
	 * @param name
	 */
	public NFAState(String name) {
		initDefault(name); // Function call for initialization
		isFinal = false;
	}

	/**
	 * The constructor for the final state
	 * 
	 * @param name
	 * @param isFinal
	 */
	public NFAState(String name, boolean isFinal) {
		initDefault(name); // Function call for initialization
		this.isFinal = isFinal;
	}

	/**
	 * This is the default/basic initialization
	 * 
	 * @param name the label of the state
	 */
	private void initDefault(String name) {
		this.name = name;
		delta = new HashMap<Character, LinkedHashSet<NFAState>>();
	}

	/**
	 * Check whether the state is final.
	 * 
	 * @return return true if final else otherwise
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * Add the transition entry to the state
	 * 
	 * @param onSymb the alphabet symbol
	 * @param toState the state  @this transitions to
	 */
	public void addTransition(char onSymb, NFAState toState) {
		LinkedHashSet<NFAState> states = delta.get(onSymb);
		if(states != null){
			states.add(toState);
			delta.put(onSymb, states);
		}else{
			states = new LinkedHashSet<NFAState>();
			states.add(toState);
			delta.put(onSymb, states);
		}
	}

	/**
	 * Determine the state @this transitions to on a given symbol
	 * 
	 * @param symb
	 * @return the next DFA state
	 */
	public LinkedHashSet<NFAState> getTo(char symb) {
		LinkedHashSet<NFAState> tempTransition = delta.get(symb);
		if(tempTransition == null)
			return new LinkedHashSet<NFAState>();
		
		return delta.get(symb);
	}
}