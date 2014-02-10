Language used-Java

PROGRAM DESCRIPTION
The program is divided into three classes:
reversi.java
boardNode.java

An object of boardNode.java represents a node that is pushed into the tree. Each node stores information about the node's name, alpha value, beta value, node value, depth of the node, state of board associated with it and list of children of that node.

reversi.java contains following functions:
main()-Takes the arguments, processes the input file and selects the task to perform.
processinputfile()-Takes the input file, parses it and stores the initial configuration of the board.
evalNumberOfPieces()-evaluation function to find the difference of black and white.
evalPositionalweight()-evaluation function to find out the difference of black and white based on positional weights.
haveNoLegalMoves()-checks if the board and for the player, it has legal moves.
predictLegalMoves() and predictMove()- predicts the legal moves for a given board configuration and player.
isFull()-checks if board is Full
flipMoves()-flips the pieces as per the player's needs.
tiebreaker()-breaks the tie if more than one move has the same evaluation value.
selectbestmove()-selects the best move for the root.
minimax(),simMoves() and printTraverseLog()-creates the minimax tree for task 1, and then helps in implementing minimax and thus prints the logs.
alphabeta(), alphabetasimulation() and printAlphaBetaTraverse()-creates the tree for task 2,3 and then performs alpha beta pruning with the help of both the evaluation function.
 

ANALYSIS OF SIMILARITIES/DIFFERENCES BETWEEN TASK 2 AND 3
In both the task 2 and task 3 we are performing alpha beta pruning on the tree.

The difference between task 2 and task 3 is the difference of evaluation function. In task 2, the evaluated value of the node is the difference of number of blacks and whites. While, in task 3, the evaluated value of the node is the difference of the positional weights of the blacks and whites.



HOW TO COMPILE/EXECUTE
The Program needs java version 1.6 or higher on aludra.
For compiling(we have to compile all the java files(boardNode.java,reversi.java))

javac *.java

For execution

java reversi -t <task> -s <start_node> -i <input_file> -op <output_path> -ol <output_log>

where task can be 1,2,3