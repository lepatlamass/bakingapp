package com.movieapp.konwo.sweetbaking.api;

import com.movieapp.konwo.sweetbaking.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesService {
    @GET("baking.json")
    Call<List<Recipe>> getRecipes();
}