package at.ac.tuwien.big.momot.examples.modularization;

import org.eclipse.emf.henshin.interpreter.EGraph;

import at.ac.tuwien.big.momot.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.match.ExecutionResult;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public abstract class ArchitectureFitnessDimension extends AbstractEGraphFitnessDimension {

	public ArchitectureFitnessDimension(String name) {
		super(name);
	}
	
	public ArchitectureFitnessDimension(String name, FunctionType type) {
		super(name, type);
	}

	@Override
	protected double evaluate(MatchSolution solution, ExecutionResult result) {
		ArchitectureStringModel model = solution.getAttribute(
				ArchitectureFitnessFunction.ATTRIBUTE_STRING_MODEL, 
				ArchitectureStringModel.class);			
		if(model == null)
			return WORST_FITNESS;
		return evaluate(solution, result.getResultGraph(), model);
	}

	protected abstract double evaluate(MatchSolution solution, EGraph eGraph,
			ArchitectureStringModel model);

}
