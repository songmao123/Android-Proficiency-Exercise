package com.sqsong.gankiosample.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sqsong.gankiosample.adapter.StringConverter;
import com.sqsong.gankiosample.util.Util;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;

/**
 * Created by 青松 on 2016/11/25.
 */
@Entity
public class GankData implements Parcelable, MultiItemEntity {

    public static final int TYPE_WITH_IMAGE = 1;
    public static final int TYPE_WITHOUT_IMAGE = 2;

    @Unique
    @NotNull
    private String _id;
    private String createdAt;
    private String desc;
    @Convert(converter = StringConverter.class, columnType = String.class)
    private List<String> images;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;
    // add a new filed, ths filed for database sort.
    private Date date;

    public GankData() {
    }

    protected GankData(Parcel in) {
        _id = in.readString();
        createdAt = in.readString();
        desc = in.readString();
        images = in.createStringArrayList();
        publishedAt = in.readString();
        source = in.readString();
        type = in.readString();
        url = in.readString();
        used = in.readByte() != 0;
        who = in.readString();
        long time = in.readLong();
        date = time == -1 ? null : new Date(time);
    }

    @Generated(hash = 1050347922)
    public GankData(@NotNull String _id, String createdAt, String desc, List<String> images,
            String publishedAt, String source, String type, String url, boolean used,
            String who, Date date) {
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.images = images;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.used = used;
        this.who = who;
        this.date = date;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(createdAt);
        dest.writeString(desc);
        dest.writeStringList(images);
        dest.writeString(publishedAt);
        dest.writeString(source);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeByte((byte) (used ? 1 : 0));
        dest.writeString(who);
        dest.writeLong(date != null ? date.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GankData> CREATOR = new Creator<GankData>() {
        @Override
        public GankData createFromParcel(Parcel in) {
            return new GankData(in);
        }

        @Override
        public GankData[] newArray(int size) {
            return new GankData[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    @Override
    public int getItemType() {
        return (images == null || images.size() < 1) ? TYPE_WITHOUT_IMAGE : TYPE_WITH_IMAGE;
    }

    public boolean getUsed() {
        return this.used;
    }

    public Date getDate() {
        return Util.getPublishDate(publishedAt);
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
