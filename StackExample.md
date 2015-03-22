# Stack Domain #
This page shows the stack problem domain, i.e., the meta-model representing a simple set of loaded stacks that are connected in a circular way and the transformation rules that manipulate these stacks.

## Meta-Model ##
![https://momot.googlecode.com/git/wiki/img/stack_mm.svg](https://momot.googlecode.com/git/wiki/img/stack_mm.svg)

## Rules ##
![https://momot.googlecode.com/git/wiki/img/stack_rules_no_annotation.png](https://momot.googlecode.com/git/wiki/img/stack_rules_no_annotation.png)

Additional attribute condition in rules "shiftLeft" and "shiftRight":
```
SufficientLoad: 
amount <= fromLoad
```

## Example ##
A Java project based on this problem domain can be found in the [repository](https://code.google.com/p/momot/source/browse/#git%2Fprojects%2Fat.ac.tuwien.big.momot.examples.stack). In this example the goal is to equally distribute the load between all stacks within a stack mode, i.e., minimize the overall load standard deviation with as few rule applications as possible. As an input model was already modeled, only the rules "shiftLeft" and "shiftRight" are used.