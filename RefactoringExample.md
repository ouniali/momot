# Refactoring Domain #
This page shows the refactoring problem domain, i.e., the meta-model representing a simple class diagram containing entities and properties, and the transformation rules that manipulate these two entities.

## Meta-Model ##
![https://momot.googlecode.com/git/wiki/img/refactoring_mm.svg](https://momot.googlecode.com/git/wiki/img/refactoring_mm.svg)

## Rules ##
![https://momot.googlecode.com/git/wiki/img/refactoring_rule_createRootClass.png](https://momot.googlecode.com/git/wiki/img/refactoring_rule_createRootClass.png)
![https://momot.googlecode.com/git/wiki/img/refactoring_rule_extractSuperClass.png](https://momot.googlecode.com/git/wiki/img/refactoring_rule_extractSuperClass.png)
![https://momot.googlecode.com/git/wiki/img/refactoring_rule_pullUpAttribute.png](https://momot.googlecode.com/git/wiki/img/refactoring_rule_pullUpAttribute.png)

Additional OCL Attribute condition in Rule "pullUpAttribute":
```
AllSubHaveProperty: 
ocl:self.entitys->select(en | en.name=e).specialization
                ->collect(g | g.specific)
                ->forAll(e | e.ownedAttribute->exists(p | p.name = n))
```

## Example ##
A Java project based on this problem domain can be found in the [repository](https://code.google.com/p/momot/source/browse/#git%2Fprojects%2Fat.ac.tuwien.big.momot.examples.refactoring). In this example we use all three rules to modify a given model to reduce the number of elements in the model, i.e., the number of entities and properties. Rule priority is given as additional weight in the OCL objective dimension, so that the rules are more or less ordered ("pullUpAttribute" > "extractSuperClass" > "createRootClass"):

OCL Objective dimension for reducing the number of elements:
```
propertys->size() * 1.1 + entitys->size()
```