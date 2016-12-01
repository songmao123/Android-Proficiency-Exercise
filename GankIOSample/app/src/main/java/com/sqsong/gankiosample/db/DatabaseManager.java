package com.sqsong.gankiosample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sqsong.gankiosample.model.DaoMaster;
import com.sqsong.gankiosample.model.DaoSession;
import com.sqsong.gankiosample.model.GankData;
import com.sqsong.gankiosample.model.GankDataDao;
import com.sqsong.gankiosample.ui.GankPostFragment;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by 青松 on 2016/11/25.
 */

public class DatabaseManager {

    public static final String DB_NAME = "gank.db";
    public static final String TYPE_ANDROID = "Android";
    public static final String TYPE_IOS = "iOS";
    public static final String TYPE_WEB = "前端";

    private Context mContext;
    private DaoMaster.DevOpenHelper mDbHelper;
    private static DatabaseManager instance;

    private DatabaseManager(Context context) {
        this.mContext = context;
        this.mDbHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager(context);
                }
            }
        }
        return instance;
    }

    private SQLiteDatabase getReadableDataBase() {
        if (mDbHelper == null) {
            mDbHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME);
        }
        return mDbHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWriteableDataBase() {
        if (mDbHelper == null) {
            mDbHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME);
        }
        return mDbHelper.getWritableDatabase();
    }

    /**
     * query gank data from db.
     * @param type 0: Android data  1: IOS data  2: Web Data
     * @param page
     * @param pageSize
     * @return gank data lists;
     */
    public List<GankData> queryDataWithLimit(int type, int page, int pageSize) {
        DaoMaster daoMaster = new DaoMaster(getReadableDataBase());
        DaoSession daoSession = daoMaster.newSession();
        GankDataDao gankDataDao = daoSession.getGankDataDao();
        QueryBuilder<GankData> builder = gankDataDao.queryBuilder();
        String queryType;
        if (type == GankPostFragment.TYPE_ANDROID) {
            queryType = TYPE_ANDROID;
        } else if (type == GankPostFragment.TYPE_IOS) {
            queryType = TYPE_IOS;
        } else {
            queryType = TYPE_WEB;
        }
        builder.where(GankDataDao.Properties.Type.eq(queryType));
        builder.offset((page - 1) * pageSize);
        // query data order by publish date.
        builder.limit(pageSize).orderDesc(GankDataDao.Properties.Date);
        return builder.list();
    }

    public Observable<List<GankData>> getCachePostObservable(final int type, final int page, final int pageSize) {
        return Observable.create(new Observable.OnSubscribe<List<GankData>>() {
            @Override
            public void call(Subscriber<? super List<GankData>> subscriber) {
                List<GankData> gankDatas = queryDataWithLimit(type, page, pageSize);
                subscriber.onNext(gankDatas);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * inert gank data to database, and remove duplicate data from db.
     * @param gankDatas gank data lists.
     */
    public void insertGankData(List<GankData> gankDatas) {
        if (gankDatas == null || gankDatas.size() < 1) return;

        DaoMaster daoMaster = new DaoMaster(getWriteableDataBase());
        DaoSession daoSession = daoMaster.newSession();
        GankDataDao gankDataDao = daoSession.getGankDataDao();
        gankDataDao.insertOrReplaceInTx(gankDatas);
    }

}
