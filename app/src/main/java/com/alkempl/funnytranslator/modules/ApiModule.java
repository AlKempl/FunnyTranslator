package com.alkempl.funnytranslator.modules;

import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
public class ApiModule {
    private static final String parallelDotsUrl = "https://apis.paralleldots.com/v4/";
    private static final String PARALLEL_DOTS_API_KEY = "emSSmNAKCWfXrB4EXXWOlZ1SZkVXevnsQinAZQWQFeg";

    private static final String translateUrl = "https://systran-systran-platform-for-language-processing-v1.p.rapidapi.com/translation/text/translate";
    private static final String TRANSLATE_API_KEY = "ba4c5570c5mshcec50e8bbe46502p1fc864jsn90c16519925a";

    private static final String funTranslationUrl = "https://api.funtranslations.com/translate/";

    private static final OkHttpClient client = new OkHttpClient();

    public static String getTranslation(String text, String languageCode) {

        HttpUrl.Builder httpBuilder = HttpUrl
                .parse(translateUrl)
                .newBuilder()
                .addQueryParameter("source", languageCode)
                .addQueryParameter("target", "en")
                .addQueryParameter("input", text);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .addHeader("x-rapidapi-key", TRANSLATE_API_KEY)
                .addHeader("x-rapidapi-host", "systran-systran-platform-for-language-processing-v1.p.rapidapi.com")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getFunTranslation(String text, String language) {

        HttpUrl.Builder httpBuilder = HttpUrl
                .parse(funTranslationUrl)
                .newBuilder()
                .addPathSegment(language)
                .addQueryParameter("text", text);

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}