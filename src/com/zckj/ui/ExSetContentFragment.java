package com.zckj.ui;

import android.R.integer;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zckj.data.AsyncGetFilePath;
import com.zckj.data.ExSetFilePathAdapter;
import com.zckj.data.FileNote;
import com.zckj.data.FileUtil;
import com.zckj.data.IconTreeItemHolder;
import com.zckj.data.Logger;
import com.zckj.data.OnTreeNodeClickListener;
import com.zckj.data.Setting;
import com.zckj.data.TreeNode;
import com.zckj.data.Util;
import com.zckj.data.XmlSetting;
import com.linux.vshow.Constant;
import com.linux.vshow.R;
import com.linux.vshow.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExSetContentFragment extends BaseFragment implements
		OnTreeNodeClickListener {

	public static final int STYLE_EX_DISPLAY = 0x003001;
	public static final int STYLE_EX_STORAGE = 0x003002;
	public static final int STYLE_EX_PASSWORD = 0x003003;
	public static final int STYLE_EX_UPDATE_PATH = 0x003004;
	public static final int STYLE_EX_CLEAR_FETE = 0x003005;

	public static final int ROTATE_0 = 1;
	public static final int ROTATE_90 = 2;
	public static final int ROTATE_180 = 3;
	public static final int ROTATE_270 = 4;

	private boolean isFirstRun = true;

	public ExSetContentFragment() {
		default_layout_style = STYLE_EX_DISPLAY;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		switch (style) {
		case STYLE_EX_DISPLAY:
			rootView = inflater.inflate(R.layout.layout_ex_display, container,
					false);
			initExDisplayLayout();
			break;
		case STYLE_EX_PASSWORD:
			rootView = inflater.inflate(R.layout.layout_ex_password, container,
					false);
			initExPassWordLayout();
			break;
		case STYLE_EX_STORAGE:
//			rootView = inflater.inflate(R.layout.layout_update_path, container,
//					false);
			rootView = inflater.inflate(R.layout.layout_ex_storage, container,
					false);
			initExStorageLayout();
			break;
		case STYLE_EX_UPDATE_PATH:
			rootView = inflater.inflate(R.layout.layout_update_path, container,
					false);
			initExUpdatePathLayout();
			break;
		case STYLE_EX_CLEAR_FETE:
			rootView = inflater.inflate(R.layout.layout_ex_clear_fete,
					container, false);
			initExClearFeteLayout();
			break;
		default:
			rootView = inflater.inflate(R.layout.layout_ex_display, container,
					false);
			initExDisplayLayout();
			break;
		}
		return rootView;
	}

	void initExClearFeteLayout() {
	}

	RadioGroup rgRotate;

	void initExDisplayLayout() {
		rgRotate = (RadioGroup) rootView
				.findViewById(R.id.id_ex_rotate_radiogroup);
		rgRotate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.id_ex_rb_rotate_0) {
					setRotate(ROTATE_0);

				} else if (checkedId == R.id.id_ex_rb_rotate_90) {
					setRotate(ROTATE_90);

				} else if (checkedId == R.id.id_ex_rb_rotate_180) {
					setRotate(ROTATE_180);

				} else if (checkedId == R.id.id_ex_rb_rotate_270) {
					setRotate(ROTATE_270);

				}

			}
		});
		initExDisplayLayoutData();
	}

	void initExDisplayLayoutData() {
		int rotate = Setting.getRotate(context);
		switch (rotate) {
		case ROTATE_0:
			((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_0))
					.setChecked(true);
			break;
		case ROTATE_90:
			((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_90))
					.setChecked(true);
			break;
		case ROTATE_180:
			((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_180))
					.setChecked(true);
			break;
		case ROTATE_270:
			((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_270))
					.setChecked(true);
			break;
		default:
			break;
		}
		setRotate(rotate);
	}

	void setRotate(int rotate) {
//		try {
//			int screenChange = Settings.System.getInt(
//					context.getContentResolver(),
//					Settings.System.ACCELEROMETER_ROTATION);
//			if (screenChange == 0) {
//				Logger.e("已关闭重力感应");
//			}
//			if (screenChange == -1) {
//				Logger.e("不支持重力感应");
//			}
//			if (screenChange != 1) {// 自动打开重力感应
//				Settings.System.putInt(context.getContentResolver(),
//						Settings.System.ACCELEROMETER_ROTATION, 1); // 设置打开
//			}
//		} catch (Settings.SettingNotFoundException e) {
//			e.printStackTrace();
//		}

		switch (rotate) {
		case ROTATE_0:
//			getActivity().setRequestedOrientation(
//					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			
			SystemProperties.set("persist.sys.rotation","0");

			Setting.setRotate(context, ROTATE_0);
			XmlSetting.setXmlRotateAngle(ROTATE_0 + "");
			
			Tool.saveConfig(XmlSetting.XML_ROTATE_ANGLE +  "!" +ROTATE_0 , Constant.advance);
			break;
		case ROTATE_90:
//			getActivity().setRequestedOrientation(
//					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			SystemProperties.set("persist.sys.rotation","90");
			
			Setting.setRotate(context, ROTATE_90);
			XmlSetting.setXmlRotateAngle(ROTATE_90 + "");
			
			Tool.saveConfig("rotate_angle!" +ROTATE_90 , Constant.advance);
			break;
		case ROTATE_180:
//			getActivity().setRequestedOrientation(
//					ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			
			SystemProperties.set("persist.sys.rotation","180");
			System.setProperty("persist.sys.rotation","180");
			Setting.setRotate(context, ROTATE_180);
			XmlSetting.setXmlRotateAngle(ROTATE_180 + "");
			
			Tool.saveConfig("rotate_angle!" +ROTATE_180 , Constant.advance);
			break;
		case ROTATE_270:
//			getActivity().setRequestedOrientation(
//					ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			SystemProperties.set("persist.sys.rotation","270");
			
			Setting.setRotate(context, ROTATE_270);
			XmlSetting.setXmlRotateAngle(ROTATE_270 + "");
			
			Tool.saveConfig("rotate_angle!" +ROTATE_270 , Constant.advance);
			break;
		default:
			break;
		}
		
		SystemProperties.set("persist.sys.rotation","270");
		int r = SystemProperties.getInt("persist.sys.rotation",0);
		Logger.e("rotate:--->" + r);
	}

	EditText etPassWord;// 密码
	EditText etPassWordRp;// 重复密码
	EditText etPassWordOld;// 旧的密码
	Button btSaveView;// 保存
	View viewOldPw;//

	void initExPassWordLayout() {
		etPassWord = (EditText) rootView.findViewById(R.id.id_et_password);
		etPassWordRp = (EditText) rootView.findViewById(R.id.id_et_password_rp);
		etPassWordOld = (EditText) rootView
				.findViewById(R.id.id_et_password_old);
		btSaveView = (Button) rootView.findViewById(R.id.id_bt_save);
		viewOldPw = rootView.findViewById(R.id.id_layout_old_pw);

		btSaveView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strPassWord = etPassWord.getText().toString();
				String strPassWordRp = etPassWordRp.getText().toString();
				if (isSetAdminPw()) {
					String strPassWordOld = etPassWordOld.getText().toString();
					if (Util.isEmpty(strPassWordOld)
							|| !XmlSetting.getXmlAdminPw().equalsIgnoreCase(
									strPassWordOld)) {
						etPassWordOld.setError("密码验证失败");
						setFocus(etPassWordOld);
					} else {
						setPassWord(strPassWord, strPassWordRp);
					}
				} else {
					setPassWord(strPassWord, strPassWordRp);
				}
			}
		});

		if (isSetAdminPw()) {
			showOldView(true);
		}
	}

	void showOldView(boolean b) {
		if (b) {
			viewOldPw.setVisibility(View.VISIBLE);
		} else {
			viewOldPw.setVisibility(View.GONE);
		}
		etPassWordOld.setText("");
		etPassWord.setText("");
		etPassWordRp.setText("");
	}

	// 是否设置了管理员密码
	public static boolean isSetAdminPw() {
		String pw = XmlSetting.getXmlAdminPw();
		if (Util.isEmpty(pw)) {
			return false;
		}
		return true;
	}

	void setPassWord(String strPassWord, String strPassWordRp) {
		if (Util.isEmpty(strPassWord) && Util.isEmpty(strPassWordRp)) {
			Util.showPostMsg("已取消密码");
			XmlSetting.setXmlAdminPw("");
			showOldView(false);
		} else if (Util.isEmpty(strPassWord)) {
			etPassWord.setError("请检查输入");
			setFocus(etPassWord);
		} else if (Util.isEmpty(strPassWordRp)) {
			etPassWordRp.setError("请检查输入");
			setFocus(etPassWordRp);
		} else if (!strPassWord.equalsIgnoreCase(strPassWordRp)) {
			etPassWord.setError("请检查输入");
			setFocus(etPassWord);
		} else if (strPassWord.equalsIgnoreCase(strPassWordRp)) {
			Util.showPostMsg("已设置密码");
			XmlSetting.setXmlAdminPw(strPassWord);
			showOldView(true);
			
			Tool.saveConfig(XmlSetting.XML_ADMIN_PW +  "!" + strPassWord, Constant.advance);
		} else {
			Util.showPostMsg("未完成设置");
		}
	}

	TextView currentPathText;
	ListView filePathList;
	ProgressBar loadBar;
	Button btSave;

	RadioButton rbChip, rbSd;
	RadioGroup radioGroup;
	/**
	 * 初始化 存储路径 布局
	 */
	void initExStorageLayout() {
		//initExUpdatePathLayout();
		rbSd = (RadioButton) rootView.findViewById(R.id.id_ex_sd);
		rbChip = (RadioButton) rootView.findViewById(R.id.id_ex_chip);
		radioGroup = ((RadioGroup)rootView.findViewById(R.id.id_ex_group));
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				if (id == R.id.id_ex_sd) {
					XmlSetting.setXmlStoragePath("0");
					
					Tool.saveConfig("savedir!" + 0, Constant.saveDIR);//
//					Logger.e("--->sd");
				}else if (id == R.id.id_ex_chip) {
					XmlSetting.setXmlStoragePath("1");
					
					Tool.saveConfig("savedir!" + 1, Constant.saveDIR);
//					Logger.e("--->chip");
				}
				
			}
		});
		
		//读取之前的设置
		String[] pos = Tool.loadConfig(new String[] { "savedir" },
				Constant.saveDIR);
		int ps = 0;
		if (pos != null) {
			if (pos.length == 1) {
				try {
					ps = Integer.parseInt(pos[0].trim());
				} catch (Exception e) {
				}
			}
		}
		
		if (ps == 0) {
			radioGroup.check(R.id.id_ex_sd);
		}else if (ps==1) {
			radioGroup.check(R.id.id_ex_chip);
		}
		
		if (!Environment.MEDIA_MOUNTED.equals("/mnt/extsd")) {
			radioGroup.check(R.id.id_ex_chip);
			rbSd.setEnabled(false);
			rbSd.setVisibility(View.INVISIBLE);
		}
	}

	class SetFocusToPathView implements Runnable {
		@Override
		public void run() {
			setFocusToCurrentPath(currentPathText);
		}
	}

	void setFocusToCurrentPath(TextView currentPathText) {
		if (isFirstRun) {
			isFirstRun = false;
			return;
		}
		setFocus(currentPathText);
	}

	FileNote fileNote;
	String currentFilePath = "/";

	/**
	 * 初始化 存储设置 布局数据
	 */
	private void initExStorageLayoutData() {
		String path;
		if (style == STYLE_EX_STORAGE) {
			path = XmlSetting.getXmlStoragePath();
			if (Util.isEmpty(path)) {
				currentFilePath = FileUtil.getSDPath();
			} else {
				currentFilePath = path;
			}
		} else if (style == STYLE_EX_UPDATE_PATH) {
			path = XmlSetting.getXxmlUpdatePath();
			if (Util.isEmpty(path)) {
				currentFilePath = FileUtil.getSDPath();
			} else {
				currentFilePath = path;
			}
		}

		// FileNote fileNote = FileUtil.getFileNote(currentFilePath);
		updateCurrentFilePathText(currentFilePath);
		updateListViewData(currentFilePath);
	}

	void updateCurrentFilePathText(String filePath) {
		if (currentPathText != null)
			currentPathText.setText(filePath);
	}

	void updateListViewData(String filePath) {
		File temp = new File(filePath);
		if (!temp.canRead()) {
			updateListViewData(FileUtil.getCurrentPathPrev(filePath));
			updateCurrentFilePathText(FileUtil.getCurrentPathPrev(filePath));
			Util.showPostMsg(filePath + " 路径不可读");
			return;
		}

		if (loadBar != null)
			loadBar.setVisibility(View.VISIBLE);
		new AsyncGetFilePath() {
			@Override
			public void onGetFilePath(FileNote fileNote) {
				Logger.e(fileNote.currentFilePath + " 含文件夹数 "
						+ fileNote.fileFolderPath.size());

				ExSetContentFragment.this.fileNote = fileNote;
				if (ExSetContentFragment.this.fileNote != null
						&& filePathList != null
						&& ExSetContentFragment.this.fileNote.fileFolderPath
								.size() > 0) {
					hideEmptyPathLayout();
					filePathList.setAdapter(new ExSetFilePathAdapter(context,
							ExSetContentFragment.this.fileNote));
				} else {
					Logger.e("返回焦点");
					showEmptyPathLayout();
					filePathList.setAdapter(null);
					setFocusToCurrentPath(currentPathText);// 返回焦点...
				}
				if (loadBar != null)
					loadBar.setVisibility(View.INVISIBLE);
			}
		}.execute(filePath);
	}

	View listEmptyHeaderView;

	void showEmptyPathLayout() {
		listEmptyHeaderView.setVisibility(View.VISIBLE);
	}

	void hideEmptyPathLayout() {
		if (listEmptyHeaderView != null) {
			listEmptyHeaderView.setVisibility(View.GONE);
		}
	}

	private void setFocus(View v) {
		// v.setFocusable(true);
		// v.setFocusableInTouchMode(true);
		v.requestFocus();
		// v.requestFocusFromTouch();
	}

	RelativeLayout layoutRoot;
	AndroidTreeView treeView;
	TextView tvCurrentPath;
	String strCurrentPath = FileUtil.getSDPath();//
	TreeNode rootNode = TreeNode.root();
	TreeNode curNode = rootNode;// 保存当前的node
	Button btSaveUpdatePath;
