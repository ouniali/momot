package at.ac.tuwien.big.momot.examples.modularization;

import java.io.IOException;

import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;

import at.ac.tuwien.big.moea.run.executor.EvolutionarySearchExecutorFactory;
import at.ac.tuwien.big.moea.run.experiment.Experiment;
import at.ac.tuwien.big.moea.run.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.ui.MetricPlotter;
import at.ac.tuwien.big.momot.operator.mutation.MatchParameterMutation;
import at.ac.tuwien.big.momot.operator.mutation.MatchPlaceholderMutation;
import at.ac.tuwien.big.momot.rule.ParameterManager;
import at.ac.tuwien.big.momot.rule.parameter.IParameterValue;
import at.ac.tuwien.big.momot.rule.parameter.increment.IncrementalStringValue;
import at.ac.tuwien.big.momot.rule.parameter.increment.IncrementalStringValue.StringMode;
import at.ac.tuwien.big.momot.rule.parameter.random.RandomListValue;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class ArchitectureComponentCreation {
	public static void createComponentsMain(Integer maxComponents) throws IOException {
		ArchitectureOrchestration orchestration = new ArchitectureOrchestration(
				"model/", "architecture.henshin", "ArchitectureModelNoComponents.xmi", 10);
		
		orchestration.getRuleManager().ignoreRules(
				"createClass", "createClassDependency", "removeAssignment", "reassignClass");
		
		
		IParameterValue<String> componentNamesValue =
				new IncrementalStringValue("Component_", "a", "", StringMode.CAPITALIZED);
		if(maxComponents > 0) 
			componentNamesValue = new RandomListValue<String>(
					componentNamesValue, 
					maxComponents);
		
		ParameterManager parameterManager = orchestration.getParameterManager();
		parameterManager.setParameterValue("createComponent::componentName", componentNamesValue);
		
		EvolutionarySearchExecutorFactory<MatchSolution> problemExecutor = orchestration.createEvolutionaryExecutorFactory(
				50, 
				100,
				new TournamentSelection(2),
				new OnePointCrossover(1.0), 
				new MatchPlaceholderMutation(0.15),
				new MatchParameterMutation(0.1, orchestration.getParameterManager()))
			.setReferenceSetFile("output/components_reference_set.csv");

		Experiment experiment = new Experiment()
			.useAnalyzer(orchestration.createAnalyzer().includeAllMetrics().showAll())
			.useMetricPlotter(new MetricPlotter())
			.useProgressListener(new SeedRuntimePrintListener())
//			.useHypervolumeCollector(true)
			.saveReferenceFile("output/components_reference_set.csv")
			.printLogging(true);
		
		experiment.addExecutor("NSGA-II", problemExecutor.createNSGAIIExecutor());
		experiment.addExecutor("NSGA-III", problemExecutor.createNSGAIIIExecutor(0, 4));
		experiment.addExecutor("EpsilonMOEA", problemExecutor.createEpsilonMOEAExecutor(0.02));
		
		experiment.run(5);
		
		System.out.println(orchestration.print(experiment.getReferenceSet()));
//		experiment.show(false, AccumulatorUtil.Keys.INDICATOR_HYPERVOLUME);
//		experiment.printAnalysis();
	}
	
	public static void main(String[] args) throws IOException {
		createComponentsMain(4);
	}
}
