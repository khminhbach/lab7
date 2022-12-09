import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class InnReservations {

  public static void main(String[] args) {
    int input = getInput();
    while (input != 0) {
      String result = request(input);
      System.out.println(result);
      input = getInput();
    }
  }

  private static String request(int input) {
    return "1";
  }

  private static int getInput() {
    Scanner keyboard = new Scanner(System.in);
    System.out.println("(0) Quit\n" +
        "(1) Rooms and Rates\n" +
        "(2) Reservations\n" +
        "(3) Reservation Change\n" +
        "(4) Reservation Cancellation\n" +
        "(5) Detailed Reservation Information\n" +
        "(6) Revenue\n");
    int input = keyboard.nextInt();

    return input;
  }

}
