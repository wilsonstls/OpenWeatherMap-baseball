package net.aksingh.owmjapis;


import org.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;



/**
  * Written by: Kevin Wilson
  * Date: 8/11/15
  * setup to run in Intellij IDEA
  */


public class WeatherMapApp {


    public static void main(String[] args)
            throws IOException, JSONException, ParseException, IllegalArgumentException {


        System.out.println("\n   Hey Baseball fans, get the weather for the home town of your favorite team. ");

        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");

        BaseballTeams bbTeams = (BaseballTeams) context.getBean("teamNames");
        System.out.println(" ");
        Map<String, String> tmpTMap = bbTeams.getTeamMap();
        tmpTMap.forEach((k, v) -> System.out.println("   " + k));

        System.out.println("\nType in the name of the team you are interested in, as shown above, and hit enter  ");
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


    private static ImageIcon runCityApp(String key, int cityID) throws JSONException, MalformedURLException {

        //declares an object of
        OpenWeatherMap owm = new OpenWeatherMap("");
        CurrentWeather cwc = owm.currentWeatherByCityCode(cityID);

        String CityName;
        CityName = cwc.getCityName();


        JPanel P = new JPanel();
        JLabel B1 = new JLabel("\n");
        JLabel L1 = new JLabel("The " + key + " home field is in " + CityName);
        JLabel L2 = new JLabel("Current weather conditions for ");
        JLabel B2 = new JLabel("\n");
        JLabel L3 = new JLabel(CityName);
        JLabel B3 = new JLabel("\n");
        L1.setAlignmentX(Component.CENTER_ALIGNMENT);
        L2.setAlignmentX(Component.CENTER_ALIGNMENT);
        L3.setAlignmentX(Component.CENTER_ALIGNMENT);

        P.setLayout(new BoxLayout(P, BoxLayout.PAGE_AXIS));
        P.add(L1);
        P.add(B1);
        P.add(L2);
        P.add(B2);
        P.add(B2);
        P.add(L3);
        P.add(B3);


        // print the current temperature
        JLabel L5 = new JLabel("   " + Math.round(cwc.getMainInstance().getTemperature()) + "ºF");
        P.add(L5);
        String TempDescription;
        if (cwc.getMainInstance().getTemperature() > 84.99) {
            TempDescription = "hot temperatures"; }
        else {
            if (cwc.getMainInstance().getTemperature() > 74.99)
                TempDescription = "warm temperatures";
            else if (cwc.getMainInstance().getTemperature() > 64.99)
                TempDescription = "mild temperatures";
            else TempDescription = "cool temperatures";
            }

        //create object weatherInstance of the AbstractWeather class
        AbstractWeather.Weather weatherInstance = cwc.getWeatherInstance(0);

            //  fetch the condition image icon located in your resources
            ImageIcon icon = new ImageIcon("C:/LaunchCode/OWM JAPIs 2.5.0.5/OWM JAPIs 2.5.0.5/src/main/resources/" + weatherInstance.getWeatherIconName() + ".png");
            JLabel imagelabel = new JLabel();
            imagelabel.setIcon(icon);
            P.add(imagelabel);

            JLabel L6 = new JLabel(weatherInstance.getWeatherDescription() + " and " + TempDescription);
            L6.setAlignmentX(Component.CENTER_ALIGNMENT);
            P.add(L6);
            JLabel B6 = new JLabel("\n");
            P.add(B6);

            if (cwc.getMainInstance().hasHumidity()) {
                JLabel L7 = new JLabel("Humidity level:  " + Math.round(cwc.getMainInstance().getHumidity()) + "%");
                L7.setAlignmentX(Component.CENTER_ALIGNMENT);
                P.add(L7);
            }
            if (cwc.hasWindInstance()) {
                AbstractWeather.Wind windInstance = cwc.getWindInstance();
                if (windInstance.hasWindSpeed()) {
                    // declare an object of the "Tools" class to get speed & direction
                    Tools tools = new Tools();
                    float wDeg = windInstance.getWindDegree();
                    JLabel L8 = new JLabel("Winds:  " + tools.convertDegree2Direction(wDeg) + " at "
                            + windInstance.getWindSpeed() + " MPH");
                    L8.setAlignmentX(Component.CENTER_ALIGNMENT);
                    P.add(L8);
                }
                if (cwc.getWindInstance().hasWindGust()) {
                    JLabel L9 = new JLabel("   - reports of Wind Gusts - ");
                    L9.setAlignmentX(Component.CENTER_ALIGNMENT);
                    P.add(L9);
                }
            }

        JFrame frame = new JFrame("Current Conditions");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(P);
        frame.setVisible(true);

        return null;

    } //closes runCityApp
}//closes WeatherMapApp

