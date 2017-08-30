package io.temperley.ui;

/**
 * Created by willtemperley@gmail.com on 06-Jun-17.
 */

        import java.io.IOException;
        import java.lang.reflect.Type;
        import java.util.List;
        import java.util.Map;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.reflect.TypeToken;
        import org.apache.http.Consts;
        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.methods.HttpRequestBase;
        import org.apache.http.impl.client.HttpClientBuilder;
        import org.apache.http.util.EntityUtils;

/**
 *
 * @author David
 */
public class TileServerApi {

//    public
    private final String servletURL;
    Gson gson = new GsonBuilder().create();
    Type intIntMapType = new TypeToken<Map<Integer, Integer>>() {}.getType();
    Type intStringMapType = new TypeToken<Map<Integer, String>>() {}.getType();

    public TileServerApi(String servletURL) {
        this.servletURL = servletURL;
    }

//    public static void main(String[] args) throws IOException {
//
////        TileServerApi tileServerApi = new TileServerApi();
////        String http = tileServerApi.doGet(servletURL + "colourmap");
//
//        System.out.println("http = " + http);
//
//
//        for (Map.Entry<Integer, Integer> integerIntegerEntry : colourMap.entrySet()) {
//            System.out.println("integerIntegerEntry = " + integerIntegerEntry);
//        }
//    }

    public Map<Integer, String> getColourMap() {
        try {
            String url = servletURL + "colourmap";
            String body = doGet(url);
            return gson.fromJson(body, intStringMapType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Map<Integer, Integer> getImageStats(String imageHash) {
        try {
            String url = servletURL + imageHash;
            String body = doGet(url);
            return gson.fromJson(body, intIntMapType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String postHttp(String url, List<NameValuePair> params, List<NameValuePair> headers) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        post.getEntity().toString();

        if (headers != null) {
            for (NameValuePair header : headers) {
                post.addHeader(header.getName(), header.getValue());
            }
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(post);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);
        }
        return null;
    }

    private String doGet(String url) throws IOException {
        HttpRequestBase request = new HttpGet(url);

//        if (headers != null) {
//            for (NameValuePair header : headers) {
//                request.addHeader(header.getName(), header.getValue());
//            }
//        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);
        }
        return null;
    }
}
