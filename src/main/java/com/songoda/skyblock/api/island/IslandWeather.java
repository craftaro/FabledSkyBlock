package com.songoda.skyblock.api.island;

import org.bukkit.WeatherType;

public interface IslandWeather {

    boolean isSynchronised();

    void setSynchronised(boolean synchronised);

    int getTime();

    void setTime(int time);

    WeatherType getWeather();

    void setWeather(WeatherType weather);

}
