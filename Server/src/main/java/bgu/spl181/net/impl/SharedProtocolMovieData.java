package bgu.spl181.net.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedProtocolMovieData<T> extends SharedProtocolUsersData<T> {
    protected final String moviesPath = "Database/Movies.json";

    public SharedProtocolMovieData(ConnectionsImpl connectionsImpl) {
        super(connectionsImpl);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Integer.class, (JsonSerializer<Integer>) (integer, type, jsonSerializationContext) -> new JsonPrimitive(integer.toString()));
        gson = gsonBuilder.create();
    }


    //after finishing with the arrayList- should call readLock().unlock
    public ArrayList<Movie> getMovies() {
        File jsonFile = new File(moviesPath);
        try (FileReader reader = new FileReader(jsonFile)) {
            Type arrayListType = new TypeToken<ArrayList<Movie>>() {
            }.getType();
            JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
            return gson.fromJson(jsonElement.getAsJsonObject().get("movies"), arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateMovies(ArrayList<Movie> movies) {
        File jsonFile = new File(moviesPath);
        try (FileWriter writer = new FileWriter(jsonFile)) {
            JsonArray jsonArrayMovies = gson.toJsonTree(movies).getAsJsonArray();
            JsonObject jsonMovies = new JsonObject();
            jsonMovies.add("movies", jsonArrayMovies);
            writer.write(jsonMovies.toString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
