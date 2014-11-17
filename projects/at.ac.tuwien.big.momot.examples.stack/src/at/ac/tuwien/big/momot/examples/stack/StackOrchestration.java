package at.ac.tuwien.big.momot.examples.stack;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import at.ac.tuwien.big.moea.util.CollectionUtil;
import at.ac.tuwien.big.moea.util.EcoreUtil;
import at.ac.tuwien.big.moea.util.MathUtil;
import at.ac.tuwien.big.momot.MomotOrchestration;
import at.ac.tuwien.big.momot.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.fitness.dimension.MatchSolutionLengthDimension;
import at.ac.tuwien.big.momot.match.ExecutionResult;
import at.ac.tuwien.big.momot.rule.ParameterManager;
import at.ac.tuwien.big.momot.rule.parameter.random.RandomIntegerValue;
import at.ac.tuwien.big.momot.solution.MatchSolution;
import at.ac.tuwien.big.momot.solution.repair.MatchPlaceholderSolutionRepairer;

public class StackOrchestration extends MomotOrchestration {

	private int maxShift = 5;
	
	public StackOrchestration(String initialGraphPath, int nrVariables) {
		super("model/", "stack.henshin", initialGraphPath, nrVariables);
		
		IEGraphMultiDimensionalFitnessFunction fitnessFunction = getFitnessFunction();
		fitnessFunction.setSolutionRepairer(new MatchPlaceholderSolutionRepairer()); // replace not executed rules with empty rules
		
		fitnessFunction.addObjective(new AbstractEGraphFitnessDimension("Standard Deviation") {
			@Override
			protected double evaluate(MatchSolution solution, ExecutionResult executionResult) {
				List<Integer> loads = new ArrayList<Integer>();
				for(EObject obj : executionResult.getResultGraph())
					CollectionUtil.addNonNull(loads, EcoreUtil.getFeatureValue(obj, "load", Integer.class));
				
				return MathUtil.getStandardDeviation(loads);
			}
		});
		
		fitnessFunction.addObjective(new MatchSolutionLengthDimension());
		
		setSolutionPrinter(new StackSolutionPrinter(getFitnessFunction()));
		
		getRuleManager().ignoreRules("createStack", "connectStacks");	// defined in same file, but not applicable
		
		ParameterManager parameterManager = getParameterManager();
		parameterManager.preserveParameters("shiftLeft.fromId", "shiftLeft.toId", "shiftRight.fromId", "shiftRight.toId");
		parameterManager.setParameterValue("shiftLeft.amount", new RandomIntegerValue(1, getMaxShift()));
		parameterManager.setParameterValue("shiftRight.amount", new RandomIntegerValue(1, getMaxShift()));
	}

	public void setMaxShift(int maxShift) {
		this.maxShift = maxShift;
		getParameterManager().setParameterValue("shiftLeft.amount", new RandomIntegerValue(1, getMaxShift()));
		getParameterManager().setParameterValue("shiftRight.amount", new RandomIntegerValue(1, getMaxShift()));
	}
	
	public int getMaxShift() {
		return maxShift;
	}
}
