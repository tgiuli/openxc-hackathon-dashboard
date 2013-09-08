package com.openxc.examples.hackathondashboard;

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
import android.view.View;

import com.openxc.VehicleManager;
import com.openxc.examples2.R;
import com.openxc.measurements.*;
import com.openxc.remote.VehicleServiceException;

public class VehicleDashboardActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    
    // Standard signals
    private TextView mAcceleratorPedalPositionView;
    private TextView mVehicleBrakeStatusView;
    private TextView mDoorStatusView;
    private TextView mVehicleEngineSpeedView;
    private TextView mFuelConsumedView;
    private TextView mFuelLevelView;
    private TextView mGearLeverPositionView;
    private TextView mHeadlampStatusView;
    private TextView mIgnitionStatusView;
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private TextView mOdometerView;
    private TextView mParkingBrakeStatusView;
    private TextView mSteeringWheelAngleView;
    private TextView mTorqueAtTransmissionView;
    private TextView mTransmissionGearPosView;
    private TextView mVehicleSpeedView;
    private TextView mWiperStatusView;
    // Extended - Fusion
    // Extended - CMAX
    private TextView mACCompressorPowerView;
    // Not used
    private TextView mWLEStatusView;
    private TextView mLeftDPadCommandView;
    private TextView mRightDPadCommandView;
    private TextView mButtonEventView;
    // Internal
    private TextView mAndroidLatitudeView;
    private TextView mAndroidLongitudeView;

    StringBuffer mBuffer;

    
    //////////////////////////
    // Listener definitions //
    //////////////////////////
    AcceleratorPedalPosition.Listener mAcceleratorPedalPosition = new AcceleratorPedalPosition.Listener() {
        public void receive(Measurement measurement) {
            final AcceleratorPedalPosition status = (AcceleratorPedalPosition) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mAcceleratorPedalPositionView.setText("" + status.getValue().doubleValue());
                }
            });
        }
    };


    BrakePedalStatus.Listener mBrakePedalStatus = new BrakePedalStatus.Listener() {
        public void receive(Measurement measurement) {
            final BrakePedalStatus status = (BrakePedalStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleBrakeStatusView.setText("" + status.getValue().booleanValue());
                }
            });
        }
    };
    
    VehicleDoorStatus.Listener mDoorStatus = new VehicleDoorStatus.Listener() {
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
    
    EngineSpeed.Listener mEngineSpeed = new EngineSpeed.Listener() {
        public void receive(Measurement measurement) {
            final EngineSpeed status = (EngineSpeed) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleEngineSpeedView.setText("" + status.getValue().doubleValue());
                }
            });
        }
    };

    FuelConsumed.Listener mFuelConsumedListener = new FuelConsumed.Listener() {
        public void receive(Measurement measurement) {
            final FuelConsumed fuel = (FuelConsumed) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mFuelConsumedView.setText("" + fuel.getValue().doubleValue());
                }
            });
        }
    };

    FuelLevel.Listener mFuelLevelListener = new FuelLevel.Listener() {
        public void receive(Measurement measurement) {
            final FuelLevel level = (FuelLevel) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mFuelLevelView.setText("" + level.getValue().doubleValue());
                }
            });
        }
    };


    GearLeverPosition.Listener mGearLeverPositionListener = new GearLeverPosition.Listener() {
    	public void receive(Measurement measurement) {
    		final GearLeverPosition status = (GearLeverPosition) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mGearLeverPositionView.setText("" + status.getValue().enumValue());
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
    

    IgnitionStatus.Listener mIgnitionStatus = new IgnitionStatus.Listener() {
        public void receive(Measurement measurement) {
            final IgnitionStatus status = (IgnitionStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mIgnitionStatusView.setText("" + status.getValue().enumValue());
                }
            });
        }
    };

    Latitude.Listener mLatitude = new Latitude.Listener() {
        public void receive(Measurement measurement) {
            final Latitude lat = (Latitude) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mLatitudeView.setText("" + lat.getValue().doubleValue());
                }
            });
        }
    };


    Longitude.Listener mLongitude = new Longitude.Listener() {
        public void receive(Measurement measurement) {
            final Longitude lng = (Longitude) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mLongitudeView.setText("" + lng.getValue().doubleValue());
                }
            });
        }
    };
    
    Odometer.Listener mOdometerListener = new Odometer.Listener() {
        public void receive(Measurement measurement) {
            final Odometer odometer = (Odometer) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mOdometerView.setText("" + odometer.getValue().doubleValue());
                }
            });
        }
    };
    
    // PARKING BRAKE ?????
    ParkingBrakeStatus.Listener mParkingBrakeStatusListener = new ParkingBrakeStatus.Listener() {
    	public void receive(Measurement measurement) {
    		final ParkingBrakeStatus status = (ParkingBrakeStatus) measurement;
    		mHandler.post(new Runnable() {
				public void run() {
					mParkingBrakeStatusView.setText("" + status.getValue().booleanValue());
				}
			});
    	}
    };
 
    SteeringWheelAngle.Listener mSteeringWheelListener = new SteeringWheelAngle.Listener() {
        public void receive(Measurement measurement) {
            final SteeringWheelAngle angle = (SteeringWheelAngle) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mSteeringWheelAngleView.setText("" + angle.getValue().doubleValue());
                }
            });
        }
    };
    

    TorqueAtTransmission.Listener mTorqueAtTransmission = new TorqueAtTransmission.Listener() {
        public void receive(Measurement measurement) {
            final TorqueAtTransmission status = (TorqueAtTransmission) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mTorqueAtTransmissionView.setText("" + status.getValue().doubleValue());
                }
            });
        }
    };


    TransmissionGearPosition.Listener mTransmissionGearPos = new TransmissionGearPosition.Listener() {
        public void receive(Measurement measurement) {
            final TransmissionGearPosition status = (TransmissionGearPosition) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mTransmissionGearPosView.setText(
                        "" + status.getValue().enumValue());
                }
            });
        }
    };
    

    VehicleSpeed.Listener mSpeedListener = new VehicleSpeed.Listener() {
        public void receive(Measurement measurement) {
            final VehicleSpeed speed = (VehicleSpeed) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleSpeedView.setText("" + speed.getValue().doubleValue());
                }
            });
        }
    };

    WindshieldWiperStatus.Listener mWiperListener = new WindshieldWiperStatus.Listener() {
        public void receive(Measurement measurement) {
            final WindshieldWiperStatus wiperStatus = (WindshieldWiperStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mWiperStatusView.setText("" + wiperStatus.getValue().booleanValue());
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


    // Extended - Fusion

    // Extended - CMAX
    ACCompressorPower.Listener mACCompressorPowerListener = new ACCompressorPower.Listener() {
    	public void receive(Measurement measurement) 
    	{
    		final ACCompressorPower power = (ACCompressorPower) measurement;
    		mHandler.post(new Runnable() {
    			public void run() { mACCompressorPowerView.setText("" + power.getValue().doubleValue()); }
    		});
    	}
    };

    // Not used

    
    WLEStatus.Listener mWLEStatusListener = new WLEStatus.Listener() {
    	public void receive(Measurement measurement) {
    		final WLEStatus wle = (WLEStatus) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mWLEStatusView.setText("" + wle.getValue().doubleValue());
    			}
    		});
    	}
    };


    LeftDPadCommand.Listener mLeftDPadListener = new LeftDPadCommand.Listener() {
    	public void receive(Measurement measurement) {
    		final LeftDPadCommand command = (LeftDPadCommand) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mLeftDPadCommandView.setText("" + command.getValue().enumValue());
    			}
    		});
    	}
    };
    

    RightDPadCommand.Listener mRightDPadListener = new RightDPadCommand.Listener() {
    	public void receive(Measurement measurement) {
    		final RightDPadCommand command = (RightDPadCommand) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mRightDPadCommandView.setText("" + command.getValue().enumValue());
    			}
    		});
    	}
    };
    

    VehicleButtonEvent.Listener mButtonEvent = new VehicleButtonEvent.Listener() {
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
    
    // Internal
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

    
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();

            try {
            	// Standard signals
                mVehicleManager.addListener(AcceleratorPedalPosition.class,	mAcceleratorPedalPosition);
                mVehicleManager.addListener(BrakePedalStatus.class,			mBrakePedalStatus);
                mVehicleManager.addListener(VehicleDoorStatus.class,		mDoorStatus);
                mVehicleManager.addListener(EngineSpeed.class,				mEngineSpeed);
                mVehicleManager.addListener(FuelConsumed.class,				mFuelConsumedListener);
                mVehicleManager.addListener(FuelLevel.class,				mFuelLevelListener);
                mVehicleManager.addListener(GearLeverPosition.class,		mGearLeverPositionListener);
                mVehicleManager.addListener(HeadlampStatus.class,			mHeadlampStatus);
                mVehicleManager.addListener(IgnitionStatus.class,			mIgnitionStatus);
                mVehicleManager.addListener(Latitude.class,					mLatitude);
                mVehicleManager.addListener(Longitude.class,				mLongitude);
                mVehicleManager.addListener(Odometer.class,					mOdometerListener);
                mVehicleManager.addListener(ParkingBrakeStatus.class,		mParkingBrakeStatus);
                mVehicleManager.addListener(SteeringWheelAngle.class,		mSteeringWheelListener);
                mVehicleManager.addListener(TorqueAtTransmission.class,		mTorqueAtTransmission);
                mVehicleManager.addListener(TransmissionGearPosition.class,	mTransmissionGearPos);
                mVehicleManager.addListener(VehicleSpeed.class,				mSpeedListener);
                mVehicleManager.addListener(WindshieldWiperStatus.class,	mWiperListener);

                // Extended signals - Fusion
                
                // Extended signals - CMAX
                mVehicleManager.addListener(ACCompressorPower.class,		mACCompressorPowerListener);
                
                // Non-used signals
                mVehicleManager.addListener(WLEStatus.class, 				mWLEStatusListener);
            	mVehicleManager.addListener(LeftDPadCommand.class, 			mLeftDPadListener);
                mVehicleManager.addListener(RightDPadCommand.class,			mRightDPadListener);
                mVehicleManager.addListener(VehicleButtonEvent.class,		mButtonEvent);

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

        // Standard Signals
        mAcceleratorPedalPositionView	= (TextView) findViewById(R.id.accelerator_pedal_position);
        mVehicleBrakeStatusView			= (TextView) findViewById(R.id.brake_pedal_status);
        mDoorStatusView					= (TextView) findViewById(R.id.door_status);
        mVehicleEngineSpeedView			= (TextView) findViewById(R.id.engine_speed);
        mFuelConsumedView				= (TextView) findViewById(R.id.fuel_consumed);
        mFuelLevelView					= (TextView) findViewById(R.id.fuel_level);
        mGearLeverPositionView			= (TextView) findViewById(R.id.gear_lever_position);
        mHeadlampStatusView				= (TextView) findViewById(R.id.headlamp_status);
        mIgnitionStatusView				= (TextView) findViewById(R.id.ignition);
        mLatitudeView					= (TextView) findViewById(R.id.latitude);
        mLongitudeView					= (TextView) findViewById(R.id.longitude);
        mOdometerView					= (TextView) findViewById(R.id.odometer);
        mParkingBrakeStatusView			= (TextView) findViewById(R.id.parking_brake_status);
        mSteeringWheelAngleView			= (TextView) findViewById(R.id.steering_wheel_angle);
        mTorqueAtTransmissionView		= (TextView) findViewById(R.id.torque_at_transmission);
        mTransmissionGearPosView		= (TextView) findViewById(R.id.transmission_gear_pos);
        mVehicleSpeedView				= (TextView) findViewById(R.id.vehicle_speed);
        mWiperStatusView				= (TextView) findViewById(R.id.wiper_status);
        
        // Extended Signals - Fusion
        
        // Extended Signals - CMAX
        mACCompressorPowerView			= (TextView) findViewById(R.id.ac_compressor_power);
        
        // Not use Signals
        mWLEStatusView					= (TextView) findViewById(R.id.wle_status);
        mLeftDPadCommandView			= (TextView) findViewById(R.id.left_dpad_command);
        mRightDPadCommandView			= (TextView) findViewById(R.id.right_dpad_command);
        mButtonEventView				= (TextView) findViewById(R.id.button_event);
        
        // Internal Signals
        mAndroidLatitudeView			= (TextView) findViewById(R.id.android_latitude);
        mAndroidLongitudeView			= (TextView) findViewById(R.id.android_longitude);
        
        // Other variables
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
    	BaseMeasurement<?> cmd;
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
        	cmd = new ClimateMode(ClimateMode.ClimateControls.AUTO);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send AC AUTO command", e);
            }
            return true; 
        case R.id.max_ac:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.MAX_AC);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send AC MAX command", e);
            }
            return true; 
        case R.id.recirc:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.RECIRCULATION);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send AC recirculation command", e);
            }
            return true; 
        case R.id.rear_defrost:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.REAR_DEFROST);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send rear defrost command", e);
            }
            return true; 
        case R.id.front_defrost:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.FRONT_DEFROST);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send front defrost command", e);
            }
            return true; 
        case R.id.max_defrost:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.MAX_DEFROST);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send max defrost command", e);
            }
            return true; 
        case R.id.fan_dec:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.FAN_SPEED_DECREMENT);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send fan speed decrement command", e);
            }
            return true; 
        case R.id.fan_inc:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.FAN_SPEED_INCREMENT);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send fan speed increment command", e);
            }
            return true; 
        case R.id.panel_vent:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.PANEL_VENT);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send panel vent command", e);
            }
            return true; 
        case R.id.panel_floor:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.PANEL_FLOOR);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send panel floor command", e);
            }
            return true; 
        case R.id.floor:
        	cmd = new ClimateMode(ClimateMode.ClimateControls.FLOOR);
        	try {
        		mVehicleManager.send(cmd);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Unable to send floor command", e);
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
            
        case R.id.set_phevblue:
        	// Only show items that are available in PHEV BLUE
        	((TextView)findViewById(R.id.header_phevblue_label)).setVisibility(View.VISIBLE);
        	((TextView)findViewById(R.id.header_phevred_label)).setVisibility(View.GONE);
        	((TextView)findViewById(R.id.header_mediacmax_label)).setVisibility(View.GONE);
        	setVisibilityForPhevBlue();
        	return true;
        	
        case R.id.set_phevred:
        	// Only show items that are available in PHEV RED
        	((TextView)findViewById(R.id.header_phevblue_label)).setVisibility(View.GONE);
        	((TextView)findViewById(R.id.header_phevred_label)).setVisibility(View.VISIBLE);
        	((TextView)findViewById(R.id.header_mediacmax_label)).setVisibility(View.GONE);
        	setVisibilityForPhevRed();
        	return true;
        	
        case R.id.set_phevmedia:
        	// Only show items that are available in PHEV MEDIA
        	((TextView)findViewById(R.id.header_phevblue_label)).setVisibility(View.GONE);
        	((TextView)findViewById(R.id.header_phevred_label)).setVisibility(View.GONE);
        	((TextView)findViewById(R.id.header_mediacmax_label)).setVisibility(View.VISIBLE);
        	setVisibilityForPhevMedia();
        	return true;

        case R.id.set_showall:
        	// Show all items!
        	((TextView)findViewById(R.id.header_phevblue_label)).setVisibility(View.GONE);
        	((TextView)findViewById(R.id.header_phevred_label)).setVisibility(View.GONE);
        	((TextView)findViewById(R.id.header_mediacmax_label)).setVisibility(View.GONE);
        	setVisibilityForAll();
        	return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    
    private void setVisibilityForAll() {
    	((TextView)findViewById(R.id.accelerator_pedal_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.accelerator_pedal_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.door_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.door_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_level)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_level_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.gear_lever_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.gear_lever_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.headlamp_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.headlamp_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ignition)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ignition_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.parking_brake_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.parking_brake_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.steering_wheel_angle)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.steering_wheel_angle_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.torque_at_transmission)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.torque_at_transmission_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.transmission_gear_pos)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.transmission_gear_pos_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.vehicle_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.vehicle_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ac_compressor_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ac_compressor_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.air_conditioning_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.air_conditioning_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_level)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_level_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_temperature)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_temperature_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_plug_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_plug_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.customer_soc)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.customer_soc_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_current)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_current_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_voltage)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_voltage_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.electric_range)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.electric_range_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_mode)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_mode_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_state_of_charge)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_state_of_charge_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.heater_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.heater_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hours_until_charged)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hours_until_charged_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_current)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_current_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_voltage)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_voltage_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.last_regen_event_score)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.last_regen_event_score_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.overall_state_of_charge)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.overall_state_of_charge_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_drive_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_drive_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_engine_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_engine_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_left)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_left_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_right)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_right_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wle_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wle_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.left_dpad_command)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.left_dpad_command_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.right_dpad_command)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.right_dpad_command_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.button_event)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.button_event_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.android_latitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.android_latitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.android_longitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.android_longitude_label)).setVisibility(View.VISIBLE);    
    }

    private void setVisibilityForPhevBlue() {
    	((TextView)findViewById(R.id.accelerator_pedal_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.accelerator_pedal_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.door_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.door_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_level)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_level_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.gear_lever_position)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.gear_lever_position_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.headlamp_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.headlamp_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ignition)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ignition_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.parking_brake_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.parking_brake_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.steering_wheel_angle)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.steering_wheel_angle_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.torque_at_transmission)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.torque_at_transmission_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.transmission_gear_pos)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.transmission_gear_pos_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.vehicle_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.vehicle_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ac_compressor_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ac_compressor_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.air_conditioning_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.air_conditioning_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_level)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_level_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_temperature)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_temperature_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_plug_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_plug_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.customer_soc)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.customer_soc_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_current)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_current_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_voltage)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_voltage_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.electric_range)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.electric_range_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.engine_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.engine_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_mode)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_mode_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_state_of_charge)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_state_of_charge_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.heater_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.heater_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hours_until_charged)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hours_until_charged_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_current)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_current_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_voltage)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_voltage_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.last_regen_event_score)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.last_regen_event_score_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.overall_state_of_charge)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.overall_state_of_charge_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_drive_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_drive_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_engine_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_engine_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_left)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_left_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_right)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_front_right_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wle_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.wle_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.left_dpad_command)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.left_dpad_command_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.right_dpad_command)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.right_dpad_command_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.button_event)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.button_event_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_latitude)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_latitude_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_longitude)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_longitude_label)).setVisibility(View.GONE);    
    }

    private void setVisibilityForPhevRed() {
    	((TextView)findViewById(R.id.accelerator_pedal_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.accelerator_pedal_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.brake_pedal_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.door_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.door_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.engine_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.fuel_consumed_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.fuel_level)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.fuel_level_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.gear_lever_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.gear_lever_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.headlamp_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.headlamp_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ignition)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ignition_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.latitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.odometer_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.parking_brake_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.parking_brake_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.steering_wheel_angle)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.steering_wheel_angle_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.torque_at_transmission)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.torque_at_transmission_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.transmission_gear_pos)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.transmission_gear_pos_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.vehicle_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.vehicle_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.wiper_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ac_compressor_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ac_compressor_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.air_conditioning_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.air_conditioning_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_level)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_level_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_temperature)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.battery_temperature_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_plug_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_plug_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.charging_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.customer_soc)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.customer_soc_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_current)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_current_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_voltage)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.dcdc_voltage_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.electric_range)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.electric_range_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_mode)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_mode_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_state_of_charge)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ev_state_of_charge_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.heater_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.heater_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hours_until_charged)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hours_until_charged_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_current)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_current_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_voltage)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hv_battery_voltage_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.last_regen_event_score)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.last_regen_event_score_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.overall_state_of_charge)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.overall_state_of_charge_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_drive_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_drive_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_engine_power)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.relative_engine_power_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.tire_pressure)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_left)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_left_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_right)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_right_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.wle_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.wle_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.left_dpad_command)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.left_dpad_command_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.right_dpad_command)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.right_dpad_command_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.button_event)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.button_event_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_latitude)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_latitude_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_longitude)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_longitude_label)).setVisibility(View.GONE);    
    }

    private void setVisibilityForPhevMedia() {
    	((TextView)findViewById(R.id.accelerator_pedal_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.accelerator_pedal_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.brake_pedal_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.door_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.door_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.engine_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_consumed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_level)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.fuel_level_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.gear_lever_position)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.gear_lever_position_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.headlamp_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.headlamp_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ignition)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ignition_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.latitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.longitude_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.odometer_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.parking_brake_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.parking_brake_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.steering_wheel_angle)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.steering_wheel_angle_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.torque_at_transmission)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.torque_at_transmission_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.transmission_gear_pos)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.transmission_gear_pos_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.vehicle_speed)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.vehicle_speed_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.wiper_status_label)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.ac_compressor_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ac_compressor_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.air_conditioning_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.air_conditioning_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_level)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_level_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_temperature)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.battery_temperature_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_plug_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_plug_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.charging_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.customer_soc)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.customer_soc_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_current)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_current_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_voltage)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.dcdc_voltage_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.electric_range)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.electric_range_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.engine_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.engine_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_mode)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_mode_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_state_of_charge)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.ev_state_of_charge_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.heater_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.heater_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hours_until_charged)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hours_until_charged_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_current)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_current_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_voltage)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hv_battery_voltage_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.hybrid_state_of_charge_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.last_regen_event_score)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.last_regen_event_score_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.overall_state_of_charge)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.overall_state_of_charge_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_drive_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_drive_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_engine_power)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.relative_engine_power_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_left)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_left_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_right)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_front_right_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_left_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_rear_right_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.tire_pressure_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.wle_status)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.wle_status_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.left_dpad_command)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.left_dpad_command_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.right_dpad_command)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.right_dpad_command_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.button_event)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.button_event_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_latitude)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_latitude_label)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_longitude)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.android_longitude_label)).setVisibility(View.GONE);    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}
