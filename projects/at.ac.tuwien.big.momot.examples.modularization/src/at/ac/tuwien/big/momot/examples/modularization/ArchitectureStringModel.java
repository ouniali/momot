package at.ac.tuwien.big.momot.examples.modularization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.impl.ProfilingApplicationMonitor;

import at.ac.tuwien.big.moea.util.EcoreUtil;
import at.ac.tuwien.big.moea.util.MathUtil;
import at.ac.tuwien.big.momot.MomotFactory;

public class ArchitectureStringModel implements Serializable {
	private static final long serialVersionUID = 6030420952939740782L;

	public static final String EMPTY_MODEL = "empty_model.xmi";
	
	private Map<String, HashSet<String>> classes = new HashMap<String, HashSet<String>>();
	private Map<String, HashSet<String>> components = new HashMap<String, HashSet<String>>();
	private Map<String, String> classToComponent = new HashMap<String, String>();
	
	public ArchitectureStringModel() { }
	
	public ArchitectureStringModel(EGraph graph) {
		for(EObject obj : graph) {
			if(EcoreUtil.isEClass(obj, "ArchitectureModel")) {
				; // do nothing
			} else if(EcoreUtil.isEClass(obj, "Class")) {
				String name = EcoreUtil.getFeatureValue(obj, "name", String.class);
				addClass(name);
				
				@SuppressWarnings("unchecked")
				EList<EObject> dependencies = EcoreUtil.getFeatureValue(obj, "dependsOn", EList.class);
				for(EObject dependency : dependencies) {
					String dependencyName = EcoreUtil.getFeatureValue(dependency, "name", String.class);
					addDependency(name, dependencyName);
				}
				
				EObject component = EcoreUtil.getFeatureValue(obj, "isEncapsulatedBy", EObject.class);
				if(component != null) {
					String componentName = EcoreUtil.getFeatureValue(component, "name", String.class);
					addEncapsulation(componentName, name);
				}
				
			} else if(EcoreUtil.isEClass(obj, "Component")) {
				String name = EcoreUtil.getFeatureValue(obj, "name", String.class);
				addComponent(name);
				// COMPONENT_ENCAPSULATES set via CLASS__IS_ENCAPSULATED_BY
			}
		}
	}
	
	public ArchitectureStringModel addComponent(String name) {
		if(name == null)
			return this;
		Set<String> list = components.get(name);
		if(list == null)
			components.put(name, new HashSet<String>());
		return this;
	}
	
	public ArchitectureStringModel addEncapsulation(String componentName, String className) {
		if(componentName == null || className == null)
			return this;
		addComponent(componentName);
		addClass(className);
		classToComponent.put(className, componentName);
		components.get(componentName).add(className);
		return this;
	}
	
	public ArchitectureStringModel addEncapsulation(String componentName, String... classNames) {
		for(String className : classNames)
			addEncapsulation(componentName, className);
		return this;
	}
	
	public ArchitectureStringModel addClass(String name) {
		if(name == null)
			return this;
		Set<String> list = classes.get(name);
		if(list == null)
			classes.put(name, new HashSet<String>());
		return this;
	}
	
	public ArchitectureStringModel addDependency(String fromClass, String toClass) {
		addClass(fromClass);
		addClass(toClass);
		classes.get(fromClass).add(toClass);
		return this;
	}
	
	public ArchitectureStringModel addDependency(String fromClass, String... toClasses) {
		for(String toClass : toClasses)
			addDependency(fromClass, toClass);
		return this;
	}
	
	public Set<String> getClasses() {
		return classes.keySet();
	}
	
	public Set<String> getComponentNames() {
		return components.keySet();
	}
	
	public Set<String> getDependencies(String className) {
		return classes.get(className);
	}
	
	public String getComponent(String className) {
		return classToComponent.get(className);
	}
	
	public Set<String> getClasses(String componentName) {
		return components.get(componentName);
	}
	
	public Map<String, HashSet<String>> getComponents() {
		return components;
	}
	
	public Set<String> getUnassignedClasses() {
		Set<String> allClasses = new HashSet<>(classes.keySet());
		allClasses.removeAll(classToComponent.keySet());
		return allClasses;
	}
	
