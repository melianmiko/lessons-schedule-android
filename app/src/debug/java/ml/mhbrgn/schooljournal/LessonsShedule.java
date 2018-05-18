package ml.mhbrgn.schooljournal;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class LessonsShedule extends Application {
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) Stetho.initializeWithDefaults(this);
    }
}

