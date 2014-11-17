package at.ac.tuwien.big.momot.match.monitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.BasicApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.impl.MatchImpl;

public class RuleApplicationMonitor extends BasicApplicationMonitor {
	
	private boolean onlySuccessful = false;

	private List<Match> completeMatches = new ArrayList<Match>();
	private List<Match> partialMatches = new ArrayList<Match>();
	private List<Match> resultMatches = new ArrayList<Match>();
	
	public void notifyExecute(UnitApplication application, boolean success) {
		if(isOnlySuccessful() && !success)
			return;
		
		if(!(application instanceof RuleApplication))
			return;
				
		RuleApplication app = (RuleApplication) application;
		addNonNull(completeMatches, app.getCompleteMatch(), false);
		addNonNull(partialMatches, app.getPartialMatch(), false);
		addNonNull(resultMatches, app.getResultMatch(), true);
	}
	
	public void addNonNull(List<Match> list, Match element, boolean isResult) {
		if(element != null)
			list.add(new MatchImpl(element, isResult));
	}
	
	public <T> void addNonNull(List<T> list, T element) {
		if(element != null)
			list.add(element);
	}
	
	public boolean isOnlySuccessful() {
		return onlySuccessful;
	}
	
	public RuleApplicationMonitor setOnlySuccessful(boolean onlySuccessful) {
		this.onlySuccessful = onlySuccessful;
		return this;
	}

	public List<Match> getCompleteMatches() {
		return completeMatches;
	}

	public List<Match> getPartialMatches() {
		return partialMatches;
	}

	public List<Match> getResultMatches() {
		return resultMatches;
	}
	
	public boolean toFile(String file) {
		try {
			new PrintWriter(file).append(toString()).flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public String toString() {
		String result = new String();
		result += "=== Complete Matches ===\n";
		result += getCompleteMatches().toString();
		
		result += "=== Partial  Matches ===\n";
		result += getPartialMatches().toString();
		
		result += "=== Result   Matches ===\n";
		result += getResultMatches().toString();
		
		return result;
	}
}
