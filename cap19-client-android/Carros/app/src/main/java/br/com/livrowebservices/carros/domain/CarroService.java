package br.com.livrowebservices.carros.domain;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.livrowebservices.carros.utils.HttpHelper;
import livroandroid.lib.utils.IOUtils;

public class CarroService {
    private static final String URL_BASE = "http://livrowebservices.com.br/rest/carros";
    private static final boolean LOG_ON = true;
    private static final String TAG = "CarroService";

    public static List<Carro> getCarros(Context context, String tipo) throws IOException {
        String url = URL_BASE + "/tipo/" + tipo;
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        return carros;
    }

    public static ResponseWithURL postFotoBase64(Context context,File file) throws IOException {
        String url = URL_BASE + "/postFotoBase64";

        Log.d(TAG,"postFotoBase64: " + url);
        Log.d(TAG,"postFotoBase64 File: " + file);

        // Converte para Base64
        byte[] bytes = IOUtils.toBytes(new FileInputStream(file));
        String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

        //Log.d(TAG, "base64: " + base64);

        Map<String,String> params = new HashMap<String,String>();
        params.put("fileName", file.getName());
        //params.put("base64", "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");
        params.put("base64", base64);

        Log.d(TAG,">> postFotoBase64: " + params);
        HttpHelper http = new HttpHelper();
        http.setContentType("application/x-www-form-urlencoded");
        http.setCharsetToEncode("UTF-8");
        String json = http.doPost(url, params, "UTF-8");
        Log.d(TAG,"<< postFotoBase64: " + json);

        ResponseWithURL response = new Gson().fromJson(json, ResponseWithURL.class);

        Log.d(TAG,"ResponseWithURL: " + response);

        return response;
    }

    public static ResponseWithURL postFotoBase64(Context context,String fileName,String base64) throws IOException {
        String url = URL_BASE + "/postFotoBase64";

        Log.d(TAG,"postFotoBase64: " + url);
        Log.d(TAG, "fileName File: " + fileName);

        Map<String,String> params = new HashMap<String,String>();
        params.put("fileName", fileName);
        params.put("base64", base64);

        Log.d(TAG,">> postFotoBase64: " + params);
        HttpHelper http = new HttpHelper();
        http.setContentType("application/x-www-form-urlencoded");
        http.setCharsetToEncode("UTF-8");
        String json = http.doPost(url, params, "UTF-8");
        Log.d(TAG,"<< postFotoBase64: " + json);

        ResponseWithURL response = new Gson().fromJson(json, ResponseWithURL.class);

        Log.d(TAG,"ResponseWithURL: " + response);

        return response;
    }

    public static Response saveCarro(Context context,Carro carro) throws IOException {
        String url = URL_BASE;

        String jsonCarro = new Gson().toJson(carro);
        Log.d(TAG, ">> saveCarro: " + jsonCarro);
        HttpHelper http = new HttpHelper();
        http.setContentType("application/json; charset=utf-8");
        String json = http.doPost(url,jsonCarro.getBytes(),"UTF-8");
        Log.d(TAG,"<< saveCarro: " + json);

        Response response = new Gson().fromJson(json, Response.class);

        return response;
    }

    public static List<Carro> buscaCarros(Context context, String nome) throws IOException {
        String url = URL_BASE + "/nome/" + nome;
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        return carros;
    }

    private static List<Carro> parserJSON(Context context, String json) throws IOException {
        List<Carro> carros = new ArrayList<Carro>();
        try {
//            JSONObject root = new JSONObject(json);
//            JSONObject obj = root.getJSONObject("carros");
//            JSONArray jsonCarros = obj.getJSONArray("carro");

            JSONArray jsonCarros = new JSONArray(json);

            // Insere cada carro na lista
            for (int i = 0; i < jsonCarros.length(); i++) {
                JSONObject jsonCarro = jsonCarros.getJSONObject(i);
                Carro c = new Carro();
                // Lê as informações de cada carro
                c.id = jsonCarro.optLong("id");
                c.tipo = jsonCarro.optString("tipo");
                c.nome = jsonCarro.optString("nome");
                c.desc = jsonCarro.optString("desc");
                c.urlFoto = jsonCarro.optString("urlFoto");
                c.urlInfo = jsonCarro.optString("urlInfo");
                c.latitude = jsonCarro.optString("latitude");
                c.longitude = jsonCarro.optString("longitude");
                if (LOG_ON) {
                    //Log.d(TAG, "Carro ("+c.id+") " + c.nome + " > " + c.urlFoto);
                }
                carros.add(c);
            }
            if (LOG_ON) {
                Log.d(TAG, carros.size() + " encontrados.");
            }
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new IOException(e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage(),e);
            throw new IOException("Erro ao ler os dados: " + e.getMessage(), e);
        }
        return carros;
    }

    public static Response delete(Context context, Carro c) throws IOException {
        String url = URL_BASE + "/"+c.id;
        HttpHelper http = new HttpHelper();
        http.setContentType("application/json; charset=utf-8");
        String json = http.doDelete(url);
        Log.d(TAG,"Delete json: " + json);

        Gson gson = new Gson();
        Response response = gson.fromJson(json,Response.class);
        return response;
    }


}