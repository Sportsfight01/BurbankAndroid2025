package backend;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import common.AppController;
import common.Common;
import common.TransparentProgressDialog;
import interfaces.WebApiJobDetailsResponseCallBack;
import interfaces.WebApiResponseCallback;
import models.MyPlaceCredentials;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WebApi {
    private static Context context;
    OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String returnString = "";

    static OkHttpClient clientForMP;

    public WebApi(Context context) {
        WebApi.context = context;
        client = getUnsafeOkHttpClient(false);
    }

    private static OkHttpClient getUnsafeOkHttpClient(boolean addCookies) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.addInterceptor(new LoggingInterceptor());
            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            builder.cookieJar(cookieJar);

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder.connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).retryOnConnectionFailure(addCookies).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OkHttpClient getClient() {
        return getUnsafeOkHttpClient(true);
    }

    public void getDataWithHeaders(String url, String base64String, final WebApiResponseCallback callback, final TransparentProgressDialog pd) {
        final Request request = new Request.Builder().url(url).addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + base64String).build();
        Log.d("ServerUrl", url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onErrorResult(e.fillInStackTrace().toString());
                cancelProgressDialog(pd);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    if (response != null) {
                        callback.onSuccessResult(response.body().string());
                    } else {
                        callback.onErrorResult("WebAPI Not Responding ");
                    }
                } else {
                    callback.onErrorResult(response.message());
                }
                cancelProgressDialog(pd);
            }
        });
    }

    public void postData(String url, String json, final WebApiResponseCallback callback, final TransparentProgressDialog pd) {
        OkHttpClient client1 = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(new LoggingInterceptor())
                .build();
        // socket timeout
        Log.d("ServerUrl", url);
        System.out.println("CLIENT URL:: "+url);
        RequestBody reqBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(reqBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cancelProgressDialog(pd);
                callback.onErrorResult(e.fillInStackTrace().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                cancelProgressDialog(pd);
                if (response.code() == 200 || response.code() == 201) {
                    if (response != null) {
                        callback.onSuccessResult(response.body().string());
                    } else {
                        callback.onErrorResult(Common.somethingErrorMessage);
                    }
                } else {
                    callback.onErrorResult(Common.somethingErrorMessage);
                }
            }
        });
    }

    public String postData(String url, String json) {
        RequestBody reqBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(reqBody).build();
        Log.d("ServerUrl", url);
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return result;
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return "";
    }

    public void postData(String url, String json, final WebApiResponseCallback callback) {
        RequestBody reqBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(reqBody).build();
        Log.d("ServerUrl", url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                callback.onErrorResult(e.fillInStackTrace().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 || response.code() == 201) {
                    if (response != null) {
                        String result = response.body().string();
                        callback.onSuccessResult(result);
                    } else {
                        callback.onErrorResult("WebAPI Not Responding ");
                    }
                } else {
                    callback.onErrorResult("Internal Server Error...");
                }
            }
        });
    }

    public void postData(String url, String json, final WebApiJobDetailsResponseCallBack callback) {
        RequestBody reqBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(reqBody).build();
        Log.d("ServerUrl", url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                callback.onJobDetailsErrorResult(e.fillInStackTrace().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 || response.code() == 201) {
                    if (response != null) {
                        String result = response.body().string();
                        callback.onJobDetailsSuccessResult(result);
                    } else {
                        callback.onJobDetailsErrorResult("WebAPI Not Responding ");
                    }
                } else {
                    callback.onJobDetailsErrorResult("Internal Server Error...");
                }
            }
        });
    }

    public void cancelProgressDialog(final TransparentProgressDialog pd) {
        if (pd != null) {
            pd.cancel();
        }
    }


    public void getDataNewMyPlace(String url, MyPlaceCredentials my_Place_Details, final WebApiJobDetailsResponseCallBack callback) {
        final Request request = new Request.Builder().url(url)
                .addHeader("Authorization", my_Place_Details.getEncoded())
                .addHeader("ContractNumber", my_Place_Details.getJobNumber())
                .build();
        Log.d("ServerUrl", url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onJobDetailsErrorResult(e.fillInStackTrace().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 || response.code() == 201) {
                    if (response != null) {
                        String result = response.body().string();
                        callback.onJobDetailsSuccessResult(result);
                    } else {
                        callback.onJobDetailsErrorResult("WebAPI Not Responding ");
                    }
                } else {
                    callback.onJobDetailsErrorResult("Internal Server Error...");
                }
            }
        });
    }

    public String getDataWithHeaders(String url, String base64String, String contractNumber) {
        Log.d("ServerUrl", url);
        final Request request = new Request.Builder().url(url)
                .addHeader("Authorization", "Basic " + base64String)
                .addHeader("ContractNumber", contractNumber)
                .build();

        try {
            clientForMP = getClient();
            Response response = clientForMP.newCall(request).execute();
            String result = response.body().string();
            return result;
        }/** @exception IOException On input error.*/ catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }

    public String postData_to_MyPlace(String url, String json) {
        Log.d("ServerUrl", url);

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            clientForMP = getClient();
            Response response = clientForMP.newCall(request).execute();

            String result = response.body().string();
            return result;
        }/** @exception IOException On input error.*/ catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }


    /************get myPlace data********************************/
    public String getData_From_MyPlace(String url) {
        Log.d("ServerUrl", url);
        Request request = new Request.Builder().url(url).build();
        try {
            //clientForMP = getClient();
            if (clientForMP == null) {
                clientForMP = getClient();
            }
            Response response = clientForMP.newCall(request).execute();
            System.out.println("getData_From_MyPlace headers:: "+response.headers());

            String result = response.body().string();
            int length = result.length();
            return result;
        } /** @exception IOException On input error.*/ catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
            return "Error :" + ex.getMessage();
        }
    }

    /************get myPlace data********************************/
    public String getFAQData_From_MyPlace(String url) {
        Request request = new Request.Builder().url(url).build();
        Log.d("ServerUrl", url);
        try {
            clientForMP = getClient();
            Response response = clientForMP.newCall(request).execute();
            String result = response.body().string();
            int length = result.length();
            return result;
        } /** @exception IOException On input error.*/ catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }

     private static class LoggingInterceptor implements Interceptor {
        @NonNull
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            Response response = chain.proceed(request);
            System.out.println("Resposnse code url:: "+request.url()+" code:: "+response.code());

           if(response.code()==401){
               System.out.println("Resposnse code without header:: "+response.code());
               String newToken="";
               JsonObject jsonObject = new JsonObject();
               jsonObject.addProperty("Username", AppController.AuthUserName);
               jsonObject.addProperty("Password", AppController.AuthPassword);

               String result = AppController.controller.webApiCall().postData_to_MyPlace("http://10.6.45.14:8085/api/api/Account/Authenticate", jsonObject.toString());
               JSONObject jsonObject1 = null;
               try {
                   jsonObject1 = new JSONObject(result);
                   newToken=jsonObject1.getString("Token");

                   Request originalRequest = chain.request();
                   Request.Builder authorisedRequestBuilder = originalRequest.newBuilder();

                   Request modifiedRequest = authorisedRequestBuilder
                           .addHeader("Accept", "application/json")
                           .header("Authorization", "Bearer "+newToken).build();
//               val retryOtherresponse= chain.proceed(modifiedRequest)
                   response.close();
                   response=chain.proceed(modifiedRequest);

                   System.out.println("Resposnse code with header:: "+response.code());


               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }

            return response;
        }
    }












    /*public String getContactDetails(String url2, String url3) {
        Log.d("ServerUrl",url2);
        Log.d("ServerUrl",url3);
        Request request2 = new Request.Builder().url(url2).build();
        try {
            //clientForMP = getClient();
            Response response2 = clientForMP.newCall(request2).execute();
            String result2 = response2.body().string();
            int length = result2.length();
            if(result2!=null && !result2.equalsIgnoreCase("null")){
                Request request3 = new Request.Builder().url(url3).build();
                try {
                    //clientForMP = getClient();
                    Response response3 = clientForMP.newCall(request3).execute();
                    String contactDetails = response3.body().string();
                    int contactLength = contactDetails.length();
                    return contactDetails;
                } *//** @exception IOException On input error.*//* catch (Exception ex) {
                    //for printing the type of exception..
                    ex.fillInStackTrace();
                }
            }
        } *//** @exception IOException On input error.*//* catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }

    public String postDataNewMyPlace(String url, String json, MyPlaceCredentials my_Place_Details) {
        RequestBody body = RequestBody.create(JSON, json);
        Log.d("ServerUrl",url);
        *//*Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", my_Place_Details.getEncoded())
                .addHeader("ContractNumber", my_Place_Details.getJobNumber())
                .addHeader("Content-Type", "application/json")
                .build();*//*

        Request request = new Request.Builder().url(url).post(body).build();
        try {
            clientForMP = getClient();
            Response response = clientForMP.newCall(request).execute();
            String test = response.body().string();
            return response.body().string();
        }*//** @exception IOException On input error.*//* catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }

    public String getDataNewMyPlace(String url, MyPlaceCredentials my_Place_Details) {
        Log.d("ServerUrl",url);
        final Request request = new Request.Builder().url(url)
                .addHeader("Authorization", my_Place_Details.getEncoded())
                .addHeader("ContractNumber", my_Place_Details.getJobNumber())
                .build();
        try {
            clientForMP = getClient();
            Response response = clientForMP.newCall(request).execute();

            String test = response.body().string();
            return response.body().string();
        }*//** @exception IOException On input error.*//* catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }*/
    /*public String postData_to_MyPlace(String url, String header, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", header)
                .build();
        try {
            clientForMP = getClient();
            Response response = clientForMP.newCall(request).execute();

            return response.body().string();
        }*//** @exception IOException On input error.*//* catch (Exception ex) {
            //for printing the type of exception..
            ex.fillInStackTrace();
        }
        return "";
    }*/
}

