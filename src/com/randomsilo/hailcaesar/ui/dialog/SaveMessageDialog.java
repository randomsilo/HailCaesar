package com.randomsilo.hailcaesar.ui.dialog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.R;
import com.randomsilo.hailcaesar.Utility;

public class SaveMessageDialog {

	public static void DisplaySaveMessageDialog(
			final String fileNamePath
			, final String message, final Activity activity) {

		final Context context = activity.getApplicationContext();
		
		// Parse File Pieces
		final String path = Utility.GetPath(fileNamePath);
		final String fileName = Utility.GetFileNameWithoutExtension(fileNamePath);

		// Resources
		final Resources res = activity.getResources();
		
		// Alert Dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle(res.getString(R.string.dialog_file_save_title));
		alert.setMessage(res.getString(R.string.dialog_file_save_message));

		// Vertical Layout
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		// File Name EditText
		final EditText fileNameEditText = new EditText(activity);
		fileNameEditText
				.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
						| InputType.TYPE_TEXT_FLAG_CAP_WORDS
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		fileNameEditText.setText(fileName);
		layout.addView(fileNameEditText);

		// Clear Button
		final Button fixFileName = new Button(activity);
		fixFileName.setText(R.string.dialog_file_save_button_format_name);
		fixFileName.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View arg0) {
            	fileNameEditText.setText(Utility.GetTimePrefix());
			}
		});
		layout.addView(fixFileName);
		
		// Set View
		alert.setView(layout);
		
		// Save Button
		alert.setPositiveButton(
				res.getString(R.string.dialog_file_save_button_save),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						String newfileName = fileNameEditText.getText()
								.toString();
						String fileNameExt = newfileName
								+ GlobalSettings.ExtensionMessage;

						try {
							File messageFile = new File(path, fileNameExt);
							messageFile.createNewFile();
							PrintWriter out;
							out = new PrintWriter(messageFile);
							out.println(message);
							out.flush();
							out.close();

							if (!newfileName.equals(fileName)) {
								File oldFile = new File(fileNamePath);
								boolean deleteSuccess = oldFile.delete();
								if (!deleteSuccess) {
									Toast.makeText(context,
											"File to remove original file",
											Toast.LENGTH_LONG).show();
								}
							}
						} catch (IOException e) {
							Toast.makeText(context,
									"Save error. " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						}
					}
				});

		// Exit Button
		alert.setNegativeButton(
				res.getString(R.string.dialog_file_save_button_exit),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.dismiss();
					}
				});

		// Create Dialog
		final AlertDialog dialog = alert.create();
		dialog.show();
		
		// Theme Adjustments
		final int color = activity.getResources().getColor(R.color.theme_main);
		int alertTitleId = activity.getResources().getIdentifier("alertTitle", "id", "android");
		final Window window = dialog.getWindow();
		final View view = window.getDecorView();
		final TextView alertTitle = (TextView)view.findViewById(alertTitleId);
		alertTitle.setTextColor(color);
		
		final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
		final View titleDivider = dialog.findViewById(titleDividerId);
		titleDivider.setBackgroundColor(color);
		
		return;
	}

}
