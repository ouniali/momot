package at.ac.tuwien.big.momot.examples.modularization;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;

import at.ac.tuwien.big.moea.run.executor.EvolutionarySearchExecutorFactory;
import at.ac.tuwien.big.moea.run.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.momot.MomotFactory;
import at.ac.tuwien.big.momot.operator.mutation.MatchParameterMutation;
import at.ac.tuwien.big.momot.operator.mutation.MatchPlaceholderMutation;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class ArchitectureClassAssignment {
	
	public static EGraph createAssignmentModel(MomotFactory factory, String resource) {
		ArchitectureStringModel model = new ArchitectureStringModel()
		.addComponent("ComponentA")
		.addComponent("ComponentB")
		.addDependency("A", "B")
		.addDependency("B", "A")
		.addDependency("C", "D")
		.addDependency("D", "C")
		.addDependency("D", "B")
		;
		
		System.out.println(model.toString(true));
	
		EGraph createdGraph = model.toEGraph(factory);
		factory.saveGraph(createdGraph, resource);
		return createdGraph;
	}
	
	public static void main(String[] args) throws IOException {
		MomotFactory factory = new MomotFactory("model/", "architecture.henshin");
		EGraph initialGraph = createAssignmentModel(factory, "TwoComponentsFourClasses.xmi");
		ArchitectureOrchestration orchestration = new ArchitectureOrchestration(factory, initialGraph, 8);
			
		orchestration.getRuleManager().ignoreRules("createClass", "createClassDependency", "removeAssignment", "createComponent", "reassignClass");
			
		EvolutionarySearchExecutorFactory<MatchSolution> problemExecutor = orchestration.createEvolutionaryExecutorFactory(50, 100)
				.setSelection(new TournamentSelection(2))
				.addVariations(new OnePointCrossover(1.0), new MatchPlaceholderMutation(0.15), new MatchParameterMutation(0.15, orchestration.getParameterManager()));

		Analyzer analyzer = orchestration.createAnalyzer().includeAllMetrics().showAll();
		File referenceFile = new File("output/assignment_reference_set.csv");
		FileUtils.touch(referenceFile);
		
		System.out.println("Start executions...");
		
		Executor executor = problemExecutor.createNSGAIIExecutor().withProgressListener(new SeedRuntimePrintListener());
		analyzer.addAll("EpsilonMOEA", executor.runSeeds(5));
		System.out.println("EpsilonMOEA-Results added.");
		
		analyzer.saveReferenceSet(referenceFile);
		System.out.println(orchestration.print(analyzer.getReferenceSet()));
	}
}
