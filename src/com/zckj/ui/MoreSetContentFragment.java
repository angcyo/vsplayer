package com.zckj.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.zckj.data.ApkUtil;
import com.zckj.data.Logger;
import com.zckj.data.OnTaskAdd;
import com.zckj.data.Setting;
import com.zckj.data.ShutdownHelper;
import com.zckj.data.ShutdownTaskAdapter;
import com.zckj.data.ShutdownTaskRecord;
import com.zckj.data.VolumeHelper;
import com.zckj.data.VolumeTaskAdapter;
import com.zckj.data.VolumeTaskRecord;
import com.zckj.data.XmlSetting;
import com.linux.vshow.Constant;
import com.linux.vshow.R;
import com.linux.vshow.Tool;

import java.io.File;
import java.util.List;

public class MoreSetContentFragment extends BaseFragment implements OnTaskAdd,
		AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener {
	public static final int STYLE_MORE_RUN_BMP = 0x004001;
	public static final int STYLE_MORE_TIME_VOLUME = 0x004002;
	public static final int STYLE_MORE_TIME_COLSE = 0x004003;
	public static final int STYLE_MORE_APK_VER = 0x004004;
	public static final int STYLE_MORE_ADD_TASK = 0x004005;//废弃

	public static final int RESULT_RUN_BMP = 0x00002;

	public MoreSetContentFragment() {
		default_layout_style = STYLE_MORE_RUN_BMP;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		switch (style) {
			case STYLE_MORE_RUN_BMP:
				rootView = inflater.inflate(R.layout.layout_more_run_bmp, container, false);
				initRunBmpLayout();
				break;
			case STYLE_MORE_TIME_VOLUME:
				rootView = inflater.inflate(R.layout.layout_more_time_volume, container, false);
				initTimeVolumeLayout();
				break;
			case STYLE_MORE_TIME_COLSE:
				rootView = inflater.inflate(R.layout.layout_more_time_close, container, false);
				initTimeClose();
				break;
			case STYLE_MORE_APK_VER:
				rootView = inflater.inflate(R.layout.layout_more_apk_ver, container, false);
				initApkVerLayout();
				break;
			case STYLE_MORE_ADD_TASK:
				rootView = inflater.inflate(R.layout.layout_more_add_task, container, false);
				initAddTaskLayout();
				break;
			default:
				rootView = inflater.inflate(R.layout.layout_more_run_bmp, container, false);
				initRunBmpLayout();
				break;
		}
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_RUN_BMP) {
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				String[] proj = {MediaStore.Images.Media.DATA};
				ContentResolver cr = context.getContentResolver();
				Cursor cursor = cr.query(uri, proj, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				bmpRunBmpPath = cursor.getString(column_index);
				cursor.close();
				bmpRunBmp = BitmapFactory.decodeFile(bmpRunBmpPath);
				Logger.e("选择的图片路径:::" + bmpRunBmpPath);
				imgRunBmp.setImageBitmap(bmpRunBmp);

				btRunBmpOk.setEnabled(true);
			} else {
				Toast.makeText(context, "未成功,请重新选择图片!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	ImageView imgRunBmp;//展示已选择的图片
	Button btBrowseRunBmp;//浏览按钮
	Button btRunBmpOk;//确定按钮
	Bitmap bmpRunBmp;//保存位图
	String bmpRunBmpPath;//已设置的图片路径

	/**
	 * 初始化 启动画面 布局
	 */
	void initRunBmpLayout() {
		imgRunBmp = (ImageView) rootView.findViewById(R.id.id_more_img_run_bmp);
		btBrowseRunBmp = (Button) rootView.findViewById(R.id.id_more_browse_bmp);
		btRunBmpOk = (Button) rootView.findViewById(R.id.id_more_run_bmp_ok);
		btRunBmpOk.setEnabled(false);

		String filePath = Setting.getRunBmpPath(context);
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				bmpRunBmp = BitmapFactory.decodeFile(filePath);
				bmpRunBmpPath = filePath;
			}
		}

		if (bmpRunBmp != null) {
			imgRunBmp.setImageBitmap(bmpRunBmp);
		}

		//浏览图片
		btBrowseRunBmp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getImage();
			}
		});

		//保存图片路径
		btRunBmpOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Setting.setRunBmpPath(context, bmpRunBmpPath);
				XmlSetting.setXmlRunImage(bmpRunBmpPath);
				Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
				
				Tool.saveConfig(XmlSetting.XML_RUN_IMAGE + "!" + bmpRunBmpPath, Constant.advance);
			}
		});
	}

	void getImage() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, RESULT_RUN_BMP);
	}


	Button btAddTaskVolume;
	AddTaskVolumeDialog addVolumeDialog;
	ListView volumeTaskList;
	View emptyView;

	//初始化定时音量布局
	void initTimeVolumeLayout() {
		btAddTaskVolume = (Button) rootView.findViewById(R.id.id_more_add_task_volume);
		volumeTaskList = (ListView) rootView.findViewById(R.id.id_more_list_task_volume);
		emptyView = rootView.findViewById(R.id.id_more_layout_empty);

		btAddTaskVolume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (addVolumeDialog == null) {
					addVolumeDialog = new AddTaskVolumeDialog(context, R.style.CustomDialog, MoreSetContentFragment.this);
					addVolumeDialog.setContentView(R.layout.dialog_add_task_volume);
				}
				addVolumeDialog.show();
			}
		});

		volumeTaskList.setOnItemLongClickListener(this);
		volumeTaskList.setOnItemClickListener(this);

		initVolumeTaskData();
	}

	List<VolumeTaskRecord> listVolumeTask;
	VolumeTaskAdapter adapterVolume;

	void initVolumeTaskData() {
		listVolumeTask = VolumeHelper.getAll();
		if (listVolumeTask == null || listVolumeTask.size() == 0) {
			emptyView.setVisibility(View.VISIBLE);//显示任务为空的布局
			adapterVolume = null;
			volumeTaskList.setAdapter(null);
		} else {
			emptyView.setVisibility(View.GONE);//不显示,并且不占布局空间
			if (adapterVolume == null) {
				adapterVolume = new VolumeTaskAdapter(context, listVolumeTask);
				volumeTaskList.setAdapter(adapterVolume);
			} else {
				adapterVolume.setDataChanged(listVolumeTask);
			}
		}
	}

	Button btAddTaskShutdown;
	AddTaskShutdownDialog addShutdownDialog;
	ListView shutdownTaskList;
	View emptyViewShutdown;

	//初始化定时开关机布局
	void initTimeClose() {
		btAddTaskShutdown = (Button) rootView.findViewById(R.id.id_more_add_task_shutdown);
		emptyViewShutdown = rootView.findViewById(R.id.id_more_layout_empty_shutdown);
		shutdownTaskList = (ListView) rootView.findViewById(R.id.id_more_list_task_shutdown);

		btAddTaskShutdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (addShutdownDialog == null) {
					addShutdownDialog = new AddTaskShutdownDialog(context, R.style.CustomDialog, MoreSetContentFragment.this);
					addShutdownDialog.setContentView(R.layout.dialog_add_task_shutdown);
				}
				addShutdownDialog.show();
			}
		});

		shutdownTaskList.setOnItemClickListener(this);
		shutdownTaskList.setOnItemLongClickListener(this);

		initShutdownTaskData();
	}

	List<ShutdownTaskRecord> listShutdownTask;
	ShutdownTaskAdapter adapterShutdownTask;

	void initShutdownTaskData() {
		listShutdownTask = ShutdownHelper.getAll();
		if (listShutdownTask == null || listShutdownTask.size() == 0) {
			emptyViewShutdown.setVisibility(View.VISIBLE);//显示任务为空的布局
			adapterShutdownTask = null;
			shutdownTaskList.setAdapter(null);
		} else {
			emptyViewShutdown.setVisibility(View.GONE);//不显示,并且不占布局空间
			if (adapterShutdownTask == null) {
				adapterShutdownTask = new ShutdownTaskAdapter(context, listShutdownTask);
				shutdownTaskList.setAdapter(adapterShutdownTask);
			} else {
				adapterShutdownTask.setDataChanged(listShutdownTask);
			}
		}
	}

	void initApkVerLayout() {
		int verCode = ApkUtil.getAppVersionCode(context);
		String verName = ApkUtil.getAppVersionName(context);

		((TextView) rootView.findViewById(R.id.id_apk_ver_code)).setText(String.valueOf(verCode));
		((TextView) rootView.findViewById(R.id.id_apk_ver_name)).setText(verName);
	}

	/**
	 * 初始化 添加任务 布局
	 */
	void initAddTaskLayout() {
	}

	@Override
	public void onTaskAdd() {
		if (style == STYLE_MORE_TIME_VOLUME) {
			initVolumeTaskData();
			
			if(listVolumeTask==null)
				return;
			
			StringBuffer stringBuffer= new StringBuffer();
			VolumeTaskRecord taskRecord;
			for (int i = 0; i < listVolumeTask.size(); i++) {
				taskRecord = listVolumeTask.get(i);
						
				if (taskRecord.state == BaseTaskDialog.STATE_ENABLED) {
					stringBuffer.append(taskRecord.volume + "_");
					stringBuffer.append(taskRecord.period + "_");
					stringBuffer.append(taskRecord.hour + ":" +taskRecord.minute);
					if (i != listVolumeTask.size()-1) {
						stringBuffer.append("^");
					}
				}
				
			}
		
			
			Tool.saveConfig("volume!" + stringBuffer.toString(), Constant.advance);
		}
		if (style == STYLE_MORE_TIME_COLSE) {
			initShutdownTaskData();
			
			if(listShutdownTask==null)
				return;
			
			StringBuffer stringBuffer= new StringBuffer();
			ShutdownTaskRecord shutdownTaskRecord;
			for (int i = 0; i < listShutdownTask.size(); i++) {
				shutdownTaskRecord = listShutdownTask.get(i);
						
				if (shutdownTaskRecord.state == BaseTaskDialog.STATE_ENABLED) {
					stringBuffer.append(shutdownTaskRecord.taskType + "_");
					stringBuffer.append(shutdownTaskRecord.period + "_");
					stringBuffer.append(shutdownTaskRecord.hour + ":" +shutdownTaskRecord.minute);
					if (i != listShutdownTask.size()-1) {
						stringBuffer.append("^");
					}
				}
				
			}
		
			
			Tool.saveConfig("shut!" + stringBuffer.toString(), Constant.config);
			Constant.li.writeLog("0000 " + R.string.log38
					+ stringBuffer.toString());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.id_more_list_task_volume) {
			final VolumeTaskRecord taskRecord = (VolumeTaskRecord) parent.getAdapter().getItem(position);
			showChangeVolumeTaskDialog(taskRecord);
		}
		if (parent.getId() == R.id.id_more_list_task_shutdown) {
			final ShutdownTaskRecord taskRecord = (ShutdownTaskRecord) parent.getAdapter().getItem(position);
			showChangeShutdownTaskDialog(taskRecord);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.id_more_list_task_volume) {
			showContextMenu((VolumeTaskRecord) parent.getAdapter().getItem(position));
			return true;
		}
		if (parent.getId() == R.id.id_more_list_task_shutdown) {
			showContextMenu((ShutdownTaskRecord) parent.getAdapter().getItem(position));
			return true;
		}
		return false;
	}

	private void showChangeVolumeTaskDialog(VolumeTaskRecord taskRecord) {
		AddTaskVolumeDialog dialog = new AddTaskVolumeDialog(context, R.style.CustomDialog, taskRecord, this);
		dialog.setContentView(R.layout.dialog_add_task_volume);
		dialog.show();
	}

	private void showChangeShutdownTaskDialog(ShutdownTaskRecord taskRecord) {
		AddTaskShutdownDialog dialog = new AddTaskShutdownDialog(context, R.style.CustomDialog, taskRecord, this);
		dialog.setContentView(R.layout.dialog_add_task_shutdown);
		dialog.show();
	}

	String[] strItems = new String[]{"修改任务", "删除任务", "禁用任务"};

	private void showContextMenu(final SugarRecord record) {
		//final VolumeTaskRecord taskRecord = (VolumeTaskRecord) parent.getAdapter().getItem(position);
		new AlertDialog.Builder(context).setItems(strItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						if (style == STYLE_MORE_TIME_VOLUME) {
							showChangeVolumeTaskDialog((VolumeTaskRecord) record);
						}
						if (style == STYLE_MORE_TIME_COLSE) {
							showChangeShutdownTaskDialog((ShutdownTaskRecord) record);
						}
						break;
					case 1:
						if (style == STYLE_MORE_TIME_VOLUME) {
							VolumeHelper.delete(record.getId());
							initVolumeTaskData();
						}
						if (style == STYLE_MORE_TIME_COLSE) {
							ShutdownHelper.delete(record.getId());
							initShutdownTaskData();
						}
						break;
					case 2:
						if (style == STYLE_MORE_TIME_VOLUME) {
							((VolumeTaskRecord) record).state = AddTaskVolumeDialog.STATE_DISABLE;
							record.save();
							initVolumeTaskData();
						}
						if (style == STYLE_MORE_TIME_COLSE) {
							((ShutdownTaskRecord) record).state = AddTaskVolumeDialog.STATE_DISABLE;
							record.save();
							initShutdownTaskData();
						}
						break;
					default:
						break;
				}
			}
		}).show();
	}
}
//修改于:2015年5月15日,星期五
