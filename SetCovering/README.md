## Ant Colony metaheuristic for solving the set covering problem

The Ant Colony metaheuristic is a population based metaheuristic that generates a series of solutions in each iteration using a greedy randomised search which utilises the memory of the search. Each solution generated in an iterations is called an Ant, and these ants leave behind a "pheromone trail", indicating which elements were used in previous iterations. 

This project implements the Ant Colony metaheuristic used for solving the set covering problem in Java.

The folder `Data` contains 5 different problem instances downloaded from http://people.brunel.ac.uk/~mastjjb/jeb/orlib/scpinfo.html

The folder `src` contains the files with the Java code written for this project:
 - `Instance.java` implements an Instance class, which loads a problem instance from one of the .txt files and stores the relevant information in appropriate data types.
 - `Ant.java` implements an Ant class, with a method to construct a feasible solution to the set covering problem and a method to improve a solution using a local search procedure.
 - `MultiThreadAnt.java` is used to parallelize the process of generating Ants/solutions, as $M$ ants are generated independently of each other in each iteration of the algorithm.
 - `App.java` contains the main loop of the metaheuristic used to find good solutions to the set covering problem. This is where ants are generated in each iteration, the best solution found is tracked and the pheromone trail is updated.

The folder `bin` contains the .class files.
