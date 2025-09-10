package com.dmss.burbankapp.helpers;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    // leave empty for now
}

/*OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    OkHttpUrlLoader factory = new OkHttpUrlLoader(client);

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
       // super.registerComponents(context, glide, registry);

        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    glide.registry.replace(GlideUrl::class.java,InputStream::class.java,factory)*/
