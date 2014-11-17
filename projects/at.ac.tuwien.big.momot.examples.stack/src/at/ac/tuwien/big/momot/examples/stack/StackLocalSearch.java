package at.ac.tuwien.big.momot.examples.stack;

import java.io.IOException;

import org.eclipse.ocl.ParserException;

import at.ac.tuwien.big.moea.fitness.comparator.ObjectiveFitnessComparator;
import at.ac.tuwien.big.moea.run.collector.LocalBestFitnessCollector;
import at.ac.tuwien.big.moea.run.executor.LocalSearchExecutorFactory;
import at.ac.tuwien.big.moea.run.experiment.Experiment;
import at.ac.tuwien.big.moea.run.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.ui.MetricPlotter;
import at.ac.tuwien.big.moea.util.AccumulatorUtil;
import at.ac.tuwien.big.momot.MomotOrchestration;
import at.ac.tuwien.big.momot.algorithm.local.neighborhood.IncreasingNeighborhoodFunction;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class StackLocalSearch {	
	public static void executeLocalExperiment(MomotOrchestration orchestration) throws IOException {
		LocalSearchExecutorFactory<MatchSolution> localSearchExecutorFactory = orchestration.createLocalExecutorFactory(
				2000, // maximum number of evaluations
				new IncreasingNeighborhoodFunction(orchestration.getProblem(), 100), // at most 100 neighbors
				new ObjectiveFitnessComparator<MatchSolution>(
						orchestration.getFitnessFunction().getObjectiveIndex("Standard Deviation")),
				orchestration.createNewSolution(0))
			.setReferenceSetFile("output/local_reference_set.csv");
		
		Experiment experiment = new Experiment()
			.useAnalyzer(orchestration.createAnalyzer().includeAllMetrics().showAll())
			.useMetricPlotter(new MetricPlotter())
			.useProgressListener(new SeedRuntimePrintListener())
			.saveReferenceFile("output/evolutionary_reference_set.csv")
			.printLogging(true);
	
		experiment.addExecutor("HillClimbing", localSearchExecutorFactory.createHillClimbingExecutor());
		experiment.addExecutor("RandomDescent", localSearchExecutorFactory.createRandomDescentExecutor());
		experiment.useCollector(new LocalBestFitnessCollector());

		experiment.run(1);
	
		experiment.getMetricPlotter().show(true, AccumulatorUtil.Keys.LOCAL_BEST_FITNESS, AccumulatorUtil.Keys.ELAPSED_TIME);
		System.out.println(orchestration.print(experiment.getReferenceSet()));
	}
	
	public static void main(String[] args) throws ParserException, IOException {
		executeLocalExperiment(new StackOrchestration("model_five_stacks.xmi", 8));
	}
}
