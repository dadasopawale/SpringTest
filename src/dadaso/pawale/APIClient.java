package dadaso.pawale; 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class APIClient {

    private CredentialsProvider credentials;    
    public APIClient(){}
	
	public static void main(String[] args) {
		APIClient aPIClient = new APIClient();
		try {
			aPIClient.patchMethod();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
	}
    
    @SuppressWarnings("unchecked")
	public final void patchMethod() throws IOException {
    	/*HttpHost proxy = new HttpHost("proxy.com", 80, "http");
    	DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);*/
        CloseableHttpClient client = HttpClients.custom()
        							.setDefaultCredentialsProvider(credentials)
        							//.setRoutePlanner(routePlanner)
        							.build();
        
        try {
            HttpPatch httppost = new HttpPatch("https://ekoy-dev1.fa.em2.oraclecloud.com/crmRestApi/resources/latest/opportunities/10006");
            httppost.addHeader("Authorization", "Basic RHBfSW50ZWdyYXRvcjpEcHdvcmxkQDEyMw==");
            httppost.addHeader("Content-Type", "application/json");
            Gson gson = gsonWithAdapters();
            JsonObject jObject = new JsonObject();
            jObject.addProperty("SalesMethod", "Standard Sales Process");
            String json = gson.toJson(jObject);
            StringEntity jsonEntity = new StringEntity(json);
            httppost.setEntity(jsonEntity);
            CloseableHttpResponse response = client.execute(httppost);
            System.out.println(response.getStatusLine().getStatusCode());
            try {
                JsonObject responseParse = responseToJsonElement(response).getAsJsonObject();
                Map<String,Object> map = new HashMap<String,Object>();
                map = gson.fromJson(responseParse, map.getClass());
                System.out.println("Json Responce : " + responseParse.toString());
                System.out.println("PrimaryOrganizationId : " + map.get("PrimaryOrganizationId"));
                
            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
     
    }
    
    public final Gson gsonWithAdapters() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, INT_ADAPTER)
                .registerTypeAdapter(int.class, INT_ADAPTER)
                .registerTypeAdapter(Boolean.class, BOOL_ADAPTER)
                .registerTypeAdapter(boolean.class, BOOL_ADAPTER)
                .create();
        return gson;
    }
    public final JsonElement responseToJsonElement(final CloseableHttpResponse response) throws IOException {
        String responseString = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        checkResponseForExceptions(statusCode,responseString);
        JsonElement jsonResponse = new JsonParser().parse(responseString);
        return jsonResponse;
    }
   
    private static final TypeAdapter<Integer> INT_ADAPTER = new TypeAdapter<Integer>() {
        @Override public void write(final JsonWriter out, final Integer value) throws IOException {
            if (value == -1) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }
        @Override public Integer read(final JsonReader in) throws IOException {
            return in.nextInt();
        }

    };
    
    static final TypeAdapter<Boolean> BOOL_ADAPTER = new TypeAdapter<Boolean>() {
        @Override public void write(final JsonWriter out, final Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }
        @Override public Boolean read(final JsonReader in) throws IOException {
            return in.nextBoolean();
        }
    };
    
    private void checkResponseForExceptions(final int statuscode, final String response) {
        if (!(Integer.toString(statuscode).startsWith("2"))) {
            throw new APIException("\nAPI response error found with status code:"
                    + statuscode
                    + "\nThe response content was this:"
                    + response);
        }
    }
    
    @SuppressWarnings("serial")
	class APIException extends RuntimeException {
        public APIException(final String message) {
            super(message);
        }

    }
    
}