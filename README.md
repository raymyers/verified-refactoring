# Verified Refactoring
> Exploring techniques for code refactoring with formal verification.

Refactoring is changing the structure of a program without changing the behavior. Traditional refactoring tools help us achieve this using syntax transformations. By using Formal Methods to reason about the semantics of the code, we can open up more posibilities!

This collection is inspired by [Provable Refactorings](https://github.com/digdeeproots/provable-refactorings) by Arlo Belshee, with the addition of mechanically verified proof rather than informal arguments to ensure behavior is unchanged.

## Techniques and examples
* Formal Specification driven
  * Dafny
* Symbolic Execution
  * C + Klee
* Functional implementation as spec
  * Dafny
  * Coq + Gallina
* Proving program equivalence of an embedded language
  * Coq + Imp
* Proving equivelance of a program transformation
  * CompCert C Compiler

## Cool Papers
[Simple formally verified compiler
in Lean](https://uu.diva-portal.org/smash/get/diva2:1613286/FULLTEXT01.pdf) - 2021 - Leo Okawa Ericson

[Kayak: Safe Semantic Refactoring to Java Streams](https://arxiv.org/pdf/1712.07388) - 2017 - David, Kesseli, Kroening]

[Abstract Execution: Automatically Proving Infinitely Many Programs](https://www.dominic-steinhoefel.de/publication/steinhoefel-20-2) - 2020 Steinhoefel PhD thesis. [Slides on Refinity](https://www.dominic-steinhoefel.de/talk/how-to-prove-the-correctness-of-refactoring-rules/how-to-prove-the-correctness-of-refactoring-rules.pdf)

