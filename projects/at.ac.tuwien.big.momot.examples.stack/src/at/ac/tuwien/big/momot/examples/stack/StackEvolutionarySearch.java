package at.ac.tuwien.big.momot.examples.stack;

import java.io.IOException;

import org.eclipse.ocl.ParserException;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;

import at.ac.tuwien.big.moea.run.executor.EvolutionarySearchExecutorFactory;
import at.ac.tuwien.big.moea.run.experiment.Experiment;
import at.ac.tuwien.big.moea.run.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.ui.MetricPlotter;
import at.ac.tuwien.big.moea.util.AccumulatorUtil;
import at.ac.tuwien.big.momot.MomotOrchestration;
import at.ac.tuwien.big.momot.operator.mutation.MatchParameterMutation;
import at.ac.tuwien.big.momot.operator.mutation.MatchPlaceholderMutation;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class StackEvolutionarySearch {	
	public static void executeEvolutionaryExperiment(MomotOrchestration orchestration) throws IOException {
		EvolutionarySearchExecutorFactory<MatchSolution> problemExecutor = orchestration.createEvolutionaryExecutorFactory(
				100,
				100,
				new TournamentSelection(2),
				new OnePointCrossover(1.0),
				new MatchParameterMutation(0.25, orchestration.getParameterManager()),
				new MatchPlaceholderMutation(0.15)
			).setReferenceSetFile("output/evolutionary_reference_set.csv");
				
		Experiment experiment = new Experiment()
			.useAnalyzer(orchestration.createAnalyzer().includeAllMetrics().showAll())
			.useMetricPlotter(new MetricPlotter())
			.useProgressListener(new SeedRuntimePrintListener())
			.useHypervolumeCollector(true)
			.saveReferenceFile("output/evolutionary_reference_set.csv")
			.printLogging(true);
		
		experiment.addExecutor("NSGA-II", problemExecutor.createNSGAIIExecutor());
		experiment.addExecutor("NSGA-III", problemExecutor.createNSGAIIIExecutor(0, 4));
		experiment.addExecutor("EpsilonMOEA", problemExecutor.createEpsilonMOEAExecutor(0.02));
		
		experiment.run(2);
		
		System.out.println(orchestration.print(experiment.getReferenceSet()));
		experiment.show(false, AccumulatorUtil.Keys.INDICATOR_HYPERVOLUME);
		experiment.printAnalysis();
	}
	
	public static void main(String[] args) throws IOException, ParserException {
		executeEvolutionaryExperiment(new StackOrchestration("model_five_stacks.xmi", 8));
	}
}
