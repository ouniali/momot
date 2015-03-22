# Modularization Domain #
This page shows the modularization problem domain, i.e., the meta-model representing a simple architectural software design and transformation rules that manipulate that software design.

## Meta-Model ##
![https://momot.googlecode.com/git/wiki/img/modularization_mm.svg](https://momot.googlecode.com/git/wiki/img/modularization_mm.svg)

## Rules ##
![https://momot.googlecode.com/git/wiki/img/modularization_rules.png](https://momot.googlecode.com/git/wiki/img/modularization_rules.png)

## Example ##
A Java project based on this problem domain can be found in the [repository](https://code.google.com/p/momot/source/browse/#git%2Fprojects%2Fat.ac.tuwien.big.momot.examples.modularization). In this example, we assume that we have an existing set of classes and their dependencies, with the goal to group these classes into components. As objectives, we try to minimize the coupling between the components and maximize the cohesion within each component. We use the rules "createComponent" and "assignClass" for this example.