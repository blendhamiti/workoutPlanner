Creating sample database for the program:
In the project zip file, there is a folder 'mysql' which contain the create statements queries (create-statements.sql - to create the schema and the tables necessary to run the program).

How to run:
Before running the program, you need to configure the program to work with your database. To do so, edit the WorkoutPlannerDaoImpl class and add the correct information for variables edit variables JDBC driver, database URL, username and password.
To run the program, simply run the Client class.

Use guide:
The program allows you to create routines that consist of exercises. Use the 'Create new routine' button to create a routine.
Exercises are defined by their name, duration or repetitions, and sets. The bottom left section shows all exercises available, and allows you to add new exercises thorugh the 'Create new exercise" button.
A routine can contain an exercise only once. To add an exercise to a routine, first select a routine in the top left section. Then, the exercises that belong to the selected routine are shown in the bottom left section. Now, to add an exericse, select the desired exercise from the collection of all exercises and press the arror pointing left. Similarly, to delete an exercise from a routine, select the exercise in the bottom left section and press the button with an arrow that points to the right.


 