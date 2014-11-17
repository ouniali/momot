package at.ac.tuwien.big.momot.examples.modularization;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.henshin.interpreter.EGraph;

import at.ac.tuwien.big.moea.fitness.dimension.IFitnessDimension.FunctionType;
import at.ac.tuwien.big.momot.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.fitness.dimension.MatchSolutionLengthDimension;
import at.ac.tuwien.big.momot.match.ExecutionResult;
import at.ac.tuwien.big.momot.match.MatchHelper;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class ArchitectureFitnessFunction extends EGraphMultiDimensionalFitnessFunction {

	public static final String ATTRIBUTE_STRING_MODEL = "ArchitectureStringModel";
	private static final int PENALTY = 1000;
	
	public ArchitectureFitnessFunction(EGraph initialGraph, MatchHelper matchHelper) {
		super(initialGraph, matchHelper);

		addObjective(new ArchitectureFitnessDimension("Coupling") {		
			@Override
			protected double evaluate(MatchSolution solution, EGraph eGraph, ArchitectureStringModel model) {
				return model.calculateCoupling();
			}
		});
		
		addObjective(new ArchitectureFitnessDimension("Cohesion", FunctionType.Maximum) {		
			@Override
			protected double evaluate(MatchSolution solution, EGraph eGraph, ArchitectureStringModel model) {
				return model.calculateCohesion();
			}
		});
		
//		addObjective(new ArchitectureFitnessDimension("NrClassesPerComponent", FunctionType.Minimum) {		
//			@Override
//			protected double evaluate(MatchSolution solution, EGraph eGraph, ArchitectureStringModel model) {
//				return model.calculateStdDevClassesPerComponent();
//			}
//		});
		
		addObjective(new MatchSolutionLengthDimension());
		
		addConstraint(new ArchitectureFitnessDimension("UnassignedClasses") {		
			@Override
			protected double evaluate(MatchSolution solution, EGraph eGraph, ArchitectureStringModel model) {
				return model.getUnassignedClasses().size() * PENALTY;
			}
		});
		
		addConstraint(new ArchitectureFitnessDimension("SingleComponent") {		
			@Override
			protected double evaluate(MatchSolution solution, EGraph eGraph, ArchitectureStringModel model) {
				Set<String> components = model.getComponentNames();
				if(components.size() <= 1)
					return CONSTRAINT_VIOLATED;
				return CONSTRAINT_OK;
			}
		});
		
		addConstraint(new ArchitectureFitnessDimension("EmptyComponent") {
			@Override
			protected double evaluate(MatchSolution solution, EGraph eGraph, ArchitectureStringModel model) {
				int empty = 0;
				Set<Entry<String, HashSet<String>>> components = model.getComponents().entrySet();
				for(Entry<String, HashSet<String>> entry : components) {
					if(entry.getValue().isEmpty())
						empty++;
				}
				return empty * PENALTY;
			}
		});
	}
	
	@Override
	protected void preprocessEvaluation(MatchSolution solution,	ExecutionResult executionResult) {
		super.preprocessEvaluation(solution, executionResult);
		solution.setAttribute(ATTRIBUTE_STRING_MODEL, 
				new ArchitectureStringModel(solution.getStoredResultGraph()));
	}
	
	@Override
	protected void postprocessEvaluation(MatchSolution solution, double result) {
		super.postprocessEvaluation(solution, result);
//		solution.removeAttribute(ATTRIBUTE_STRING_MODEL);
	}
}
