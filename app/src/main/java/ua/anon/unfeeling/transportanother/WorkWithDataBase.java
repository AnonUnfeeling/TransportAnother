package ua.anon.unfeeling.transportanother;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

class WorkWithDataBase{

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static final String BASE_URL = "http://carstop.in.ua";

    Retrofit restAdapter = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build();

    final Links apiService =
            restAdapter.create(Links.class);

    public String[] getStatus(int id){
        String[] status = new String[5];

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","stat_user");
        nameValuePair.put("in", ""+id+",@p1,@p2,@p3,@p4,@p5");
        nameValuePair.put("out", "@p1,@p2,@p3,@p4,@p5");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            int i =0;
            for (Map.Entry e : respons.entrySet()) {
                try {
                    status[i] = (String.valueOf(e.getValue()));
                }catch (NullPointerException| NumberFormatException ex){
                    status[i]="0";
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }


    public int[] setNumberPhone(Long numberPhone) {
        int[] data = new int[2];

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","login_");
        nameValuePair.put("in", ""+numberPhone+",@p1,@p2");
        nameValuePair.put("out", "@p1,@p2");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            int i = 0;
            Double[] d = new Double[2];
            for (Map.Entry e : respons.entrySet()) {
                d[i] = (Double) e.getValue();
                i++;
            }

            for (int j = 0; j < d.length; j++) {
                data[j]=d[j].intValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public double[] search(int id, int driver, int target, double x, double y, String exeption){
        double[] data = new double[10];

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","search_");
        nameValuePair.put("in", ""+id+","+driver+","+target+","+x+","+y+","+exeption+",@p1,@p2,@p3,@p4,@p5,@p6,@p7,@p8,@p9");
        nameValuePair.put("out", "@p1,@p2,@p3,@p4,@p5,@p6,@p7,@p8,@p9");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            int i = 0;
            for (Map.Entry e : respons.entrySet()) {
                try {
                    data[i] = (Double) e.getValue();
                    i++;
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return data;
    }

    public void sos(int idUser, @SuppressWarnings("SameParameterValue") int idContact, double x, double y){
        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","sos_");
        nameValuePair.put("in", ""+idUser+","+idContact+","+x+","+y);

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);

        try {
           response.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] onlineStart(int id, int driver, int target, double x, double y){
        double[] data = new double[9];

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","online_start");
        nameValuePair.put("in", ""+id+","+driver+","+target+","+x+","+y+",@p1,@p2,@p3,@p4,@p5,@p6,@p7,@p8,@p9");
        nameValuePair.put("out", "@p1,@p2,@p3,@p4,@p5,@p6,@p7,@p8,@p9");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            int i = 0;
            for (Map.Entry e : respons.entrySet()) {
                if(e.getValue()!=null) {
                    data[i] = (Double) e.getValue();
                    i++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public double[] contactSet(int id, double x, double y){
        double[] xy = new double[2];

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","contact_set");
        nameValuePair.put("in", ""+id+","+x+","+y+",@p1,@p2");
        nameValuePair.put("out", "@p1,@p2");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            int i = 0;
            for (Map.Entry e : respons.entrySet()) {
                xy[i] = (Double) e.getValue();
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return xy;
    }

    public void contactEnd(int id, int rating){
        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","contact_end");
        nameValuePair.put("in", ""+id+","+rating);

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);

        try {
            response.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String contactStatus (int id){
        String status="";

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","contact_status");
        nameValuePair.put("in", ""+id+",@p1");
        nameValuePair.put("out","@p1");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);

        try {
            Response<Object> res = response.execute();
            Map<Object, String> respons = gson.fromJson(res.body().toString(), Map.class);
            for (Map.Entry e : respons.entrySet()) {
                status = (String) e.getValue();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    public int contact (int idUser, int idDriver, double x, double y, int driver){
        int idContact = 0;

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","contact_");
        nameValuePair.put("in", ""+idUser+","+idDriver+","+x+","+y+","+driver+",@p1");
        nameValuePair.put("out", "@p1");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Double d = 0.0;

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            for (Map.Entry e : respons.entrySet()) {
                try {
                    d = (double) e.getValue();
                }catch (NullPointerException ex){
                    d=0.0;
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        idContact = d.intValue();

        return idContact;
    }

    public void onlineEnd(int id){
        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","online_end");
        nameValuePair.put("in", ""+id);

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);

        try {
            response.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] ping(int idUser, int idPing,int driver, double x, double y){
        double[] xy = new double[3];

        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","ping_");
        nameValuePair.put("in", ""+idUser+","+idPing+","+driver+","+x+","+y+",@p1,@p2,@p3");
        nameValuePair.put("out", "@p1,@p2,@p3");

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);
        try {
            Response<Object> res = response.execute();
            Map<Object, Double> respons = gson.fromJson(res.body().toString(), Map.class);

            int i = 0;
            for (Map.Entry e : respons.entrySet()) {
                try {
                    xy[i] = (Double) e.getValue();
                    i++;
                }catch (NullPointerException ex){
                    xy[i]=0;
                    ex.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return xy;
    }

    public void pingSet(int id, int driver,Double x, Double y){
        Map<String,String> nameValuePair = new HashMap<String,String>();
        nameValuePair.put("proc","ping_set");
        nameValuePair.put("in", ""+id+","+driver+","+x+","+y);

        String s= gson.toJson(nameValuePair);

        Map<String,String> send = new HashMap<String,String>();
        send.put("query", s);

        Call<Object> response = apiService.test(send);

        try {
            response.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
