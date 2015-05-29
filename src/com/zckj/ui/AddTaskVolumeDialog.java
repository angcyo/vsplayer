package com.zckj.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.zckj.data.AsyncRun;
import com.zckj.data.OnTaskAdd;
import com.zckj.data.Util;
import com.zckj.data.VolumeHelper;
import com.zckj.data.VolumeTaskRecord;
import com.linux.vshow.R;

import org.joda.time.LocalDateTime;

/**
 * Created by angcyo on 2015-03-24 024.
 */
public class AddTaskVolumeDialog extends BaseTaskDialog implements
		View.OnClickListener {

	VolumeTaskRecord taskRecord;// 如果不为空,说明是修改弹出来的对话框

	public AddTaskVolumeDialog(Context context) {
		super(context);
	}

	public AddTaskVolumeDialog(Context context, int theme) {
		super(context, theme);
	}

	public AddTaskVolumeDialog(Context context, int theme, OnTaskAdd listener) {
		super(context, theme);
		this.taskAddListener = listener;
	}

	public AddTaskVolumeDialog(Context context, int theme,
			VolumeTaskRecord task, OnTaskAdd listener) {
		super(context, theme);
		this.taskAddListener = listener;
		this.taskRecord = task;
	}

	protected AddTaskVolumeDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		initDialogLayout();
	}

	private void initDialogLayout() {
		btHourAdd = (Button) findViewById(R.id.id_bt_volume_hour_add);
		btHourMinus = (Button) findViewById(R.id.id_bt_volume_hour_minus);

		btMinuteAdd = (Button) findViewById(R.id.id_bt_volume_minute_add);
		btMinuteMinus = (Button) findViewById(R.id.id_bt_volume_minute_minus);

		btVolumeAdd = (Button) findViewById(R.id.id_bt_volume_value_add);
		btVolumeMinus = (Button) findViewById(R.id.id_bt_volume_value_minus);

		btOkAndCon = (Button) findViewById(R.id.id_bt_volume_ok_and_con);
		btOk = (Button) findViewById(R.id.id_bt_volume_ok);
		btCancel = (Button) findViewById(R.id.id_bt_volume_cancel);

		timePicker = (TimePicker) findViewById(R.id.id_task_volume_timepicker);
		discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.id_task_volume_value_seekbar);

		cb1 = (CheckBox) findViewById(R.id.id_cb_volume_period1);
		cb2 = (CheckBox) findViewById(R.id.id_cb_volume_period2);
		cb3 = (CheckBox) findViewById(R.id.id_cb_volume_period3);
		cb4 = (CheckBox) findViewById(R.id.id_cb_volume_period4);
		cb5 = (CheckBox) findViewById(R.id.id_cb_volume_period5);
		cb6 = (CheckBox) findViewById(R.id.id_cb_volume_period6);
		cb7 = (CheckBox) findViewById(R.id.id_cb_volume_period7);

		btHourAdd.setOnClickListener(this);
		btHourMinus.setOnClickListener(this);

		btMinuteAdd.setOnClickListener(this);
		btMinuteMinus.setOnClickListener(this);

		btVolumeAdd.setOnClickListener(this);
		btVolumeMinus.setOnClickListener(this);

		btOkAndCon.setOnClickListener(this);
		btOk.setOnClickListener(this);
		btCancel.setOnClickListener(this);

		initDialogLayoutData();
	}

	private void initDialogLayoutData() {
		if (taskRecord != null) {// 如果是修改...弹出来的对话框.
		// btOkAndCon.setVisibility(View.INVISIBLE);//不可见
			if (taskRecord.state == AddTaskVolumeDialog.STATE_DISABLE) {
				btOkAndCon.setText("开启任务");
			} else {
				btOkAndCon.setText("禁用任务");
			}

			btOk.setText("保存修改");
			btCancel.setText("放弃修改");

			timePicker.setH(taskRecord.hour);
			timePicker.setM(taskRecord.minute);
			discreteSeekBar.setProgress(taskRecord.volume);

			int period = taskRecord.period;
			if ((period & AddTaskVolumeDialog.WEEK1) == AddTaskVolumeDialog.WEEK1) {
				cb1.setChecked(true);
			}
			if ((period & AddTaskVolumeDialog.WEEK2) == AddTaskVolumeDialog.WEEK2) {
				cb2.setChecked(true);
			}
			if ((period & AddTaskVolumeDialog.WEEK3) == AddTaskVolumeDialog.WEEK3) {
				cb3.setChecked(true);
			}
			if ((period & AddTaskVolumeDialog.WEEK4) == AddTaskVolumeDialog.WEEK4) {
				cb4.setChecked(true);
			}
			if ((period & AddTaskVolumeDialog.WEEK5) == AddTaskVolumeDialog.WEEK5) {
				cb5.setChecked(true);
			}
			if ((period & AddTaskVolumeDialog.WEEK6) == AddTaskVolumeDialog.WEEK6) {
				cb6.setChecked(true);
			}
			if ((period & AddTaskVolumeDialog.WEEK7) == AddTaskVolumeDialog.WEEK7) {
				cb7.setChecked(true);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.id_bt_volume_hour_add) {
			timePicker.setH(timePicker.getH() + 1);
		} else if (id == R.id.id_bt_volume_hour_minus) {
			timePicker.setH(timePicker.getH() - 1);

		} else if (id == R.id.id_bt_volume_minute_add) {
			timePicker.setM(timePicker.getM() + 1);

		} else if (id == R.id.id_bt_volume_minute_minus) {
			timePicker.setM(timePicker.getM() - 1);

		} else if (id == R.id.id_bt_volume_value_add) {
			discreteSeekBar.setProgress(discreteSeekBar.getProgress() + 1);
			showFloater();

		} else if (id == R.id.id_bt_volume_value_minus) {
			discreteSeekBar.setProgress(discreteSeekBar.getProgress() - 1);
			showFloater();

		} else if (id == R.id.id_bt_volume_ok_and_con

		) {
			if (taskRecord == null) {
				asyncSaveTask(false);
			} else {// 如果是修改
				taskRecord.hour = getHour();
				taskRecord.minute = getMinute();
				taskRecord.volume = getVolumeValue();
				taskRecord.period = getPeriod();
				if (taskRecord.state == STATE_ENABLED) {
					taskRecord.state = STATE_DISABLE;
					Util.showPostMsg("任务已禁用");
					((Button) v).setText("开启任务");
				} else {
					taskRecord.state = STATE_ENABLED;
					Util.showPostMsg("任务已启动");
					((Button) v).setText("禁用任务");
				}
				taskRecord.remark = new LocalDateTime()
						.toString("yyyy-MM-dd HH:mm:ss");
				taskRecord.save();
				if (taskAddListener != null) {
					taskAddListener.onTaskAdd();
				}
				
				this.cancel();
			}

		} else if (id == R.id.id_bt_volume_ok) {
			if (taskRecord == null) {
				asyncSaveTask(true);
			} else {// 如果是修改
				taskRecord.hour = getHour();
				taskRecord.minute = getMinute();
				taskRecord.volume = getVolumeValue();
				taskRecord.period = getPeriod();
				taskRecord.state = STATE_ENABLED;
				taskRecord.remark = new LocalDateTime()
						.toString("yyyy-MM-dd HH:mm:ss");
				taskRecord.save();

				if (taskAddListener != null) {
					taskAddListener.onTaskAdd();
				}
				this.cancel();
				Util.showPostMsg("执行完成");
			}
		} else if (id == R.id.id_bt_volume_cancel) {
			this.cancel();

		}
	}

	void asyncSaveTask(final boolean cancel) {
		new AsyncRun() {
			@Override
			public void doBack() {
				VolumeHelper.save(getHour(), getMinute(), getVolumeValue(),
						getPeriod(), STATE_ENABLED,
						new LocalDateTime().toString("yyyy-MM-dd HH:mm:ss"));
			}

			@Override
			public void doPost() {
				if (taskAddListener != null) {
					taskAddListener.onTaskAdd();
				}
				if (cancel) {
					AddTaskVolumeDialog.this.cancel();
				}
				Util.showPostMsg("执行完成");
			}
		}.execute();
	}

	boolean isShowFloater = false;

	void showFloater() {
		if (discreteSeekBar != null && !isShowFloater) {
			discreteSeekBar.showFloater();
			isShowFloater = true;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					hideFloater();
				}
			}, 1500);// 1.5秒之后隐藏浮子
		}
	}

	void hideFloater() {
		if (discreteSeekBar != null && isShowFloater) {
			discreteSeekBar.hideFloater();
			isShowFloater = false;
		}
	}
}
// 修改于:2015年5月15日,星期五
