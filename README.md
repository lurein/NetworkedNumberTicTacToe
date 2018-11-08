# NetworkedNumberTicTacToe
A very simple networked form of NumberTicTacToe, utilizing TCP. </br>
In order to win either a row, column or diagonal must add up to 15. </br>

The TCPServer program creates a new thread to deal with each client which conencts to it, and so the server can play with multiple clients simultaneously. </br>

This current game takes place in the command-line, with a simple gridding function converting the gameboard array into a 3x3 grid.

