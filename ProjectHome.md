This framework combines search-based optimization (population-based approaches and single-point approaches) with model transformations to solve problems from different software engineering areas.

In this framework, problems are represented as Ecore meta-models and their respective model instances. These problem instances can be manipulated through dedicated model transformations modeled as graph transformation rules in the Henshin language. Search-based optimization techniques can then be used to search for a rule orchestration, i.e., an ordered sequence of rules and their parameters, to produce a model displaying a given set of characteristics. Desired characteristics are specified as objectives, e.g., through OCL, and unwanted characteristics are specified as constraints, e.g., through OCL or Negative Application Conditions (NACs).

This repository contains all code necessary to run the framework, except the third-party dependencies, e.g., Henshin or the MOEA Framework (see external links on the left side). Additionally three example projects show how the framework can be used:

  * StackExample
  * ModularizationExample
  * RefactoringExample