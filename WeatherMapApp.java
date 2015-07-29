package net.aksingh.owmjapis;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.*;
import java.io.IOException;
import org.json.JSONException;
import java.text.ParseException;
import java.util.Scanner;


/**
  * Written by: Kevin Wilson
  * Date: 7/15/15
  */


public class WeatherMapApp {


    public static void main(String[] args)
            throws IOException, JSONException, ParseException, IllegalArgumentException {


        System.out.println("\n   Baseball fans, get the weather for the home town of your favorite team. ");
        System.out.println("            National League teams only at this time.\n");

        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        BaseballTeams bbTeams = (BaseballTeams) context.getBean("teamNames");
        System.out.println(" ");
        Map<String, String> tmpTMap = bbTeams.getTeamMap();
        tmpTMap.forEach((k, v) -> System.out.println("   " + k));

        System.out.println("\nType in the name of the team you are interested in and hit enter  ");
        Scanner symbolInput = new Scanner(System.in);
        String strKey = symbolInput.nextLine();

        /* check for valid entry */
        if (!tmpTMap.containsKey(strKey))
          throw new IllegalArgumentException("!! Input Error - input entry not valid ");

        /* Input entry is valid */
         String value = tmpTMap.get(strKey);
         int cityID = Integer.parseInt(value);
         WeatherMapApp.runCityApp(strKey, cityID);


    }  //closes main


    private static void runCityApp(String key, int cityID) throws JSONException {

            //declares an object of OpenWeatherMap
            OpenWeatherMap owm = new OpenWeatherMap("");

            CurrentWeather cwc = owm.currentWeatherByCityCode(cityID);
            String CityName;
            CityName = cwc.getCityName();


            System.out.println("\nThe " + key + " home field is in " + CityName + "\n");
            System.out.println("\nCurrent weather conditions in  ");
            System.out.println(CityName + "\n");

            // print the current temperature
            System.out.println("Temperature: " + Math.round(cwc.getMainInstance().getTemperature()) + "ºF");
            String TempDescription;
            if (cwc.getMainInstance().getTemperature() > 84.99)
                TempDescription = "hot temperatures";
            else {
                if (cwc.getMainInstance().getTemperature() > 74.99)
                    TempDescription = "warm temperatures";
                else if (cwc.getMainInstance().getTemperature() > 64.99)
                    TempDescription = "mild temperatures";
                else TempDescription = "cool temperatures";
            }

            if (cwc.isValid()) {
                AbstractWeather.Weather weatherInstance = cwc.getWeatherInstance(0);
                System.out.println("\n" + weatherInstance.getWeatherDescription() + " and " + TempDescription);
            }


            if (cwc.getMainInstance().hasHumidity())
                System.out.println("\nHumidity level:  " + Math.round(cwc.getMainInstance().getHumidity()) + "%");
            if (cwc.hasWindInstance()) {
                AbstractWeather.Wind windInstance = cwc.getWindInstance();
                if (windInstance.hasWindSpeed()) {
                    // declare an object of the "Tools" class to get speed & direction
                    Tools tools = new Tools();
                    float wDeg = windInstance.getWindDegree();
                    System.out.println("Winds:  " + tools.convertDegree2Direction(wDeg) + " at "
                            + windInstance.getWindSpeed() + " MPH");
                }
                if (cwc.getWindInstance().hasWindGust())
                    System.out.println("   - reports of Wind Gusts - ");
            }


    }  //closes runCityApp

}  //closes WeatherMapApp
