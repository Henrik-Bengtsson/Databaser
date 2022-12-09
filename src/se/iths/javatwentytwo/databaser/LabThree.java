package se.iths.javatwentytwo.databaser;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class LabThree {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while(true){
            printMenu();
            String option = scanner.nextLine();
            switch (option){
                case "1" -> selectAllGames();
                case "2" -> addGame();
                case "3" -> updateGame();
                case "4" -> deleteGame();
                case "5" -> selectAllCategory();
                case "6" -> addCategory();
                case "7" -> updateCategory();
                case "8" -> deleteCategory();
                case "9" -> searchGame();
                case "10" -> searchCategory();
                case "11" -> numberOfGames();
                case "q", "Q" -> System.exit(1);
            }
        }
    }

    private static Connection connect(){

        String url = "jdbc:sqlite:/Users/henri/Iths/Databaser/SQLite/henrikBengtssonJava22.db";

        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;

    }

    private static void printMenu(){
        System.out.print(
                """
                -----------------------
                1 - Visa alla spel
                2 - Lägg till ett spel
                3 - Uppdatera ett spel
                4 - Ta bort ett spel
                5 - Visa alla kategorier
                6 - Lägg till kategori
                7 - Uppdatera kategori
                8 - Ta bort kategori
                9 - Sök på spel
                10 - Sök på kategori
                11 - Visa antal spel
                Q, q - Stäng programmet
                -----------------------
                """);
    }

    private static void selectAllGames(){
        String sql = "SELECT * FROM spel ";

        try {
            Statement stmt  = Objects.requireNonNull(connect()).createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println(rs.getInt("spelId") +  "\t" +
                        rs.getString("spelNamn") + "\t" +
                        rs.getInt("spelPris") + "\t" +
                        rs.getString("spelUtgivare"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addGame(){
        String sql = "INSERT INTO spel(spelNamn, spelPris, spelUtgivare) VALUES (?,?,?)";

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            inputInformation(stmt);
            stmt.executeUpdate();
            addGameCategory();

            System.out.println("Du har lagt till ett spel.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addGameCategory() throws SQLException {
        String sqlMany = "INSERT INTO spelKategori(spelKategoriSId, spelKategoriKId) VALUES ((SELECT MAX(spelId) FROM spel),?)";

        PreparedStatement stmtCategory = Objects.requireNonNull(connect()).prepareStatement(sqlMany);
        selectAllCategory();
        System.out.println("Ange kategori (nummer)");
        stmtCategory.setInt(1, Integer.parseInt(scanner.nextLine()));
        stmtCategory.executeUpdate();
        System.out.println("Vill du lägga till fler kategorier? Y/N");
        String answer = scanner.nextLine().toLowerCase();
        if(answer.equals("y"))
            addGameCategory();
    }

    private static void updateGame(){
        String sql = "UPDATE spel SET spelNamn = ?, spelPris = ?, spelUtgivare = ? WHERE spelId = ?";

        System.out.println("Vilket spel vill du uppdatera (ange nummer)");
        int gameId = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            inputInformation(stmt);
            stmt.setInt(4, gameId);
            stmt.executeUpdate();
            System.out.println("Du har uppdaterat spelet");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void inputInformation(PreparedStatement stmt) throws SQLException {
        System.out.println("Skriv namn:");
        stmt.setString(1, scanner.nextLine());
        System.out.println("Skriv pris:");
        stmt.setInt(2, Integer.parseInt(scanner.nextLine()));
        System.out.println("Skriv tillverkare:");
        stmt.setString(3, scanner.nextLine());
    }

    private static void deleteGame(){
        String sql = "DELETE FROM spel WHERE spelId = ?";

        System.out.println("Vilket spel vill du ta bort (ange nummer)");
        int gameId = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            stmt.setInt(1, gameId);
            stmt.executeUpdate();
            deleteGameCat(gameId);
            System.out.println("Du har tagit bort spelet");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteGameCat(int gameId) throws SQLException{
        String sql = "DELETE FROM spelKategori WHERE spelKategoriSId = ?";

        PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql);
        stmt.setInt(1, gameId);
        stmt.executeUpdate();
    }

    private static void selectAllCategory(){
        String sql = "SELECT * FROM kategori";

        try (Statement stmt  = Objects.requireNonNull(connect()).createStatement()){
            ResultSet rs    = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println(rs.getInt("kategoriId") +  "\t" +
                        rs.getString("kategoriNamn"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addCategory(){
        String sql = "INSERT INTO kategori(kategoriNamn) VALUES (?)";

        System.out.println("Skriv in den nya kategorin");

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            stmt.setString(1, scanner.nextLine());
            stmt.executeUpdate();
            System.out.println("Du har lagt till en kategri.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateCategory(){
        String sql = "UPDATE kategori SET kategoriNamn = ? WHERE kategoriId = ?";

        System.out.println("Vilken kategori vill du uppdatera (ange nummer)");
        int categoryId = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            stmt.setString(1, scanner.nextLine());
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
            System.out.println("Du har uppdaterat kategorin");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteCategory(){
        String sql = "DELETE FROM kategori WHERE kategoriId = ?";

        System.out.println("Vilken kategori vill du ta bort (ange nummer)");
        int catId = Integer.parseInt(scanner.nextLine());

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            stmt.setInt(1, catId);
            stmt.executeUpdate();
            deleteCatCat(catId);
            System.out.println("Du har tagit bort kategorin");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteCatCat(int catId) throws SQLException{
        String sql = "DELETE FROM spelKategori WHERE spelKategoriKId = ?";

        PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql);
        stmt.setInt(1, catId);
        stmt.executeUpdate();
    }

    private static void searchGame(){
        String sql = "SELECT * FROM spel WHERE spelNamn = ?";

        System.out.println("Skriv in vilket spel: ");

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            stmt.setString(1, scanner.nextLine());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("spelId") +  "\t" +
                        rs.getString("spelNamn") + "\t" +
                        rs.getInt("spelPris") + "\t" +
                        rs.getString("spelUtgivare"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchCategory(){
        String sql = "SELECT * FROM kategori " +
                "INNER JOIN spelKategori ON kategori.kategoriId = spelKategori.spelKategoriKId " +
                "INNER JOIN spel ON spelKategori.spelKategoriSId = spel.spelId " +
                "WHERE kategoriId = ?";

        selectAllCategory();
        System.out.println("Ange kategori (ange nummer)");

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt(scanner.nextLine()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("spelId") +  "\t" +
                        rs.getString("spelNamn") + "\t" +
                        rs.getInt("spelPris") + "\t" +
                        rs.getString("spelUtgivare"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void numberOfGames(){
        String sql = "SELECT COUNT(spelId) AS spelAntal FROM spel";

        try (PreparedStatement stmt = Objects.requireNonNull(connect()).prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            System.out.println("Antal spel: " + rs.getInt("spelAntal"));
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}