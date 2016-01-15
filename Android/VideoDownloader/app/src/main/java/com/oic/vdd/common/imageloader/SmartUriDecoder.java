package com.oic.vdd.common.imageloader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;

import java.io.IOException;

public class SmartUriDecoder implements ImageDecoder {
    private final ContentResolver m_contentResolver;
    private final BaseImageDecoder m_imageUriDecoder;
 
    public SmartUriDecoder(ContentResolver contentResolver, BaseImageDecoder imageUriDecoder) {
        if (imageUriDecoder == null) {
            throw new NullPointerException("Image decoder can't be null");
        } 
 
        m_contentResolver = contentResolver;
        m_imageUriDecoder = imageUriDecoder;
    } 
 
    @Override 
    public Bitmap decode(ImageDecodingInfo info) throws IOException {
        if (TextUtils.isEmpty(info.getImageKey())) {
            return null; 
        } 
 
        String cleanedUriString = cleanUriString(info.getImageKey());
        Uri uri = Uri.parse(cleanedUriString);
        if (isVideoUri(uri)) {
            return makeVideoThumbnail(info.getTargetSize().getWidth(), info.getTargetSize().getHeight(), getVideoFilePath(uri));
        } 
        else { 
            return m_imageUriDecoder.decode(info);
        } 
    } 
 
    private Bitmap makeVideoThumbnail(int width, int height, String filePath) {
        if (filePath == null) {
            return null; 
        } 
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        Bitmap scaledThumb = scaleBitmap(thumbnail, width, height);
        if(thumbnail != null && !thumbnail.isRecycled()) {
            thumbnail.recycle();
        }
        return scaledThumb;
    } 
 
    private boolean isVideoUri(Uri uri) {
        String mimeType = m_contentResolver.getType(uri)+"";
        return mimeType.startsWith("video/")
                ||uri.toString().endsWith(".mp4")
                ||uri.toString().endsWith(".avi")
                ||uri.toString().endsWith(".mkv")
                ||uri.toString().endsWith(".mpeg")
                ||uri.toString().endsWith(".mov")
                ||uri.toString().endsWith(".dat")
                ||uri.toString().endsWith(".avi");
    } 
 
    private String getVideoFilePath(Uri uri) {
        String columnName = MediaStore.Video.VideoColumns.DATA;
        Cursor cursor = m_contentResolver.query(uri, new String[] { columnName }, null, null, null);
        if(cursor!=null){
            try {
                int dataIndex = cursor.getColumnIndex(columnName);
                if (dataIndex != -1 && cursor.moveToFirst()) {
                    return cursor.getString(dataIndex);
                }
            }
            finally {
                cursor.close();
            }
        }

        return uri.toString();
    } 
 
    private Bitmap scaleBitmap(Bitmap origBitmap, int width, int height) {
        if(origBitmap == null){
            return null;
        }
        float scale = Math.min(
                ((float)width) / ((float)origBitmap.getWidth()),
                ((float)height) / ((float)origBitmap.getHeight())
        ); 
        return Bitmap.createScaledBitmap(origBitmap,
                (int)(((float)origBitmap.getWidth()) * scale),
                (int)(((float)origBitmap.getHeight()) * scale),
                false 
        ); 
    } 
 
    private String cleanUriString(String contentUriWithAppendedSize) {
        // replace the size at the end of the URI with an empty string. 
        // the URI will be in the form "content://....._256x256 
        return contentUriWithAppendedSize.replaceFirst("_\\d+x\\d+$", "");
    } 
}