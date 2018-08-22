package nu.yona.app.api.manager.network;

import java.util.Locale;

import nu.yona.app.api.model.AppMetaInfo;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.listener.DataLoadListenerImpl;
import retrofit2.Retrofit;

public class AppNetworkImpl extends BaseImpl {
    /**
     * post open app event from the device.
     *
     * @param url the device password
     * @param listener       the listener
     */
    public void postYonaOpenAppEvent(String url, String yonaPassword, DataLoadListenerImpl listener) {
        AppMetaInfo appMetaInfo = new AppMetaInfo();
        getRestApi().postOpenAppEvent(url, yonaPassword,  Locale.getDefault().toString().replace('_', '-'),appMetaInfo).enqueue(getCall(listener));
    }
}
