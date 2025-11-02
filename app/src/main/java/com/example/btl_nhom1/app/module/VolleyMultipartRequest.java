package com.example.btl_nhom1.app.module;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import lombok.Getter;

public class VolleyMultipartRequest extends Request<NetworkResponse> {
    private static final String TAG = "VolleyMultipart";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;

        Log.d(TAG, "Creating multipart request to: " + url);
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // Thêm text parameters
            Map<String, String> params = getParams();
            if (params != null && !params.isEmpty()) {
                Log.d(TAG, "Adding text params: " + params.size() + " fields");
                textParse(dos, params, getParamsEncoding());
            }

            // Thêm file data
            Map<String, DataPart> data = getByteData();
            if (data != null && !data.isEmpty()) {
                Log.d(TAG, "Adding file data: " + data.size() + " files");
                dataParse(dos, data);
            }

            // Kết thúc multipart
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            byte[] result = bos.toByteArray();
            Log.d(TAG, "Body size: " + result.length + " bytes");
            return result;

        } catch (IOException e) {
            Log.e(TAG, "Error building body: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private void textParse(DataOutputStream dos, Map<String, String> params, String encoding)
            throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dos, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + encoding, e);
        }
    }

    private void dataParse(DataOutputStream dos, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dos, entry.getValue(), entry.getKey());
        }
    }

    private void buildTextPart(DataOutputStream dos, String parameterName, String parameterValue)
            throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(parameterValue + lineEnd);

        Log.d(TAG, "Added param: " + parameterName + " = " + parameterValue);
    }

    private void buildDataPart(DataOutputStream dos, DataPart dataPart, String inputName)
            throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataPart.getFileName() + "\"" + lineEnd);
        if (dataPart.getType() != null && !dataPart.getType().trim().isEmpty()) {
            dos.writeBytes("Content-Type: " + dataPart.getType() + lineEnd);
        }
        dos.writeBytes(lineEnd);

        dos.write(dataPart.getContent());

        dos.writeBytes(lineEnd);

        Log.d(TAG, "Added file: " + inputName + " = " + dataPart.getFileName() +
                " (" + dataPart.getContent().length + " bytes)");
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            Log.d(TAG, "Response code: " + response.statusCode);
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Log.e(TAG, "Parse error: " + e.getMessage(), e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        Log.e(TAG, "Request error: " + error.getMessage());
        mErrorListener.onErrorResponse(error);
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }

    /**
     * Data class cho file upload
     */
    @Getter
    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

    }
}