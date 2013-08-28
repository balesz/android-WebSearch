package net.solutinno.websearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        mWebView.loadUrl("file:///android_asset/about/index.html");
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
