import java.sql.ResultSet;
import java.sql.Statement;
import java.net.SocketTimeoutException;
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
import java.util.Date;

// export HP_JDBC_URL="jdbc:mysql://db.labthreesixfive.com/kelyu?autoReconnect=true\&useSSL=false"
// export HP_JDBC_USER="kelyu"
// export HP_JDBC_PW="Fall22_CSC365-028922315"

public class InnReservations {
  public static String jdbcURL = System.getenv("HP_JDBC_URL");
  public static String dbUsername = System.getenv("HP_JDBC_USER");
  public static String dbPassword = System.getenv("HP_JDBC_PW");
  public static Connection conn;

  public static void main(String[] args) {
    int input = Integer.parseInt(args[0]);
    String result;
    try {
      result = request(input);
      System.out.println(result);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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

  private static String request(int input) {
    try {
      String result = "";
      switch (input) {
        case 0:
          return null;
        case 1:
          result += roomsAndRates();
          break;
        case 2:
          result += reservations();
          break;
        case 3:
          result += reservationChange();
          break;
        case 4:
          result += reservationCancellation();
          break;
        case 5:
          result += detailedReservationInformation();
          break;
        case 6:
          result += revenue();
          break;
      }
      return result;
    } catch (Exception e2) {
      e2.printStackTrace();
      return null;
    }
  }

  // R6
  private static String revenue() {
    return null;
  }

  // R5
  private static String detailedReservationInformation() {
    return null;
  }

  // R4
  private static String reservationCancellation() {
    return null;
  }

  // R3
  private static String reservationChange() {
    return null;
  }

  // R2
  private static String reservations() {
    return null;
  }

  // R1
  private static String roomsAndRates() {
    try (Connection connection = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword)) {
      String sql = "with DaysOccupiedLast180 as (\n" +
          "    select \n" +
          "    Room,\n" +
          "    SUM(DateDiff(Checkout,\n" +
          "    case \n" +
          "        when CheckIn >=  Current_Date - interval 180 day\n" +
          "        then CheckIn\n" +
          "        else Current_Date - interval 180 day\n" +
          "    end\n" +
          "    )) as DaysOccupied\n" +
          "    from lab7_reservations\n" +
          "    where CheckOut > Current_Date - interval 180 day\n" +
          "    group by Room\n" +
          "),\n" +
          "MostRecentReservation as (\n" +
          "    select Room,\n" +
          "    MAX(CheckIn) as MostRecentCheckin,\n" +
          "    MAX(Checkout) as MostRecentCheckout\n" +
          "    from lab7_reservations\n" +
          "    where CheckOut <= Current_Date\n" +
          "    group by Room\n" +
          "),\n" +
          "FirstAvailables as (\n" +
          "   select\n" +
          "   Room,\n" +
          "   Case\n" +
          "    When not exists (\n" +
          "     select * from lab7_reservations r2\n" +
          "     where r1.Room = r2.Room\n" +
          "     and CheckIn <= Current_Date\n" +
          "     and CheckOut > Current_Date\n" +
          "    )\n" +
          "    then Current_Date\n" +
          "    else (\n" +
          "       select MIN(CheckOut) from lab7_reservations r2\n" +
          "       where CheckOut > CURRENT_DATE\n" +
          "       and r2.Room = r1.Room\n" +
          "       and not exists (\n" +
          "        select Room from lab7_reservations r3 \n" +
          "        where r3.Room = r2.Room\n" +
          "        and r2.CheckOut = r3.CheckIn\n" +
          "       ) \n" +
          "    )\n" +
          "   end as FirstAvailable\n" +
          "   from lab7_reservations r1\n" +
          "   group by room\n" +
          ")\n" +
          "select \n" +
          "MostRecentReservation.Room,\n" +
          "RoomName,\n" +
          "Beds,\n" +
          "bedType,\n" +
          "maxOcc,\n" +
          "basePrice,\n" +
          "decor,\n" +
          "-- new info\n" +
          "ROUND(DaysOccupied / 180, 2) as Popularity,\n" +
          "FirstAvailable,\n" +
          "DATEDIFF(MostRecentCheckout,MostRecentCheckin) as LastStayLength,\n" +
          "MostRecentCheckout\n" +
          "from MostRecentReservation\n" +
          "join DaysOccupiedLast180 on DaysOccupiedLast180.Room = MostRecentReservation.Room\n" +
          "join FirstAvailables on FirstAvailables.Room = MostRecentReservation.Room\n" +
          "join lab7_rooms on FirstAvailables.Room = RoomCode\n" +
          "order by Popularity desc\n" +
          ";";
      try (Statement stmt = connection.createStatement()) {
        ResultSet rs = stmt.executeQuery(sql);
        String result = "sup";
        while (rs.next()) {
          System.out.println("big balls");
          String room = rs.getString("Room");
          Float popularity = rs.getFloat("Popularity");
          Date firstAvailable = rs.getDate("FirstAvailable");
          int lastStayLength = rs.getInt("LastStayLength");
          Date mostRecentCheckOut = rs.getDate("MostRecentCheckOut");
          result += String.format(
              ("\nRoom: %s\nPopularity: %.2f\nFirst Available: %tF\nLast Stay Length: %d\nMost RecentCheckout: %tF\n"),
              room, popularity, firstAvailable, lastStayLength, mostRecentCheckOut);
          System.out.println("small balls");
        }
        return result;
      } catch (SQLException e) {
        e.printStackTrace();
        return "skasd";
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return "incorrect request";
    }
  }
}
