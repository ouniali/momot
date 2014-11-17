package at.ac.tuwien.big.momot.examples.modularization;

import org.eclipse.emf.henshin.interpreter.EGraph;

import at.ac.tuwien.big.moea.solution.printer.ISolutionPrinter;
import at.ac.tuwien.big.momot.MomotFactory;
import at.ac.tuwien.big.momot.MomotOrchestration;
import at.ac.tuwien.big.momot.solution.MatchSolution;

public class ArchitectureOrchestration extends MomotOrchestration {
	
	public ArchitectureOrchestration(MomotFactory factory, EGraph initialGraph,
			int nrVariables) {
		super(factory, initialGraph, nrVariables);
		init();
	}

	public ArchitectureOrchestration(MomotFactory factory,
			String initialGraphPath, int nrVariables) {
		super(factory, initialGraphPath, nrVariables);
		init();
	}

	public ArchitectureOrchestration(String path, String moduleFile,
			EGraph initialGraph, int nrVariables) {
		super(path, moduleFile, initialGraph, nrVariables);
		init();
	}

	public ArchitectureOrchestration(String path, String moduleFile,
			String initialGraphPath, int nrVariables) {
		super(path, moduleFile, initialGraphPath, nrVariables);
		init();
	}
	
	public void init() {
		setFitnessFunction(new ArchitectureFitnessFunction(getInitialGraph(), getMatchHelper()));
		getParameterManager().preserveParameters(
				"createClass.className", 
				"createClassDependency.fromClass", "createClassDependency.toClass",
				"createComponent.componentName",
				"assignClass.className", "assignClass.componentName",
				"reassignClass.className", "reassignClass.componentName",
				"removeAssignment.className", "removeAssignment.componentName");
	}
	
	@Override
	protected ISolutionPrinter<MatchSolution> createSolutionPrinter() {
		return new ArchitectureSolutionPrinter(getFitnessFunction());
	}

}
