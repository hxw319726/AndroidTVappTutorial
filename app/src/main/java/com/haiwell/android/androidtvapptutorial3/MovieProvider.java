package com.haiwell.android.androidtvapptutorial3;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/24.
 */

public class MovieProvider {
    private static final String TAG = MovieProvider.class.getSimpleName();

    private static ArrayList<Movie> mItems = null;

    private MovieProvider() {

    }

    public static ArrayList<Movie> getMovieItems() {
        if (mItems == null) {
            mItems = new ArrayList<Movie>();

            Movie movie1 = new Movie();
            movie1.setId(1);
            movie1.setTitle("Title1");
            movie1.setStudio("studio1");
            movie1.setDescription("description1");
            movie1.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
            //movie1.setVideoUrl("http://corochann.com/wp-content/uploads/2015/07/MVI_0949.mp4");
            /* Google sample app's movie */
            movie1.setVideoUrl("http://video.tudou.com/v/XMzEwNDM3NzQ4MA==.html?spm=a2h28.8313471.ab1.da_t_1_3&recoid=733020723384347782&itemid=13746976294662913927&seccateid=");
            mItems.add(movie1);

            Movie movie2 = new Movie();
            movie2.setId(2);
            movie2.setTitle("Title2");
            movie2.setStudio("studio2");
            movie2.setDescription("description2");
            movie2.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02630.jpg");
            //movie2.setVideoUrl("http://corochann.com/wp-content/uploads/2015/07/MVI_0962.mp4");
            /* Google sample app's movie */
            movie2.setVideoUrl("http://112.5.254.248/hc.yinyuetai.com/uploads/videos/common/7ABE015D22C88FB1FC591AFF8C46C9DF.mp4?sc\\u003db8b969d10c7451e8\\u0026br\\u003d781\\u0026vid\\u003d2906257\\u0026aid\\u003d13236\\u0026area\\u003dHT\\u0026vst\\u003d0");
            mItems.add(movie2);

            Movie movie3 = new Movie();
            movie3.setId(3);
            movie3.setTitle("Title3");
            movie3.setStudio("studio3");
            movie3.setDescription("description3");
            movie3.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02529.jpg");
            movie3.setVideoUrl("http://112.5.254.242/hc.yinyuetai.com/uploads/videos/common/3476015D22C68031A2D206A796CBFDDB.mp4?sc\\u003d0e1c155549dffe48\\u0026br\\u003d779\\u0026vid\\u003d2906256\\u0026aid\\u003d13236\\u0026area\\u003dHT\\u0026vst\\u003d0");
            mItems.add(movie3);
        }
        return mItems;
    }
}
