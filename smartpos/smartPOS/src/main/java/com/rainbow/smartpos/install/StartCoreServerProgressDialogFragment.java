package com.rainbow.smartpos.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbow.smartpos.R;
import com.rainbow.smartpos.common.components.BaseExecutor;
import com.rainbow.smartpos.common.components.ComponentExecutorPool;
import com.rainbow.smartpos.common.components.LighttpdExecutor;
import com.rainbow.smartpos.common.components.MySqlExecutor;
import com.rainbow.smartpos.common.components.PHPExecutor;
import com.rainbow.smartpos.common.components.WebSocketExecutor;
import com.rainbow.smartpos.util.FileUtils;

public class StartCoreServerProgressDialogFragment extends DialogFragment {

	private static String NATIVE_DIRECTORY;
	private static String EXTERNAL_REPOSITORY;
	static {
		// BaseExecutor should be registered before other
		ComponentExecutorPool.registerExecutor(BaseExecutor.class);

		ComponentExecutorPool.registerExecutor(MySqlExecutor.class);
		ComponentExecutorPool.registerExecutor(LighttpdExecutor.class);
		ComponentExecutorPool.registerExecutor(PHPExecutor.class);
		ComponentExecutorPool.registerExecutor(WebSocketExecutor.class);
		

	}

	private final class InstallerTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String FILE_INSTALL = "";
			Boolean isInstalled = true;
			FileInputStream is = null;

			ZipInputStream zipInputStream = null;
			if (!FileUtils.checkIfExecutableExists()) {
				createDirectory("");

				try {
					// update from external repository (from
					// `/mnt/sdcard/smartpos/repository/update.zip`)

					if (new File(EXTERNAL_REPOSITORY).exists()) {

						zipInputStream = new ZipInputStream(
								new FileInputStream(EXTERNAL_REPOSITORY));

					} else {

						// use internal repository
						zipInputStream = new ZipInputStream(getActivity()
								.getAssets().open("data.zip"));
					}
				} catch (Exception e) {

				}
				ZipEntry zipEntry = null;

				try {

					while ((zipEntry = zipInputStream.getNextEntry()) != null) {

						if (zipEntry.isDirectory()) {

							createDirectory(zipEntry.getName());

						} else {

							FileOutputStream fout = null;

							fout = new FileOutputStream(NATIVE_DIRECTORY
									+ zipEntry.getName());

							publishProgress(zipEntry.getName());

							byte[] buffer = new byte[4096 * 10];
							int length = 0;

							while ((length = zipInputStream.read(buffer)) != -1) {

								fout.write(buffer, 0, length);
							}

							zipInputStream.closeEntry();
							fout.close();
						}

					}
					zipInputStream.close();
				} catch (Exception e) {

					isInstalled = false;

					// return null;

				}

				if (isInstalled || FileUtils.checkIfExecutableExists()) {
					publishProgress("Starting all service now");
					try {
						ComponentExecutorPool.connectAll();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (java.lang.InstantiationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					publishProgress("DONE");
				} else {
					publishProgress("ERROR");
				}
			}
			// check if it is agent;
			if (getDialog() != null && getDialog().isShowing()) {
				dismiss();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (mDialog != null) {

				messageView.setText("EXTRACTING: " + values[0]);

				if (values[0].equals("DONE")) {

					Toast.makeText(getActivity(),
							getString(R.string.core_apps_installed),
							Toast.LENGTH_LONG).show();

				}

				if (values[0].equals("ERROR")) {

					Toast.makeText(getActivity(),
							getString(R.string.install_failed),
							Toast.LENGTH_LONG).show();

				}

			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		/**
		 * Responsible for creating directory inside application's data
		 * directory
		 * 
		 * @param dirName
		 */
		protected void createDirectory(String dirName) {

			File file = new File(NATIVE_DIRECTORY + dirName);

			if (!file.isDirectory())
				file.mkdirs();
		}

	}

	private Dialog mDialog;
	private TextView titleView;
	private TextView messageView;

	private InstallerTask mTask;

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mTask != null) {
			mTask.cancel(false);
		}
		super.onCancel(dialog);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		NATIVE_DIRECTORY = getActivity().getApplicationInfo().dataDir + "/";

		EXTERNAL_REPOSITORY = Environment.getExternalStorageDirectory()
				.getPath() + "/" + Constants.UPDATE_FROM_EXTERNAL_REPOSITORY;

		LayoutInflater inflater = getActivity().getLayoutInflater();

		mDialog = new Dialog(getActivity());
		mDialog.setContentView(inflater.inflate(
				R.layout.dialog_install_progress, null));

		// Title View
		titleView = (TextView) mDialog.findViewById(R.id.title);
		titleView.setText(R.string.core_apps);

		// Message View
		messageView = (TextView) mDialog.findViewById(R.id.message);
		messageView.setText(R.string.installing_core_apps);

		mTask = new InstallerTask();
		// mTask.execute();
		mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return mDialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (mTask != null) {
			mTask.cancel(false);
		}
		super.onDismiss(dialog);
	}
}