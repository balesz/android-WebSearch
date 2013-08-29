package net.solutinno.websearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import net.solutinno.util.PackageHelper;

import java.io.InputStream;

public class AboutFragment extends DialogFragment
{
    WebView mWebView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mWebView = new WebView(getActivity());
        initWebView();
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.action_about)
            .setView(mWebView)
            .setPositiveButton(R.string.caption_ok, null)
            .create();

    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) this.getDialog();
        dialog.setCanceledOnTouchOutside(true);
    }

    private void initWebView() {
        String content = null;
        try {
            InputStream input = getActivity().getAssets().open("about/index.html");
            content = new String(ByteStreams.toByteArray(input), Charsets.UTF_8);
            content = content.replace("{VERSION}", PackageHelper.getPackageVersionName(getActivity()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mWebView.loadDataWithBaseURL("file:///android_asset/about/", content, null, "UTF-8", null);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        });
    }
}
