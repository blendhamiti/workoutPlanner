## Workout Planner - Documentation 

### Database configuration 
First, you will need to create the sample database to use with the program. In the project zip file, there is a folder '/mysql' which contain the create statements queries (create_statements.sql). Running these queries in a MySQL server (preferably v8.0) will create the schema and the tables used in the program. After creating the tables, you can populate them with the data found in ‘/mysql/table-data’. Each .csv file in this folder contains data for the table with the same name. It is important that you import data of table 'routines_exercises' last, because it contains relationships between the other two tables and requires data on both of them to be imported successfully. (Tip: you can use the MySQL Workbench import wizard to easily import the data into the tables.) 

### Running the program 
Before running the program, you also need to configure the program to work with your database. To do so, edit the WorkoutPlannerDaoImpl class and add the correct information for the fields JDBC_DRIVER, DATABSE_URL, USERNAME and PASSWORD. Finally, run the program by running the Client class. 

### Usage notes 
The program allows you to create routines that consist of exercises. Use the 'Create new routine' button to create a routine. 
Exercises are defined by their name, duration or repetitions, and sets. The bottom left section shows all exercises available and allows you to add new exercises through the 'Create new exercise" button. 
A routine can contain an exercise only once.  
To add an exercise to a routine, first select a routine in the top left section. Then, the exercises that belong to the selected routine are shown in the bottom left section. Now, to add an exercise, select the desired exercise from the collection of all exercises and press the arrow pointing left. Similarly, to delete an exercise from a routine, select the exercise in the bottom left section and press the button with an arrow that points to the right. 
 
