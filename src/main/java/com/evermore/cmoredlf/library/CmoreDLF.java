package com.evermore.cmoredlf.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

@SuppressWarnings("unused")
public interface CmoreDLF {
    /**
     * The onCreate() method.<br>
     * 是個onCreate() method.
     *
     * @param appInterface The appInterface.
     */
    void onCreate(AppInterface appInterface);

    /**
     * Invoking functions that declared on CmorePAAS.<br>
     * 由CmorePAAS網頁或情境驅動呼叫對應的功能，回傳一個AsyncTask供執行。
     *
     * @param method     The method name.
     * @param parameters The method parameters as an String.
     * @return an AsyncTask to be executed.
     */
    AsyncTask invoke(String method, String parameters);

    /**
     * The onDestroy() method.<br>
     * 是個onDestroy() method.
     */
    void onDestroy();

    /**
     * A wrapper contains Android Context & User-Defined Parameters Map.<br>
     * 包裝CmoreDLF物件被呼叫初始化時會帶進的工具與變數。
     */
    interface AppInterface {
        /**
         * Get the Android Context.<br>
         * 取得Android Context物件。
         *
         * @return The Android Context.
         */
        Context getContext();

        /**
         * Get the parameters feed from CmorePAAS.<br>
         * 取得由CmorePAAS平台設置的相關參數。
         *
         * @return The parameters map.
         */
        HashMap<String, String> getParameters();

        /**
         * Get the Output interface object.<br>
         * 取得Output物件已進行輸出數值或以輸出Fragment的方式調出自訂的頁面Activity。
         *
         * @return The object that implements Output interface.
         */
        Output getOutput();

        /**
         * Get the {@link ChannelOut} object to the other DLF which has been assigned to this DLF.
         * 呼叫與此DLF連結的其他DLF輸出之{@link ChannelOut}物件，若有被設定的話。
         *
         * @return The {@link ChannelOut} object or null.
         */
        @Nullable
        ChannelOut getChannelOut();

        /**
         * Output an android.support.v4.app.Fragment and request an new Activity with a content view of it.<br>
         * (Use it when you need a native view instead of the web of CmorePAAS)<br>
         * 對App輸出一個android.support.v4.app.Fragment物件，請求以他為主畫面開啟一個Activity。
         *
         * @param contentFragment The Fragment
         */
        void requestNewActivity(Fragment contentFragment);

        /**
         * Helper for calling {@link android.app.Activity#startActivityForResult(Intent, int)}.<br>
         * 呼叫 {@link android.app.Activity#startActivityForResult(Intent, int)} 功能。
         *
         * @param activityClass The target Activity's Class.
         * @param requestCode   The Request Code.
         * @param extras        The Request Extras.
         * @param callback      Callback for result.
         */
        void startActivityForResult(@NonNull Class<? extends Activity> activityClass, @Nullable Bundle extras, int requestCode, @Nullable PreferenceManager.OnActivityResultListener callback);
    }

    /**
     * The class defines the output interfaces.<br>
     * Output物件，CmoreDLF可透過這個物件去與APP介面互動。<br>
     */
    interface Output {
        /**
         * Output a data String to app.<br>
         * 對App輸出一組資料。<br>
         *
         * @param name  Data name. 資料名稱
         * @param value Data value. 資料數值
         */
        void writeDataValue(String name, String value);

        /**
         * Set a status value of this {@link CmoreDLF}, statuses will be reused but  data-values won't.<br>
         * 輸出一組狀態資訊，狀態會被儲存並做運算利用但資料不會.<br>
         *
         * @param name  Status name. 狀態名稱
         * @param value Status value. 狀態數值
         */
        void setStatus(String name, String value);

        /**
         * @param contentFragment The Fragment
         * @deprecated **Moved to AppInterface.**
         * Output an android.support.v4.app.Fragment and request an new Activity with a content view of it.<br>
         * (Use it when you need a native view instead of the web of CmorePAAS)<br>
         * 對App輸出一個android.support.v4.app.Fragment物件，請求以他為主畫面開啟一個Activity。
         */
        @Deprecated
        void requestNewActivity(Fragment contentFragment);
    }

    /**
     * Function for supplying the {@link ChannelIn} interface.<br>
     * 輸出{@link ChannelIn}介面接收輸入的物件。
     *
     * @return An ChannelIn for handling incoming object.
     */
    ChannelIn onReceiveFromChannel();

    /**
     * Class for handling incoming object from other {@link CmoreDLF}s. <br>
     * 用來處理由其他DLF輸入的物件及資訊。
     */
    class ChannelIn {
        public void onFileReceived(HashMap<String, String> metadata, File file) {
        }

        public void onInputStreamReceived(HashMap<String, String> metadata, InputStream inputStream) {
        }

        public void onJSONObjectReceived(HashMap<String, String> metadata, JSONObject jsonObject) {
        }
    }

    /**
     * Proxy for writing object to {@link ChannelIn} of the other {@link CmoreDLF}.
     * 透過{@link ChannelIn}介面輸出物件予其他{@link CmoreDLF}。
     */
    interface ChannelOut {
        /**
         * A Helper function creates a PipedOutputStream for convenience writing to inputStream.
         * 產生一個方便寫出資訊的PipedOutputStream.
         *
         * @param metadata metadata
         * @return The created PipedOutputStream which's been piped to the following ChannelIn.
         * @throws IOException when OutputStream creation failed.
         */
        PipedOutputStream createOutputStream(HashMap<String, String> metadata) throws IOException;

        void sendFile(HashMap<String, String> metadata, File file);

        void sendInputStream(HashMap<String, String> metadata, InputStream inputStream);

        void sendJSONObject(HashMap<String, String> metadata, JSONObject jsonObject);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Invoke {
        String[] commands() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Channel {
        Class[] in() default {};

        Class[] out() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface App {
        String[] outputs() default {};
    }
}
