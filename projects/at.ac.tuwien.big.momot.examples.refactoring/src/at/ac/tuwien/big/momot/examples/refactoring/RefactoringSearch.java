package at.ac.tuwien.big.momot.examples.refactoring;

import java.io.IOException;

import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.ocl.ParserException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;

import at.ac.tuwien.big.moea.run.executor.EvolutionarySearchExecutorFactory;
import at.ac.tuwien.big.moea.run.experiment.Experiment;
import at.ac.tuwien.big.moea.run.listener.RuntimePrintListener;
import at.ac.tuwien.big.moea.run.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.momot.MomotOrchestration;
import at.ac.tuwien.big.momot.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.fitness.dimension.MatchSolutionLengthDimension;
import at.ac.tuwien.big.momot.fitness.dimension.OCLQueryDimension;
import at.ac.tuwien.big.momot.operator.mutation.MatchPlaceholderMutation;
import at.ac.tuwien.big.momot.solution.MatchSolution;
import at.ac.tuwien.big.momot.solution.repair.MatchPlaceholderSolutionRepairer;

public class RefactoringSearch {
	
	public static void executeEvolutionarySearch(MomotOrchestration orchestration) throws IOException {
		System.out.println(orchestration.print(orchestration.getInitialGraph()));
		
		EvolutionarySearchExecutorFactory<MatchSolution> problemExecutor = orchestration.createEvolutionaryExecutorFactory(
				15,
				10,
				new TournamentSelection(2),
				new OnePointCrossover(1.0),  
				new MatchPlaceholderMutation(0.15))
//			.setReferenceSetFile("output/evolutionary_reference_set.csv")
			;
		
		Experiment experiment = new Experiment()
//			.useAnalyzer(orchestration.createAnalyzer().includeAllMetrics().showAll())
			.useAnalyzer(orchestration.createAnalyzer())
//			.useMetricPlotter(new MetricPlotter())
			.useProgressListener(new RuntimePrintListener())		
			.useProgressListener(new SeedRuntimePrintListener())
//			.useHypervolumeCollector(true)
			.saveReferenceFile("output/evolutionary_reference_set.csv")
			.printLogging(true);
	
		experiment.addExecutor("NSGA-II", problemExecutor.createNSGAIIExecutor());
//		experiment.addExecutor("NSGA-III", problemExecutor.createNSGAIIIExecutor(0, 4));
//		experiment.addExecutor("EpsilonMOEA", problemExecutor.createEpsilonMOEAExecutor(0.02));
//		experiment.addExecutor("RandomSearch", problemExecutor.createRandomSearchExecutor());

		experiment.run(3);
		
		NondominatedPopulation results = experiment.getReferenceSet();
		System.out.println(orchestration.print(results));
		for(Solution solution : results) {
			MatchSolution matchSolution = (MatchSolution) solution;
			EGraph graph = matchSolution.getStoredResultGraph();
			if(graph != null)
				orchestration.save(graph, "SeveralRefactorings_Solution_" + matchSolution.getNumberOfVariables(true) + ".xmi");
		}
		
//		experiment.show(false, AccumulatorUtil.Keys.INDICATOR_HYPERVOLUME);
//		experiment.printAnalysis();
	}
	
	public static MomotOrchestration createOrchestration() throws ParserException {
		final MomotOrchestration orchestration = new MomotOrchestration("model/", "Refactoring.henshin", "SeveralRefactorings.xmi", 10);
				
		IEGraphMultiDimensionalFitnessFunction fitnessFunction = orchestration.getFitnessFunction();
		fitnessFunction.saveExecutionResult(true);
		fitnessFunction.setSolutionRepairer(new MatchPlaceholderSolutionRepairer());
		
		fitnessFunction.addObjective(new OCLQueryDimension("ContentSize", 
				"propertys->size() * 1.1 + entitys->size()", 
				orchestration.createOCLHelper()));
		
		fitnessFunction.addObjective(new MatchSolutionLengthDimension());
		orchestration.setSolutionPrinter(new RefactoringSolutionPrinter(orchestration.getFitnessFunction()));
		
		return orchestration;
	}
	
	public static void main(String[] args) throws ParserException, IOException {
		executeEvolutionarySearch(createOrchestration());
	}
}
