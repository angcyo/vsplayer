package com.zckj.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.linux.vshow.R;


/**
 * 自定义控件实现ip地址特殊输入
 *
 * @author 子墨
 *         <p/>
 *         2015-1-4
 */
public class IPEditText extends LinearLayout {

    protected String beforeText;
    private EditText mFirstIP;
    private EditText mSecondIP;
    private EditText mThirdIP;
    private EditText mFourthIP;
    private String mText1;
    private String mText2;
    private String mText3;
    private String mText4;

    public IPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * 初始化控件
         */
        View view = LayoutInflater.from(context).inflate(
                R.layout.custom_my_edittext, this);
        mFirstIP = (EditText) view.findViewById(R.id.ip_first);
        mSecondIP = (EditText) view.findViewById(R.id.ip_second);
        mThirdIP = (EditText) view.findViewById(R.id.ip_third);
        mFourthIP = (EditText) view.findViewById(R.id.ip_fourth);
        
        OperatingEditText(context);

        seFocus(mFirstIP);
    }

    private void seFocus(View v) {
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.requestFocusFromTouch();
    }

    public void clearAllFocus(){
        mFirstIP.clearFocus();
        mSecondIP.clearFocus();
        mThirdIP.clearFocus();
        mFourthIP.clearFocus();
    }

    // 判断str 是否是纯数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得EditText中的内容,当每个Edittext的字符达到三位时,自动跳转到下一个EditText,当用户点击.时,
     * 下一个EditText获得焦点
     */
    private void OperatingEditText(final Context context) {
        mFirstIP.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
                 * 用户点击啊.时,下一个EditText获得焦点
                 */
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s == null || s.length() == 0) {
                    beforeText = new String("0");
                } else {
                    beforeText = new String(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged(null, mFirstIP, mSecondIP, s);

                mText1 = s.toString();

            }
        });

        mSecondIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
                 * 用户点击啊.时,下一个EditText获得焦点
                 */
                // if (s != null && s.length() > 0) {
                // if (s.length() > 2 || s.toString().trim().contains(".")) {
                // if (s.toString().trim().contains(".")) {
                // mText2 = s.toString().substring(0, s.length() - 1);
                // mSecondIP.setText(mText2);
                // } else {
                // mText2 = s.toString().trim();
                // }
                //
                // if (Integer.parseInt(mText2) > 255) {
                // Toast.makeText(context, "请输入合法的ip地址",
                // Toast.LENGTH_LONG).show();
                // return;
                // }
                //
                // Editor editor = mPreferences.edit();
                // editor.putInt("IP_SECOND", mText2.length());
                // editor.commit();
                //
                // mThirdIP.setFocusable(true);
                // mThirdIP.requestFocus();
                // }
                // }
                //
                // /**
                // * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
                // */
                // if (start == 0 && s.length() == 0) {
                // mFirstIP.setFocusable(true);
                // mFirstIP.requestFocus();
                // mFirstIP.setSelection(mPreferences.getInt("IP_FIRST", 0));
                // }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s == null || s.length() == 0) {
                    beforeText = new String("0");
                } else {
                    beforeText = new String(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged(mFirstIP, mSecondIP, mThirdIP, s);

                mText2 = s.toString();
            }
        });

        mThirdIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
                 * 用户点击啊.时,下一个EditText获得焦点
                 */
                // if (s != null && s.length() > 0) {
                // if (s.length() > 2 || s.toString().trim().contains(".")) {
                // if (s.toString().trim().contains(".")) {
                // mText3 = s.toString().substring(0, s.length() - 1);
                // mThirdIP.setText(mText3);
                // } else {
                // mText3 = s.toString().trim();
                // }
                //
                // if (Integer.parseInt(mText3) > 255) {
                // Toast.makeText(context, "请输入合法的ip地址",
                // Toast.LENGTH_LONG).show();
                // return;
                // }
                //
                // Editor editor = mPreferences.edit();
                // editor.putInt("IP_THIRD", mText3.length());
                // editor.commit();
                //
                // mFourthIP.setFocusable(true);
                // mFourthIP.requestFocus();
                // }
                // }
                //
                // /**
                // * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
                // */
                // if (start == 0 && s.length() == 0) {
                // mSecondIP.setFocusable(true);
                // mSecondIP.requestFocus();
                // mSecondIP.setSelection(mPreferences.getInt("IP_SECOND", 0));
                // }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s == null || s.length() == 0) {
                    beforeText = new String("0");
                } else {
                    beforeText = new String(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged(mSecondIP, mThirdIP, mFourthIP, s);

                mText3 = s.toString();
            }
        });

        mFourthIP.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /**
                 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
                 * 用户点击啊.时,下一个EditText获得焦点
                 */
                // if (s != null && s.length() > 0) {
                // mText4 = s.toString().trim();
                //
                // if (Integer.parseInt(mText4) > 255) {
                // Toast.makeText(context, "请输入合法的ip地址", Toast.LENGTH_LONG)
                // .show();
                // return;
                // }
                //
                // Editor editor = mPreferences.edit();
                // editor.putInt("IP_FOURTH", mText4.length());
                // editor.commit();
                // }
                //
                // /**
                // * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
                // */
                // if (start == 0 && s.length() == 0) {
                // mSecondIP.setFocusable(true);
                // mSecondIP.requestFocus();
                // mSecondIP.setSelection(mPreferences.getInt("IP_THIRD", 0));
                // }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s == null || s.length() == 0) {
                    beforeText = new String("0");
                } else {
                    beforeText = new String(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                textChanged(mThirdIP, mFourthIP, null, s);
                //
                // trimEditable(s);
                //
                // if (s == null || s.length() <= 0) {
                // mFourthIP.setText("0");
                // mFourthIP.setSelection(1);
                // } else if (s.length() >= 4) {
                // s.replace(0, s.length(), "255");
                // hideSoftInput(mFourthIP);
                // } else if (isNumeric(s.toString())
                // && Integer.valueOf(s.toString()) > 255) {
                // s.replace(0, s.length(), "255");
                // } else if (isNumeric(s.toString()) && s.length() == 3) {
                // hideSoftInput(mFourthIP);
                // }

                mText4 = s.toString();
            }
        });
    }

    protected void textChanged(EditText prevEdit, EditText currentEdit,
                               EditText nextEdit, Editable s) {
        if (s != null && s.length() > 0 && s.charAt(0) == '.') {
            s.replace(0, s.length(), beforeText);
            if (nextEdit != null) {
                nextFocusEdit(nextEdit);
            } else {
                hideSoftInput(currentEdit);
            }
        }

        trimEditable(s);

        if (s == null || s.length() <= 0) {
            currentEdit.setText("0");
            if (prevEdit != null) {
                prevFocusEdit(prevEdit);
            } else {
                currentEdit.setSelection(1);
            }
        } else if (s.length() > 1 && s.charAt(s.length() - 1) == '.') {
            s.replace(s.length() - 1, s.length(), "");
            if (nextEdit != null) {
                nextFocusEdit(nextEdit);
            } else {
                hideSoftInput(currentEdit);
            }
        } else if (s.length() >= 4) {
            s.replace(0, s.length(), "255");
            if (nextEdit != null) {
                nextFocusEdit(nextEdit);
            } else {
                hideSoftInput(currentEdit);
            }
        } else if (isNumeric(s.toString())
                && Integer.valueOf(s.toString()) > 255) {
            s.replace(0, s.length(), "255");
        } else if (isNumeric(s.toString()) && s.length() == 3) {
            if (nextEdit != null) {
                nextFocusEdit(nextEdit);
            } else {
                hideSoftInput(currentEdit);
            }
        }

    }

    protected void trimEditable(Editable s) {
        if (s == null || s.length() < 2) {
            return;
        }

        if (s.charAt(0) == '0') {
            s.delete(0, 1);

            trimEditable(s);
        }
    }

    protected void nextFocusEdit(EditText editText) {
        editText.setFocusable(true);
        editText.requestFocus();
    }

    protected void prevFocusEdit(EditText editText) {
        editText.setFocusable(true);
        editText.requestFocus();
    }

    public String getIpText() {
        mText1 = mFirstIP.getText().toString();
        mText2 = mSecondIP.getText().toString();
        mText3 = mThirdIP.getText().toString();
        mText4 = mFourthIP.getText().toString();

        if (TextUtils.isEmpty(mText1) || TextUtils.isEmpty(mText2)
                || TextUtils.isEmpty(mText3) || TextUtils.isEmpty(mText4)) {
            return null;
        }
        return mText1 + "." + mText2 + "." + mText3 + "." + mText4;
    }

    public void setIpText(String ipText) {
        if (ipText == null || ipText.length() == 0) {
            return;
        }

        String[] ip = ipText.split("\\.");
        if (ip.length < 4) {
            return;
        }

        mFirstIP.setText(ip[0]);
        mSecondIP.setText(ip[1]);
        mThirdIP.setText(ip[2]);
        mFourthIP.setText(ip[3]);

//        mFirstIP.requestFocus();
    }

    public void hideSoftInput(View focusView) {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }
}
//修改于:2015年5月15日,星期五
