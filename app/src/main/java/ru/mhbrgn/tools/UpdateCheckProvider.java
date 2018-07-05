package ru.mhbrgn.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import android.net.Uri;

public class UpdateCheckProvider {
    private Context context;
    private String message = "New version (%1$d). Download it?";

    public UpdateCheckProvider(Context context) {
        this.context = context;

        BroadcastReceiver checker = new UpdateCheckReceiver();

        IntentFilter intentFilter = new IntentFilter(UpdateCheckService.ACTION_CHECK_FOR_UPDATES);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        context.registerReceiver(checker, intentFilter);
    }

    public void checkForUpdates() {
        context.startService((new Intent(context, UpdateCheckService.class))
                .putExtra("PKG", context.getPackageName()));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    class UpdateCheckReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            final String href = intent.getStringExtra("LINK");
            int v = intent.getIntExtra("NEW_VERSION", 0);

            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setMessage(String.format(message, v));
            b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, href, Toast.LENGTH_SHORT).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(href));
                    context.startActivity(browserIntent);
                }
            });
            b.create().show();
        }
    }
}
