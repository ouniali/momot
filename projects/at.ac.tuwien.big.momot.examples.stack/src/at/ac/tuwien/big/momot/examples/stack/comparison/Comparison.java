package at.ac.tuwien.big.momot.examples.stack.comparison;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.time.StopWatch;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.binary.BitFlip;

import at.ac.tuwien.big.moea.algorithm.DynamicAlgorithmProvider;
import at.ac.tuwien.big.moea.algorithm.IAlgorithmCreator;
import at.ac.tuwien.big.moea.initialization.ExtendedRandomInitialization;
import at.ac.tuwien.big.moea.run.executor.EvolutionarySearchExecutorFactory;
import at.ac.tuwien.big.moea.run.executor.TerminatableExecutor;
import at.ac.tuwien.big.moea.run.experiment.Experiment;
import at.ac.tuwien.big.moea.run.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.util.MathUtil;
import at.ac.tuwien.big.momot.examples.stack.StackCreator;
import at.ac.tuwien.big.momot.examples.stack.StackOrchestration;
import at.ac.tuwien.big.momot.operator.mutation.MatchParameterMutation;
import at.ac.tuwien.big.momot.operator.mutation.MatchPlaceholderMutation;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class Comparison {
	public static Integer[] createRandomLoads(int nrStacks, int maxLoad) {
		Integer[] loads = new Integer[nrStacks];
		for(int i = 0; i < nrStacks; i++)
			loads[i] = new Random().nextInt(maxLoad);
		return loads;
	}
	
	public static Integer[] createRandomLoads(int nrStacks) {
		return createRandomLoads(nrStacks, nrStacks * 2);
	}
	
	public static Executor createNativeExecutor(final Integer[] initialLoads, final int populationSize, int maxEvaluations, String referenceFile) {
		String algorithmName = "NSGAII" + initialLoads.length;
		final Problem problem = new NativeStackProblem(initialLoads);
		
		DynamicAlgorithmProvider.registerAlgorithm(algorithmName,
				new IAlgorithmCreator<NSGAII>() {
					@Override
					public NSGAII newInstance() {		
						return new NSGAII(problem, 
								new NondominatedSortingPopulation(new ParetoDominanceComparator()), 
								null, 
								new TournamentSelection(2), 
								new CompoundVariation(new OnePointCrossover(1.0), new BitFlip(0.25), new EmptyFlip(0.15)), 
								new ExtendedRandomInitialization(problem, populationSize).logTime(true));
						}
				});
		
		Instrumenter instrumenter = new Instrumenter()
			.withProblemClass(problem.getClass(), Arrays.asList(initialLoads))
			.attachHypervolumeCollector()
			.withFrequency(populationSize)
			.withReferenceSet(new File(referenceFile))
			;
	
		Executor executor = new TerminatableExecutor()
			.withSameProblemAs(instrumenter)
			.withAlgorithm(algorithmName)
			.withProperty("populationSize", populationSize)
			.withProperty("maxEvaluations", maxEvaluations)
			.distributeOnAllCores()
			.withProgressListener(new SeedRuntimePrintListener())
//			.withInstrumenter(instrumenter)
			;
	
		return executor;
	}
	
	private static StackOrchestration orchestration; 
	
	public static Executor createTransformationExecutor(Integer[] initialLoads, int populationSize, int maxEvaluations, String referenceFile) {
		orchestration = new StackOrchestration(createGraph(initialLoads), initialLoads.length);
		
		int maxLoad = Integer.MIN_VALUE;
		for(int load : initialLoads)
			if(load > maxLoad)
				maxLoad = load;
		
		BitSet set = BitSet.valueOf(new long[] { maxLoad });
		int maxShift = (int) (Math.pow(2, set.length()) - 1);
		orchestration.setMaxShift(maxShift); // same max as in native solution		
		
		EvolutionarySearchExecutorFactory<MatchSolution> problemExecutor = orchestration.createEvolutionaryExecutorFactory(
				populationSize,
				maxEvaluations / populationSize,
				new TournamentSelection(2),
				new OnePointCrossover(1.0),
				new MatchParameterMutation(0.25, orchestration.getParameterManager()),
				new MatchPlaceholderMutation(0.15)
			);//.setReferenceSetFile(referenceFile);
		
		problemExecutor.getPopulationGenerator().logTime(true);
//		problemExecutor.getInstrumenter().attachHypervolumeCollector();	
		return problemExecutor.createNSGAIIExecutor(maxEvaluations).withProgressListener(new SeedRuntimePrintListener());
	}
	
	public static NondominatedPopulation referencePopulation(List<NondominatedPopulation> populations) {
		NondominatedPopulation referenceSet = new NondominatedPopulation(new ParetoDominanceComparator());
		for (NondominatedPopulation set : populations) 
			referenceSet.addAll(set);
		
		return referenceSet;
	}
	
	public static void compareExecutors(Integer[] initialLoads, int populationSize, int maxEvaluations, String referenceFile, int nrRuns) throws IOException {
		Executor transformationExecutor = createTransformationExecutor(initialLoads, populationSize, maxEvaluations, referenceFile);
		Executor nativeExecutor = createNativeExecutor(initialLoads, populationSize, maxEvaluations, referenceFile);
		
//		File referenceSet = new File(referenceFile);
		
//		Analyzer analyzer = new Analyzer()
//			.withSameProblemAs(nativeExecutor)
//			.includeAllMetrics()
//			.showStatisticalSignificance()
//			.showAll()
//			.showAggregate()
//			.withReferenceSet(referenceSet)
//			;
		
//		System.out.println("---------------------");
//		System.out.println("Solve  " + Arrays.toString(initialLoads) + " with " + populationSize + " solutions per population for " + maxEvaluations + " evaluations.");
//		System.out.println("Execute " + nrRuns + " runs per executor.");
//		System.out.println("PopulationSize: " + populationSize + ", MaxEvaluations: " + maxEvaluations);
//		System.out.println("---------------------");
//		System.out.println("Transformation");
//		System.out.println("---------------------");
//		List<NondominatedPopulation> results = 
		transformationExecutor.runSeeds(nrRuns);
//		
//		analyzer.addAll("Transformation", results);
//		
//		System.out.println("---------------------");
//		System.out.println("Native");
//		System.out.println("---------------------");
//		List<NondominatedPopulation> nativeResults = 
		nativeExecutor.runSeeds(nrRuns);
//		
//		analyzer.addAll("Native", nativeResults);
/*		
		System.out.println("-----------------------------");
		System.out.println("---------------------");
		System.out.println("Transformation");
		System.out.println("---------------------");
		System.out.println(orchestration.print(referencePopulation(results)));
		System.out.println();
		System.out.println("---------------------");
		System.out.println("Native");
		System.out.println("---------------------");
		NativeStackExample.printPopulation(referencePopulation(nativeResults), initialLoads);		
		System.out.println("-----------------------------");
		
		analyzer.printAnalysis();
*/
	}
	
	public static String toFilenameString(Integer[] loads) {
		String name = Arrays.toString(loads).replace("[", "").replace("]", "").replace(",", "").replace(" ", "-");
		if(name.length() > 30)
			return name.substring(0, 30);
		return name;
	}
	
	public static String createGraph(Integer[] loads) {
		String graphName = "comparison_" + toFilenameString(loads) + ".xmi";
		StackCreator.INSTANCE.createStacksGraph(loads, graphName);
		return graphName;
	}
	
	public static File generateReferenceSet(Integer[] loads) {
		File referenceFile = new File("referenceSets/comparison_" + toFilenameString(loads) + ".csv");
		if(referenceFile.exists()) {
			System.out.println("Reference File " + referenceFile + " already exists.");
			return referenceFile;
		}
		StopWatch watch = new StopWatch();
		watch.start();
		String graphName = createGraph(loads);
		orchestration = new StackOrchestration(graphName, loads.length);
		int maxLoad = Integer.MIN_VALUE;
		for(int load : loads)
			if(load > maxLoad)
				maxLoad = load;
		orchestration.setMaxShift(maxLoad); 
		
		EvolutionarySearchExecutorFactory<MatchSolution> problemExecutor = orchestration.createEvolutionaryExecutorFactory(
				100,
				50,
				new TournamentSelection(2),
				new OnePointCrossover(1.0),
				new MatchParameterMutation(0.25, orchestration.getParameterManager()),
				new MatchPlaceholderMutation(0.15)
			);
		
		Experiment experiment = new Experiment()
			.saveReferenceFile("referenceSets/comparison_" + toFilenameString(loads) + ".csv")
			.printLogging(true);
		
		experiment.addExecutor("NSGA-II", problemExecutor.createNSGAIIExecutor());
		experiment.addExecutor("NSGA-III", problemExecutor.createNSGAIIIExecutor(0, 4));
		experiment.addExecutor("EpsilonMOEA", problemExecutor.createEpsilonMOEAExecutor(0.02));
		experiment.run(25);

		watch.stop();
		System.out.println("Reference File generated: " + experiment.getReferenceFile() + " in " + watch);
		return experiment.getReferenceFile();
	}
	
	public static Integer[] getProblem15() {
		return new Integer[] { 23, 17, 6, 6, 20, 25, 29, 16, 21, 11, 20, 12, 25, 5, 8 };
	}
	
	public static Integer[] getProblem50() {
		return new Integer[] { 77, 49, 76, 41, 56, 86, 98, 0, 50, 76, 85, 98, 25, 57, 66, 13, 56, 75, 4, 31, 24, 23, 71, 17, 69, 87, 18, 27, 21, 54, 36, 91, 58, 12, 36, 98, 42, 52, 61, 18, 99, 67, 22, 67, 97, 20, 66, 89, 81, 84 };
	}
	
	public static Integer[] getProblem100() {
		return new Integer[] { 107, 145, 175, 191, 62, 35, 53, 179, 68, 45, 136, 89, 155, 90, 64, 146, 167, 48, 126, 84, 171, 124, 166, 45, 119, 52, 67, 88, 26, 134, 188, 147, 50, 158, 153, 99, 186, 113, 106, 138, 90, 17, 36, 59, 110, 92, 95, 14, 192, 97, 131, 191, 17, 56, 171, 71, 97, 36, 183, 195, 163, 182, 157, 82, 59, 173, 43, 66, 123, 147, 27, 37, 76, 49, 1, 66, 76, 139, 44, 191, 146, 173, 119, 46, 198, 176, 45, 166, 13, 69, 68, 194, 192, 136, 81, 45, 49, 160, 86, 153 };
	}
	
	public static void main(String[] args) throws IOException {
		Integer[] problem = createRandomLoads(50);
		System.out.println("Solving " + Arrays.toString(problem) + " (mean: " + MathUtil.getMean(problem) + ", stddev: " + MathUtil.getStandardDeviation(problem) + "):");
//		generateReferenceSet(problem);
		compareExecutors(problem, 10, 1000, "referenceSets/comparison_" + toFilenameString(problem) + ".csv", 20);
	}
}
