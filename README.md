# Verified Refactoring
> Exploring techniques for code refactoring with formal verification.

Refactoring is changing the structure of a program without changing the behavior. Traditional refactoring tools help us achieve this using syntax transformations. By using Formal Methods to reason about the semantics of the code, we can open up more posibilities!

This collection is inspired by [Provable Refactorings](https://github.com/digdeeproots/provable-refactorings) by Arlo Belshee, with the addition of mechanically verified proof rather than informal arguments to ensure behavior is unchanged.

## Techniques and examples
* Formal Specification driven
  * Dafny - [Gilded Rose](https://github.com/raymyers/gilded-rose-dafny), [Video](https://youtu.be/XNIdKXQ56o4)
  * Dafny - [Proving the correctness of AWS authorization](https://www.youtube.com/watch?v=oshxAJGrwMU)
* Symbolic Execution
  * C + Klee - [Gilded Rose](https://github.com/raymyers/gilded-rose-c-symbolic-execution), [Video](https://youtu.be/_7RXQE-pCMo)
* Functional implementation as spec
  * Dafny - Linked Lists [Video 1]([https://youtu.be/dUoyPxSfKHU](https://youtu.be/zDu4SA5T4SI)), [Video 2](https://youtu.be/dUoyPxSfKHU) 
  * Coq + Gallina
* Proving program equivalence of an embedded language
  * Coq + Imp - [Programming Language Foundations](https://softwarefoundations.cis.upenn.edu/plf-current/Equiv.html)
* Proving equivelance of a program transformation
  * [CompCert](https://compcert.org/compcert-C.html) C Compiler written in Coq, extracted to OCaml
  * [Refinity](https://www.dominic-steinhoefel.de/talk/how-to-prove-the-correctness-of-refactoring-rules/how-to-prove-the-correctness-of-refactoring-rules.pdf) - Proofs for Java Refactoring built on JML / Key
  * [Equivalence by Canonicalization for Synthesis-Backed Refactoring](https://www.youtube.com/watch?v=sK_C0tEYT84) - Examples in Elm and Python/NumPy

## Cool Papers
[Simple formally verified compiler
in Lean](https://uu.diva-portal.org/smash/get/diva2:1613286/FULLTEXT01.pdf) - 2021 - Leo Okawa Ericson

[Kayak: Safe Semantic Refactoring to Java Streams](https://arxiv.org/pdf/1712.07388) - 2017 - David, Kesseli, Kroening]

[Abstract Execution: Automatically Proving Infinitely Many Programs](https://www.dominic-steinhoefel.de/publication/steinhoefel-20-2) - 2020 Steinhoefel PhD thesis. [Slides on Refinity](https://www.dominic-steinhoefel.de/talk/how-to-prove-the-correctness-of-refactoring-rules/how-to-prove-the-correctness-of-refactoring-rules.pdf)

