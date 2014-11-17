package at.ac.tuwien.big.momot.examples.modularization;

import org.eclipse.emf.henshin.interpreter.EGraph;

import at.ac.tuwien.big.momot.fitness.IEGraphFitnessFunction;
import at.ac.tuwien.big.momot.match.ExecutionResult;
import at.ac.tuwien.big.momot.solution.MatchSolution;
import at.ac.tuwien.big.momot.solution.printer.MatchSolutionPrinter;

public class ArchitectureSolutionPrinter extends MatchSolutionPrinter {
	public ArchitectureSolutionPrinter() { }
	
	public ArchitectureSolutionPrinter(IEGraphFitnessFunction fitnessFunction) {
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
		if(graph != null)
			return new ArchitectureStringModel(graph).toString();
		
		return null;
	}
	
	private String printExecutionResult(MatchSolution solution) {		
		ArchitectureStringModel model = solution.getAttribute(ArchitectureFitnessFunction.ATTRIBUTE_STRING_MODEL, ArchitectureStringModel.class);
		if(model != null)
			return "";
		
		String graph = print(solution.getStoredResultGraph());
		if(graph != null)
			return graph;

		if(getFitnessFunction() == null)
			return "";
		
		ExecutionResult executionResult = solution.execute(getFitnessFunction());
		return new ArchitectureStringModel(executionResult.getResultGraph()).toString();
	}
}
