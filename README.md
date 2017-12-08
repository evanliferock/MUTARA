# MUTARA
This assignment, which is for Biomedical Informatics is an implementation of the MUTARA algorithm. However for this assignment
It is to mine data that does not have temporal data. The data used was FDA’s spontaneous reports.
It made it impossible for this to be an complete implementation.

Also instead of some optimizations, simplicity was necessary due to partner for this project not being familiar with code

### Running

In order to run this program, place it in a IDE and download the JAR for JSCH from http://www.jcraft.com/jsch/ and
place it in a library folder and add it as a library for the project. Then place your credentials in lines 11-14 in
App.java. The table names in the SQL database should be DEMOGRAPHIC, DRUG, and REACTION. Then run.

It will ask for input twice: for the number of drugs to test and the number of diagnosis to test