package ua.anon.unfeeling.transportanother;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface Links {

    @FormUrlEncoded
    @POST("/app/")
    Call<Object> test(@FieldMap Map<String, String> username);
}
