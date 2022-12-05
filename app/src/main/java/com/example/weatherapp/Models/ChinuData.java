package com.example.weatherapp.Models;
import java.util.List;

public class ChinuData {
    public List<Weather> weather;
    public main main;
    public String name;
    public ChinuData(String Name){ this.name=name;}
    public ChinuData(List<Weather> weather,com.example.weatherapp.Models.main main){
        this.weather = weather;
        this.main=main;
    }
    public String getName(){ return name;}
    public void setName(String name){ this.name=name;}
    public List<Weather> getWeather() {
        return weather;
    }
    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
    public com.example.weatherapp.Models.main getMain() {
        return main;
    }
    public void setMain(com.example.weatherapp.Models.main main){ this .main=main;}
}
