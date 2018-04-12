package sample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created by Dipal on 2/26/2017.
 */
public class Populate {
 //Argument:C:/Users/Dipal/Desktop/movies.dat C:/Users/Dipal/Desktop/movie_genres.dat C:/Users/Dipal/Desktop/movie_countries.dat C:/Users/Dipal/Desktop/movie_locations.dat C:/Users/Dipal/Desktop/tags.dat C:/Users/Dipal/Desktop/movie_tags.dat C:/Users/Dipal/Desktop/user_ratedmovies.dat C:/Users/Dipal/Desktop/user_ratedmovies-timestamps.dat C:/Users/Dipal/Desktop/user_taggedmovies.dat C:/Users/Dipal/Desktop/user_taggedmovies-timestamps.dat
    public static void main(String[] args) {
        if (args.length != 10) {
            System.err.println("Usage: java Populate <tags table filepath>");
            return;
        }
        if (!DatabaseHelper.loadDriver()) {
            return;
        }
        Connection connection = DatabaseHelper.connectToDatabase();
        if (connection == null) {
            return;
        }
        try {
            System.out.println("Populating table...");
            populateMoviesTable (args[0], connection);
            populateMovieGenresTable(args[1], connection);
            populatecountriesTable(args[2],connection);
            populateLocationTable(args[3],connection);
            populateTagsTable(args[4],connection);
            populateMovieTagsTable(args[5],connection);
            populateUserRatedMovieTable(args[6],connection);
            populateUserRatedMovieTimeStampTable(args[7],connection);
            populateUserTaggedMovie(args[8],connection);
            populateUserTaggedMovieTimestamp(args[9],connection);

        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e);
            return;
        }
        return;
    }



    private static boolean  populateUserTaggedMovieTimestamp(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=4)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into USER_TAGGEDMOVIES_TIMESTAMPS VALUES (" + columns[0] + ", " + columns[1] + ",'" + columns[2] + "', " + columns[3] + ")";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("User rate movies timestamp table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean populateUserTaggedMovie(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=9)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into USER_TAGGEDMOVIES VALUES (" + columns[0] + ", " + columns[1] + ",'" + columns[2] + "', " + columns[3] + "," + columns[4] + "," + columns[5] + "," + columns[6] + "," + columns[7] + "," + columns[8] + ")";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("User rate movies timestamp table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean  populateUserRatedMovieTimeStampTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=4)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into USER_RATEDMOVIES_TIMESTAMPS VALUES (" + columns[0] + ", " + columns[1] + "," + columns[2] + ", " + columns[3] + ")";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("User rate movies timestamp table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean  populateUserRatedMovieTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=9)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into USER_RATEDMOVIES VALUES (" + columns[0] + ", " + columns[1] + "," + columns[2] + ", " + columns[3] + ", " + columns[4] + ", " + columns[5] + ", " + columns[6] + ", " + columns[7] + ", " + columns[8] + ")";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Countries table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }
    private static boolean  populateMovieTagsTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=3)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into MOVIE_TAGS VALUES (" + columns[0] + ", '" + columns[1] + "'," + columns[2] + ")";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Countries table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean  populateTagsTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=2)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into TAGS VALUES ('" + columns[0] + "', '" + columns[1] + "')";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Countries table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }
    private static boolean populateLocationTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "''";
                    }
                    if (columns[index].equals(" ") || columns[index].isEmpty()) {
                        columns[index] = "''";
                    }
                }
                if (columns.length !=5)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into LOCATIONS VALUES (" + columns[0] + ", '" + columns[1] + "','" + columns[2] + "','" + columns[3] + "','" + columns[4] + "')";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Countries table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean populatecountriesTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "NULL";
                    }
                }
                if (columns.length !=2)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into COUNTRIES VALUES (" + columns[0] + ", '" + columns[1] + "')";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Countries table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean populateMovieGenresTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "NULL";
                    }
                }
                if (columns.length !=2)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into MOVIE_GENRES VALUES (" + columns[0] + ", '" + columns[1] + "')";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Movies table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }

    private static boolean populateMoviesTable(String fileName, Connection connection) throws SQLException {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            int line_index = 0;
            while((line = bufferedReader.readLine()) != null) {
                if (line_index==0)
                {
                    line_index++;
                    continue;
                }
                String[] columns =line.split("\t",-1);
                for (int index = 0; index < columns.length ; index++) {
                    columns[index] = columns[index].replace("'", "''");
                    if (columns[index].equals("\\N")) {
                        columns[index] = "NULL";
                    }
                }
                if (columns.length !=21)
                {
                    System.err.println("File has more than 2 columns");
                    return false;
                }
                Statement insertStatement = null;
                try {
                    insertStatement = connection.createStatement();
                    String insertSql = "INSERT into MOVIE VALUES (" + columns[0] + ", '" + columns[1] + "', " + columns[2] + ", '" + columns[3] + "', '" + columns[4] + "', " + columns[5] + ", '" + columns[6] + "', " + columns[7] + ", " + columns[8] + ", " + columns[9] + ", " + columns[10] + ", " + columns[11] + ", " + columns[12] + ", " + columns[13] + ", " + columns[14] + ", " + columns[15] + ", " + columns[16] + ", " + columns[17] + ", " + columns[18] + ", " + columns[19] + ", '" + columns[20] + "')";
                    System.out.println(insertSql);
                    insertStatement.executeUpdate(insertSql);
                } finally {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                }
                line_index++;
            }
            System.out.println(String.format("Movies table populated with %d rows", line_index));

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                    "Unable to open file '" +
                            fileName + "'");
            return false;
        }
        catch(IOException ex) {
            System.err.println(
                    "Error reading file '"
                            + fileName + "'");
            return false;
        }
        return true;
    }
}
