package com.openxc.examples2.dashboard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.openxc.VehicleManager;
import com.openxc.examples2.R;
import com.openxc.measurements.*;
import com.openxc.remote.VehicleServiceException;

public class VehicleDashboardActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private TextView mSteeringWheelAngleView;
    private TextView mVehicleSpeedView;
    private TextView mFuelConsumedView;
    private TextView mFuelLevelView;
    private TextView mOdometerView;
    private TextView mVehicleBrakeStatusView;
    private TextView mParkingBrakeStatusView;
    private TextView mVehicleEngineSpeedView;
    private TextView mTorqueAtTransmissionView;
    private TextView mAcceleratorPedalPositionView;
    private TextView mTransmissionGearPosView;
    private TextView mIgnitionStatusView;
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private TextView mAndroidLatitudeView;
    private TextView mAndroidLongitudeView;
    private TextView mButtonEventView;
    private TextView mDoorStatusView;
    private TextView mWiperStatusView;
    private TextView mHeadlampStatusView;
    private TextView mWLEStatusView;
    private TextView mLeftDPadCommandView;
    private TextView mRightDPadCommandView;
    StringBuffer mBuffer;

    WindshieldWiperStatus.Listener mWiperListener =
            new WindshieldWiperStatus.Listener() {
        public void receive(Measurement measurement) {
            final WindshieldWiperStatus wiperStatus =
                (WindshieldWiperStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mWiperStatusView.setText("" +
                        wiperStatus.getValue().booleanValue());
                }
            });
        }
    };

    VehicleSpeed.Listener mSpeedListener = new VehicleSpeed.Listener() {
        public void receive(Measurement measurement) {
            final VehicleSpeed speed = (VehicleSpeed) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleSpeedView.setText(
                        "" + speed.getValue().doubleValue());
                }
            });
        }
    };

    FuelConsumed.Listener mFuelConsumedListener = new FuelConsumed.Listener() {
        public void receive(Measurement measurement) {
            final FuelConsumed fuel = (FuelConsumed) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mFuelConsumedView.setText(
                        "" + fuel.getValue().doubleValue());
                }
            });
        }
    };

    FuelLevel.Listener mFuelLevelListener = new FuelLevel.Listener() {
        public void receive(Measurement measurement) {
            final FuelLevel level = (FuelLevel) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mFuelLevelView.setText(
                        "" + level.getValue().doubleValue());
                }
            });
        }
    };

    Odometer.Listener mOdometerListener = new Odometer.Listener() {
        public void receive(Measurement measurement) {
            final Odometer odometer = (Odometer) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mOdometerView.setText(
                        "" + odometer.getValue().doubleValue());
                }
            });
        }
    };

    BrakePedalStatus.Listener mBrakePedalStatus =
            new BrakePedalStatus.Listener() {
        public void receive(Measurement measurement) {
            final BrakePedalStatus status = (BrakePedalStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleBrakeStatusView.setText(
                        "" + status.getValue().booleanValue());
                }
            });
        }
    };

    ParkingBrakeStatus.Listener mParkingBrakeStatus =
            new ParkingBrakeStatus.Listener() {
    	public void receive(Measurement measurement) {
	    final ParkingBrakeStatus status = (ParkingBrakeStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mParkingBrakeStatusView.setText(
                        "" + status.getValue().booleanValue());
                }
            });
        }
    };

    HeadlampStatus.Listener mHeadlampStatus = new HeadlampStatus.Listener() {
        public void receive(Measurement measurement) {
            final HeadlampStatus status = (HeadlampStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mHeadlampStatusView.setText(
                        "" + status.getValue().booleanValue());
                }
            });
        }
    };

    EngineSpeed.Listener mEngineSpeed = new EngineSpeed.Listener() {
        public void receive(Measurement measurement) {
            final EngineSpeed status = (EngineSpeed) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleEngineSpeedView.setText(
                        "" + status.getValue().doubleValue());
                }
            });
        }
    };

    TorqueAtTransmission.Listener mTorqueAtTransmission =
            new TorqueAtTransmission.Listener() {
        public void receive(Measurement measurement) {
            final TorqueAtTransmission status = (TorqueAtTransmission) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mTorqueAtTransmissionView.setText(
                        "" + status.getValue().doubleValue());
                }
            });
        }
    };

    AcceleratorPedalPosition.Listener mAcceleratorPedalPosition =
            new AcceleratorPedalPosition.Listener() {
        public void receive(Measurement measurement) {
            final AcceleratorPedalPosition status =
                (AcceleratorPedalPosition) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mAcceleratorPedalPositionView.setText(
                        "" + status.getValue().doubleValue());
                }
            });
        }
    };


    TransmissionGearPosition.Listener mTransmissionGearPos =
            new TransmissionGearPosition.Listener() {
        public void receive(Measurement measurement) {
            final TransmissionGearPosition status =
                    (TransmissionGearPosition) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mTransmissionGearPosView.setText(
                        "" + status.getValue().enumValue());
                }
            });
        }
    };

    IgnitionStatus.Listener mIgnitionStatus =
            new IgnitionStatus.Listener() {
        public void receive(Measurement measurement) {
            final IgnitionStatus status = (IgnitionStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mIgnitionStatusView.setText(
                        "" + status.getValue().enumValue());
                }
            });
        }
    };

    VehicleButtonEvent.Listener mButtonEvent =
            new VehicleButtonEvent.Listener() {
        public void receive(Measurement measurement) {
            final VehicleButtonEvent event = (VehicleButtonEvent) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mButtonEventView.setText(
                        event.getValue().enumValue() + " is " +
                        event.getEvent().enumValue());
                }
            });
        }
    };

    VehicleDoorStatus.Listener mDoorStatus =
            new VehicleDoorStatus.Listener() {
        public void receive(Measurement measurement) {
            final VehicleDoorStatus event = (VehicleDoorStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mDoorStatusView.setText(
                        event.getValue().enumValue() + " is ajar: " +
                        event.getEvent().booleanValue());
                }
            });
        }
    };

    Latitude.Listener mLatitude =
            new Latitude.Listener() {
        public void receive(Measurement measurement) {
            final Latitude lat = (Latitude) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mLatitudeView.setText(
                        "" + lat.getValue().doubleValue());
                }
            });
        }
    };

    Longitude.Listener mLongitude =
            new Longitude.Listener() {
        public void receive(Measurement measurement) {
            final Longitude lng = (Longitude) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mLongitudeView.setText(
                        "" + lng.getValue().doubleValue());
                }
            });
        }
    };

    LocationListener mAndroidLocationListener = new LocationListener() {
        public void onLocationChanged(final Location location) {
            mHandler.post(new Runnable() {
                public void run() {
                    mAndroidLatitudeView.setText("" +
                        location.getLatitude());
                    mAndroidLongitudeView.setText("" +
                        location.getLongitude());
                }
            });
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    SteeringWheelAngle.Listener mSteeringWheelListener =
            new SteeringWheelAngle.Listener() {
        public void receive(Measurement measurement) {
            final SteeringWheelAngle angle = (SteeringWheelAngle) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mSteeringWheelAngleView.setText(
                        "" + angle.getValue().doubleValue());
                }
            });
        }
    };
    
    WLEStatus.Listener mWLEStatusListener = 
    		new WLEStatus.Listener() {
    	public void receive(Measurement measurement) {
    		final WLEStatus wle = (WLEStatus) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mWLEStatusView.setText("" + wle.getValue().doubleValue());
    			}
    		});
    	}
    };

    LeftDPadCommand.Listener mLeftDPadListener = 
    		new LeftDPadCommand.Listener() {
    	public void receive(Measurement measurement) {
    		final LeftDPadCommand command = (LeftDPadCommand) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mLeftDPadCommandView.setText("" + command.getValue().enumValue());
    			}
    		});
    	}
    };
    RightDPadCommand.Listener mRightDPadListener = 
    		new RightDPadCommand.Listener() {
    	public void receive(Measurement measurement) {
    		final RightDPadCommand command = (RightDPadCommand) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mRightDPadCommandView.setText("" + command.getValue().enumValue());
    			}
    		});
    	}
    };
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();

            try {
            	mVehicleManager.addListener(WLEStatus.class, mWLEStatusListener);
            	mVehicleManager.addListener(RightDPadCommand.class, mRightDPadListener);
            	mVehicleManager.addListener(LeftDPadCommand.class, mLeftDPadListener);
                mVehicleManager.addListener(SteeringWheelAngle.class,
                        mSteeringWheelListener);
                mVehicleManager.addListener(VehicleSpeed.class,
                        mSpeedListener);
                mVehicleManager.addListener(FuelConsumed.class,
                        mFuelConsumedListener);
                mVehicleManager.addListener(FuelLevel.class,
                        mFuelLevelListener);
                mVehicleManager.addListener(Odometer.class,
                        mOdometerListener);
                mVehicleManager.addListener(WindshieldWiperStatus.class,
                        mWiperListener);
                mVehicleManager.addListener(BrakePedalStatus.class,
                        mBrakePedalStatus);
                mVehicleManager.addListener(ParkingBrakeStatus.class,
                        mParkingBrakeStatus);
                mVehicleManager.addListener(HeadlampStatus.class,
                        mHeadlampStatus);
                mVehicleManager.addListener(EngineSpeed.class,
                        mEngineSpeed);
                mVehicleManager.addListener(TorqueAtTransmission.class,
                        mTorqueAtTransmission);
                mVehicleManager.addListener(AcceleratorPedalPosition.class,
                        mAcceleratorPedalPosition);
                mVehicleManager.addListener(TransmissionGearPosition.class,
                        mTransmissionGearPos);
                mVehicleManager.addListener(IgnitionStatus.class,
                        mIgnitionStatus);
                mVehicleManager.addListener(Latitude.class,
                        mLatitude);
                mVehicleManager.addListener(Longitude.class,
                        mLongitude);
                mVehicleManager.addListener(VehicleButtonEvent.class,
                        mButtonEvent);
                mVehicleManager.addListener(VehicleDoorStatus.class,
                        mDoorStatus);
            } catch(VehicleServiceException e) {
                Log.w(TAG, "Couldn't add listeners for measurements", e);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Couldn't add listeners for measurements", e);
            }
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(TAG, "Vehicle dashboard created");

        mRightDPadCommandView = (TextView) findViewById(
        		R.id.right_dpad_command);
        mLeftDPadCommandView = (TextView) findViewById(
        		R.id.left_dpad_command);
        mWLEStatusView = (TextView) findViewById(
        		R.id.wle_status);
        mSteeringWheelAngleView = (TextView) findViewById(
                R.id.steering_wheel_angle);
        mVehicleSpeedView = (TextView) findViewById(
                R.id.vehicle_speed);
        mFuelConsumedView = (TextView) findViewById(
                R.id.fuel_consumed);
        mFuelLevelView = (TextView) findViewById(
                R.id.fuel_level);
        mOdometerView = (TextView) findViewById(
                R.id.odometer);
        mWiperStatusView = (TextView) findViewById(
                R.id.wiper_status);
        mVehicleBrakeStatusView = (TextView) findViewById(
                R.id.brake_pedal_status);
        mParkingBrakeStatusView = (TextView) findViewById(
                R.id.parking_brake_status);
        mHeadlampStatusView = (TextView) findViewById(
                R.id.headlamp_status);
        mVehicleEngineSpeedView = (TextView) findViewById(
                R.id.engine_speed);
        mTorqueAtTransmissionView = (TextView) findViewById(
                R.id.torque_at_transmission);
        mAcceleratorPedalPositionView = (TextView) findViewById(
                R.id.accelerator_pedal_position);
        mTransmissionGearPosView = (TextView) findViewById(
                R.id.transmission_gear_pos);
        mIgnitionStatusView = (TextView) findViewById(
                R.id.ignition);
        mLatitudeView = (TextView) findViewById(
                R.id.latitude);
        mLongitudeView = (TextView) findViewById(
                R.id.longitude);
        mAndroidLatitudeView = (TextView) findViewById(
                R.id.android_latitude);
        mAndroidLongitudeView = (TextView) findViewById(
                R.id.android_longitude);
        mButtonEventView = (TextView) findViewById(
                R.id.button_event);
        mDoorStatusView = (TextView) findViewById(
                R.id.door_status);
        mBuffer = new StringBuffer();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(new Intent(this, VehicleManager.class),
                mConnection, Context.BIND_AUTO_CREATE);

        LocationManager locationManager = (LocationManager)
            getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(
                    VehicleManager.VEHICLE_LOCATION_PROVIDER, 0, 0,
                    mAndroidLocationListener);
        } catch(IllegalArgumentException e) {
            Log.w(TAG, "Vehicle location provider is unavailable");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mIsBound) {
            Log.i(TAG, "Unbinding from vehicle service");
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	CruiseControl cmd;
    	VolumeSet vol_cmd;
    	VolumeSetPoint vol2_cmd;
    	

        switch (item.getItemId()) {
        case R.id.vol2_up:
        	vol_cmd = new VolumeSet(10);
            try {
                mVehicleManager.send(vol_cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
       	case R.id.vol2_down:
       		vol_cmd = new VolumeSet(-10);
            try {
                mVehicleManager.send(vol_cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.vol_mid:
        	vol2_cmd = new VolumeSetPoint(20);
            try {
                mVehicleManager.send(vol2_cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
       	case R.id.vol_mute:
       		vol2_cmd = new VolumeSetPoint(0);
            try {
                mVehicleManager.send(vol2_cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.beep:
        	BeepRequest beep_cmd = new BeepRequest(true);
            try {
                mVehicleManager.send(beep_cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_on:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.ON);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_off:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.OFF);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_set:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.SET);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_resume:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.RESUME);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_cancel:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.CANCEL);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_inc:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.INCREMENT);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.cruise_dec:
        	cmd = new CruiseControl(CruiseControl.CruiseCommands.DECREMENT);
            try {
                mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send cruise command", e);
            }
            return true; 
        case R.id.ac_on:
        	AirConditioning ac = new AirConditioning(AirConditioning.ACCommands.ON);
        	try {
        		mVehicleManager.send(ac);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send AC command", e);
            }
            return true; 
        case R.id.max_ac:
        	ClimateMode cm = new ClimateMode(ClimateMode.ClimateControls.MAX_AC);
        	try {
        		mVehicleManager.send(cm);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send AC MAX command", e);
            }
            return true; 
        case R.id.recirc:
        	FreshAirVent fav = new FreshAirVent(FreshAirVent.VentModes.RECIRCULATING);
        	try {
        		mVehicleManager.send(fav);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send recirculating command", e);
            }
            return true; 
        case R.id.temp_lo:
        	// 21.1C
        	ClimateTemperature ct = new ClimateTemperature(21.1);
        	try {
        		mVehicleManager.send(ct);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send temperature command", e);
            }
            return true; 
        case R.id.temp_hi:
        	// 26.6C
        	ClimateTemperature cth = new ClimateTemperature(26.6);
        	try {
        		mVehicleManager.send(cth);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send temperature command", e);
            }
            return true; 
        case R.id.defrost:
        	RearDefrost rd = new RearDefrost(RearDefrost.DefrostCommand.ON); 
        	try {
        		mVehicleManager.send(rd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send defrost command", e);
            }
            return true; 
        case R.id.door_lock:
        	DoorLocks dll = new DoorLocks(DoorLocks.LockCommands.LOCK_ALL);
        	try {
        		mVehicleManager.send(dll);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send lock command", e);
            }
            return true; 
        case R.id.door_unlock:
        	DoorLocks dlu = new DoorLocks(DoorLocks.LockCommands.UNLOCK_ALL);
        	try {
        		mVehicleManager.send(dlu);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send unlock command", e);
            }
            return true; 
        case R.id.desired_soc:
        	// 25%
        	DesiredSoc dsoc = new DesiredSoc(25);
        	try {
        		mVehicleManager.send(dsoc);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send SOC command", e);
            }
            return true; 
        case R.id.engine_speed:
        	// 2k RPM
        	DesiredEngineSpeed des = new DesiredEngineSpeed(2000);
        	try {
        		mVehicleManager.send(des);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send engine speed command", e);
            }
            return true; 
        case R.id.engine_mode_ev:
        	// EV Mode
        	EngineMode evm = new EngineMode(EngineMode.EngineModes.EV_DRIVING);
        	try {
        		mVehicleManager.send(evm);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send ev mode command", e);
            }
            return true; 
        case R.id.engine_mode_on:
        	// force engine on sustain batt
        	EngineMode evmo = new EngineMode(EngineMode.EngineModes.CHARGE_SUSTAINING_ENGINE_ON);
        	try {
        		mVehicleManager.send(evmo);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send ev mode command", e);
            }
            return true; 
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}