	public int calculateCohesion() {
		int sumInnerLinks = 0;
		for(String component : getComponentNames())
			sumInnerLinks += calculateCohesion(component);
		return sumInnerLinks;
	}
	
	public int calculateCohesion(String componentName) {
		int innerLinks = 0;
		for(String clazz : getClasses(componentName)) {
			for(String dependency : getDependencies(clazz))
				if(componentName.equals(getComponent(dependency)))
					innerLinks++;
		}
		return innerLinks;
	}
	
	public double calculateMeanClassesPerComponent() {
		int nrClasses = getClasses().size();
		int nrComponents = getComponentNames().size();
		return nrClasses / (double)nrComponents;
	}
	
	public double calculateStdDevClassesPerComponent() {
		List<Integer> nrclasses = new ArrayList<>();
		for(Entry<String, HashSet<String>> entry : components.entrySet())
			nrclasses.add(entry.getValue().size());

		return MathUtil.getStandardDeviation(nrclasses);
	}
	
	public int calculateCoupling() {
		int sumOutgoingLinks = 0;
		for(String component : getComponentNames())
			sumOutgoingLinks += calculateCoupling(component);
		return sumOutgoingLinks;
	}
	
	public int calculateCoupling(String componentName) {
		int outgoingLinks = 0;
		for(String clazz : getClasses(componentName)) {
			for(String dependency : getDependencies(clazz))
				if(!componentName.equals(getComponent(dependency)))
					outgoingLinks++;
		}
		return outgoingLinks;
	}
	
	public String toString(boolean calculateFitnessValues) {
		String result = "";
		result += "ArchitectureModel\n";
		
		for(String component : getComponentNames()) 
			result += "Component: " + component + "\n";
		
		for(String clazz : getClasses()) {
			result += "Class: " + clazz + "\n";
			result += "  depends on: " + getDependencies(clazz) + "\n";
			
			String component = getComponent(clazz);
			result += "  encapsuled by: " + ((component == null) ? "---" : component) + "\n";
		}
		if(calculateFitnessValues) {
			result += "----------\n";
			result += "Unassigned Classes: " + getUnassignedClasses() + "\n";
			result += "Coupling: " + calculateCoupling() + "\n";
			result += "Cohesion: " + -calculateCohesion() + "\n";
		}
		return result;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	public EGraph toEGraph(MomotFactory factory) {
		EGraph graph = factory.loadGraph(EMPTY_MODEL);		
		
		ProfilingApplicationMonitor monitor = new ProfilingApplicationMonitor();
		
		RuleApplication createComponentApp = factory.createRuleApplication("createComponent", graph);
		
		for(String componentName : getComponentNames()) {
			createComponentApp.setParameterValue("componentName", componentName);
			boolean success = createComponentApp.execute(monitor);
			if(!success)
				System.err.println("Could not create component " + componentName);
		}
		
		RuleApplication createClassApp = factory.createRuleApplication("createClass", graph);
		
		for(String className : getClasses()) {
			createClassApp.setParameterValue("className", className);
			boolean success = createClassApp.execute(monitor);
			if(!success)
				System.err.println("Could not create class " + className);
		}
	
		RuleApplication connectClassesApp = factory.createRuleApplication("createClassDependency", graph);
		
		for(String className : getClasses()) {
			connectClassesApp.setParameterValue("fromClass", className);
			
			for(String dependency : getDependencies(className)) {
				connectClassesApp.setParameterValue("toClass", dependency);
				boolean success = connectClassesApp.execute(monitor);
				if(!success)
					System.err.println("Could not connect " + className + " with " + dependency);
			}
		}
		
		RuleApplication assignClassApp = factory.createRuleApplication("assignClass", graph);
		
		for(String component : getComponentNames()) {
			assignClassApp.setParameterValue("componentName", component);
			
			for(String dependency : getClasses(component)) {
				assignClassApp.setParameterValue("className", dependency);
				boolean success = assignClassApp.execute(monitor);
				if(!success)
					System.err.println("Could not connect " + component + " with " + dependency);
			}
		}
		
		return graph;
	}
}
