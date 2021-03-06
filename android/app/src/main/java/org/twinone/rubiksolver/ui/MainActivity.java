package org.twinone.rubiksolver.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.twinone.rubiksolver.robot.RobotScheduler;
import org.twinone.rubiksolver.robot.SlightlyMoreAdvancedMapper;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static final int SIZE = 3;
    private BluetoothAdapter mBluetoothAdapter;
    private RobotScheduler mRobotScheduler;
    private SlightlyMoreAdvancedMapper mMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new CameraFragment()).commit();


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                connectBluetooth();
            }
        });
    }


    public void connectBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
        Toast.makeText(this, "Enable Bluetooth and pair", Toast.LENGTH_LONG).show();
            return;
        }
        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("98:D3:31:30:3B:D8");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("20:17:06:12:47:36");
        for (BluetoothDevice x : mBluetoothAdapter.getBondedDevices()) {
            Log.d("Main", "Address: " + x.getAddress());
            for (ParcelUuid uuid : x.getUuids()) {
                Log.d("Main", "parceluuid:" + uuid.toString());
            }
        }
        try {
            mBluetoothAdapter.cancelDiscovery();

            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            socket.connect();

            mRobotScheduler = new RobotScheduler(socket.getInputStream(), socket.getOutputStream(), 10);
            Toast.makeText(this, "Connected to arduino", Toast.LENGTH_SHORT).show();

            mMapper = new SlightlyMoreAdvancedMapper();

        } catch (IOException e) {
            Log.d("Main", "Exception connecting to bluetooth: ", e);
        }
    }

    public RobotScheduler getRobotScheduler() {
        return mRobotScheduler;
    }
    public SlightlyMoreAdvancedMapper getMapper() {
        return mMapper;
    }

}
