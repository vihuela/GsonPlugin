package com.ricky.plugin_gson_sdk;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;


public class GsonPluginUtil {

  private static JsonSyntaxErrorListener mListener;

  public static void setListener(JsonSyntaxErrorListener listener) {
    mListener = listener;
  }

  /**
   * used for array、collection、map、object
   * skipValue when expected token error
   *
   * @param in input json reader
   * @param expectedToken expected token
   */
  public static boolean checkJsonToken(JsonReader in, JsonToken expectedToken) {
    if (in == null || expectedToken == null) {
      return false;
    }
    JsonToken inToken = null;
    try {
      inToken = in.peek();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (inToken == expectedToken) {
      return true;
    }
    if (inToken != JsonToken.NULL) {
      String exception = "expected " + expectedToken + " but was " + inToken + " path " + in.getPath();
      notifyJsonSyntaxError(exception);
    }
    skipValue(in);
    return false;
  }

  /**
   * used for basic data type, we only deal type Number and Boolean
   * skipValue when json parse error
   *
   * @param in input json reader
   * @param exception json parse exception
   */
  public static void onJsonTokenParseException(JsonReader in, Exception exception) {
    if (in == null || exception == null) {
      return;
    }
    skipValue(in);
    notifyJsonSyntaxError(exception.getMessage());
  }

  private static void skipValue(JsonReader in) {
    if (in == null) {
      return;
    }
    try {
      in.skipValue();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void notifyJsonSyntaxError(String exception) {
    if (mListener == null) {
      return;
    }
    String invokeStack = Log.getStackTraceString(new Exception("syntax error exception"));
    mListener.onJsonSyntaxError(exception,invokeStack);
  }

  public interface JsonSyntaxErrorListener {
    public void onJsonSyntaxError(String exception, String invokeStack);
  }
}
