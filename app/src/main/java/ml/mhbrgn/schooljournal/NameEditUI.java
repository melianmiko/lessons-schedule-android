package ml.mhbrgn.schooljournal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;

class NameEditUI {
    static class OnCompleteListener {
        void onComplete(LessonName name) { }
    }

    private View layout;
    private BottomSheetDialog dialog;

    @SuppressLint("InflateParams")
    NameEditUI(Context context, final LessonName name, final OnCompleteListener complete) {
        // Create dialog
        dialog = new BottomSheetDialog(context);
        // Create layout
        layout = LayoutInflater.from(context).inflate(R.layout.name_enter_ui,null,false);
        ((TextInputEditText)layout.findViewById(R.id.name_input)).setText(name.name);
        // Pin to dialog
        dialog.setContentView(layout);
        // Buttons setup
        layout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        layout.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText tv = layout.findViewById(R.id.name_input);
                name.name = tv.getText().toString();
                complete.onComplete(name);
                dialog.hide();
            }
        });

        // SHOW!
        dialog.show();
    }
}
