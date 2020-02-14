package com.ve.irrigation.irrigation;


import android.content.Context;

import dagger.Component;

@Component(modules = MyContext.class)
public interface MyComponent {

    Context getMyContext(Context context);

}
