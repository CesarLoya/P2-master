package fa.nfa;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dfa.DFA;
import fa.State;

/**
 * Implementation of NFA class 
 *
 */
public class NFA implements fa.FAInterface, NFAInterface {

	// Variables
	private Set<NFAState> states; // set of states
	private NFAState start; // the start state
	/* the alphabets - used in addTransition & getDFA functions */
	private Set<Character> alphabets;

	/**
	 * The default constructor instantiate state and alphabets variables.
	 * 
	 * @pram: none
	 */
	public NFA() {
		alphabets = new HashSet<Character>();
		states = new HashSet<NFAState>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fa.FAInterface#addStartState(java.lang.String)
	 */
	@Override
	public void addStartState(String name) {
		NFAState s = getState(name);
		if (s == null) {
			s = new NFAState(name);
			states.add(s);
		}
		start = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fa.FAInterface#addState(java.lang.String)
	 */
	@Override
	public void addState(String name) {
		NFAState state = new NFAState(name);
		states.add(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fa.FAInterface#addFinalState(java.lang.String)
	 */
	@Override
	public void addFinalState(String name) {
		NFAState state = new NFAState(name, true);
		states.add(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fa.FAInterface#addTransition(java.lang.String, char,
	 * java.lang.String)
	 */
	@Override
	public void addTransition(String fromState, char onSymbol, String toState) {

		(getState(fromState)).addTransition(onSymbol, getState(toState));
		if (!alphabets.contains(onSymbol) && onSymbol != 'e') {
			alphabets.add(onSymbol);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fa.FAInterface#getStartState()
	 */
	@Override
	public NFAState getStartState() {
		return start;
	}

	/**
	 * Check if a state with such label has already been created and returns
	 * either a new state or already created state.
	 * 
	 * @param name
	 * @return NFAState
	 */
	private NFAState getState(String name) {
		NFAState retVal = null;
		for (NFAState state : states) {
			if (state.getName().equals(name)) {
				retVal = state;
				break;
			}
		}
		return retVal;
	}


	/* (non-Javadoc)
	 * @see fa.FAInterface#isFinal(fa.State)
	 */
	@Override
	public boolean isFinal(State s) {
		for (NFAState nfa : states) {
			if (nfa.getName().equals(s.getName())) {
				return nfa.isFinal();
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see nfa.NFAInterface#getDFA()
	 */
	@Override
	public DFA getDFA() {
		DFA dfa = new DFA();
		/*Using LinkedList to act like a Queue data structure*/
		LinkedList<HashSet<NFAState>> queue = new LinkedList<HashSet<NFAState>>();
		/*A set of visited states*/
		Set<Set<NFAState>> isVisited = new HashSet<Set<NFAState>>();
		HashSet<NFAState> nStart = new HashSet<NFAState>(); // a set of new states
		nStart.add(start); // adding a start state
		nStart.addAll(eClosure(start, new HashSet<NFAState>()));
		boolean isFinalSt = false;
		/*For each nfa states we check to see if it is final, if so set isFinal to true*/
		for (NFAState nfa : nStart) {
			if (nfa.isFinal()) {
				isFinalSt = true;
			}
		}
		if (isFinalSt == true) { //if final state then we add new state to the dfa
			dfa.addFinalState(nStart.toString());
		}
		dfa.addStartState(nStart.toString());
		queue.addFirst(nStart); 
		while (!queue.isEmpty()) {
			/*while we have more states in queue, we dequeue then process*/
			HashSet<NFAState> temp = queue.removeLast();
			isVisited.add(temp);
			String current = temp.toString();
			for (Character cha : alphabets) {
				isFinalSt = false;
				HashSet<NFAState> nwSet = new HashSet<NFAState>();
				for (NFAState nfa : temp) {
					HashSet<NFAState> transition = nfa.getTo(cha);
					if (transition != null) {
						for (NFAState t : transition) {
							eClosure(t, nwSet);
							if (t.isFinal()) {
								isFinalSt = true;
							}
							nwSet.add(t);
						}
					}
				}
				String newState = nwSet.toString();
				/*if the state is final, haven't visited & is'nt in the queue then make it final*/
				if (isFinalSt == true) {
					if (!isVisited.contains(nwSet) && !queue.contains(nwSet)) {
						dfa.addFinalState(newState);
					}
					dfa.addTransition(current, cha, newState);
				} else {
					if (!isVisited.contains(nwSet) && !queue.contains(nwSet)) {
						dfa.addState(newState);
					}
					dfa.addTransition(current, cha, newState);
				}
				if (!isVisited.contains(nwSet) && !queue.contains(nwSet)) {
					queue.addFirst(nwSet);
				}
			}
		}
		return dfa;
	}

	/**
	 * Handles and sets the epsilon transitions using recursive calls
	 * Used in getDFA function above 
	 * @param state
	 * @param goTo
	 * @return goTo state
	 */
	private HashSet<NFAState> eClosure(NFAState state, HashSet<NFAState> goTo) {
		HashSet<NFAState> current = state.getTo('e');
		if (current != null) {
			for (NFAState nfa : current) {
				NFAState temp = nfa;
				goTo.add(temp);
				eClosure(temp, goTo);
			}
		}
		return goTo;
	}
}