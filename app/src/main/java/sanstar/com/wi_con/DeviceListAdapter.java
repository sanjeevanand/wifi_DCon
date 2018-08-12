package sanstar.com.wi_con;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sanstar.com.wi_con.Api.ApiClient;
import sanstar.com.wi_con.Api.ApiInterface;
import sanstar.com.wi_con.extraview.Utilities;
import sanstar.com.wi_con.pojo.DeviceActionModle;
import sanstar.com.wi_con.pojo.DeviceActionResult;
import sanstar.com.wi_con.pojo.DeviceListModle;
import sanstar.com.wi_con.pojo.RealTimeDevice;

/**
 * Created by shivamraj on 12/08/18.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    private Context context;
    List<RealTimeDevice> deviceListModles = new ArrayList<>();
    private String timervalues="";
    String str1[] =null;
    String timer1Values,timer2Values,edDeviceNamevalues;

    ApiInterface apiInterface;
    Utilities utilities;
    DeviceActionResult deviceActionModles = new DeviceActionResult();
    String devicename, deviceaction;

    public DeviceListAdapter(Context context, List<RealTimeDevice> deviceListModles) {
        this.context =context;
        this.deviceListModles =deviceListModles;
        notifyDataSetChanged();
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_device_details, parent, false);
        // set the view's size, margins, paddings and layout parameters¥
        DeviceViewHolder vh = new DeviceViewHolder(v); // pass the view to View Holder
        return vh;


    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, final int position) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        holder.deviceName.setText(deviceListModles.get(position).getDevice());
        holder.deviceStatus.setText(deviceListModles.get(position).getSwitchStatus());
        timervalues = deviceListModles.get(position).getTime();
        str1 = timervalues.split(":");//str1[0]="01" str1[1]="30"
        holder.deviceTimeHar.setText(str1[0]);
        holder.deviceTimerMin.setText(str1[1]); 
        holder.imTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_timer_popup);
                final  LinearLayout llcancel = (LinearLayout)dialog.findViewById(R.id.cancel);
                final  LinearLayout llok = (LinearLayout)dialog.findViewById(R.id.ok);
                final Spinner spinnerTimer1 =(Spinner)dialog.findViewById(R.id.timer1);
                final Spinner spinnerTimer2 =(Spinner)dialog.findViewById(R.id.timer2);
                spinnerTimer1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                         timer1Values=  spinnerTimer1.getItemAtPosition(i).toString();
                        Toast.makeText(context,"Values"+timer1Values,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                spinnerTimer2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                         timer2Values=  spinnerTimer2.getItemAtPosition(i).toString();
                        Toast.makeText(context,"Values"+timer2Values,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                llok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });

                llcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        holder.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devicename = deviceListModles.get(position).getDevice();
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_device_update);
                final  LinearLayout llcancel = (LinearLayout)dialog.findViewById(R.id.cancel);
                final EditText edDeviceName= (EditText) dialog.findViewById(R.id.ed_devicename);
                final  LinearLayout llok = (LinearLayout)dialog.findViewById(R.id.ok);
                edDeviceName.setText(deviceListModles.get(position).getDevice());
                llok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (edDeviceName.getText().length() ==0){
                            Toast.makeText(context,"enter device name",Toast.LENGTH_SHORT).show();
                        }else {
                            edDeviceNamevalues = edDeviceName.getText().toString();
                            utilities.displayProgressDialog(context,"Loading....",true);

                            Call<DeviceActionModle> call = apiInterface.getDeviceUpdate(devicename,edDeviceNamevalues);
                            call.enqueue(new Callback<DeviceActionModle>() {
                                @Override
                                public void onResponse(Call<DeviceActionModle> call, Response<DeviceActionModle> response) {
                                    utilities.cancelProgressDialog();
                                    deviceActionModles= response.body().getResult();
                                    holder.deviceName.setText(deviceActionModles.getValue());
                                }

                                @Override
                                public void onFailure(Call<DeviceActionModle> call, Throwable t) {
                                    utilities.cancelProgressDialog();

                                }
                            });
                            dialog.dismiss();
                        }

                    }
                });
                llcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        holder.deviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilities.displayProgressDialog(context,"Loading....",true);
                 devicename = deviceListModles.get(position).getDevice();
                deviceaction  = deviceListModles.get(position).getSwitchStatus();

                Call<DeviceActionModle> call = apiInterface.getdeviceAction(devicename,deviceaction);
                call.enqueue(new Callback<DeviceActionModle>() {
                    @Override
                    public void onResponse(Call<DeviceActionModle> call, Response<DeviceActionModle> response) {
                        utilities.cancelProgressDialog();
                        deviceActionModles=  response.body().getResult();
                        holder.deviceStatus.setText(deviceActionModles.getValue());
                    }

                    @Override
                    public void onFailure(Call<DeviceActionModle> call, Throwable t) {
                        Log.i("ERROR",t.toString());
                        utilities.cancelProgressDialog();

                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return deviceListModles.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView deviceName,deviceStatus,deviceTimeHar,deviceTimerMin;
        public ImageView imRefresh,imTimer,imEdit;
        public DeviceViewHolder(View itemView) {
            super(itemView);
            deviceName = (TextView)itemView.findViewById(R.id.devicename);
            deviceTimeHar = (TextView)itemView.findViewById(R.id.devicetime_har);
            deviceTimerMin = (TextView)itemView.findViewById(R.id.devicetime_min);
            deviceStatus = (TextView)itemView.findViewById(R.id.device_status);
            imRefresh = (ImageView)itemView.findViewById(R.id.device_refresh);
            imTimer = (ImageView)itemView.findViewById(R.id.device_timerset);
            imEdit = (ImageView)itemView.findViewById(R.id.devicename_update);
            imRefresh.setOnClickListener(this);
            imTimer.setOnClickListener(this);
            imEdit.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
