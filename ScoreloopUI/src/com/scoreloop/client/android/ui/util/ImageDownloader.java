/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scoreloop.client.android.ui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

/**
 * \internal
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {

	private static final int				HARD_CACHE_CAPACITY	= 150;			// cache up to 150 images
	private static final int				DELAY_BEFORE_PURGE	= 180 * 1000;	// 3 minutes in milliseconds
	private static ImageDownloader			imageDownloader		= null;
	private static Cache<String, Bitmap>	_cache				= null;

	private static void assertImageDownloader() {
		if (imageDownloader == null) {
			imageDownloader = new ImageDownloader();
		}
	}

	private static void assertCache() {
		if (_cache == null) {
			_cache = new Cache<String, Bitmap>(HARD_CACHE_CAPACITY);
		}
	}

	/**
	 * Download the specified image from the Internet and binds it to the provided ImageView. The
	 * binding is immediate if the image is found in the cache and will be done asynchronously
	 * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
	 *
	 * @param url The URL of the image to download.
	 * @param loadingDrawable The drawable which is displayed while downloading
	 * @param imageView The ImageView to bind the downloaded image to.
	 * @param errorDrawable The drawable which is displayed if an error occurred while downloading
	 */
	public static void downloadImage(String url, Drawable loadingDrawable, ImageView imageView, Drawable errorDrawable) {
		if (url == null) {
			return;
		}
		assertImageDownloader();
		assertCache();
        final Cache<String, Bitmap>.CacheEntry cacheEntry = _cache.getCacheEntry(url);
        if (cacheEntry == null) {
			imageDownloader.forceDownload(url, loadingDrawable, imageView, errorDrawable);
		} else {
			cancelPotentialDownload(url, imageView);
            final Bitmap bitmap = cacheEntry.getValue();
            if (bitmap == null && errorDrawable != null) {
                imageView.setImageDrawable(errorDrawable);
            } else {
                imageView.setImageBitmap(bitmap);
            }
		}
	}

	/**
	 * Same as download but the image is always downloaded and the cache is not used.
	 * Kept private at the moment as its interest is not clear.
	 */
	private void forceDownload(String url, Drawable drawable, ImageView imageView, Drawable errorDrawable) {
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, errorDrawable);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(drawable, task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	/**
	 * Returns true if the current download has been canceled or if there was no download in
	 * progress on this image view.
	 * Returns false if the download in progress deals with the same url. The download is not
	 * stopped in that case.
	 */
	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	/**
	 * @param imageView Any imageView
	 * @return Retrieve the currently active download task (if any) associated with this imageView.
	 * null if there is no such task.
	 */
	private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	BitmapResult downloadBitmapHttp(String url) {
		// Requires API Level 8: AndroidHttpClient is not allowed to be used from the main thread
		// final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
		// AndroidHttpClient.newInstance("Android");
		final HttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

        // todo implement HttpCacheStorage for caching of redirects

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				return BitmapResult.createNotFound();
			} else if (statusCode != HttpStatus.SC_OK) {
				return BitmapResult.createError();
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					// return BitmapFactory.decodeStream(inputStream);
					// Bug on slow connections, fixed in future release.
					return new BitmapResult(BitmapFactory.decodeStream(new FlushedInputStream(inputStream)));
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			getRequest.abort();
		} catch (IllegalStateException e) {
			getRequest.abort();
		} catch (Exception e) {
			getRequest.abort();
		} // finally {
			// Requires API Level 8
			// if ((client instanceof AndroidHttpClient)) {
			// ((AndroidHttpClient) client).close();
			// }
		// }
		return BitmapResult.createError();
	}

	BitmapResult downloadBitmap(Context context, String url) {
		BitmapResult bitmapResult = LocalImageStorage.getBitmap(context, url);
		if (bitmapResult == null) {
			bitmapResult = downloadBitmapHttp(url);
			if (bitmapResult.isCachable()) { // store to shared location cache
				LocalImageStorage.putBitmap(context, url, bitmapResult);
			}
		}
		return bitmapResult;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * \internal
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, BitmapResult> {
		private String							url;
		private final WeakReference<ImageView>	imageViewReference;
		private final Drawable					errorDrawable;

		public BitmapDownloaderTask(ImageView imageView, Drawable errorDrawable) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.errorDrawable = errorDrawable;
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected BitmapResult doInBackground(String... params) {
			url = params[0];
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				return downloadBitmap(imageView.getContext(), url);
			}
			return null;
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(BitmapResult bitmapResult) {
            final Bitmap bitmap = isCancelled() || bitmapResult == null ? null : bitmapResult.getBitmap();

            if (bitmapResult != null && bitmapResult.isCachable()) {
                addBitmapToCache(url, bitmap);
            }

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with it
				// Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
				if (this == bitmapDownloaderTask) {
					if (bitmap == null && errorDrawable != null) {
						imageView.setImageDrawable(errorDrawable);
					} else {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		}
	}

	static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask>	bitmapDownloaderTaskReference;

		public DownloadedDrawable(Drawable drawable, BitmapDownloaderTask bitmapDownloaderTask) {
			super(((BitmapDrawable) drawable).getBitmap());
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	public static class LocalImageStorage {
		static boolean isStorageWritable() {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		}

		static boolean isStorageReadable() {
			return isStorageWritable() || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		}

		private static File getCacheDir(Context context) {
			File cacheDir = null;
			File storageDir = Environment.getExternalStorageDirectory();
			if (storageDir != null) {
				File tmp = new File(storageDir, "/Android/data/" + context.getPackageName() + "/cache/");
				if ((tmp.exists() && tmp.isDirectory()) || tmp.mkdirs()) {
					cacheDir = tmp;
				}
			}
			return cacheDir;
		}

		private static File getCacheFile(Context context, String url) {
			File cacheFile = null;
			File cacheDir = getCacheDir(context);
			if (cacheDir != null) {
				String fileName = Base64.encodeBytes(url.getBytes());
				cacheFile = new File(cacheDir, fileName);
			}
			return cacheFile;
		}

		public static BitmapResult getBitmap(Context context, String url) {
			if (isStorageReadable()) {
				File cacheFile = getCacheFile(context, url);
				if (cacheFile != null && cacheFile.exists() && cacheFile.canRead()) {
                    if (cacheFile.length() == 0) {
                        return BitmapResult.createNotFound();
                    } else {
					    return new BitmapResult(BitmapFactory.decodeFile(cacheFile.getAbsolutePath()));
                    }
				}
			}
			return null;
		}

        public static boolean putBitmap(Context context, String url, Bitmap bitmap) {
            return putBitmap(context, url, new BitmapResult(bitmap));
        }

		public static boolean putBitmap(Context context, String url, BitmapResult bitmapResult) {
			if (isStorageWritable()) {
				File cacheFile = getCacheFile(context, url);
				try {
					if (bitmapResult.isNotFound()) {
						cacheFile.createNewFile();
                        return true;
					} else {
						FileOutputStream os = new FileOutputStream(cacheFile);
						bitmapResult.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, os);
						os.close();
						return true;
					}
				} catch (Exception e) {
					// ignore
				}
			}
			return false;
		}
	}

	/**
	 * Adds this bitmap to the cache.
	 * @param bitmap The newly downloaded bitmap.
	 */
	private void addBitmapToCache(String url, Bitmap bitmap) {
    	_cache.put(url, bitmap, DELAY_BEFORE_PURGE);
	}

	static class BitmapResult {

		private enum Status {
			OK, NOT_FOUND, ERROR
		}

		static BitmapResult createNotFound() {
			return new BitmapResult(null, ImageDownloader.BitmapResult.Status.NOT_FOUND);
		}

		static BitmapResult createError() {
			return new BitmapResult(null, ImageDownloader.BitmapResult.Status.ERROR);
		}

		private final Bitmap	bitmap;
		private final Status	status;

		BitmapResult(Bitmap bitmap) {
			this.bitmap = bitmap;
			this.status = ImageDownloader.BitmapResult.Status.OK;
		}

		BitmapResult(Bitmap bitmap, Status status) {
			this.bitmap = bitmap;
			this.status = status;
		}

		Bitmap getBitmap() {
			return bitmap;
		}

		boolean isCachable() {
			return status != ImageDownloader.BitmapResult.Status.ERROR;
		}

		boolean isNotFound() {
			return status == ImageDownloader.BitmapResult.Status.NOT_FOUND;
		}
	}

}