//	btCreatePath, btDeletePath;
	ProgressBar pbLoadView;

	void initExUpdatePathLayout() {
		layoutRoot = (RelativeLayout) rootView
				.findViewById(R.id.id_layout_update_path);
		tvCurrentPath = (TextView) rootView
				.findViewById(R.id.id_ex_current_path);
		btSaveUpdatePath = (Button) rootView
				.findViewById(R.id.id_ex_file_path_save);
		pbLoadView = (ProgressBar) rootView
				.findViewById(R.id.id_ex_file_path_load_bar);
//		btCreatePath = (Button) rootView
//				.findViewById(R.id.id_ex_file_path_create);
//		btDeletePath = (Button) rootView
//				.findViewById(R.id.id_ex_file_path_delete);

		btSaveUpdatePath.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (style == STYLE_EX_STORAGE) {
					XmlSetting.setXmlStoragePath(tvCurrentPath.getText()
							.toString());
					Util.showPostMsg("已设置路径:"
							+ tvCurrentPath.getText().toString());
				} else if (style == STYLE_EX_UPDATE_PATH) {
					XmlSetting.setXxmlUpdatePath(tvCurrentPath.getText()
							.toString());
					Util.showPostMsg("已更新路径:"
							+ tvCurrentPath.getText().toString());
				}
			}
		});
