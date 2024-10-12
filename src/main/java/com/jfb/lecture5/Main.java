package com.jfb.lecture5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfb.lecture5.model.BusTicket;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Main {
    private final static String FILE_PATH = "src/main/resources/ticketData.txt";

    private static int totalTickets;
    private static int validTickets;
    private static int totalStartDateViolation;
    private static int totalPriceViolation;
    private static int totalTicketTypeViolation;
  public static void main(String[] args) {
    try(BufferedReader bufferedTickets = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;

        while((line = bufferedTickets.readLine()) != null) {
            line = line.replace("“", "\"");

            BusTicket busTicket = new ObjectMapper().readValue(line, BusTicket.class);

            if(busTicket == null) {
                throw new CustomException(TicketException.TICKET_NOT_FOUND);
            }
            String ticketClass = busTicket.getTicketClass();
            String ticketType = busTicket.getTicketType();
            String startDate = busTicket.getStartDate();
            String price = busTicket.getPrice();

            totalTickets++;

            boolean isTicketClassValid = validateTicketProperty(ticketClass);

            boolean isTicketTypeValid = validateTicketProperty(ticketType) && validateTicketType(ticketType);
            if(!isTicketTypeValid) {
                totalTicketTypeViolation++;
            }

            boolean isStartDateValid = validateStartDate(startDate);
            if(!isStartDateValid) {
                totalStartDateViolation++;
            }

            boolean isPriceValid = validateTicketPrice(price);
            if(!isPriceValid) {
                totalPriceViolation++;
            }


            if(isTicketClassValid) {
                validTickets++;
            }
        }

        String biggestViolation = findBiggestViolation();

        System.out.println("Total={" + totalTickets + "}");
        System.out.println("Valid={" + validTickets + "}");
        System.out.println("Most popular violation={" + biggestViolation + "}");

    } catch (FileNotFoundException e) {
        System.out.println("File with tickets not found. " + e.getMessage());
    } catch (IOException exception) {
        System.out.println(exception.getMessage());
    }
  }

    public static boolean validateTicketProperty(String property) {
        return property != null && !property.isEmpty();
    }

    public static boolean validateTicketType(String ticketType) {
      try {
          TicketType.valueOf(ticketType);
          return true;
      } catch (IllegalArgumentException e) {
          return false;
      }
    }

    public static boolean validateTicketPrice(String price) {
        if(price == null) {
            return false;
        }

        int parsedPrice = Integer.parseInt(price);

        return parsedPrice > 0 && parsedPrice % 2 == 0;
    }

    public static boolean validateStartDate(String startDate) {

      if(startDate == null || startDate.isEmpty()) {
        return false;
      }

      LocalDate today = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      LocalDate parsedStartDate;
      try {
          parsedStartDate = LocalDate.parse(startDate, formatter);
      } catch (DateTimeParseException e) {
          return false;
      }

        return !parsedStartDate.isAfter(today);
    }

  public static String findBiggestViolation() {
      int maxViolation = Math.max(totalTicketTypeViolation, Math.max(totalStartDateViolation, totalPriceViolation));

      if(maxViolation == totalTicketTypeViolation) {
          return "ticket type";
      } else if (maxViolation == totalStartDateViolation) {
          return "start date";
      } else {
          return "price";
      }
  }
}
