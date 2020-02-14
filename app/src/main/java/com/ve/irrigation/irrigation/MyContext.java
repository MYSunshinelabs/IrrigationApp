package com.ve.irrigation.irrigation;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class MyContext {
    private Context context;


    public MyContext(Context context) {
        this.context = context;
    }


    @Provides
    public Context getContext() {
        return context;
    }
}