//		btDeletePath.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String filePath = tvCurrentPath.getText().toString();
//				File file = new File(filePath);
//				if (!file.exists()) {
//					Util.showPostMsg("已不存在");
//				} else if (FileUtil.deleteFiles(file)) {
//					Util.showPostMsg(filePath + " 删除成功");
//					treeView.removeNode(curNode);
//					curNode = rootNode;
//					setUpdateCurrentFilePathText("/");
//				} else {
//					Util.showPostMsg(filePath + " 删除失败");
//				}
//			}
//		});
//
//		btCreatePath.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				View view = View.inflate(context,
//						R.layout.layout_create_folder, null);
//				final EditText editText = (EditText) view
//						.findViewById(R.id.id_et_name);
//				Button create = (Button) view.findViewById(R.id.id_bt_create);
//				Button cancel = (Button) view.findViewById(R.id.id_bt_cancel);
//
//				final Dialog dialog = new Dialog(context, R.style.CustomDialog);
//				dialog.setContentView(view, new ViewGroup.LayoutParams(600,
//						ViewGroup.LayoutParams.WRAP_CONTENT));
//
//				create.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						String curFilePath = tvCurrentPath.getText().toString();
//						String strFileName = editText.getText().toString();
//						File file = new File(curFilePath + File.separator
//								+ strFileName);
//						if (Util.isEmpty(strFileName)) {
//							editText.setError("请输入文件夹名称");
//							setFocus(editText);
//						} else if (file.exists()) {
//							editText.setError("文件名称已经存在");
//							setFocus(editText);
//						} else {
//							if (file.mkdir()) {
//								Util.showPostMsg("创建成功");
//								dialog.cancel();
//
//								try {
//
//									FileNote fileNote = FileUtil
//											.getFileNote(curFilePath);
//									fileNote.currentFilePath = curFilePath;
//									fileNote.fileFolderName.set(curNode
//											.getChildren().size(), strFileName);
//									TreeNode newNode = createTreeNodeView(
//											curNode.getChildren().size(),
//											fileNote);
//
//									curNode.getViewHolder().getTreeView()
//											.addNode(curNode, newNode);
//									// curNode = newNode;
//									// tvCurrentPath.setText(file.getAbsolutePath());
//
//								} catch (Exception e) {
//
//								}
//
//							} else {
//								Util.showPostMsg("创建失败");
//							}
//						}
//					}
//				});
//				cancel.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						dialog.cancel();
//					}
//				});
//				dialog.show();
//			}
//		});

		showTreeView();
		initExUpdatePathLayoutData();
	}

	private void showTreeView() {

		rootNode = TreeNode.root();
		treeView = new AndroidTreeView(context, rootNode);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		View view = treeView.getView();
		layoutRoot.addView(view, params);
		
		btSaveUpdatePath.setNextFocusDownId(R.id.tree_view);
		btSaveUpdatePath.setNextFocusUpId(R.id.tree_view);
		view.setNextFocusDownId(R.id.id_ex_file_path_save);
		view.setNextFocusUpId(R.id.id_ex_file_path_save);
	}

	String rootPath = "/mnt";
	
	private void initExUpdatePathLayoutData() {
		String path;
		if (style == STYLE_EX_STORAGE) {
			path = XmlSetting.getXmlStoragePath();
			if (Util.isEmpty(path)) {
				strCurrentPath = rootPath;
			} else {
				strCurrentPath = path;
			}
		} else if (style == STYLE_EX_UPDATE_PATH) {
			path = XmlSetting.getXxmlUpdatePath();
			if (Util.isEmpty(path)) {
				strCurrentPath = rootPath;
			} else {
				strCurrentPath = path;
			}
		}

		setUpdateCurrentFilePathText(strCurrentPath);
		setUpdateTreeNodeData(null, strCurrentPath);
		//expandTreeViewToPath(strCurrentPath);
	}

	Handler handler = new Handler();

	/**
	 * 依次展开路径
	 *
	 * @param strCurrentPath
	 */
	private void expandTreeViewToPath(final String strCurrentPath) {
		if (Util.isEmpty(strCurrentPath)) {
			return;
		}

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
			}
		}, 300);
	}

	// 获取当前路径的所有node
	List<TreeNode> getAllNodeFromCurrentPath(String path) {
		if (Util.isEmpty(path)) {
			return null;
		}

		File tempFile = new File(path);
		if (!tempFile.exists() || !tempFile.isDirectory()
				|| !tempFile.canRead()) {
			Util.showPostMsg(path + " 无效的路径");
			return null;
		}

		FileNote fileNote = FileUtil.getFileNote(path);
		List<TreeNode> list = new ArrayList<TreeNode>();

		if (fileNote != null && fileNote.fileFolderName.size() > 0) {
			// Logger.e("数量" + fileNote.fileFolderPath.size());
			int count = 0;
			for (String name : fileNote.fileFolderName) {
				TreeNode treeNode = createTreeNodeView(count, fileNote);// 创建node
				List<File> listFolder = FileUtil
						.listFolder(fileNote.fileFolderPath.get(count));// 判断文件夹是否含有子文件夹
				if (listFolder == null || listFolder.size() < 1) {
					((IconTreeItemHolder.IconTreeItem) treeNode.getValue()).hasChildFolder = false;
				} else {
					// Logger.e("路径" + fileNote.fileFolderPath.get(count) +
					// "  含有子文件夹" + listFolder.size());
					((IconTreeItemHolder.IconTreeItem) treeNode.getValue()).hasChildFolder = true;
				}
				list.add(treeNode);
				count++;
			}
		}
		return list;
	}

	// 创建一个tree Item
	TreeNode createTreeNodeView(int index, FileNote fileNote) {
		TreeNode node = new TreeNode(new IconTreeItemHolder.IconTreeItem(index,
				fileNote)).setViewHolder(new IconTreeItemHolder(context, this));
		return node;
	}

	@Override
	public void onTreeItemClick(TreeNode node,
			IconTreeItemHolder.IconTreeItem item) {
		if (!node.isExpanded()) {
			treeView.expandNode(node);// 展开node
			if (!item.isLoad) {// 如果没有装载过数据
				setUpdateTreeNodeData(node,
						item.fileNote.fileFolderPath.get(item.index));// 更新数据
				item.isLoad = true;
			}
		} else {
			treeView.collapseNode(node);
		}

		setUpdateCurrentFilePathText(item.fileNote.fileFolderPath
				.get(item.index));

		curNode = node;// 保存当前的tree node
	}

	void setUpdateCurrentFilePathText(String path) {
		if (tvCurrentPath != null) {
			tvCurrentPath.setText(path);
		}
		File file = new File(path);

		if (file.isDirectory() && file.canWrite()) {
//			btCreatePath.setEnabled(true);
//			btDeletePath.setEnabled(true);
			btSaveUpdatePath.setEnabled(true);
		} else {
//			btCreatePath.setEnabled(false);
//			btDeletePath.setEnabled(false);
			btSaveUpdatePath.setEnabled(false);
		}
	}

	// 将所有node,添加到指定的node中,如果不指定,表示添加到根node
	synchronized void setUpdateTreeNodeData(TreeNode node, String path) {
		final TreeNode tempNode;
		String tempPath;
		if (node == null) {
			tempNode = rootNode;
			tempPath = rootPath;
		} else {
			tempNode = node;
			tempPath = new String(path);
		}

		pbLoadView.setVisibility(View.VISIBLE);
		new AsyncTask<String, Void, List<TreeNode>>() {
			@Override
			protected List<TreeNode> doInBackground(String... params) {
				try {
					List<TreeNode> listNode = getAllNodeFromCurrentPath(params[0]);
					return listNode;
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<TreeNode> treeNodes) {
				super.onPostExecute(treeNodes);
				if (treeNodes == null) {
					Util.showPostMsg("无法读取");
				} else if (treeNodes.size() > 0) {
					for (TreeNode node1 : treeNodes) {
						treeView.addNode(tempNode, node1);
					}
				}
				pbLoadView.setVisibility(View.INVISIBLE);
			}
		}.execute(tempPath);
	}

	void removeAllTreeNode(TreeNode node) {
		if (node != null) {
			List<TreeNode> nodeList = node.getChildren();
			if (nodeList == null || nodeList.size() <= 0) {
				return;
			}

			Logger.e("需要删除数量:" + nodeList.size());
			for (TreeNode node1 : nodeList) {
				node.deleteChild(node1);
			}
		}
	}
}
// 修改于:2015年5月15日,星期五
