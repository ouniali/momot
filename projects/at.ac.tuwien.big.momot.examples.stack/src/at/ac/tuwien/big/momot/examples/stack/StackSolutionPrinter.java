package at.ac.tuwien.big.momot.examples.stack;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;

import at.ac.tuwien.big.moea.util.EcoreUtil;
import at.ac.tuwien.big.momot.fitness.IEGraphFitnessFunction;
import at.ac.tuwien.big.momot.match.ExecutionResult;
import at.ac.tuwien.big.momot.solution.MatchSolution;
import at.ac.tuwien.big.momot.solution.printer.MatchSolutionPrinter;

public class StackSolutionPrinter extends MatchSolutionPrinter {

	public StackSolutionPrinter(IEGraphFitnessFunction fitnessFunction) {
		super(fitnessFunction);
	}
	
	@Override
	public String print(MatchSolution solution) {
		String txt = super.print(solution);
		txt += "------------\n";
		txt += printExecutionResult(solution);
		return txt;
	}
	
	@Override
	public String print(EGraph graph) {
		String result = "";
		String delim = "";
		for(EObject obj : graph) {
			if(EcoreUtil.isEClass(obj, "Stack")) {
				String id = EcoreUtil.getFeatureValue(obj, "id", String.class);
				Integer load = EcoreUtil.getFeatureValue(obj, "load", Integer.class);
				result += delim + id + ": " + load;
				delim = "\n";
			}
		}
		return result;
	}
	
	public String printExecutionResult(MatchSolution solution) {
		EGraph result = solution.getStoredResultGraph();
		if(result != null)
			return print(result);

		ExecutionResult executionResult = solution.execute(getFitnessFunction());
		return print(executionResult.getResultGraph());
	}
}
