package template;

import com.android.ddmlib.IDevice;
import template.adb.command.Command;

/**
 * Created by brucetoo on 25/03/2017.
 */
public interface OnConnectCallBack {
    void connectCallBack(boolean noDevice,IDevice devices[],Command runnable);
}
