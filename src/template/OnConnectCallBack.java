package template;

import com.android.ddmlib.IDevice;
import template.adb.command.Command;
import template.ui.NotificationHelper;

/**
 * Created by brucetoo on 25/03/2017.
 */
public interface OnConnectCallBack {

    void onFirstCall(IDevice[] devices);

    void onRunCommand(Command runnable);

    void onDeviceConnected(IDevice device);

    void onDeviceDisconnected(IDevice iDevice);

    void onDeviceChanged(IDevice iDevice, int changeMask);
}
