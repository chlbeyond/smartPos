package com.rainbow.smartpos.login;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.rainbow.smartpos.printer.LocalPrinter;
import com.rainbow.smartpos.service.PrintService;

public class SanyiPintServiceConnection implements ServiceConnection {

	private LocalPrinter printer;
	private PrinterServiceStartListener listener;
	PrintService.PrinterServiceBinder binder;
	public PrintService printService;

	public SanyiPintServiceConnection(LocalPrinter printer, PrinterServiceStartListener listener) {
		super();
		this.printer = printer;
		this.listener = listener;
	}

	public LocalPrinter getPrinter(){
		return this.printer;
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		// We've bound to LocalService, cast the IBinder and get
		// LocalService instance
		binder = (PrintService.PrinterServiceBinder) service;
		printService = binder.getService();
		if (printer != null) {

			if (printService.restartServer(printer)) {
				listener.onPrintServiceStartSuccess();
			} else {
				listener.onPrintServiceStartFailed();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
	}

	public static interface PrinterServiceStartListener {
		void onPrintServiceStartSuccess();

		void onPrintServiceStartFailed();
	}

}